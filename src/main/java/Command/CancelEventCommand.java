package Command;

import Managment.ScheduleManager;
import Model.*;
import State.EventState;

import java.util.Map;

/**
 * Команда отмены мероприятия с освобождением ресурсов через callback.
 * При отмене сохраняет предыдущее состояние и удаляет событие из расписания.
 * Undo — восстанавливает мероприятие, но только если достаточно ресурсов.
 */
public class CancelEventCommand implements Command, ResourceReleaseCallback {

    private final Event event;
    private final StaffPool staffPool;
    private final EquipmentPool equipmentPool;
    private final ScheduleManager scheduleManager;

    private EventState previousState;

    public CancelEventCommand(Event event, StaffPool staffPool, EquipmentPool equipmentPool, ScheduleManager scheduleManager) {
        this.event = event;
        this.staffPool = staffPool;
        this.equipmentPool = equipmentPool;
        this.scheduleManager = scheduleManager;
    }

    @Override
    public void releaseStaff(StaffType type, int count) {
        staffPool.release(type, count);
    }

    @Override
    public void releaseEquipment(EquipmentType type, int count) {
        equipmentPool.release(type, count);
    }

    @Override
    public void execute() {
        previousState = event.getState();
        event.cancel(this);
        scheduleManager.remove(event);
        scheduleManager.saveToFiles();

        System.out.println("Мероприятие отменено: " + event.getDisplayName());
    }

    /**
     * Восстанавливает отменённое мероприятие.
     * Сначала проверяет, достаточно ли ресурсов для восстановления (через hasEnoughResources())
     * Если ресурсов недостаточно — отменяет undo и сообщает пользователю.
     * Только после проверки возвращает ресурсы через reserve() и восстанавливает состояние.
     */
    @Override
    public void undo() {

        if (!hasEnoughResources()) {
            System.err.println("Невозможно восстановить мероприятие: недостаточно ресурсов!");
            System.err.println("Увеличьте количество ресурсов через меню редактирования.");
            return;
        }

        for (Map.Entry<StaffType, Integer> entry : event.getStaffNeeded().entrySet()) {
            staffPool.reserve(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<EquipmentType, Integer> entry : event.getEquipmentNeeded().entrySet()) {
            equipmentPool.reserve(entry.getKey(), entry.getValue());
        }

        if (previousState != null) {
            event.setState(previousState);
        }

        scheduleManager.addBack(event);

        System.out.println("Отмена отмены мероприятия: " + event.getDisplayName());
    }

    private boolean hasEnoughResources() {
        for (var entry : event.getStaffNeeded().entrySet()) {
            if (staffPool.isNotEnough(entry.getKey(), entry.getValue())) {
                return false;
            }
        }
        for (var entry : event.getEquipmentNeeded().entrySet()) {
            if (equipmentPool.isNotEnough(entry.getKey(), entry.getValue())) {
                return false;
            }
        }
        return true;
    }
}
