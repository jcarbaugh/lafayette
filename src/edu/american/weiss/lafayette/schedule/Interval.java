package edu.american.weiss.lafayette.schedule;

public abstract class Interval implements Schedule {
    
    protected long startTime;
    protected long duration;
    protected long remaining;
    
    public void start() {
        startTime = System.currentTimeMillis();
        if (remaining == 0) {
            reset(0);
        } else {
            duration = remaining;
        }
    }
    
    public void pause(long currentTime) {
        long elapsed = currentTime - startTime;
        if (elapsed < duration) {
            remaining = duration - elapsed;
        } else {
            remaining = 0;
        }
    }
    
    public boolean isInInterval(long currentTime) {
        //System.out.println(currentTime + " " + startTime + " " + (currentTime - startTime) + " " + duration);
        boolean isInInterval = true;
        if ((currentTime - startTime) > duration) {
            isInInterval = false;
        }
        return isInInterval;
    }
    
    public abstract void reset(long restDuration);
    
    
}