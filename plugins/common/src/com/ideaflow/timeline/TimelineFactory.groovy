package com.ideaflow.timeline

import com.ideaflow.model.BandEnd
import com.ideaflow.model.BandStart
import com.ideaflow.model.BandType
import com.ideaflow.model.Conflict
import com.ideaflow.model.EditorActivity
import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.model.ModelEntity
import com.ideaflow.model.Note
import com.ideaflow.model.Resolution


class TimelineFactory {

    Timeline timeline

    Timeline create(IdeaFlowModel ifm) {
        TimelineBuilder builder = new TimelineBuilder()
        ifm.entityList.each { ModelEntity entity ->
            builder.addEntity(entity)
        }
        return builder.timeline
    }

    private static class TimelineBuilder {
        Timeline timeline = new Timeline()
        Map<BandType, BandStart> activeBands = [:]
        Conflict activeConflict
        int relativeTime = 0

        void addEntity(BandStart bandStart) {
            // what if band is already started???
            activeBands.put(bandStart.type, bandStart)
        }

        void addEntity(BandEnd bandEnd) {
            BandStart bandStart = activeBands.remove(bandEnd.type)

            if (bandStart != null) {
                timeline.addTimeBand(new TimeBand(relativeTime, bandStart, bandEnd))
            } else {
                // expode???
            }
        }

        void addEntity(EditorActivity editorActivity) {
            relativeTime += editorActivity.duration
            timeline.addActivityDetail(new ActivityDetail(relativeTime, editorActivity))
        }

        void addEntity(Conflict conflict) {
            activeConflict = conflict
            //if already set, exploded!!
        }

        void addEntity(Resolution resolution) {
            //if no active conflict, explode!!
            timeline.addConflictBand(new ConflictBand(relativeTime, activeConflict, resolution))
        }

        void addEntity(Note note) {
            timeline.addEvent(new Event(relativeTime, note))
        }

        void addEntity(ModelEntity unknown) {
            throw new RuntimeException("Unknown ModelEntity type ${unknown?.class}")
        }

    }



}
