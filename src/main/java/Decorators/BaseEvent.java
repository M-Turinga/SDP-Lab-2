package Decorators;

import Model.CostCalculator;
import Model.Event;
import Model.EventDate;

/**
 * Базовый компонент для паттерна Декоратор.
 * Оборачивает мероприятие Event и предоставляет интерфейс EventComponent.
 * Реальная стоимость вычисляется через CostCalculator.
 */
public class BaseEvent implements EventComponent {
    private final Event event;

    public BaseEvent(Event event) {
        this.event = event;
    }

    public int getCost() {
        return CostCalculator.calculate(event);
    }

    @Override
    public EventDate getDate() {
        return event.getDate();
    }

    @Override
    public int getDuration() {
        return event.getDurationHours();
    }

    @Override
    public Event getEvent() {
        return event;
    }

}
