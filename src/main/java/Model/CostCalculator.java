package Model;

/**
 * Класс для расчёта базовой стоимости мероприятия.
 * Формула: (стоимость места + сумма по сотрудникам + сумма по оборудованию) * коэффициент категории.
 */
public class CostCalculator {

    /**
     * Расчёт полной стоимости с учётом коэффициента категории
     * Базовая стоимость = (аренда места + сумма(сотрудники) + сумма(оборудование)) * длительность.
     * Итоговая = базовая * коэффициент категории.
     */
    public static int calculate(Event event) {

        if (event.getCategory() == null) {
            return calculateBase(event);
        }

        int baseCost = calculateBase(event);
        double coefficient = getCoefficient(event.getCategory());
        return (int)(baseCost * coefficient);
    }

    private static int calculateBase(Event event) {
        int placeCost = event.getPlace().getBaseCostPerHour() * event.getDurationHours();

        int staffCost = 0;
        for (StaffType type : event.getStaffNeeded().keySet()) {
            staffCost += type.getBaseCostPerHour() * event.getStaffNeeded().get(type) * event.getDurationHours();
        }

        int equipmentCost = 0;
        for (EquipmentType type : event.getEquipmentNeeded().keySet()) {
            equipmentCost += type.getBaseCostPerHour() * event.getEquipmentNeeded().get(type) * event.getDurationHours();
        }

        return placeCost + staffCost + equipmentCost;
    }

    private static double getCoefficient(EventCategory category) {
        return switch (category) {
            case WEDDING -> 1.4;
            case NEW_YEAR_PARTY -> 1.5;
            case BIRTHDAY -> 1.3;
            case MALL_OPENING -> 1.2;
        };
    }
}