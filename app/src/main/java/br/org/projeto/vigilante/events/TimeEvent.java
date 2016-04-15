package br.org.projeto.vigilante.events;

public class TimeEvent {

    private final int mHour;

    private final int mMinute;

    public TimeEvent(int hour, int minute) {
        mHour = hour;
        mMinute = minute;
    }

    public int getHour() {
        return mHour;
    }

    public int getMinute() {
        return mMinute;
    }

}
