package com.ideaflow.report

import com.ideaflow.model.Timeline
import com.ideaflow.model.EventType


class ReportGenerator {

    Timeline timeline

    ReportGenerator(Timeline timeline) {
        this.timeline = timeline
    }

    void writeConsumptionReport(Writer writer) {

        writer.write("File Consumption Report\n\n")

        def groups = timeline.groupByIntervalName().sort { entry1, entry2 ->
            entry2.value <=> entry1.value
        }
        groups.entrySet().each { entry ->
            writer.write("File: ${convertToTime(entry.value)} - ${entry.key}\n")
        }
        writer.write("\n\n")
    }

    void writeConflictSummaryReport(Writer writer) {
        writer.write("Conflict Summary Report\n\n")

        int totalNonConflict = timeline.totalNonConflict
        int totalConflict = 0
        def conflicts = timeline.groupByConflict()

        conflicts.each { conflict ->
            totalConflict += conflict.duration
        }

        writer.write("Total Conflict Time: ${convertToTime(totalConflict)}\n")
        writer.write("Total Non-Conflict Time: ${convertToTime(totalNonConflict)}\n")

        def percent = calculatePercent(totalConflict, totalNonConflict)
        writer.write("Percent Conflict Time: $percent%\n")
        writer.write("\n\n")
    }

    void writeConflictDetailReport(Writer writer) {
        writer.write("Conflict Detail Report\n\n")

        def conflicts = timeline.groupByConflict()

        conflicts.each { conflict ->
            writer.write("Conflict: ${conflict.conflictNote}\n")
            writer.write("-- duration: ${convertToTime(conflict.duration)}\n")
            writer.write("-- resolve: ${conflict.resolveNote}\n")
            writer.write("\n")
        }
        writer.write("\n\n")
    }

    void writeNormalizedTimeline(Writer writer) {
        writer.write('CONFLICT,TIME,DURATION,CONTEXT,COMMENT\n')

        timeline.events.each { event ->
            String type = event.type
            String time = convertToTime(event.eventTime)
            String conflict = event.isConflict()? '1' : '0'
            String context = event.type == EventType.interval? event.text : '------------------------'
            String duration = event.type == EventType.interval? event.duration : '-------------------'
            String comment = event.type != EventType.interval? "$type: ${event.text}" : ''
            writer.write("$conflict,$time,$duration,$context,$comment\n")
        }
    }

    double calculatePercent(Integer conflict, Integer nonConflict) {
        int total = conflict + nonConflict
        double percent = ((double)conflict)*100/total

        percent = Math.round(percent*100)/100
        return percent
    }

    String convertToTime(def time) {
        int duration = time as Integer
        int minutes = duration / 60
        int hours = minutes / 60
        minutes = minutes - (60 * hours)
        int seconds = duration - (60 * minutes) - (60 * 60 * hours)

        "${padLeft(hours)}:${padLeft(minutes)}:${padLeft(seconds)}"
    }

    private String padLeft(def number) {
        int intNum = number as Integer
        String strNum = intNum as String
        strNum.padLeft(2, '0')
    }
}
