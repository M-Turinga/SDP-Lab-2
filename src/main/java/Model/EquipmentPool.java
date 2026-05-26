package Model;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Пул оборудования (паттерн Resource Pool).
 * Хранит общее количество (totalCount) и доступное для резервирования (availableCount).
 * Методы: reserve() — забронировать, release() — освободить.
 * При создании всем типам оборудования выдаётся 10 единиц по умолчанию.
 */
public class EquipmentPool implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Map<EquipmentType, Integer> totalCount;
    private final Map<EquipmentType, Integer> availableCount;

    public EquipmentPool() {
        totalCount = new HashMap<>();
        availableCount = new HashMap<>();
        for (EquipmentType type : EquipmentType.values()) {
            totalCount.put(type, 10);
            availableCount.put(type, 10);
        }
    }

    public boolean isNotEnough(EquipmentType type, int requested) {
        return availableCount.getOrDefault(type, 0) < requested;
    }

    public void reserve(EquipmentType type, int count) {
        if (isNotEnough(type, count)) {
            throw new IllegalStateException("Недостаточно ресурсов");
        }
        availableCount.put(type, availableCount.get(type) - count);
    }

    public void release(EquipmentType type, int count) {
        availableCount.put(type, availableCount.get(type) + count);
    }

    public int getTotal(EquipmentType type) {
        return totalCount.getOrDefault(type, 0);
    }

    /**
     * Обновляет общее количество ресурсов.
     * Если новое общее количество МЕНЬШЕ текущего доступного,
     * то доступное количество урезается до нового общего.
     * Это предотвращает ситуацию, когда availableCount > totalCount.
     */
    public void updateTotal(EquipmentType type, int newTotal) {
        totalCount.put(type, newTotal);
        int oldAvailable = availableCount.getOrDefault(type, 0);
        if (oldAvailable > newTotal) {
            availableCount.put(type, newTotal);
        }
    }

}
