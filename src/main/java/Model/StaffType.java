package Model;

/**
 * Перечисление типов сотрудников с базовой стоимостью за час.
 * Используется для расчёта стоимости и отображения в UI.
 */
public enum StaffType {
    PHOTOGRAPHER("Фотограф", 5000),
    HOST("Ведущий", 7000),
    SOUND_ENGINEER("Звукооператор", 6000),
    DECORATOR("Декоратор", 4000);

    private final String displayName;
    private final int baseCostPerHour;

    StaffType(String displayName, int baseCostPerHour) {
        this.displayName = displayName;
        this.baseCostPerHour = baseCostPerHour;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getBaseCostPerHour() {
        return baseCostPerHour;
    }
}
