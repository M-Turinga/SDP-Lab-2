package Template_Method;

import Managment.ScheduleManager;
import Model.EventCategory;
import Model.StaffType;
import Model.EquipmentType;

import java.util.Map;

/**
 * Реализация шаблонного метода для "Новогоднего корпоратива".
 * Требует 2 колонки, 2 освещения, 2 микрофона по умолчанию
 * и двух сотрудников (ведущий + звукооператор).
 */
public class NewYearPartyProcess extends EventCreationProcess {

    public NewYearPartyProcess(ConsoleInput input, EventRenderer renderer, ScheduleManager manager) {
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
                EquipmentType.SPEAKERS, 2,
                EquipmentType.LIGHTING, 2,
                EquipmentType.MICROPHONE, 2
        );
    }

    @Override
    protected String getDefaultEventTypeName() {
        return "Новогодний корпоратив";
    }

    @Override
    protected EventCategory getEventCategory() {
        return EventCategory.NEW_YEAR_PARTY;
    }

}
