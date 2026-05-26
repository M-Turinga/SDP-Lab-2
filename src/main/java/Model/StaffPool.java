package Model;


import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Пул сотрудников. Аналог EquipmentPool.
 * Хранит общее и доступное количество по каждому типу персонала.
 * При создании всем типам выдаётся 10 единиц по умолчанию.
 */
public class StaffPool implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Map<StaffType, Integer> totalCount;
    private final Map<StaffType, Integer> availableCount;

    public StaffPool() {
        totalCount = new HashMap<>();
        availableCount = new HashMap<>();
        for (StaffType type : StaffType.values()) {
            totalCount.put(type, 10);
            availableCount.put(type, 10);
        }
    }

    public boolean isNotEnough(StaffType type, int requested) {
        return availableCount.getOrDefault(type, 0) < requested;
    }

    public void reserve(StaffType type, int count) {
        if (isNotEnough(type, count)) {
            throw new IllegalStateException("Недостаточно ресурсов");
        }
        availableCount.put(type, availableCount.get(type) - count);
    }

    public void release(StaffType type, int count) {
        availableCount.put(type, availableCount.get(type) + count);
    }


    public int getTotal(StaffType type) {
        return totalCount.getOrDefault(type, 0);
    }

    /**
     * Обновляет общее количество ресурсов.
     * Если новое общее количество МЕНЬШЕ текущего доступного,
     * то доступное количество урезается до нового общего.
     * Это предотвращает ситуацию, когда availableCount > totalCount.
     */
    public void updateTotal(StaffType type, int newTotal) {
        totalCount.put(type, newTotal);
        int oldAvailable = availableCount.getOrDefault(type, 0);
        if (oldAvailable > newTotal) {
            availableCount.put(type, newTotal);
        }
    }
}
