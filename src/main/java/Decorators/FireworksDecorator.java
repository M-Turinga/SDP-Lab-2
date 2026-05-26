package Decorators;

/**
 * Декоратор "Фейерверк"
 * Добавляет фиксированную стоимость 10000 руб. Аналогичен CateringDecorator
 */
public class FireworksDecorator extends EventDecorator {
    public static final String NAME = "FIREWORKS";
    private static final int FIREWORK_COST = 10000;

    public FireworksDecorator (EventComponent wrapped) {
        super(wrapped);
        wrapped.getEvent().addDecorator(NAME);
    }

    @Override
    public int getCost() {
        return wrapped.getCost() + FIREWORK_COST;
    }
}
