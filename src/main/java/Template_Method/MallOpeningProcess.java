package Template_Method;

import Managment.ScheduleManager;
import Model.EventCategory;
import Model.StaffType;
import Model.EquipmentType;

import java.util.Map;

/**
 * Реализация шаблонного метода для "Открытия ТЦ".
 * Требует 3 колонки, 3 освещения, 2 микрофона, проектор по умолчанию
 * и двух сотрудников (ведущий + звукооператор).
 */
public class MallOpeningProcess extends EventCreationProcess {

    public MallOpeningProcess(ConsoleInput input, EventRenderer renderer, ScheduleManager manager) {
        super(input, renderer, manager);
    }

    @Override
    protected Map<StaffType, Integer> getDefaultStaff() {
        return Map.of(
                StaffType.HOST, 1,
                StaffType.SOUND_ENGINEER, 1
        );
    }

    @Override
    protected Map<EquipmentType, Integer> getDefaultEquipment() {
        return Map.of(
                EquipmentType.SPEAKERS, 3,
                EquipmentType.LIGHTING, 3,
                EquipmentType.MICROPHONE, 2,
                EquipmentType.PROJECTOR, 1
        );
    }

    @Override
    protected String getDefaultEventTypeName() {
        return "Открытие ТЦ";
    }

    @Override
    protected EventCategory getEventCategory() {
        return EventCategory.MALL_OPENING;
    }

}
