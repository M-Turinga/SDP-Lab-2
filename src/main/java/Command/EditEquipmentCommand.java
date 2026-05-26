package Command;

import Model.EquipmentType;
import Managment.ScheduleManager;

/**
 * Команда изменения общего количества оборудования в EquipmentPool.
 * Хранит старое и новое значение для корректного Undo.
 * Используется для редактирования ресурсов через консоль.
 */
public class EditEquipmentCommand implements Command {
    private final ScheduleManager manager;
    private final EquipmentType type;
    private final int oldTotal;
    private final int newTotal;

    public EditEquipmentCommand(ScheduleManager manager, EquipmentType type, int oldTotal, int newTotal) {
        this.manager = manager;
        this.type = type;
        this.oldTotal = oldTotal;
        this.newTotal = newTotal;
    }

    @Override
    public void execute() {
        manager.updateEquipmentTotalNoSave(type, newTotal);
        System.out.println("Количество " + type.getDisplayName() + " изменено: " + oldTotal + " -> " + newTotal);
    }

    @Override
    public void undo() {
        manager.updateEquipmentTotalNoSave(type, oldTotal);
        System.out.println("Отмена: восстановлено количество " + type.getDisplayName() + ": " + newTotal + " -> " + oldTotal);
    }
}