package Model;

import java.io.Serial;
import java.io.Serializable;

/**
 * Value-объект места проведения. Иммутабельный.
 * Содержит название, адрес и базовую стоимость аренды в час.
 * Для места "На территории заказчика" стоимость = 0.
 */
public class EventPlace implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String address;
    private final int baseCostPerHour;

    public EventPlace(String name, String address, int baseCostPerHour) {
        this.name = name;
        this.address = address;
        this.baseCostPerHour = baseCostPerHour;
    }

    public String getName() {
        return name;
    }

    public String getAddress() { return address; }

    public int getBaseCostPerHour() {
        return baseCostPerHour;
    }
}
