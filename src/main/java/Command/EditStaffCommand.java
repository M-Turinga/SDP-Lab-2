package Command;

import Model.StaffType;
import Managment.ScheduleManager;

/**
 * Команда изменения общего количества сотрудников в StaffPool.
 * Аналог EditEquipmentCommand для персонала.
 * При изменении проверяет, не превышен ли максимум, требуемый будущими мероприятиями.
 */
public class EditStaffCommand implements Command {
    private final ScheduleManager manager;
    private final StaffType type;
    private final int oldTotal;
    private final int newTotal;

    public EditStaffCommand(ScheduleManager manager, StaffType type, int oldTotal, int newTotal) {
        this.manager = manager;
        this.type = type;
        this.oldTotal = oldTotal;
        this.newTotal = newTotal;
    }

    @Override
    public void execute() {
        manager.updateStaffTotalNoSave(type, newTotal);
        System.out.println("Количество " + type.getDisplayName() + " изменено: " + oldTotal + " -> " + newTotal);
    }

    @Override
    public void undo() {
        manager.updateStaffTotalNoSave(type, oldTotal);
        System.out.println("Отмена: восстановлено количество " + type.getDisplayName() + ": " + newTotal + " -> " + oldTotal);
    }
}
