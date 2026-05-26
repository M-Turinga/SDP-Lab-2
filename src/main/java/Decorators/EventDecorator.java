package Decorators;

import Model.Event;
import Model.EventDate;

/**
 * Абстрактный декоратор для EventComponent.
 * Делегирует все вызовы обёрнутому объекту, оставляя потомкам возможность
 переопределить только getCost() (и, при необходимости, другие методы).
 */
public abstract class EventDecorator implements EventComponent {
    public EventComponent wrapped;

    public EventDecorator(EventComponent wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public int getCost() {
        return wrapped.getCost();
    }

    @Override
    public EventDate getDate() {
        return wrapped.getDate();
    }

    @Override
    public int getDuration() {
        return wrapped.getDuration();
    }

    @Override
    public Event getEvent() {
        return wrapped.getEvent();
    }
}
