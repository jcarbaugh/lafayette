# Architecture

This document covers the design rationale behind Lafayette: why it is structured the way it
is, what the structure makes cheap or expensive to change, and where the model reaches its
limits. For the mechanics of how the pieces operate, see [README.md](README.md).

## Contents

- [Design context](#design-context)
- [Core decomposition](#core-decomposition)
- [Layering](#layering)
- [Event flow](#event-flow)
- [Extension points](#extension-points)
- [Design constraints](#design-constraints)
- [Trial-phase expressiveness](#trial-phase-expressiveness)
- [Implications for future work](#implications-for-future-work)

## Design context

The prevailing tool for operant chamber control at the time this was written was MED-PC,
programmed in MedState Notation: a domain-specific language in which each experiment is
expressed directly as a set of states and timed transitions. That model is a good fit for
procedures built from timers, counters, and discrete inputs, and it makes the contingencies
of a procedure explicit and auditable.

It fits poorly, however, for procedures involving collections and general computation:
loading and shuffling image sets, building counterbalanced trial blocks with adjacency
constraints, playing audio, or rendering arbitrary geometry to a touchscreen. Lafayette
targets exactly those procedures, which is the primary reason it is a general-purpose
program rather than a state-machine script.

The design departs from the state-machine model in one specific way, described below. It
does not abandon state machines; it relocates one.

## Core decomposition

The central premise is that an operant session has an **invariant skeleton** and **variable
content**.

The skeleton is the same across essentially every procedure in the domain: an opening
period, alternation between stimulus presentations and intertrial intervals, a response
window that closes on time or on response, reinforcement delivery, correction procedures for
responding during a timeout, and a terminating condition. What varies between studies is
which stimulus appears next and what gets recorded about it.

The architecture encodes that split directly:

| Concern | Where it lives | Varies per experiment |
| --- | --- | --- |
| Session state machine | `CompositeController` | No |
| Stimulus selection policy | `Experiment` implementations | Yes |
| Stimulus rendering and touch targets | `Composite` / `CompositeElement` | Yes |
| Consequences of a response | `CompositeAction` implementations | Yes |
| Reinforcement gating | `Schedule` implementations | Yes |
| Data collection | `ChamberEventListener` recorders | Yes |
| Hardware I/O | `Hopper` implementations | No (varies per apparatus) |

`CompositeController` holds the only session-level state machine in the system
(`INITIAL → ACTIVE ⇄ REST → FINAL`, plus hopper-wait and correction handling). It is written
once and shared by every experiment. An `Experiment` is reduced to a generator with four
methods: `getInitialComposite()`, `getNextComposite()`, `getRestComposite()`, and
`getFinalComposite()`, returning `null` to end the session.

The consequence is that procedural invariants can only be implemented incorrectly in one
place, rather than being re-encoded per study. The cost is that any procedure whose skeleton
differs from the built-in one cannot be expressed without changing the framework. See
[Trial-phase expressiveness](#trial-phase-expressiveness).

## Layering

Two package roots express the intended dependency direction:

```
edu.american.weiss.lafayette   framework: no knowledge of any specific experiment
edu.american.huntsberry        experiments: composites, elements, recorders per study
com.carbauja.lafayette         author-namespaced utility interfaces
```

The framework layer defines the abstractions (`Experiment`, `Composite`, `CompositeElement`,
`CompositeAction`, `Schedule`, `Hopper`, `ChamberEventListener`) and the machinery that
drives them. The experiment layer supplies concrete implementations. Experiments are
selected at runtime by fully-qualified class name passed as an argument to `Application`,
which then reflectively instantiates the class and loads a matching properties file. No
framework code needs to know the set of available experiments.

**This direction is violated in two places**, each a point where framework code acquired a
dependency on experiment code:

- `weiss.lafayette.composite.TriangleCompositeUtil` imports `huntsberry.composite.GenericComposite`
  and `huntsberry.compositeelement.TriangleCompositeElement`.
- `weiss.lafayette.Application` imports `huntsberry.data.ODRecorder`, which is specific to
  one experiment, and `huntsberry.experiment.ObjectDiscrimination` for the `instanceof`
  check that gates its registration.

These are the places where adding a new experiment may require touching framework code.

## Event flow

All communication between subsystems passes through `EventController`, a synchronous
broadcast registry. Producers construct a `ChamberEvent` and notify; consumers implement
`ChamberEventListener`. There is no direct coupling between a producer and its consumers.

```
touchscreen press
    └─> CompositeController.mousePressed
            └─> ResponseEvent ──> EventController.notifyListeners
                                      ├─> Experiment          (looks up and runs actions at x,y)
                                      ├─> ResponseRecorderListener
                                      ├─> EventRecorderListener
                                      ├─> ODRecorder          (queued, drained on its own thread)
                                      ├─> UserInterface       (status/indicator updates)
                                      └─> HopperListener      (opens hopper on ReinforcerEvent)
```

Notification is synchronous and single-threaded: `notifyListeners` iterates the registration
list and calls each listener inline. Listeners that need to do slow work are responsible for
deferring it themselves, which the queue-based recorders (`QueuedRecorder`,
`CumulativeRecorder`) do by enqueuing the event and draining on a separate thread.

Registration order therefore matters, and is fixed in `Application.main`. Two consequences
follow: a listener that throws will prevent later listeners from seeing that event, and a
listener that blocks will stall the thread that produced the event, which for responses is
the AWT event thread.

## Extension points

The architecture optimizes for three kinds of change, in rough order of how often they were
needed:

**Adding a data collection format.** New recorders are pure additions: implement
`ChamberEventListener`, register it, write output on `DestroyEvent`. No experiment or
framework code changes. The tree contains eight recorder variants accumulated this way
(`ResponseSummaryListener` plus its `TB2`, `TB3`, and `ET` study-specific variants, the
`MedPCCumulativeRecorder` family, `ResponseRecorderListener`, `EventRecorderListener`, and
`ODRecorder`), which is the extension point that saw the most use.

**Adding an experiment.** Implement `Experiment`, add a properties file, pass the class name
on the command line. The most significant validation of this seam is `ObjectDiscrimination`:
the framework was built for key-pecking at coloured triangles, and roughly six months later
absorbed a two-alternative image discrimination task with audio feedback, image preloading,
and a sliding-window accuracy criterion, without framework changes.

**Swapping hardware.** `Hopper` implementations are selected by the `hopper_class` property
and instantiated reflectively through a static `getInstance()`. The October 2010 migration
from an Opto22 PCI-AC5 card to an Ontrak ADU USB relay controller added one class and
changed one property; no session logic moved.

A fourth property follows from the same seam: `MockHopper` implements the full `Hopper`
contract with no hardware attached, so complete sessions run on an ordinary desktop. All
session logic, timing behaviour, and data output can be exercised without a chamber.

**Interoperability as a design constraint.** `MedPCCumulativeRecorder` emits MED-PC's
cumulative-record format. Preserving the incumbent system's output format means data from
this system flows into existing analysis pipelines unchanged, decoupling adoption of the new
control software from replacement of the surrounding toolchain.

## Design constraints

Areas where the architecture imposes costs or has known weaknesses.

### Timing model

Timing is derived from polling `System.currentTimeMillis()` rather than from a scheduler.
`CompositeController.run()` is a bare `while (isActive)` loop with no sleep that compares
elapsed time against the current composite's duration. The queued recorders poll their
event queues on a one-second `Thread.sleep`, so recorded events may lag their occurrence by
up to that interval, and queue depth is unbounded in between.

This is the least rigorous part of the design, and it is load-bearing: response latency and
stimulus duration accuracy are the measurements the system exists to produce. A scheduler
or timing kernel with explicit deadlines would be a more appropriate foundation and is the
main respect in which state-machine frameworks in this domain are ahead.

### Rendering model

Composites draw immediately into a `Graphics2D` obtained from `JPanel.getGraphics()` rather
than during `paintComponent`. Rendering therefore happens on the `CompositeController`
thread rather than the AWT event thread, and drawn content is not retained across repaints.
Each composite compensates by clearing and redrawing the full response area in its `init`.
This is workable for a full-screen kiosk-style display that nothing else repaints, but it
means the display is not reconstructible from application state.

### Dual role of `Experiment`

`Experiment` extends `ChamberEventListener`, so an experiment is both a stimulus generator
(pushed to by the controller) and an event consumer (pushed to by the event bus). Trial
outcome consequently travels between the two roles through mutable instance state rather
than through a parameter or return value.

`ObjectDiscrimination` illustrates the pattern: a response fires an `ODAction`, which calls
back into the experiment to set `lastResponseWasCorrect`; that field is then read later in
`getRestComposite()` to choose the intertrial-interval colour and to update the accuracy
criterion window. The trial's outcome is real state with a defined lifetime, but the
architecture gives it no explicit representation. Adding a third participant to that
handoff requires understanding the ordering implicitly.

### `Schedule` generalization

`Schedule` unifies interval schedules (gated by elapsed time) and ratio schedules (gated by
response count) behind a single `isInInterval(long)` method. The parameter's meaning depends
on the implementing subtype, and callers must therefore know the subtype to supply a correct
value. `BaseCompositeElement.isActive()` does not: it passes `System.currentTimeMillis()`
for both branches, so ratio schedules are compared against a timestamp.

The working ratio behaviour in the system does not use this abstraction at all.
`HopperRatioAction` counts responses directly and fires a reinforcer every *n*th one.
`FixedRatio` and `VariableRatio` are unreferenced. The unified interface provided no
polymorphism benefit at any call site and concealed the type confusion.

## Trial-phase expressiveness

The most consequential structural limit is that `getNextComposite()` takes no arguments and
returns a single composite. The experiment is asked "what is next" with no indication of
where in a trial the session currently is, and can return only one screen.

This is sufficient for procedures where a trial is one stimulus presentation, which covers
every completed experiment in the tree. It is not sufficient for procedures with internal
phase structure, where the same trial spans several screens whose content depends on earlier
screens in the same trial. Matching-to-sample is the canonical example: a sample stimulus,
optionally a delay, then a choice array containing the sample among distractors.

Two aspects of the framework obstruct this:

1. **No phase parameter.** The generator interface cannot express "this call is the sample
   phase of trial *n*." `MTS` works around this with an instance field toggled on each call:

   ```java
   currentComposite *= -1;	// swap composite
   if (currentComposite == SAMPLE_COMPOSITE) {
       // show sample
   } else if (currentComposite == MATCH_COMPOSITE) {
   }
   ```

   Both branches are empty, and the method returns the same single-image composite either
   way.

2. **The controller may interleave rest composites.** After an active composite,
   `CompositeController` inserts a rest composite with probability `rest_probability` before
   requesting another active one. An experiment therefore cannot guarantee that two
   composites it intends as consecutive phases of one trial are actually adjacent.

`MTS` is the point at which the generator model stopped being sufficient. Completing it
requires extending the framework rather than adding another `Experiment` implementation.

## Implications for future work

The architecture's central decision, factoring the invariant session skeleton out of
individual experiments, holds up. The extension points that were exercised most (recorders,
experiments, hardware) are the ones that remained cheap.

The gap is that the state machine was generalized at only one level. `CompositeController`
handles session structure; nothing handles trial structure, which is left to mutable fields
in experiment implementations and is not expressible when a trial spans multiple screens.

An extension that addresses both the [dual-role](#dual-role-of-experiment) and
[trial-phase](#trial-phase-expressiveness) constraints would introduce an explicit,
per-experiment trial state machine beneath the session state machine, with:

- named phases declared by the experiment (`SAMPLE → DELAY → CHOICE`, or
  `PRESENT → RESPOND → FEEDBACK`),
- the current phase and accumulated trial state passed into the generator rather than held
  in fields,
- a guarantee that the controller does not interleave rest composites within a trial.

That is additive with respect to the existing seams: `Composite`, `CompositeElement`,
`CompositeAction`, and the recorder listeners are unaffected, and single-phase experiments
reduce to a one-phase trial.

Two other changes are independent of the above and can be made separately:

- Replacing the polling loops with a scheduler, which is the highest-value change for
  measurement validity.
- Restoring the framework/experiment dependency direction at the three points listed under
  [Layering](#layering).
