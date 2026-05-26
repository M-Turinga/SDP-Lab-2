package Model;

/**
 * Интерфейс обратного вызова для освобождения ресурсов при отмене мероприятия.
 * Реализуется CancelEventCommand, чтобы event мог вызвать releaseStaff/releaseEquipment
 * без жёсткой привязки к конкретному классу.
 */
public interface ResourceReleaseCallback {
    void releaseStaff(StaffType type, int count);
    void releaseEquipment(EquipmentType type, int count);
}
