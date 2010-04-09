package edu.american.weiss.lafayette.schedule;

public class FixedRatio extends Ratio {
    
    private long fixedDuration;
    
    public FixedRatio(long fixedDuration) {
        this.fixedDuration = fixedDuration;
        duration = fixedDuration;
    }
    
    public void reset(long restDuration) {
        duration = fixedDuration + restDuration;
    }

}