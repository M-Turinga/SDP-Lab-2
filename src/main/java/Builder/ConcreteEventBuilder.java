package Builder;

import Model.*;
import State.DraftState;
import java.util.HashMap;

/**
 * Реализация паттерна Строитель (Builder) для поэтапного создания мероприятия.
 * Инициализирует новое событие в состоянии черновика (DraftState).
 * В методе build() выполняет валидацию обязательных полей перед созданием.
 */
public class ConcreteEventBuilder implements EventBuilder {
    private final Event event;

    public ConcreteEventBuilder() {
        this.event = new Event();
        this.event.setState(new DraftState());
        this.event.setStaffNeeded(new HashMap<>());
        this.event.setEquipmentNeeded(new HashMap<>());
    }

    @Override
    public EventBuilder setCategory(EventCategory category) {
        event.setCategory(category);
        return this;
    }

    @Override
    public EventBuilder setDate(EventDate date, int durationHours) {
        event.setDate(date);
        event.setDurationHours(durationHours);
        return this;
    }

    @Override
    public EventBuilder setPlace(EventPlace place) {
        event.setPlace(place);
        return this;
    }

    @Override
    public EventBuilder addStaff(StaffType staffType, int count) {
        event.getStaffNeeded().put(staffType, count);
        return this;
    }

    @Override
    public EventBuilder addEquipment(EquipmentType equipmentType, int count) {

        event.getEquipmentNeeded().put(equipmentType, count);
        return this;
    }

    @Override
    public Event build() {
        if (event.getDate() == null) throw new IllegalStateException("Дата не указана");
        if (event.getDurationHours() == 0) throw new IllegalStateException("Продолжительность не указана");
        if (event.getPlace() == null) throw new IllegalStateException("Место не указано");
        if (event.getEquipmentNeeded() == null) throw new IllegalStateException("Оборудование не указано");
        if (event.getStaffNeeded() == null) throw new IllegalStateException("Сотрудники не указаны");
        return event;
    }
}
