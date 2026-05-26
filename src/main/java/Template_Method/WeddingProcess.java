package Template_Method;

import Managment.ScheduleManager;
import Model.EventCategory;
import Model.StaffType;
import Model.EquipmentType;

import java.util.Map;

/**
 * Реализация шаблонного метода для "Свадьбы".
 * Требует 2 колонки, 1 освещение по умолчанию
 * и 3-х сотрудников (2 фотографа + 1 ведущий).
 */
public class WeddingProcess extends EventCreationProcess {

    public WeddingProcess(ConsoleInput input, EventRenderer renderer, ScheduleManager manager) {
        super(input, renderer, manager);
    }

    @Override
    protected Map<StaffType, Integer> getDefaultStaff() {
        return Map.of(
                StaffType.PHOTOGRAPHER, 2,
                StaffType.HOST, 1
        );
    }

    @Override
    protected Map<EquipmentType, Integer> getDefaultEquipment() {
        return Map.of(
                EquipmentType.SPEAKERS, 2,
                EquipmentType.LIGHTING, 1
        );
    }

    @Override
    protected String getDefaultEventTypeName() {
        return "Свадьба";
    }

    @Override
    protected EventCategory getEventCategory() {
        return EventCategory.WEDDING;
    }

}

