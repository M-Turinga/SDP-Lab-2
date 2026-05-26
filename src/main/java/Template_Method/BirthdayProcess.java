package Template_Method;

import Managment.ScheduleManager;
import Model.EventCategory;
import Model.StaffType;
import Model.EquipmentType;

import java.util.Map;

/**
 * Конкретная реализация шаблонного метода для "Дня рождения".
 * Определяет:
 * - стандартных сотрудников (ведущий)
 * - стандартное оборудование (колонки, микрофон)
 * - категорию BIRTHDAY
 */
public class BirthdayProcess extends EventCreationProcess {

    public BirthdayProcess(ConsoleInput input, EventRenderer renderer, ScheduleManager manager) {
        super(input, renderer, manager);
    }

    @Override
    protected Map<StaffType, Integer> getDefaultStaff() {
        return Map.of(StaffType.HOST, 1);
    }

    @Override
    protected Map<EquipmentType, Integer> getDefaultEquipment() {
        return Map.of(
                EquipmentType.SPEAKERS, 1,
                EquipmentType.MICROPHONE, 1
        );
    }

    @Override
    protected String getDefaultEventTypeName() {
        return "День рождения";
    }

    @Override
    protected EventCategory getEventCategory() {
        return EventCategory.BIRTHDAY;
    }

}
