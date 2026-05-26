package State;

import Model.Event;

import java.io.Serial;
import java.io.Serializable;

/**
 * Состояние "Черновик" — начальное состояние при создании через Builder.
 * При confirm() переходит в ConfirmedState, при cancel() — в CancelledState.
 * Редактирование разрешено.
 */
public class DraftState implements EventState, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void confirm(Event event) {
        event.setState(new ConfirmedState());
        System.out.println("Мероприәтие подтверждено");
    }

    @Override
    public void cancel(Event event) {
        event.setState(new CancelledState());
        System.out.println("Мероприәтие отменено (из черновика)");
    }

    @Override
    public boolean canEdit() {
        return true;
    }

    @Override
    public boolean canCancel() { return true; }

    @Override
    public String getStatusName() {
        return "Черновик";
    }
}
