package com.newiron.ideaflow.data

import org.junit.Test

class TestRelativeTime {

    int TWENTY_SEC = 20
    int THREE_HOURS = 3*60*60
    int TWO_MINUTES = 2*60

    @Test
    void testFormat_ShouldDivideIntoHoursMinSeconds() {
        RelativeTime time = new RelativeTime(THREE_HOURS + TWO_MINUTES + TWENTY_SEC)
        assert time.getTime() == "3:02:20"
    }

    @Test
    void testFormat_ShouldLeftPadMinutes() {
        RelativeTime time = new RelativeTime(TWENTY_SEC)
        assert time.getTime() == "0:00:20"
    }

    @Test
    void testFormat_ShouldLeftPadSeconds() {
        RelativeTime time = new RelativeTime(TWO_MINUTES)
        assert time.getTime() == "0:02:00"
    }


}
