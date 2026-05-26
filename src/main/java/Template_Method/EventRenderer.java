package Template_Method;

import Model.*;

import java.util.List;
import java.util.Map;

/**
 * Класс для отображения информации о мероприятии в консоли.
 * Отвечает за форматированный вывод текущего состояния,
 * меню редактирования, списков сотрудников/оборудования/декораторов.
 * Отделяет логику отображения от логики ввода (ConsoleInput).
 */
public class EventRenderer {
    /**
     * Детальный вывод состояния мероприятия с расчётом промежуточных стоимостей.
     * Для каждого типа сотрудника/оборудования показывает:
     * - количество
     * - стоимость за час (количество * базовая ставка)
     * - итого за всё мероприятие (стоимость_за_час * длительность)
     * Это помогает пользователю понять из чего складывается общая цена.
     */
    public void showCurrentState(EventDate date, EventPlace place, int durationHours,
                                                         String eventTypeName,
                                                         Map<StaffType, Integer> staff,
                                                         Map<EquipmentType, Integer> equipment,
                                                         List<String> decorators,
                                                         int totalCost) {
    System.out.println("\n=== ТЕКУЩЕЕ СОСТОЯНИЕ ===");
    System.out.println("Дата: " + date);
    System.out.println("Место: " + place.getName() + " (" + place.getBaseCostPerHour() + " руб/час)");
    System.out.println("Адрес: " + place.getAddress());
    System.out.println("Длительность: " + durationHours + " часов");
    System.out.println("Тип: " + eventTypeName);

    System.out.println("\n--- СОТРУДНИКИ ---");
    if (staff.isEmpty()) {
        System.out.println("  (не выбрано)");
    } else {
        for (var entry : staff.entrySet()) {
            int cost = entry.getKey().getBaseCostPerHour() * entry.getValue() * durationHours;
            System.out.printf("  %s: %d шт. (стоимость: %d руб/час, итого: %d руб)%n",
                    entry.getKey().getDisplayName(), entry.getValue(),
                    entry.getKey().getBaseCostPerHour() * entry.getValue(), cost);
        }
    }

    System.out.println("\n--- ОБОРУДОВАНИЕ ---");
    if (equipment.isEmpty()) {
        System.out.println("  (не выбрано)");
    } else {
        for (var entry : equipment.entrySet()) {
            int cost = entry.getKey().getBaseCostPerHour() * entry.getValue() * durationHours;
            System.out.printf("  %s: %d шт. (стоимость: %d руб/час, итого: %d руб)%n",
                    entry.getKey().getDisplayName(), entry.getValue(),
                    entry.getKey().getBaseCostPerHour() * entry.getValue(), cost);
        }
    }

    System.out.println("\n--- ДОПОЛНИТЕЛЬНЫЕ ОПЦИИ ---");
    if (decorators.isEmpty()) {
        System.out.println("  (нет)");
    } else {
        for (String d : decorators) {
            System.out.println("  + " + d);
        }
    }

    System.out.println("\nОБЩАЯ СТОИМОСТЬ: " + totalCost + " руб.");
}

    public void showStaffEditMenu(StaffType[] types,
                                  Map<StaffType, Integer> current,
                                  Map<StaffType, Integer> available) {
        System.out.println("\n--- РЕДАКТИРОВАНИЕ СОТРУДНИКОВ ---");
        for (int i = 0; i < types.length; i++) {
            int cur = current.getOrDefault(types[i], 0);
            int avail = available.get(types[i]);
            System.out.printf("%d. %s: текущее %d (доступно всего: %d)%n",
                    i + 1, types[i].getDisplayName(), cur, avail);
        }
        System.out.println("0. Выход");
    }

    public void showEquipmentEditMenu(EquipmentType[] types,
                                      Map<EquipmentType, Integer> current,
                                      Map<EquipmentType, Integer> available) {
        System.out.println("\n--- РЕДАКТИРОВАНИЕ ОБОРУДОВАНИЯ ---");
        for (int i = 0; i < types.length; i++) {
            int cur = current.getOrDefault(types[i], 0);
            int avail = available.get(types[i]);
            System.out.printf("%d. %s: текущее %d (доступно всего: %d)%n",
                    i + 1, types[i].getDisplayName(), cur, avail);
        }
        System.out.println("0. Выход");
    }

    public void showDecoratorMenu(boolean cateringSelected,
                                  boolean fireworksSelected,
                                  boolean flowersSelected) {
        System.out.println("\n--- ДОПОЛНИТЕЛЬНЫЕ ОПЦИИ ---");
        System.out.println("1. Кейтеринг (+15000 руб) " + (cateringSelected ? "[ВЫБРАНО]" : ""));
        System.out.println("2. Фейерверк (+10000 руб) " + (fireworksSelected ? "[ВЫБРАНО]" : ""));
        System.out.println("3. Цветочные инсталляции (+10000 руб) " + (flowersSelected ? "[ВЫБРАНО]" : ""));
        System.out.println("0. Выход");
    }

    public void showEditMenu() {
        System.out.println("\n--- МЕНЮ РЕДАКТИРОВАНИЯ ---");
        System.out.println("1. Редактировать сотрудников");
        System.out.println("2. Редактировать оборудование");
        System.out.println("3. Добавить дополнительные опции");
        System.out.println("4. Показать текущее состояние");
        System.out.println("5. Подтвердить мероприятие");
        System.out.println("6. Отмена (вернуться в главное меню)");
        System.out.print("Ваш выбор: ");
    }
}
