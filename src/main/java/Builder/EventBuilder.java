package Builder;

import Model.*;

/**
 * Интерфейс строителя мероприятий. Позволяет задавать категорию, дату, место,
 * необходимые ресурсы (сотрудники/оборудование) и получать готовый объект Event
 */
public interface EventBuilder {
    EventBuilder setCategory (EventCategory category);
    EventBuilder setDate(EventDate date, int durationHours);
    EventBuilder setPlace(EventPlace place);
    EventBuilder addStaff(StaffType staffType, int count);
    EventBuilder addEquipment(EquipmentType equipmentType, int count);
    Event build();
}
