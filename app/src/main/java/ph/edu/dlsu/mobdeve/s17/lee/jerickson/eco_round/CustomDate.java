package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import java.util.Calendar;

public class CustomDate {
    public static final String[] monthString = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public int day_in_month, month, year;

    // By default, creates a CustomDate for today
    public CustomDate() {
        Calendar c = Calendar.getInstance();

        this.year = c.get(Calendar.YEAR);
        this.day_in_month = c.get(Calendar.DAY_OF_MONTH);
        this.month = c.get(Calendar.MONTH);
    }

    // When supplied the day, month, and year, create the date accordingly.
    // Month here is expected to be 0-11
    public CustomDate(int year, int month, int day_in_month) {
        this.year = year;
        this.day_in_month = day_in_month;
        this.month = month;
    }

    // Example output if CustomDate has 2020, 10, 1: Oct 1, 2020
    public String toStringFull() {
        return monthString[month] + " " + this.day_in_month + ", " + this.year;
    }

    // Example output if CustomDate has 2020, 10, 1: Oct 1
    public String toStringNoYear() {
        return monthString[month] + " " + this.day_in_month;
    }
}