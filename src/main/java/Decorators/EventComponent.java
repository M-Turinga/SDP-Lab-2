package Decorators;

import Model.Event;
import Model.EventDate;

/**
 * Общий интерфейс для всех компонентов мероприятия (базовых и декорированных).
 * Позволяет единообразно получать стоимость, дату, длительность и исходный Event.
 */
public interface EventComponent {

    int getCost();
    EventDate getDate();
    int getDuration();
    Event getEvent();
}
