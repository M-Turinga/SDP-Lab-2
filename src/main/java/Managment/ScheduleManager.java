package Managment;

import Command.CommandHistory;
import Decorators.*;
import Model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Центральный менеджер системы. Хранит:
 * - список мероприятий (schedule) в памяти
 * - пулы ресурсов (StaffPool, EquipmentPool)
 * - историю команд (CommandHistory)
 * Отвечает за поиск по дате, проверку занятости, сохранение/загрузку в файлы.
 * При изменении общего количества ресурсов проверяет, достаточно ли их для будущих мероприятий.
 */
public class ScheduleManager implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final List<EventComponent> schedule;
    private StaffPool staffPool;
    private EquipmentPool equipmentPool;
    private final CommandHistory history;

    /**
     * Конструктор с внедрением зависимостей.
     * Получает пулы ресурсов и историю команд из Spring контекста.
     */
    public ScheduleManager(StaffPool staffPool, EquipmentPool equipmentPool, CommandHistory history) {
        this.schedule = new ArrayList<>();
        this.staffPool = staffPool;
        this.equipmentPool = equipmentPool;
        this.history = history;

        loadFromFiles();
    }

    public StaffPool getStaffPool() {
        return staffPool;
    }

    public EquipmentPool getEquipmentPool() {
        return equipmentPool;
    }

    public CommandHistory getHistory() {
        return history;
    }

    public List<EventComponent> getSchedule() {
        return new ArrayList<>(schedule);
    }

    public EventComponent findByDate(EventDate date) {
        for (EventComponent eventComponent : schedule) {
            if (eventComponent.getDate().equals(date)) {
                return eventComponent;
            }
        }
        return null;
    }

    public boolean isDateOccupied(EventDate date) {
        for (EventComponent component : schedule) {
            if (component.getDate().equals(date)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Обновляет общее количество ресурсов (сотрудников/оборудования) без автоматического сохранения в файл.
     * Перед изменением проверяет, достаточно ли нового количества для всех будущих мероприятий,
     * чтобы не заблокировать уже запланированные события.
     * Бросает IllegalStateException, если нового количества недостаточно.
     */
    public void updateStaffTotalNoSave(StaffType type, int newTotal) {
        int maxRequiredInOneDay = getMaxRequiredInOneDay(type);
        if (maxRequiredInOneDay > newTotal) {
            throw new IllegalStateException("Ошибка: требуется минимум " + maxRequiredInOneDay + " " + type.getDisplayName());
        }
        staffPool.updateTotal(type, newTotal);
    }

    public void updateEquipmentTotalNoSave(EquipmentType type, int newTotal) {
        int maxRequiredInOneDay = getMaxRequiredInOneDay(type);
        if (maxRequiredInOneDay > newTotal) {
            throw new IllegalStateException("Ошибка: требуется минимум " + maxRequiredInOneDay + " " + type.getDisplayName());
        }
        equipmentPool.updateTotal(type, newTotal);
    }

    /**
     * Вычисляет максимальное количество ресурсов, необходимое в один день среди всех БУДУЩИХ мероприятий.
     * Нужно для проверки при уменьшении общего пула: нельзя установить лимит меньше,
     * чем требуется для уже забронированных событий.
     * Игнорирует прошедшие мероприятия
     */
    private int getMaxRequiredInOneDay(StaffType type) {
        int maxRequired = 0;
        for (EventComponent component : schedule) {
            EventDate date = component.getDate();
            if (date != null && date.isInFuture()) {
                int required = component.getEvent().getStaffNeeded().getOrDefault(type, 0);
                if (required > maxRequired) {
                    maxRequired = required;
                }
            }
        }
        return maxRequired;
    }

    private int getMaxRequiredInOneDay(EquipmentType type) {
        int maxRequired = 0;
        for (EventComponent component : schedule) {
            EventDate date = component.getDate();
            if (date != null && date.isInFuture()) {
                int required = component.getEvent().getEquipmentNeeded().getOrDefault(type, 0);
                if (required > maxRequired) {
                    maxRequired = required;
                }
            }
        }
        return maxRequired;
    }

    private void savePools() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("staff_pool.ser"))) {
            oos.writeObject(staffPool);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении сотрудников: " + e.getMessage());
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("equip_pool.ser"))) {
            oos.writeObject(equipmentPool);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении оборудования: " + e.getMessage());
        }
    }

    private void loadPools() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("staff_pool.ser"))) {
            staffPool = (StaffPool) ois.readObject();
        } catch (FileNotFoundException e) {
            // Нет файла при первом запуске — оставляем значения по умолчанию (10)
            // Пользователь может задать свои значения через меню редактирования
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка при загрузке пула сотрудников: " + e.getMessage());
            System.err.println("Будут использованы значения по умолчанию.");
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("equip_pool.ser"))) {
            equipmentPool = (EquipmentPool) ois.readObject();
        } catch (FileNotFoundException e) {
            // Нет файла при первом запуске — оставляем значения по умолчанию (10)
            // Пользователь может задать свои значения через меню редактирования
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка при загрузке пула оборудования: " + e.getMessage());
            System.err.println("Будут использованы значения по умолчанию.");
        }
    }

    /**
     * Загружает расписание из файла schedule.dat.
     * Восстанавливает не только Event, но и повторно навешивает декораторы через getEventComponent().
     * Если файла нет (первый запуск) — просто создаёт пустое расписание.
     * Загружает пулы ресурсов через loadPools().
     */
    @SuppressWarnings("unchecked")
    private void loadFromFiles() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("schedule.dat"))) {
            List<SaveData> loaded = (List<SaveData>) ois.readObject();
            schedule.clear();

            for (SaveData sd : loaded) {
                EventComponent component = getEventComponent(sd);
                schedule.add(component);
            }

            loadPools();

        } catch (FileNotFoundException e) {
            // файла нет — первый запуск
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка загрузки: " + e.getMessage());
        }
    }

    /**
     * Восстанавливает цепочку декораторов из списка decorators в Event.
     * Идёт по списку и последовательно оборачивает BaseEvent в соответствующие декораторы.
     * Порядок обёртывания не важен, так как все добавляют фиксированную сумму.
     */
    private static EventComponent getEventComponent(SaveData sd) {
        Event event = sd.getEvent();
        EventComponent component = new BaseEvent(event);

        for (String decoratorName : event.getDecorators()) {
            if (CateringDecorator.NAME.equals(decoratorName)) {
                component = new CateringDecorator(component);
            } else if (FireworksDecorator.NAME.equals(decoratorName)) {
                component = new FireworksDecorator(component);
            } else if (FlowerDecorator.NAME.equals(decoratorName)) {
                component = new FlowerDecorator(component);
            }
        }
        return component;
    }

    /**
     * Сохраняет текущее расписание в файл schedule.dat.
     * Сохраняет только Event без декораторов, потому что декораторы восстанавливаются
     * по списку decorators при загрузке. Это упрощает сериализацию и избегает дублирования.
     * Дополнительно сохраняет пулы сотрудников и оборудования через savePools().
     */
    public void saveToFiles() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("schedule.dat"))) {
        List<SaveData> toSave = new ArrayList<>();
            for (EventComponent component : schedule) {
                Event rawEvent = component.getEvent();
                toSave.add(new SaveData(rawEvent));
            }
        oos.writeObject(toSave);
        savePools();
    } catch (IOException e) {
        System.err.println("Ошибка сохранения: " + e.getMessage());
    }
    }

    /**
     * Удаляет мероприятие из расписания по объекту Event.
     * Использует removeIf с равенством компонентов (через getEvent().equals(event)),
     * потому что schedule хранит EventComponent, а не Event напрямую.
     */
    public void remove(Event event) {
        schedule.removeIf(component -> component.getEvent().equals(event));
    }

    public void addBack(Event event) {
        BaseEvent baseEvent = new BaseEvent(event);
        schedule.add(baseEvent);
    }

    /**
     * Добавляет мероприятие в расписание без дополнительных проверок.
     * Используется только внутри CreateEventCommand, где проверки уже были выполнены.
     * Отдельный метод нужен, чтобы случайно не обойти валидацию в других местах.
     */
    public void addDirect(BaseEvent baseEvent) {
        schedule.add(baseEvent);
    }

}
