package test.support

import com.ideaflow.model.Event
import com.ideaflow.model.Interval
import com.ideaflow.model.EventType


class FixtureSupport {

    static final String FILE = "file"
    static final String OTHER_FILE = "otherFile"
    static final String FILE1 = "file1"
    static final String FILE2 = "file2"
    static final String FILE3 = "file3"
    static final String FILE4 = "file4"


    static final long SHORT_DELAY = 5;
    static final long LONG_DELAY = 999999;

    static final long NOW
    static final long TIME1
    static final long TIME2
    static final long TIME3
    static final long TIME4


    static {
        def cal = Calendar.getInstance()
        cal.set(Calendar.MILLISECOND, 0)
        NOW = cal.getTime().time

        TIME1 = NOW + LONG_DELAY
        TIME2 = TIME1 + LONG_DELAY
        TIME3 = TIME2 + LONG_DELAY
        TIME4 = TIME3 + LONG_DELAY

    }

    private Event createEvent(String comment, long time) {
        new Event(EventType.note, comment, new Date(time))
    }

    private Event createEvent(EventType type, long time) {
        new Event(type, 'test', new Date(time))
    }

    private Interval createInterval(String name, long time) {
        new Interval(new Date(time), name, 5)
    }

    private Interval createInterval(String name, int duration, long time) {
        new Interval(new Date(time), name, duration)
    }

}
