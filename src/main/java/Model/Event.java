package Model;

import State.EventState;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Основная сущность "Мероприятие". Содержит все атрибуты:
 * категорию, дату, длительность, место, состояние (State),
 * необходимых сотрудников, оборудование и список декораторов.
 * Состояние управляет подтверждением и отменой (паттерн State).
 * Поддерживает сериализацию для сохранения в файлы.
 */
public class Event implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private EventCategory category;
    private EventDate date;
    private int durationHours;
    private EventPlace place;
    private EventState state;
    private Map<StaffType, Integer> staffNeeded;
    private Map<EquipmentType, Integer> equipmentNeeded;
    private List<String> decorators = new ArrayList<>();
    private String customName;

    public Event() {}

    public Event(EventCategory category,
                 EventDate date,
                 int durationHours,
                 EventPlace place,
                 EventState state,
                 Map<StaffType, Integer> staffNeeded,
                 Map<EquipmentType, Integer> equipmentNeeded) {
        this.category = category;
        this.date = date;
        this.durationHours = durationHours;
        this.place = place;
        this.state = state;
        this.staffNeeded = staffNeeded;
        this.equipmentNeeded = equipmentNeeded;
    }

    public EventCategory getCategory() {
        return category;
    }

    public void setCategory(EventCategory category) {
        this.category = category;
    }

    public EventDate getDate() {
        return date;
    }

    public void setDate(EventDate date) {
        this.date = date;
    }

    public int getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(int durationHours) {
        this.durationHours = durationHours;
    }

    public EventPlace getPlace() {
        return place;
    }

    public void setPlace(EventPlace place) {
        this.place = place;
    }

    public void setState(EventState newState) {
        this.state = newState;
    }

    public EventState getState() {
        return state;
    }

    public Map<StaffType, Integer> getStaffNeeded() {
        return staffNeeded;
    }

    public void setStaffNeeded(Map<StaffType, Integer> staffNeeded) {
        this.staffNeeded = staffNeeded;
    }

    public Map<EquipmentType, Integer> getEquipmentNeeded() {
        return equipmentNeeded;
    }

    public void setEquipmentNeeded(Map<EquipmentType, Integer> equipmentNeeded) {
        this.equipmentNeeded = equipmentNeeded;
    }

    public void setDecorators(List<String> decorators) {
        this.decorators = new ArrayList<>(decorators);
    }

    public List<String> getDecorators() {
        return new ArrayList<>(decorators);
    }

    public void addDecorator(String decoratorName) {
        if (!decorators.contains(decoratorName)) {
            decorators.add(decoratorName);
        }
    }

    public String getDisplayName() {
        if (category != null) {
            return category.getDisplayName();
        }
        if (customName != null && !customName.isEmpty()) {
            return customName;
        }
        return "Свой тип";
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public void confirm() { state.confirm(this);}

    /**
     * Отменяет мероприятие через паттерн State.
     * Перед сменой состояния на CancelledState освобождает все занятые ресурсы
     * через переданный callback.
     * Колбэк нужен, чтобы Event не знал о существовании StaffPool/EquipmentPool напрямую
     */
    public void cancel(ResourceReleaseCallback callback) {
        for (var entry : staffNeeded.entrySet()) {
            callback.releaseStaff(entry.getKey(), entry.getValue());
        }
        for (var entry : equipmentNeeded.entrySet()) {
            callback.releaseEquipment(entry.getKey(), entry.getValue());
        }
        state.cancel(this);
    }

    public boolean canEdit() {return state.canEdit();}

}
