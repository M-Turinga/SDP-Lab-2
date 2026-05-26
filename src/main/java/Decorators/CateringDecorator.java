package Decorators;

/**
 * Декоратор "Кейтеринг". Добавляет фиксированную стоимость 15000 руб.
 * При создании автоматически добавляет себя в список decorators мероприятия.
 */
public class CateringDecorator extends EventDecorator {
    public static final String NAME = "CATERING";
    private static final int CATERING_COST = 15000;

    public CateringDecorator(EventComponent wrapped) {
        super(wrapped);
        wrapped.getEvent().addDecorator(NAME);
    }

    @Override
    public int getCost() {
        return wrapped.getCost() + CATERING_COST;
    }
}
