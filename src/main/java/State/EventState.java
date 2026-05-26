package State;

import Model.Event;

/**
 * Интерфейс для паттерна Состояние (State).
 * Определяет поведение мероприятия при подтверждении и отмене,
 * а также отвечает на вопросы: можно ли редактировать/отменять.
 */
public interface EventState {
    void confirm(Event event);
    void cancel(Event event);
    boolean canEdit();
    boolean canCancel();
    String getStatusName();

}
