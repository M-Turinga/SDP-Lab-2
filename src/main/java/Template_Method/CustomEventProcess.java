package Template_Method;

import Managment.ScheduleManager;
import Model.EventCategory;
import Model.StaffType;
import Model.EquipmentType;

import java.util.HashMap;
import java.util.Map;

/**
 * Реализация шаблонного метода для "Своего типа" мероприятия.
 * Не имеет категории (category = null) и дефолтных ресурсов.
 * Название задаётся пользователем через setCustomEventName().
 */
public class CustomEventProcess extends EventCreationProcess {

    public CustomEventProcess(ConsoleInput input, EventRenderer renderer, ScheduleManager manager) {
        super(input, renderer, manager);
    }

    @Override
    protected Map<StaffType, Integer> getDefaultStaff() {
        return new HashMap<>();
    }

    @Override
    protected Map<EquipmentType, Integer> getDefaultEquipment() {
        return new HashMap<>();
    }

    @Override
    protected String getDefaultEventTypeName() {
        return customEventName != null ? customEventName : "Свой тип";
    }

    @Override
    protected EventCategory getEventCategory() {
        return null;
    }

    @Override
    protected boolean isCustomType() {
        return true;
    }
}
