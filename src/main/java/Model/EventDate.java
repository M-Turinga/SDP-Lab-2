package Model;

import java.io.Serial;
import java.io.Serializable;

/**
 * Value-объект для даты мероприятия. Иммутабельный.
 * Содержит логику сравнения с сегодняшней датой (isInFuture, isInPast).
 * Переопределяет equals() для корректного поиска по дате.
 */
public class EventDate implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int day;
    private final int month;
    private final int year;

    public EventDate(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public static EventDate todayDate() {
        java.time.LocalDate today = java.time.LocalDate.now();
        return new EventDate(today.getDayOfMonth(), today.getMonthValue(), today.getYear());
    }

    public boolean isInFuture() {
        EventDate today = todayDate();
        if (this.year != today.year) return this.year > today.year;
        if (this.month != today.month) return this.month > today.month;
        return this.day > today.day;
    }

    public boolean isInPast() {
        EventDate today = todayDate();
        if (this.year != today.year) return this.year < today.year;
        if (this.month != today.month) return this.month < today.month;
        return this.day < today.day;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EventDate that = (EventDate) obj;
        return day == that.day && month == that.month && year == that.year;
    }

    @Override
    public String toString() {
        return day + "." + month + "." + year;
    }
}
