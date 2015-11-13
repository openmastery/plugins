package com.ideaflow.controller

import com.ideaflow.model.entry.BandStart
import com.ideaflow.model.entry.Conflict

class TaskStatus {

    Conflict activeConflict
    BandStart activeBandStart

   	boolean isOpenConflict() {
        activeConflict != null
   	}

   	boolean isOpenBand() {
        activeBandStart != null
   	}

}
