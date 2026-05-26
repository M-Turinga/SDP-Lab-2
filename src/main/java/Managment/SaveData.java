/**
 * Обёртка для сериализации Event.
 * Нужна, чтобы при загрузке из файла можно было восстановить не только Event,
 * но и повторно навесить декораторы (сохраняется только исходное событие).
 */

package Managment;

import Model.Event;

import java.io.Serial;
import java.io.Serializable;

public class SaveData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Event event;


    public SaveData(Event event) {
        this.event = event;
    }

    public Event getEvent() { return event; }

}
