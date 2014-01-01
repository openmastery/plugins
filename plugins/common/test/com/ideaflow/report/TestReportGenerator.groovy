package com.ideaflow.report

import com.ideaflow.model.EventType
import com.ideaflow.model.Timeline
import test.support.FixtureSupport

@Mixin(FixtureSupport)
class TestReportGenerator extends GroovyTestCase {

    ReportGenerator reportGenerator
    Timeline timeline
    StringWriter writer

    void setUp() {
        timeline = new Timeline()
        reportGenerator = new ReportGenerator(timeline)
        writer = new StringWriter()
    }

    void testConvertToTime_ShouldZeroPadElements() {
        String timeStr = reportGenerator.convertToTime(73)
        assert "00:01:13" == timeStr
    }

    void testConvertToTime_ShouldSplitInHoursMinutesSeconds() {
        String timeStr = reportGenerator.convertToTime( (60*60*3)+(60*51)+29)
        assert "03:51:29" == timeStr
    }

    void testWriteConsumptionReport_ShouldSortTimeDescending() {
        timeline.addInterval(FILE, 2)
        timeline.addInterval(OTHER_FILE, 10)

        reportGenerator.writeConsumptionReport(writer)
        String expected = "File Consumption Report\n\n" +
                "File: 00:00:10 - $OTHER_FILE\n" +
                "File: 00:00:02 - $FILE\n" +
                "\n\n"
        assertEquals(expected, writer.toString())
    }

    void testWriteConflictSummary() {
        timeline.addInterval(FILE, 5)
        timeline.addEvent('conflict', EventType.startConflict)
        timeline.addInterval(FILE, 10)
        timeline.addEvent('conflict', EventType.endConflict)
        timeline.addInterval(FILE, 1)

        reportGenerator.writeConflictSummaryReport(writer)

        String expected = "Conflict Summary Report\n\n" +
                "Total Conflict Time: 00:00:10\n" +
                "Total Non-Conflict Time: 00:00:06\n" +
                "Percent Conflict Time: 62.5%\n" +
                "\n\n"

        assertEquals(expected, writer.toString())

    }

    void testWriteConflictDetail() {
        timeline.addEvent('starting', EventType.startConflict)
        timeline.addInterval(FILE, 10)
        timeline.addEvent('ending', EventType.endConflict)

        reportGenerator.writeConflictDetailReport(writer)

        String expected = "Conflict Detail Report\n\n" +
                "Conflict: starting\n" +
                "-- duration: 00:00:10\n" +
                "-- resolve: ending\n" +
                "\n\n\n"

        assertEquals(expected, writer.toString())
    }

    void testWriteNormalizedTimeline() {
        timeline.addEvent('mynote', EventType.note)
        timeline.addInterval(FILE, 5)
        timeline.addEvent('start', EventType.startConflict)
        timeline.addInterval(OTHER_FILE, 10)
        timeline.addEvent('end', EventType.endConflict)

        reportGenerator.writeNormalizedTimeline(writer)

        String expected = "CONFLICT,TIME,DURATION,CONTEXT,COMMENT\n" +
                "0,00:00:00,-------------------,------------------------,note: mynote\n" +
                "0,00:00:00,5,$FILE,\n" +
                "1,00:00:05,-------------------,------------------------,startConflict: start\n" +
                "1,00:00:05,10,$OTHER_FILE,\n" +
                "1,00:00:15,-------------------,------------------------,endConflict: end\n"

        assertEquals(expected, writer.toString())
    }
}
