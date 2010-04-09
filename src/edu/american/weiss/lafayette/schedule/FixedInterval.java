package edu.american.weiss.lafayette.schedule;

public class FixedInterval extends Interval {
    
    private long fixedDuration;
    
    public FixedInterval(long fixedDuration) {
        this.fixedDuration = fixedDuration;
        duration = fixedDuration;
    }
    
    public void reset(long restDuration) {
        duration = fixedDuration;
    }

}