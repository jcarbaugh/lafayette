package edu.american.weiss.lafayette.schedule;

public interface Schedule {

    public void start();
    public void pause(long l);
    public void reset(long restDuration);
    public boolean isInInterval(long l);
    
}