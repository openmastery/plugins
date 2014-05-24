package com.newiron.ideaflow.data

import org.junit.Test

class TestRelativeTime {

    int TWENTY_SEC = 20
    int THREE_HOURS = 3*60*60
    int TWO_MINUTES = 2*60

    @Test
    void testFormat_ShouldDivideIntoHoursMinSeconds() {
        TimePositionOld time = new TimePositionOld(THREE_HOURS + TWO_MINUTES + TWENTY_SEC)
        assert time.getLongTime() == "3:02:20"
    }

    @Test
    void testFormat_ShouldLeftPadMinutes() {
        TimePositionOld time = new TimePositionOld(TWENTY_SEC)
        assert time.getLongTime() == "0:00:20"
    }

    @Test
    void testFormat_ShouldLeftPadSeconds() {
        TimePositionOld time = new TimePositionOld(TWO_MINUTES)
        assert time.getLongTime() == "0:02:00"
    }


}
