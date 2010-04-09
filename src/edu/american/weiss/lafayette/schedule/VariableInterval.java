package edu.american.weiss.lafayette.schedule;

import java.util.Random;


public class VariableInterval extends Interval {

    private long maxDuration;
    private long minDuration;
    private Random rand;
    
    public VariableInterval(long minDuration, long maxDuration) {
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
        rand = new Random();
        reset(0);
    }
    
    public void reset(long restDuration) {
    	int randInt = rand.nextInt((int) (maxDuration - minDuration));
        duration = randInt + minDuration;
        startTime = System.currentTimeMillis();
    }
    
}