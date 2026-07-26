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
mkdir -p build logs
javac -d build $(find src -name '*.java')

# run an experiment (fully-qualified experiment class as the only argument)
java -cp conf:build edu.american.weiss.lafayette.Application \
    edu.american.huntsberry.experiment.ObjectDiscrimination
```

`conf/` must be on the classpath, since properties are loaded as classpath resources, not
as files. `logs/` must exist; nothing creates it.

With no argument, `Application` runs the built-in `TestExperimentImpl` demo.

**Ant.** `build.xml` has `compile`, `dist`, `run`, `run-od`, `run-tb`, `run-mts`, and
`run-jar` targets, and `dist` produces a zip with `run.sh` / `run.bat` wrappers. The
`compile` target also calls `<javah>`, which depends on the `javah` tool removed in JDK 10,
so the Ant build will not work on a modern JDK without dropping that step. Plain `javac`
over `src/` compiles cleanly.

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
build.xml                       Ant build (compile / dist / run targets)
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
    server/                         TCP control server (unused, see gaps)
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
| `HopperTraining` | Alternates a grey screen with hopper access | Complete; no properties file |
| `Shaping` | Full-screen grey; any response opens the hopper | Complete |
| `PeckTraining` / `PeckTrainingRed` | Blue bottom-left / red top-right triangle keys on an FR schedule via `HopperRatioAction` | Complete; no properties file |
| `AutoShaping` / `AutoShapingRed` | Autoshaping with blue/red keys; reinforcer delivered if the key is *not* touched | Incomplete; see gaps |
| `TerminalBaseline` | The core stimulus-control task: one of eight red/blue triangles (4 orientations × 2 colours) inside a white frame, each colour on its own VI schedule | Working; last fixed Dec 2010 |
| `Test1` | Warm-up block of random single-colour composites, then shuffled blocks of blue / red / compound (red+blue) composites, no reinforcement | Complete; no properties file |
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
| `composite_min`, `composite_max`, `composite_duration` | Active composite duration bounds |
| `rest_min`, `rest_max`, `rest_duration`, `rest_probability` | Rest composite duration and likelihood |
| `response_correction_duration` | Extension applied when the subject responds during a rest |
| `ratio` | `PeckTraining` (responses per reinforcer) |
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
`Application` registers, currently `ResponseRecorderListener`, `EventRecorderListener`,
and `ODRecorder`.

| File | Written by | Contents |
| --- | --- | --- |
| `responselog_<start>.log` | `ResponseRecorderListener` | Every response: elapsed ms, (x, y), composite group, element group, plus a line per composite transition |
| `composite.log` | `EventRecorderListener` | Tab-separated elapsed ms and composite id, one row per transition |
| `od_<timestamp>.log` | `ODRecorder` | Per-trial `ObjectDiscrimination` results: trial number, `correct` / `incorrect` / `refused`, response time. Ends with a summary block: trial count, refused, correct, incorrect, % correct, mean response time |
| `run.log` | `run.sh` / `run.bat` | Redirected stdout, including the shutdown summary from `DataRecorder.debug()` |

Three more recorders exist but are not currently registered:

- `MedPCCumulativeRecorder` and its subclasses (`MasterRedBlueRecorder`,
  `CompositeGroupCumulativeRecorder`) emit `!medpc_<start>_<group>` files in MED-PC's
  cumulative-record format: a header of start/stop dates and identifiers, then one
  `time.typeindex0` datum per event, with time in centiseconds.
- The `ResponseSummaryListener*` family writes `response_summary.log`, a cross-tabulation of
  responses by composite group against element group. There are four variants, one per study
  design (`TB2`, `TB3`, `ET`, and the base red/blue version).

Both sets are commented out of `Application`'s imports and wiring (see below).

## Known gaps

What the crash and the passage of time cost, roughly in order of how much it matters:

**Missing files.** `.gitignore` excludes `lib/`, `media/`, and `logs/`, so none of their
contents survived:

- `adu.dll`, `pci-ac5.dll`, and the JNI C source
  (`edu_american_weiss_lafayette_io_jni_ADUController.c`) referenced by `lib/jni.bat`.
  Only the build command line remains. Loading either controller class will fail without them.
- The `media/` directory: `greendino.jpg`, `greenshape.jpg`, `correct.wav`, and
  `incorrect.wav`, the stimuli and feedback sounds `ObjectDiscrimination` needs.
- `logs/`, which several recorders assume already exists.

**Absolute paths in config.** `conf/application.properties` and
`conf/ObjectDiscrimination.properties` point at `~/Projects/lafayette/...`, with
the Windows equivalents commented out beneath. `conf/MTS.properties` points `imageDirectory`
at a Photo Booth folder, a stand-in used to test the image cache. All need updating.

**Missing properties files.** Only `MTS`, `ObjectDiscrimination`, and `TerminalBaseline`
have one. `PeckTraining`, `PeckTrainingRed`, `AutoShaping`, `AutoShapingRed`,
`HopperTraining`, and `Test1` read keys that no checked-in file defines
(`ratio`, `composite_min`/`max`, `rest_min`/`max`, `rest_duration`, `composite_duration`,
`block_count`), and will fail with a `NumberFormatException` on a null value.

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

- `ControlServer` / `SocketHandler` implement a small TCP protocol (`getData`,
  `setVariable`, `getCumulativeRecorder`) for remote monitoring, but nothing ever starts the
  server.
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

**Wiring quirks.** `Application` registers `ODRecorder` unconditionally, for every
experiment, not just `ObjectDiscrimination`. `DataRecorderListener` is never registered,
which means the static `DataRecorder` store is never populated, so the shutdown summary
prints nothing and `SocketHandler`'s `getData` would report zeros.

**Smaller traps.**

- `Chamber` calls `Application.getProperty("hopper_class", "…MockHopper")`, but that
  two-argument overload takes a *sub-key*, not a default. It works only because the lookup
  falls back to the bare `hopper_class` key; if that key were absent it would return null.
- `HopperListener` opens the hopper for `reinforcement_duration` from the properties file,
  ignoring the duration carried on the `ReinforcerEvent` it received.
- `VariableInterval.reset()` calls `rand.nextInt(max - min)`, which throws if
  `interval_min == interval_max`.
- `CompositeController.run()` is a busy-wait with no sleep, so it pins a core for the
  duration of a session.
- `AbstractHopper.run()` never exits its loop; the process relies on `System.exit(0)`.

## Notes for restoring it

A reasonable order of attack:

1. **Get `ObjectDiscrimination` running end to end with `MockHopper`.** It is the most
   complete experiment and exercises images, audio, per-trial logging, and the criterion
   logic. It needs: a `logs/` directory, the four `media/` files recreated, and the paths in
   `conf/ObjectDiscrimination.properties` and `conf/application.properties` pointed
   somewhere real. Making the paths relative would remove that step permanently.
2. **Then `TerminalBaseline`**, which needs no media and exercises the schedule machinery
   and the composite/element geometry. It was working as of the last commit that touched it.
3. **Re-register `DataRecorderListener`** in `Application` if the aggregate counts and the
   shutdown summary are wanted, and make `ODRecorder` registration conditional on the
   experiment.
4. **Write the missing properties files** for the experiments that lack one. The required
   keys are listed above and each experiment's constructor makes them explicit.
5. **Fix the Ant build** by dropping the `<javah>` step from the `compile` target; it is
   only needed when rebuilding the native ADU bindings, which can't be rebuilt anyway
   without the missing C source.
6. **`MTS` needs designing, not just fixing.** The sample/match alternation was never
   written, so finishing it means deciding what the task should do rather than recovering
   what it did.
