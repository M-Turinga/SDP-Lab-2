package Command;

import Decorators.BaseEvent;
import Managment.ScheduleManager;
import Model.*;

import java.util.Map;

/**
 * Команда создания мероприятия: резервирует ресурсы в пулах,
 * добавляет событие в расписание и сохраняет данные в файлы.
 * Undo — удаляет событие и возвращает ресурсы обратно в пулы.
 */
public class CreateEventCommand implements  Command {

    private final Event event;
    private final ScheduleManager scheduleManager;
    private final StaffPool staffPool;
    private final EquipmentPool equipmentPool;

    public CreateEventCommand(Event event, ScheduleManager scheduleManager,
                              StaffPool staffPool, EquipmentPool equipmentPool) {
        this.event = event;
        this.scheduleManager = scheduleManager;
        this.staffPool = staffPool;
        this.equipmentPool = equipmentPool;
    }

    /**
     * Создаёт мероприятие: резервирует ресурсы в пулах, добавляет в расписание,
     * сохраняет в файл. Резервирование ДО добавления в расписание,
     * чтобы нельзя было создать событие на которое не хватает ресурсов.
     */
    @Override
    public void execute() {
        for (Map.Entry<StaffType, Integer> entry : event.getStaffNeeded().entrySet()) {
            staffPool.reserve(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<EquipmentType, Integer> entry : event.getEquipmentNeeded().entrySet()) {
            equipmentPool.reserve(entry.getKey(), entry.getValue());
        }

        BaseEvent baseEvent = new BaseEvent(event);
        scheduleManager.addDirect(baseEvent);

        scheduleManager.saveToFiles();

        System.out.println("Мероприятие создано: " + event.getDisplayName());
    }

    @Override
    public void undo() {
        scheduleManager.remove(event);

        for (Map.Entry<StaffType, Integer> entry : event.getStaffNeeded().entrySet()) {
            staffPool.release(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<EquipmentType, Integer> entry : event.getEquipmentNeeded().entrySet()) {
            equipmentPool.release(entry.getKey(), entry.getValue());
        }

        System.out.println("Создание мероприятия отменено: " + event.getDisplayName());
    }

}
