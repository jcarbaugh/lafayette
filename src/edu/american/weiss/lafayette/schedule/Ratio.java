package edu.american.weiss.lafayette.schedule;

public abstract class Ratio implements Schedule {
    
    protected long duration;
    protected long remaining;

    public void start() {
        if (remaining == 0) {
            reset(0);
        } else {
            duration = remaining;
        }
    }
    
    public void pause(long behaviors) {
        if (behaviors < duration) {
            remaining = duration - behaviors;
        } else {
            remaining = 0;
        }
    }
    
    public boolean isInInterval(long behaviors) {
        boolean isInInterval = true;
        if (behaviors > duration) {
            isInInterval = false;
        }
        return isInInterval;
    }
    
    public abstract void reset(long restDuration);

}