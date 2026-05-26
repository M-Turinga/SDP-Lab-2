package State;

import Model.Event;

import java.io.Serial;
import java.io.Serializable;

/**
 * Состояние "Подтверждено". Позволяет редактировать мероприятие.
 * При вызове cancel() переходит в CancelledState.
 */
public class ConfirmedState implements EventState, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void confirm(Event event) {
        throw new IllegalStateException("Мероприятие уже подтверждено");
    }

    @Override
    public void cancel(Event event) {
        event.setState(new CancelledState());
    }

    @Override
    public boolean canEdit() {
        return true;
    }

    @Override
    public boolean canCancel() {  return true; }

    @Override
    public String getStatusName() {
        return "Подтверждено";
    }
}
