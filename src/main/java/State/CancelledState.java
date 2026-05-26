package State;

import Model.Event;

import java.io.Serial;
import java.io.Serializable;

/**
 * Состояние "Отменено". Не позволяет редактировать мероприятие.
 * При повторном вызове cancel() выводит сообщение, что уже отменено.
 * Невозможно подтвердить (confirm) — бросает исключение.
 */
public class CancelledState implements EventState, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void confirm(Event event) {
        throw new IllegalStateException("Отмененное мероприятие нельзя подтвердить");
    }

    @Override
    public void cancel(Event event) {
        System.out.println("Мероприятие уже отменено");
    }

    @Override
    public boolean canEdit() {
        return false;
    }

    @Override
    public boolean canCancel() { return false; }

    @Override
    public String getStatusName() {
        return "Отменено";
    }
}
