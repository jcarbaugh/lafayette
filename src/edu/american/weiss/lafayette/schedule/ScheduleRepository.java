package edu.american.weiss.lafayette.schedule;

import java.util.HashMap;


public class ScheduleRepository {
    
    private HashMap scheduleMap;
    private static ScheduleRepository srInstance;
    
    private ScheduleRepository() {
        scheduleMap = new HashMap(10);
    }
    
    public static ScheduleRepository getInstance() {
        if (srInstance == null) {
            srInstance = new ScheduleRepository();
        }
        return srInstance;
    }
    
    public Schedule getSchedule(Class compositeClass, String schedId) {
        Schedule s = (Schedule) scheduleMap.get(compositeClass.getName() + schedId);
        return s;
    }
    
    public void addSchedule(Class compositeClass, String schedId, Schedule s) {
        scheduleMap.put(compositeClass.getName() + schedId, s);
    }
    
    public void removeSchedule(Class compositeClass, String schedId) {
    	scheduleMap.remove(compositeClass.getName() + schedId);
    }

}