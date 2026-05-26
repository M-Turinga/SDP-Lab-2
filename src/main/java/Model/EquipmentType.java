package Model;

/**
 * Перечисление типов оборудования с базовой стоимостью за час.
 * Используется для расчёта стоимости и отображения в UI.
 */
public enum EquipmentType {
    PROJECTOR("Проектор", 2000),
    SPEAKERS("Колонки", 3000),
    MICROPHONE("Микрофон", 500),
    LIGHTING("освещение", 2500);

    private final String displayName;
    private final int baseCostPerHour;

    EquipmentType(String displayName, int baseCostPerHour) {
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
