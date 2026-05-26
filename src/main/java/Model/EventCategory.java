package Model;

/**
 * Перечисление предопределённых категорий мероприятий.
 */
public enum EventCategory {
    WEDDING("Свадьба"),
    BIRTHDAY("День рождения"),
    NEW_YEAR_PARTY("Новогодний корпоратив"),
    MALL_OPENING("Открытие ТЦ");

    private final String displayName;

    EventCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
