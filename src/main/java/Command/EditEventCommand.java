package Command;

import Managment.ScheduleManager;
import Model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Команда редактирования мероприятия.
 * Сохраняет полный снимок состояния (старые и новые значения):
 * - ресурсы (сотрудники, оборудование)
 * - дата, место, длительность
 * - декораторы
 * Освобождает старые ресурсы и резервирует новые.
 */
public class EditEventCommand implements Command {
    private final ScheduleManager manager;
    private final Event event;
    private final Map<StaffType, Integer> oldStaff;
    private final Map<EquipmentType, Integer> oldEquipment;
    private final Map<StaffType, Integer> newStaff;
    private final Map<EquipmentType, Integer> newEquipment;

    private final EventDate oldDate;
    private final EventPlace oldPlace;
    private final int oldDurationHours;
    private final List<String> oldDecorators;

    private final EventDate newDate;
    private final EventPlace newPlace;
    private final int newDurationHours;
    private final List<String> newDecorators;

    public EditEventCommand(ScheduleManager manager, Event event,
                            Map<StaffType, Integer> oldStaff,
                            Map<EquipmentType, Integer> oldEquipment,
                            Map<StaffType, Integer> newStaff,
                            Map<EquipmentType, Integer> newEquipment,
                            EventDate oldDate, EventPlace oldPlace, int oldDurationHours, List<String> oldDecorators,
                            EventDate newDate, EventPlace newPlace, int newDurationHours, List<String> newDecorators) {
        this.manager = manager;
        this.event = event;
        this.oldStaff = oldStaff;
        this.oldEquipment = oldEquipment;
        this.newStaff = newStaff;
        this.newEquipment = newEquipment;
        this.oldDate = oldDate;
        this.oldPlace = oldPlace;
        this.oldDurationHours = oldDurationHours;
        this.oldDecorators = oldDecorators;
        this.newDate = newDate;
        this.newPlace = newPlace;
        this.newDurationHours = newDurationHours;
        this.newDecorators = newDecorators;
    }

    /**
     * Сложное редактирование с полным сохранением старого состояния.
     * Сначала освобождает старые ресурсы, потом резервирует новые.
     * Такой порядок позволяет избежать двойного бронирования, если старые и новые ресурсы пересекаются.
     * В undo — обратный порядок.
     */
    @Override
    public void execute() {
        for (var entry : oldStaff.entrySet()) {
            manager.getStaffPool().release(entry.getKey(), entry.getValue());
        }
        for (var entry : oldEquipment.entrySet()) {
            manager.getEquipmentPool().release(entry.getKey(), entry.getValue());
        }

        for (var entry : newStaff.entrySet()) {
            manager.getStaffPool().reserve(entry.getKey(), entry.getValue());
        }
        for (var entry : newEquipment.entrySet()) {
            manager.getEquipmentPool().reserve(entry.getKey(), entry.getValue());
        }

        event.setDate(newDate);
        event.setPlace(newPlace);
        event.setDurationHours(newDurationHours);
        event.setStaffNeeded(new HashMap<>(newStaff));
        event.setEquipmentNeeded(new HashMap<>(newEquipment));
        event.setDecorators(new ArrayList<>(newDecorators));

        manager.saveToFiles();
        System.out.println("Изменения применены");
    }

    @Override
    public void undo() {
        for (var entry : newStaff.entrySet()) {
            manager.getStaffPool().release(entry.getKey(), entry.getValue());
        }
        for (var entry : newEquipment.entrySet()) {
            manager.getEquipmentPool().release(entry.getKey(), entry.getValue());
        }

        for (var entry : oldStaff.entrySet()) {
            manager.getStaffPool().reserve(entry.getKey(), entry.getValue());
        }
        for (var entry : oldEquipment.entrySet()) {
            manager.getEquipmentPool().reserve(entry.getKey(), entry.getValue());
        }

        event.setDate(oldDate);
        event.setPlace(oldPlace);
        event.setDurationHours(oldDurationHours);
        event.setStaffNeeded(new HashMap<>(oldStaff));
        event.setEquipmentNeeded(new HashMap<>(oldEquipment));
        event.setDecorators(new ArrayList<>(oldDecorators));

        manager.saveToFiles();
        System.out.println("Изменения отменены");
    }
}