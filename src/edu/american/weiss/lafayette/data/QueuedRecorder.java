package edu.american.weiss.lafayette.data;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.american.weiss.lafayette.event.ChamberEvent;
import edu.american.weiss.lafayette.event.listener.ChamberEventListener;

public abstract class QueuedRecorder implements ChamberEventListener, Runnable {

	/** How long to wait between drain passes when the queue is empty. */
	private static final long POLL_INTERVAL = 25;

	/** How long destroy() waits for the final drain before giving up. */
	private static final long DRAIN_TIMEOUT = 5000;

	// written by the shutdown thread, read by the recorder thread
	private volatile boolean isRunning;
	private volatile Thread recorderThread;

	// events are queued by the thread raising them and drained by the recorder
	private Queue<ChamberEvent> eventQueue;

	protected long startTime;
	protected long stopTime;

	public QueuedRecorder() {
		eventQueue = new ConcurrentLinkedQueue<ChamberEvent>();
		// set here, not in run(), so a destroy() that lands before the thread
		// is scheduled can't be undone by run() setting it back to true
		isRunning = true;
	}

	public void run() {

		recorderThread = Thread.currentThread();
		startTime = System.currentTimeMillis();

		while (isRunning) {

			drainQueue();

			try {
				Thread.sleep(POLL_INTERVAL);
			} catch (InterruptedException ie) { }

		}

		// everything queued between the last pass and the stop signal
		drainQueue();

		stopTime = System.currentTimeMillis();

		destroyChild();

	}

	private void drainQueue() {
		ChamberEvent ce;
		while ((ce = eventQueue.poll()) != null) {
			processChamberEvent(ce);
		}
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public void handleChamberEvent(ChamberEvent ce) {
		eventQueue.add(ce);
	}

	/**
	 * Stop the recorder and wait for it to drain what is still queued. Callers
	 * shutting the application down must invoke this before System.exit(0),
	 * and only after the last event they care about has been raised.
	 */
	public void destroy() {

		isRunning = false;

		Thread thread = recorderThread;

		if (thread != null) {
			try {
				// bounded: a wedged processChamberEvent must not hang shutdown
				thread.join(DRAIN_TIMEOUT);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}
		}

	}

	protected abstract void destroyChild();
	protected abstract void processChamberEvent(ChamberEvent ce);

}
