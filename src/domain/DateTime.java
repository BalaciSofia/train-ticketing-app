package domain;

public class DateTime {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

     public DateTime(int year, int month, int day, int hour, int minute) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    
    public String toString() {
        return String.format("%04d-%02d-%02d %02d:%02d", year, month, day, hour, minute);
    }
    
}
