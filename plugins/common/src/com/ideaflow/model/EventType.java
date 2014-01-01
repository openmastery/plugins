package com.ideaflow.model;

import java.util.ArrayList;
import java.util.List;


public enum EventType {
    startConflict,
    endConflict,
    note,
    open,
    closed,
    interval;

    static List<EventType> FLOW_TYPES;
    
    static {
    	FLOW_TYPES = new ArrayList<EventType>();
    	FLOW_TYPES.add(startConflict);
    	FLOW_TYPES.add(endConflict);
    	FLOW_TYPES.add(note);
    }

    static boolean isFlowType(EventType type) {
        return FLOW_TYPES.contains(type);
    }

}