package edu.american.weiss.lafayette.schedule;

import java.util.Random;


public class VariableRatio extends Ratio {

    private long maxDuration;
    private long minDuration;
    private Random rand;
    
    public VariableRatio(long minDuration, long maxDuration) {
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
        rand = new Random();
        long randLong = rand.nextLong() * (maxDuration - minDuration);
        duration = randLong + minDuration;
    }
    
    public void reset(long restDuration) {
        long randLong = rand.nextLong() * (maxDuration - minDuration);
        duration = randLong + minDuration + restDuration;
    }

}