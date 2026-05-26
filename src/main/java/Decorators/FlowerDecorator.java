package Decorators;

/**
 * Декоратор "Цветочные инсталляции"
 * Добавляет фиксированную стоимость 10000 руб. Аналогичен CateringDecorator
 */
public class FlowerDecorator extends EventDecorator {
    public static final String NAME = "FLOWERS";
    private static final int FLOWER_COST = 10000;

    public FlowerDecorator(EventComponent wrapped) {
        super(wrapped);
        wrapped.getEvent().addDecorator(NAME);
    }

    @Override
    public int getCost() {
        return wrapped.getCost() + FLOWER_COST;
    }
}
