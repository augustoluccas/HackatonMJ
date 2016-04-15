package br.org.projeto.vigilante.events;

public class DateEvent {

    private final int mDay;

    private final int mMonth;

    private final int mYear;

    public DateEvent(int day, int month, int year) {
        mDay = day;
        mMonth = month;
        mYear = year;
    }

    public int getDay() {
        return mDay;
    }

    public int getMonth() {
        return mMonth;
    }

    public int getYear() {
        return mYear;
    }
}
