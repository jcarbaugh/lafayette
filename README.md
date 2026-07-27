# Lafayette

A Java application for running operant conditioning experiments in a touchscreen-equipped operant chamber. It draws visual stimuli on a full-screen display, records the subject's responses, drives a food hopper through a digital I/O board, and logs everything to disk.

Originally written for research at American University, and later extended and used for research at the National Institutes of Health.

> **Recovered source.** The original working copy was lost in a hard drive crash. This
> repository holds the files that could be restored. All 122 source files compile, but
> several supporting pieces (native libraries, media, some config) were not recoverable,
> and a few experiments were mid-rewrite when the drive failed. See
> [Known gaps](#known-gaps) before trying to run anything.

---

## Contents

- [History](#history)
- [Quick start](#quick-start)
- [How it works](#how-it-works)
- [Repository layout](#repository-layout)
- [Experiments](#experiments)
- [Configuration](#configuration)
- [Hardware I/O](#hardware-io)
- [Data output](#data-output)
- [Known gaps](#known-gaps)
- [Notes for restoring it](#notes-for-restoring-it)

---

## History

The commit history covers **April 2010 – July 2011** and falls into three phases:

| Period | Work |
| --- | --- |
| Apr 2010 | Initial import: the framework plus the red/blue triangle experiments (`TerminalBaseline`, `Test1`, `PeckTraining`, `AutoShaping`, `Shaping`, `Habituation`, `HopperTraining`). Hopper driven by an Opto22 PCI-AC5 card. |
| Sep 2010 – Dec 2010 | The `ObjectDiscrimination` task: image stimuli, WAV feedback, per-trial logging, and early termination on a performance criterion. Hopper control moved to a USB ADU relay controller. |
| Jul 2011 | `MTS` (matching-to-sample) started, along with `ImageCache`. Left unfinished. |

The two `edu.american` namespaces reflect that split: `weiss` is the reusable framework,
`huntsberry` holds the experiment-specific composites, elements, and recorders. Both are
named for the labs the code was written for. `com.carbauja` is the author's own namespace
and contains a single orphaned interface.

The vocabulary in the older code (hopper, peck training, autoshaping, habituation) is
standard pigeon operant work. The later `ObjectDiscrimination` recorder writes a
`Monkey ID` field into its log header.

## Quick start

Requirements: a JDK (verified building on JDK 26) and a display. Without native libraries
present the app falls back to `MockHopper`, so it runs on a normal desktop for development.

```sh
# compile
./mvnw compile

# run an experiment (fully-qualified experiment class as the only argument)
java -cp conf:target/classes edu.american.weiss.lafayette.Application \
    edu.american.huntsberry.experiment.ObjectDiscrimination

# or the red/blue triangle task
java -cp conf:target/classes edu.american.weiss.lafayette.Application \
    edu.american.huntsberry.experiment.TerminalBaseline
```

`ObjectDiscrimination` ends on its own, once the accuracy criterion is met or the trial
count runs out. `TerminalBaseline` does not: it has no trial cap and no criterion, so it
runs until the operator ends the session (see [Runtime controls](#runtime-controls)).

`conf/` must be on the classpath, since properties are loaded as classpath resources, not
as files. `logs/` must exist; nothing creates it, so an empty one is tracked in the repo.
Both `log_path` and the `ObjectDiscrimination` media paths are relative to the working
directory, so run from the repository root.

With no argument, `Application` runs the built-in `TestExperimentImpl` demo.

**Maven.** The build is `pom.xml`, driven through the checked-in wrapper, so a JDK is the
only prerequisite — `./mvnw` downloads Maven itself on first use and needs network access
to do it.

| Command | Result |
| --- | --- |
| `./mvnw compile` | classes in `target/classes` |
| `./mvnw package` | `target/lafayette.jar` (executable manifest) and `target/lafayette-dist.zip` |
| `./mvnw clean` | removes `target/` |
| `./mvnw compile exec:exec` | runs the built-in demo |
| `./mvnw compile exec:exec -Pod` \| `-Ptb` \| `-Pmts` | runs `ObjectDiscrimination`, `TerminalBaseline`, or `MTS` |
| `./mvnw compile exec:exec -Dexperiment=<class>` | runs any other experiment |

`exec:exec` does not compile on its own, hence the explicit `compile` — the Ant run targets
got that from a `depends`. It forks a JVM rather than running in Maven's, so the
application's `System.exit(0)` shutdown path ends the app and not the build.

The dist zip unpacks into a ready run directory: `conf/`, `lib/lafayette.jar`, an empty
`logs/`, and the two launcher scripts, with `run.sh` already executable. Two differences
from the Ant zip it replaces: it lands in `target/` rather than the repository root, and it
is named for the project version instead of carrying a date stamp, which is what Maven's
version field is for.

The source tree stays where it is rather than moving to `src/main/java`; `pom.xml` points
`sourceDirectory` at `src`. `conf/` is deliberately **not** a resource directory, so the
properties files stay editable beside the jar instead of being sealed inside it.

The `<javah>` step that made the old Ant build fail on JDK 10 and later is gone with the
build file that contained it. Nothing in the project needs it: it generated headers for the
native ADU bindings, whose C source did not survive (see [Known gaps](#known-gaps)).

### Runtime controls

Responses are mouse presses on the response panel, which in the chamber is a touchscreen.
Three keys are handled while an experiment is running:

| Key | Effect |
| --- | --- |
| `Esc` | Ends the session: stops the composite loop and shows the final composite |
| `Space` | Manually fires a reinforcer event (opens the hopper) |
| `Break` / `Cancel` | Immediate shutdown: closes the hopper, flushes recorders, exits |

With `show_debugbar=true` there is also an on-screen **Exit** button, a status line, and a
hopper indicator that turns green while the hopper is open.

The keys need the window to hold keyboard focus, and the window now claims it. `init()`
calls `claimKeyboardFocus()` once the window is actually on screen, which asks the OS to
bring the application forward and then puts focus on the response panel; a
`WindowFocusListener` puts it back there every time the window is activated again, so
clicking away and back does not leave the keys dead. The response panel is the deliberate
focus owner, and the debug bar's **Exit** button is no longer focusable, so it can neither
take the initial focus nor swallow `Space` as a button press.

**On macOS the application still cannot bring itself forward.** Launched from a terminal on
macOS 26 the window comes up showing but inactive — no active window, no focus owner —
because the OS does not let a background process steal focus, and `Desktop.requestForeground`
does not override that. This is a platform restriction rather than an application bug: an
unmodified build behaves identically. Click the window once (or use the debug bar's **Exit**
button, which goes through Swing and needs no focus); after that first activation focus
lands on the response panel and stays there for the rest of the session. See
[Known gaps](#known-gaps).

## How it works

### The composite model

A **composite** is one full-screen stimulus configuration, or what the subject sees during
one segment of the session. Each composite owns a list of **composite elements**: shapes
(usually `Polygon`s) with a fill colour, an outline colour, and a z-index. Elements are the
touch targets. Each element carries a list of **composite actions** that fire when it is
touched.

Composites are typed, and the type drives the state machine:

```
INITIAL_COMPOSITE → ACTIVE_COMPOSITE ⇄ REST_COMPOSITE → … → FINAL_COMPOSITE
```

### The main loop

`CompositeController` (a `Runnable` on its own thread) drives the session. Each iteration:

1. If the current composite's duration has elapsed (or a change was forced), pick the next one.
2. If the hopper is still open and the experiment wants reinforcement to run to completion
   (`isReinforcementWaiting()`), extend the current composite by 50 ms and re-check.
3. If the current composite is a rest period, the experiment is *correcting*, and the
   subject responded during it, extend the rest by `response_correction_duration` and reset
   the response count. This is the standard correction procedure for responding during a
   timeout.
4. Otherwise ask the experiment for the next composite. After an INITIAL or REST composite
   the next is always an active one; after an active one it's a rest with probability
   `rest_probability`, else another active one.
5. A `null` from the experiment ends the session, and the final composite is displayed.

Note that this loop does not sleep; it spins.

### Events

Everything is broadcast through `EventController` to registered `ChamberEventListener`s.
The event types:

| Event | Fired when |
| --- | --- |
| `ResponseEvent` | The subject touches the screen (carries x, y and the active composite) |
| `CompositeTransitionEvent` | A composite is replaced (carries both previous and new) |
| `ReinforcerEvent` | A reinforcer is earned or manually triggered |
| `ReinforcerCompleteEvent` | The hopper closes |
| `DestroyEvent` | Shutdown; recorders use this to flush and close their files |

`BaseExperimentImpl` handles `ResponseEvent` itself: it asks the active composite for the
actions at the touched point and runs them, either inline or on a new thread depending on
each action's `runAsThread()`.

### Actions

| Action | Effect |
| --- | --- |
| `HopperAction` | Fires a `ReinforcerEvent`, which opens the hopper |
| `HopperRatioAction` | Counts responses; fires a `ReinforcerEvent` every *n*th one |
| `AudioAction` | Plays a preloaded WAV by id |
| `RestAction` | Forces a transition into a rest composite |
| `NextCompositeAction` | Forces a transition to the next composite |

Experiments can also attach a *global* action to a composite, which fires on any response
anywhere in that composite, optionally gated by a schedule.

### Schedules

`ScheduleRepository` maps (composite class, schedule id) → `Schedule`. A composite element
or a composite's global action can name a schedule id; the action only fires when the
schedule's interval has lapsed, after which the schedule resets.

- `FixedInterval` / `VariableInterval`: time-based gating (VI is the one actually used, by
  `TerminalBaseline` and `Test1`)
- `FixedRatio` / `VariableRatio`: response-count gating (see [Known gaps](#known-gaps);
  these are not wired up correctly)

### Chamber and hardware

`Chamber` is the singleton facade over the physical apparatus: the `UserInterface`
(full-screen `JFrame`, drawn into with a `Graphics2D`) and the `Hopper`. The hopper
implementation is chosen by the `hopper_class` property and reflectively instantiated via
its static `getInstance()`. A background thread polls the active hopper and closes it once
its duration has elapsed.

## Repository layout

```
pom.xml                         Maven build (compile / package / exec profiles)
mvnw, mvnw.cmd, .mvn/           Maven wrapper; no Maven install needed
assembly/dist.xml               Layout of the distribution zip
run.sh, run.bat                 Launcher wrappers used by the dist zip
conf/                           Properties files (loaded from the classpath)
lib/jni.bat                     MSVC command line that builds adu.dll
src/
  edu/american/weiss/lafayette/     ── the framework ──
    Application.java                Entry point, property store, wiring, shutdown
    EventController.java            Listener registry / broadcast
    ImageManager.java               Eager image cache, keyed by id
    ImageCache.java                 Lazy queue-based image cache (used by MTS)
    chamber/                        UserInterface, Chamber, Hopper implementations, audio
    composite/                      Composite / CompositeElement / CompositeAction, controller
    actions/                        Hopper, audio, rest, next-composite actions
    schedule/                       FI / VI / FR / VR schedules and the repository
    experiment/                     Experiment interface + BaseExperimentImpl
    event/                          Chamber event types and the listener interface
    data/                           Recorders and log writers, incl. MedPC output
    io/jni/                         Native bindings: ADUController, Opto22Controller
    gui/                            "Processing, please wait" shutdown frame
    client/                         Remote monitoring client (stubs, see gaps)
    xml/                            XML experiment loader (commented out, see gaps)
  edu/american/huntsberry/          ── experiment-specific code ──
    experiment/                     The nine experiments
    composite/                      Concrete composites (stimulus screens)
    compositeelement/               Concrete elements (touch targets)
    data/                           ODRecorder and the MedPC cumulative recorders
    test/                           Older duplicate composites, unreferenced
  com/carbauja/lafayette/data/      DataWriter interface (orphaned)
```

## Experiments

An experiment implements `Experiment` (usually by extending `BaseExperimentImpl`) and
supplies the initial, next, rest, and final composites. `Application` loads
`<SimpleClassName>.properties` from the classpath when it instantiates the class, so
`ObjectDiscrimination` reads `conf/ObjectDiscrimination.properties`.

| Experiment | What it does | State |
| --- | --- | --- |
| `Habituation` | Hopper held open continuously, no discriminative stimuli | Complete |
| `HopperTraining` | Alternates a grey screen with hopper access | Complete |
| `Shaping` | Full-screen grey; any response opens the hopper | Complete |
| `PeckTraining` / `PeckTrainingRed` | Blue bottom-left / red top-right triangle keys on an FR schedule via `HopperRatioAction` | Complete |
| `AutoShaping` / `AutoShapingRed` | Autoshaping with blue/red keys; reinforcer delivered if the key is *not* touched | Incomplete; see gaps |
| `TerminalBaseline` | The core stimulus-control task: one of eight red/blue triangles (4 orientations × 2 colours) inside a white frame, each colour on its own VI schedule | Working; verified end to end July 2026 |
| `Test1` | Warm-up block of random single-colour composites, then shuffled blocks of blue / red / compound (red+blue) composites, no reinforcement | Complete |
| `ObjectDiscrimination` | Two images side by side, positions randomised. Touching the correct one plays a WAV and opens the hopper; the incorrect one plays a different WAV. Ends early once a sliding-window accuracy criterion is met | Working; the most finished experiment |
| `MTS` | Matching-to-sample | **Unfinished**; see gaps |

`TestExperimentImpl` (in `experiment/test/`) is the no-argument demo: six alternating
blue/red composites on FI and VI schedules.

### ObjectDiscrimination in detail

The most complete task, and the one the later work was built around:

1. A `StartComposite` shows "CLICK TO START" and preloads the images offscreen.
2. Each trial displays the correct and incorrect image, offset a quarter-screen left and
   right, with the side chosen at random.
3. A touch on either image records the outcome (`correct` / `incorrect`), plays the
   corresponding WAV, opens the hopper if correct, and forces an inter-trial interval.
4. No touch within `max_response_time` records the trial as `refused`.
5. The ITI screen is black after a correct trial, blue otherwise.
6. A sliding window of the last `criteria_trials` outcomes is kept. Once
   `criteria_threshold` of them are correct, the session ends early on an orange
   "CRITERIA MET" screen; otherwise it ends on a red screen after `trials` trials.

### TerminalBaseline in detail

One of the original April 2010 tasks, and the one that exercises the most of the framework:
the schedule repository, global composite actions, and the element geometry and z-ordering.

1. The session opens on a black frame for 5 s, then alternates active and rest composites
   until the operator ends it.
2. Each active composite is a `GenericComposite` holding two elements: a white
   `BlackFrameElement` (a 110 × 110 box at z-index 0) and, on top of it at z-index 1, one
   of eight right triangles — four orientations × red/blue — filling half of the inner
   100 × 100 box. Both are centred on the response panel. Everything is computed from
   `ui.getResponseSize()` in the experiment's *constructor*, so the response panel must
   already have been laid out; `Application` does call `ui.init()` first.
3. Every composite carries a `HopperAction` as its global action, gated by the VI schedule
   registered for its colour. A touch anywhere fires the hopper only once the interval has
   lapsed, after which the schedule resets — so reinforcement is intermittent, and the two
   colours run independent schedules that persist across composites.
4. `rest_probability` is 100, so every active composite is followed by a rest. Because
   `isCorrecting()` is true, responding during a rest extends it by
   `response_correction_duration` — the standard correction procedure.
5. `getNextComposite()` never returns `null`, so nothing ends the session from the inside.
   `Esc` stops the loop and shows the final black frame.

A scripted session in July 2026 confirmed the whole path on JDK 26 with `MockHopper`: all
eight triangles appeared; touches inside a triangle resolved to it and touches on the
surrounding frame resolved to the white element beneath, so the z-index ordering works both
ways; 76 touches on active composites produced 15 reinforcers rather than 76; rests that
were responded to came out exactly `response_correction_duration` longer than rests that
were not; and the session ended on the final composite with both recorders flushed.

Note that reinforcers came far further apart than the 2–5 s the VI is configured for — no
gap under 6 s. That is the schedule working as designed rather than a fault: a schedule
only advances while a composite of its colour is on screen (`start()` on display,
`pause()` on destroy), and once the interval has lapsed the reinforcer still waits for the
next response.

## Configuration

Properties are loaded in two passes: `conf/application.properties` first, then
`conf/<ExperimentName>.properties`, which can override anything global. All lookups go
through the flat `Application` property store.

### `application.properties` (global)

| Key | Meaning |
| --- | --- |
| `hopper_class` | Hopper implementation to instantiate (`MockHopper`, `AduHopper`, `Opto22Hopper`) |
| `reinforcement_duration` | How long the hopper stays open, in ms |
| `warmup_duration` | Warm-up period in ms (used by `Test1`) |
| `show_cursor` | `true` shows a crosshair cursor; `false` hides it entirely |
| `show_debugbar` | `true` shows the exit button, status line, and hopper indicator |
| `xplatform_laf` | `true` forces the cross-platform Swing look and feel |
| `log_path` | Directory for log output. **Must end with a path separator** |

### Per-experiment keys

| Key | Used by |
| --- | --- |
| `trials`, `iti`, `max_response_time` | `ObjectDiscrimination`, `MTS` |
| `criteria_trials`, `criteria_threshold` | `ObjectDiscrimination` |
| `correct_image_path`, `incorrect_image_path` | `ObjectDiscrimination` |
| `correct_response_wav`, `incorrect_response_wav` | `ObjectDiscrimination` |
| `imageDirectory` | `MTS` |
| `interval_min`, `interval_max` | `TerminalBaseline`, `Test1` (VI schedule bounds) |
| `composite_min`, `composite_max` | Active composite duration bounds (`TerminalBaseline`, `Test1`, `PeckTraining` / `PeckTrainingRed`) |
| `composite_duration` | `Test1` (fixed duration of a test-block composite) |
| `rest_min`, `rest_max`, `rest_probability` | Rest composite duration bounds and likelihood |
| `rest_duration` | `BaseExperimentImpl.getRestDuration()`; only `HopperTraining` calls it |
| `response_correction_duration` | Extension applied when the subject responds during a rest. Read only when the experiment's `isCorrecting()` is true — of the experiments here, `TerminalBaseline` and `Test1` |
| `ratio` | `PeckTraining`, `PeckTrainingRed` (responses per reinforcer, via `HopperRatioAction`) |
| `pseudo_random` | `true` prevents the same composite appearing twice in a row |
| `block_count` | `Test1` (number of test blocks before the session ends) |
| `show_active_area` | `true` outlines invisible touch targets in cyan, a debugging aid |
| `adu_relay` | Which ADU relay drives the hopper (`K0`…`K7`) |

Several keys support a per-composite override: `Application.getProperty(key, subKey)` tries
`key.subKey` first and falls back to `key`. `TerminalBaseline` uses this to give red and
blue composites different interval bounds (`interval_min.red`, `interval_min.blue`) and
each of the eight composite ids its own duration.

`conf/adu.properties` maps relay names to their bitmask values and shouldn't need editing.
`conf/javax.comm.properties` is a leftover from an earlier serial-port approach; nothing in
the current source uses it.

## Hardware I/O

Three hopper implementations, selected by `hopper_class`:

- **`Opto22Hopper`** → `Opto22Controller`, JNI over `pci-ac5.dll`, an Opto22 PCI-AC5
  digital I/O card. The original hardware.
- **`AduHopper`** → `ADUController`, JNI over `adu.dll`, an Ontrak ADU-series USB relay
  controller. Sends `mk<mask>` commands, where the mask comes from `adu.properties` via
  `adu_relay`. This replaced the Opto22 card in October 2010.
- **`MockHopper`**: no hardware. Tracks state and fires the same events, so the full
  session logic runs on a desktop. This is the default in the checked-in config.

`lib/jni.bat` holds the MSVC command line that built `adu.dll` against Ontrak's
`AduHid.lib`:

```bat
cl -I"%JAVA_HOME%\include" -I"%JAVA_HOME%\include\win32" -LD ".\edu_american_weiss_lafayette_io_jni_ADUController.c" -Fe"adu.dll" -link"AduHid.lib"
```

Both native paths are Windows-only, so hardware runs required Windows; development happened
on macOS against `MockHopper`.

## Data output

Everything lands in `log_path`. Which files appear depends on which recorders
`Application` registers: `ResponseRecorderListener` and `EventRecorderListener` for every
session, plus `ODRecorder` for `ObjectDiscrimination` only.

| File | Written by | Contents |
| --- | --- | --- |
| `responselog_<start>.log` | `ResponseRecorderListener` | Every response: elapsed ms, (x, y), composite group, element group, plus a line per composite transition |
| `composite.log` | `EventRecorderListener` | Tab-separated elapsed ms and composite id, one row per transition |
| `od_<timestamp>.log` | `ODRecorder` | Per-trial `ObjectDiscrimination` results: trial number, `correct` / `incorrect` / `refused`, response time. Ends with a summary block: trial count, refused, correct, incorrect, % correct, mean response time |
| `run.log` | `run.sh` / `run.bat` | Redirected stdout |

Three more recorders exist but are not currently registered:

- `MedPCCumulativeRecorder` and its subclasses (`MasterRedBlueRecorder`,
  `CompositeGroupCumulativeRecorder`) emit `!medpc_<start>_<group>` files in MED-PC's
  cumulative-record format: a header of start/stop dates and identifiers, then one
  `time.typeindex0` datum per event, with time in centiseconds.
- The `ResponseSummaryListener*` family writes `response_summary.log`, a cross-tabulation of
  responses by composite group against element group. There are four variants, one per study
  design (`TB2`, `TB3`, `ET`, and the base red/blue version).

Both sets are commented out of `Application`'s imports and wiring (see below).

### How recorders are flushed

`ODRecorder` is the only `QueuedRecorder`: it does not write on the thread that raises an
event, it queues the event and writes from its own thread. That makes shutdown ordering
load-bearing, because the file is only complete once the queue is empty.

Each pass of `QueuedRecorder.run()` drains the queue to empty, then sleeps 25 ms, so the
recorder keeps up with a session instead of falling behind it. `Application.shutdown()`
raises the `DestroyEvent`, tears down the event controller so nothing further can be
queued, and only then calls `QueuedRecorder.destroy()`, which clears the run flag and
**blocks** until the recorder has made a final drain pass and run `destroyChild()`. The
join is capped at five seconds so a wedged `processChamberEvent` cannot hang shutdown
behind the `ProcessingFrame`. Only after that does `System.exit(0)` run.

The order matters in both directions: the `DestroyEvent` is what makes `ODRecorder` close
its `FileOutputStream`, so it has to be queued *before* the stop signal, and the drain has
to finish *before* the exit. This is what puts the summary block in `od_<timestamp>.log`.
A recorder added later gets the same treatment for free by extending `QueuedRecorder`, but
it does need wiring into `shutdown()` alongside `odRecorder`.

Because the queue is written by whichever thread raised the event and drained by the
recorder thread, `eventQueue` is a `ConcurrentLinkedQueue` and `isRunning` is `volatile`.
Do not "simplify" either back: without the `volatile` the recorder thread can hoist the
flag read out of its loop, never see the stop signal, and turn shutdown into a five-second
hang. `isRunning` is set true in the constructor rather than at the top of `run()`, so a
`destroy()` arriving before the thread is scheduled cannot be undone by the thread starting.

## Known gaps

What the crash and the passage of time cost, roughly in order of how much it matters:

**Missing files.** `.gitignore` excluded `lib/`, `media/`, and `logs/`, so none of their
contents survived:

- `adu.dll`, `pci-ac5.dll`, and the JNI C source
  (`edu_american_weiss_lafayette_io_jni_ADUController.c`) referenced by `lib/jni.bat`.
  Only the build command line remains. Loading either controller class will fail without them.
  If the C source ever turns up, note that the `javah` its header generation depended on was
  removed in JDK 10; `javac -h <dir>` does that job now. `lib/` is also *still* ignored, so a
  recovered dll has to be force-added or it will be lost the same way twice.
- The `media/` directory: `greendino.jpg`, `greenshape.jpg`, `correct.wav`, and
  `incorrect.wav`, the stimuli and feedback sounds `ObjectDiscrimination` needs.
  **Recreated, and not the originals** — see the next entry. `media/` and `logs/` are no
  longer ignored, so this cannot happen again.

**Recreated media.** The four files under `media/` are stand-ins generated to get
`ObjectDiscrimination` running; they are *not* the stimuli any published result was
collected with. `greendino.jpg` is a green dinosaur silhouette and `greenshape.jpg` a green
triangle, both 200×200 on black to match the composite's black background; `correct.wav` is
a rising two-tone chime and `incorrect.wav` a low buzz, both 16-bit PCM mono. Anything
comparing against historical data needs the real stimuli, which are gone.

**Absolute paths in config.** `conf/MTS.properties` still points `imageDirectory` at a Photo
Booth folder, a stand-in used to test the image cache, and needs updating.
`conf/application.properties` and `conf/ObjectDiscrimination.properties` used to carry the
same problem; their paths are now repo-relative.

**Reconstructed experiment parameters.** Every experiment now has a properties file, so
none of them still crash on a null value — but the six written in July 2026
(`HopperTraining`, `PeckTraining`, `PeckTrainingRed`, `AutoShaping`, `AutoShapingRed`,
`Test1`) carry **invented values, not recovered ones**. Nothing in the repo or the commit
history records what these tasks were actually run at. The numbers were chosen to be
behaviourally sensible and to make a session observable in a reasonable time: `ratio = 1`
because peck training reinforces every peck, a long variable ITI against a short key
presentation for autoshaping, and `Test1`'s shared keys mirrored from
`TerminalBaseline.properties`. Anything comparing against historical data needs the real
parameters, which are gone — the same caveat as the recreated media above.

**Lost recorder classes.** `Application` imports, commented out,
`edu.american.huntsberry.data.TerminalBaseline2Recorder` and `TerminalBaseline3Recorder`.
Neither is in the repo. The `ResponseSummaryListenerTB2` / `TB3` classes that pair with them
did survive, so the study designs are partly recoverable from those.

**`MTS` is unfinished.** The sample/match alternation in `getNextComposite()` is an empty
`if`/`else`, `MTSComposite` draws a single image at the origin with all the touch-target and
scoring code commented out, and `getFinalComposite()` picks the "CRITERIA MET" screen at
random rather than from actual performance. Also, `ImageCache.loadDirectory` will throw an
NPE if `imageDirectory` does not exist.

**`AutoShaping` is incomplete.** `AutoComposite.initialize()` is abstract, and both
subclasses (`BlueAutoComposite`, `RedAutoComposite`) leave it as an empty
`// TODO Auto-generated method stub`, so they draw nothing. `AutoComposite.destroy()` also
hardcodes a 4000 ms reinforcer duration.

**Ratio schedules don't work.** `BaseCompositeElement.isActive()` feeds
`System.currentTimeMillis()` to both `Interval` *and* `Ratio` schedules, so a ratio schedule
is compared against a timestamp instead of a response count. `VariableRatio` compounds this
by computing its duration as `rand.nextLong() * (max - min)`, which overflows into
nonsense. Ratio-like behaviour in the working experiments comes from `HopperRatioAction`,
which counts responses itself. `FixedRatio` and `VariableRatio` are unused.

**Dead and stub code.**

- `client/LafayetteClient`, `client/net/ClientSocketProcessor`, and `client/net/SocketManager`
  are empty classes. `client/ui/ClientInterface` is a partial Swing layout with a hardcoded
  `c:\1125788029765.png` in its `main`.
- `xml/ExperimentXmlParser` is entirely commented out: an abandoned attempt to define
  experiments in XML rather than Java, using dom4j.
- `TriangleCompositeUtil` builds composites from `TriangleCompositeElement`, whose
  constructor body is commented out, so it produces elements with no shape. Nothing
  references either class; they probably belonged to an experiment that didn't survive.
- `edu.american.huntsberry.test` holds an older duplicate set of composites. Nothing imports
  from it.
- `com.carbauja.lafayette.data.DataWriter` is an interface with no implementations.

**Smaller traps.**

- `Chamber` calls `Application.getProperty("hopper_class", "…MockHopper")`, but that
  two-argument overload takes a *sub-key*, not a default. It works only because the lookup
  falls back to the bare `hopper_class` key; if that key were absent it would return null.
- `HopperListener` opens the hopper for `reinforcement_duration` from the properties file,
  ignoring the duration carried on the `ReinforcerEvent` it received.
- The window claims keyboard focus once it is on screen, but on macOS the OS will not let a
  terminal-launched process bring itself forward, so the window still has to be activated
  once by hand before `Esc` / `Space` / `Break` do anything. See
  [Runtime controls](#runtime-controls).
- `ReinforcerEvent`s raised by `HopperAction` carry a null composite.
  `BaseExperimentImpl` does call `Reinforcer.setComposite(comp)`, but `HopperAction`
  stores it in a private field of its own instead of the inherited
  `BaseCompositeAction.composite`, which is what its `run()` puts on the event. Nothing
  reads a composite off a reinforcer event today, so it is latent.
- `MockHopper` fires `ReinforcerCompleteEvent` twice per reinforcer: once from
  `deactivateHopper()` and once from `AbstractHopper.run()`, which notifies again right
  after calling it.
- `CompositeController.destroy()` guards a transition event with `if (isActive)`
  immediately after `setActive(false)`, so that event is never sent.
- `VariableInterval.reset()` calls `rand.nextInt(max - min)`, which throws if
  `interval_min == interval_max`.
- `CompositeController.run()` is a busy-wait with no sleep, so it pins a core for the
  duration of a session. The fields it spins on (`isActive`, `isForcedChange`,
  `isForcedRest`, `compositeDuration`, …) are `volatile` for a reason: without it a modern
  JIT hoists the reads out of the loop and the controller never observes a response, so
  every experiment hangs on the start screen. Do not "clean up" those keywords.
- `AbstractHopper.run()` never exits its loop; the process relies on `System.exit(0)`.

## Notes for restoring it

A reasonable order of attack:

1. ~~**Get `ObjectDiscrimination` running end to end with `MockHopper`.**~~ Done. The paths
   are relative, the media are recreated, `logs/` is tracked, and the composite loop no
   longer hangs (see the `volatile` note above). A session runs start screen → trials →
   criterion, and writes all three logs.
2. ~~**Then `TerminalBaseline`**, which needs no media and exercises the schedule machinery
   and the composite/element geometry.~~ Done. It needed no changes: the schedules, the
   correction procedure, and the element geometry all still work as written. What the run
   turned up was in the framework around it — `ODRecorder` writing a junk log for every
   experiment (fixed), the `QueuedRecorder` drain problem (fixed in step 3 below), and the
   keyboard-focus problem (fixed in step 4 below, apart from a macOS platform restriction).
3. ~~**Make `QueuedRecorder` drain properly**, since as it stands a long
   `ObjectDiscrimination` session loses the tail of its own data: drain the whole queue
   each pass rather than one event, sleep far less than a second, and call `destroy()` from
   `Application.shutdown()` so the final flush actually runs before `System.exit(0)`.~~
   Done, and it needed a fourth change: `destroy()` now blocks until the recorder has
   drained, since signalling it without waiting still let `System.exit(0)` win the race.
   It also had to go from `protected` to `public` to be reachable from `Application`, and
   the queue and run flag had to be made thread-safe first — see
   [How recorders are flushed](#how-recorders-are-flushed). Measured on a 25-trial session
   at ~2.6 transitions/second: before, the log had 7 trials and no summary block; after, all
   25 and the summary.
4. ~~**Give the chamber window keyboard focus** once it is on screen, so the documented
   `Esc` / `Space` / `Break` controls work reliably rather than depending on how the window
   happened to be activated.~~ Done on the application's side, with one platform caveat that
   is not the application's to fix. `init()` now calls `claimKeyboardFocus()` *after* the
   window is up — the old `requestFocus` ran in `initComponents()`, before the frame was
   realized, so it never did anything — and that asks the OS to bring the app forward, then
   focuses the response panel. A `WindowFocusListener` re-focuses the panel on every
   activation. The **Exit** button is no longer focusable, which matters more than it looks:
   with `show_debugbar=true` it was the natural initial focus owner and it consumed `Space`
   as a button press, so the documented "fire a reinforcer" key was **shutting the session
   down** instead. Measured on JDK 26 with `MockHopper`: the designated focus owner moved
   from the frame to the response panel, and with the window activated, real synthesized
   `Space` fired a reinforcer, `Esc` ended the session, and key code 3 ran the full shutdown
   with the recorders flushed. `Space` delivered to the **Exit** button exits the JVM on an
   unmodified build and is ignored after the change. Verified with the debug bar both on and
   off. The caveat: on macOS 26 a terminal-launched JVM cannot bring itself to the front, so
   the window still comes up inactive and needs one click before the keys do anything —
   `Desktop.requestForeground` is called and does not override it, and an unmodified build
   behaves the same way, which is what made this look intermittent (it worked in one session
   out of four) rather than broken.
5. ~~**Write the missing properties files** for the experiments that lack one. The required
   keys are listed above and each experiment's constructor makes them explicit.~~ Done, with
   two corrections to the list above it. `Habituation` and `Shaping` needed **nothing**: both
   override `isCorrecting()` to `false`, so `response_correction_duration` is never read,
   neither calls `getRestDuration()`, and the only key either touches is
   `reinforcement_duration`, which is already global. They were never broken, and got a file
   only so every experiment has one place to tune hopper time. In the other direction, `ratio`,
   `composite_duration`, and `block_count` are read through `Application.getIntProperty` rather
   than `getProperty`, so a grep for the latter misses them, and `Test1` additionally needs
   `response_correction_duration` — it is the only experiment in this set that corrects.
   Verified two ways on JDK 26 with `MockHopper`: a static pass confirming every key each
   experiment parses as a number resolves against `application.properties` plus its own file,
   and a run of all eight. Each reached and passed the code that used to throw —
   `HopperTraining` alternated a 10001 ms grey screen with 4051 ms of hopper access,
   `PeckTraining` ran a 12184 ms key presentation and a 7853 ms rest (bounds 10000–20000 and
   5000–10000), `PeckTrainingRed` 17401 ms and 6359 ms, both `AutoShaping` variants reached
   their rest composite at 12009 ms, and `Test1` ran initial → warm-up composite → rest →
   warm-up composite with the rest lasting 1927 ms against bounds of 1000–2000. As a control,
   removing `conf/PeckTraining.properties` again reproduces
   `NumberFormatException: Cannot parse null string` at `PeckTraining.<init>`. The values
   themselves are invented rather than recovered — see
   [Known gaps](#known-gaps). This does not make `AutoShaping` work: it no longer crashes, but
   its composites still draw nothing.
6. ~~**Fix the Ant build** by dropping the `<javah>` step from the `compile` target; it is
   only needed when rebuilding the native ADU bindings, which can't be rebuilt anyway
   without the missing C source.~~ Done, but by **replacing Ant with Maven** rather than
   patching it — repairing a build tool the Java ecosystem has moved off was worth less than
   putting the project on a current one, and deleting `build.xml` retires the `<javah>`
   problem outright. `pom.xml` keeps the flat `src/` tree via `sourceDirectory` instead of
   moving 117 files, and pointedly does not make `conf/` a resource directory: the properties
   have to stay editable beside the jar, not sealed into it. A script-only Maven wrapper is
   checked in, so the build needs a JDK and nothing else — no committed binary. `assembly/dist.xml`
   reproduces the old dist layout. Two behaviors that looked like details and were not:
   `Application` branches on `args.length`, not on the argument's *content*, so an unset
   experiment placeholder passed as an empty string took the wrong branch and failed with
   `Unable to locate class:` — the run profiles therefore *append* an argument rather than
   substituting into one, which is what keeps the no-argument demo working. And `verbose:jni`,
   which the Ant `run-od` / `run-tb` / `run-mts` targets all passed, is dead: nothing in `src/`
   or `conf/` reads it and `main` only ever looks at `args[0]`, so it was not carried over.
   Verified on JDK 26 with `MockHopper`: `./mvnw clean package` builds **123 classes**, exactly
   what the old `javac` over `src/` produced, with `Main-Class` set and no `conf/` leakage into
   the jar. All four run paths start a session and write logs — bare demo, `-Pod` (which also
   emits `od_<timestamp>.log`, confirming the experiment registers as a listener), `-Ptb`, and
   `-Dexperiment=…Habituation` (whose response log shows `HabituationComposite`). The dist zip
   was unpacked to a clean directory and launched through `run.sh`, which arrives at mode 0755
   and logged `TerminalBaseline` moving from `initial` to `red_bottom_right` at 5010 ms.
   One cosmetic wart: `mvnw` prints `Unable to locate a Java Runtime` on macOS when the JDK is
   not registered with `/usr/libexec/java_home`, then falls back to `PATH` and works. Setting
   `JAVA_HOME` silences it.
7. **`MTS` needs designing, not just fixing.** The sample/match alternation was never
   written, so finishing it means deciding what the task should do rather than recovering
   what it did.
