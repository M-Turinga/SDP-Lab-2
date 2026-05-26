package Template_Method;

import Builder.ConcreteEventBuilder;
import Builder.EventBuilder;
import Decorators.*;
import Managment.ScheduleManager;
import Model.*;

import java.util.*;
import java.time.Year;

/**
 * Шаблонный метод для создания мероприятия.
 * Определяет последовательность шагов:
 * 1. Выбор даты
 * 2. Выбор места
 * 3. Выбор длительности
 * 4. Инициализация ресурсов
 * 5. Редактирование (сотрудники, оборудование, опции)
 * 6. Подтверждение и финальное построение через Builder
 * Конкретные типы (свадьба, ДР и т.д.) переопределяют getDefaultStaff/getDefaultEquipment и категорию.
 */
public abstract class EventCreationProcess {

    protected ConsoleInput input;
    protected EventRenderer renderer;
    protected ScheduleManager manager;

    protected EventDate selectedDate;
    protected EventPlace selectedPlace;
    protected int durationHours;
    protected String customEventName;
    protected Map<StaffType, Integer> currentStaff;
    protected Map<EquipmentType, Integer> currentEquipment;
    protected List<String> decorators;

    /**
     * Единый конструктор для всех наследников.
     * Spring внедрит зависимости автоматически.
     */
    public EventCreationProcess(ConsoleInput input, EventRenderer renderer, ScheduleManager manager) {
        this.input = input;
        this.renderer = renderer;
        this.manager = manager;
    }

    public void setCustomEventName(String name) {
        this.customEventName = name;
    }

    /**
     * Шаблонный метод, задающий скелет алгоритма создания мероприятия.
     * Шаги:
     * 1. Выбор даты (selectDate)
     * 2. Выбор места (selectPlace)
     * 3. Выбор длительности (selectDuration)
     * 4. Инициализация ресурсов (initResources)
     * 5. Цикл редактирования (processEditMenu -> подтверждение)
     * 6. Сборка через Builder (buildAndConfirm)
     * Конкретные шаги (getDefaultStaff, getDefaultEquipment) переопределяются в наследниках.
     */
    public Event create() {
        if (!selectDate()) return null;
        if (!selectPlace()) return null;
        if (!selectDuration()) return null;

        initResources();

        do {
            renderCurrentState();
            if (!processEditMenu()) {
                return null;
            }
        } while (!isConfirmed());

        return buildAndConfirm();
    }

    protected abstract Map<StaffType, Integer> getDefaultStaff();
    protected abstract Map<EquipmentType, Integer> getDefaultEquipment();
    protected abstract String getDefaultEventTypeName();
    protected abstract EventCategory getEventCategory();

    protected boolean isCustomType() {
        return false;
    }

    private boolean selectDate() {
        while (true) {
            System.out.println("\n--- ВЫБОР ДАТЫ ---");
            int year = input.readInt("Введите год: ");

            int currentYear = Year.now().getValue();
            if (year < currentYear || year > currentYear + 10) {
                System.out.println("Ошибка: год должен быть от " + currentYear + " до " + (currentYear + 10));
                continue;
            }

            int month = input.readInt("Введите месяц (1-12): ");
            if (month < 1 || month > 12) {
                System.out.println("Ошибка: месяц должен быть от 1 до 12");
                continue;
            }

            int day = input.readInt("Введите день: ");
            if (!isValidDay(day, month, year)) {
                System.out.println("Ошибка: некорректный день для указанного месяца");
                continue;
            }

            EventDate date = new EventDate(day, month, year);

            if (!date.isInFuture()) {
                System.out.println("Ошибка: дата должна быть в будущем");
                continue;
            }

            if (manager.isDateOccupied(date)) {
                System.out.println("Ошибка: на эту дату уже есть мероприятие");
                continue;
            }

            this.selectedDate = date;
            System.out.println("Дата свободна!");
            return true;
        }
    }

    private boolean isValidDay(int day, int month, int year) {
        if (day < 1) return false;

        int maxDay = switch (month) {
            case 4, 6, 9, 11 -> 30;
            case 2 -> isLeapYear(year) ? 29 : 28;
            default -> 31;
        };

        return day <= maxDay;
    }

    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    private boolean selectPlace() {
        System.out.println("\n--- ВЫБОР МЕСТА ---");
        System.out.println("1. Банкетный зал (5000 руб/час)");
        System.out.println("2. Конференц-зал (7000 руб/час)");
        System.out.println("3. Открытая площадка (3000 руб/час)");
        System.out.println("4. На территории заказчика (бесплатно)");
        System.out.println("0. Отмена (выйти в главное меню)");

        int choice = input.readInt("Ваш выбор: ");
        if (choice == 0) return false;

        switch (choice) {
            case 1:
                selectedPlace = new EventPlace("Банкетный зал", "ул. Пушкина 1", 5000);
                return true;
            case 2:
                selectedPlace = new EventPlace("Конференц-зал", "пр. Ленина 10", 7000);
                return true;
            case 3:
                selectedPlace = new EventPlace("Открытая площадка", "Парк Горького", 3000);
                return true;
            case 4:
                System.out.println("\n--- МЕСТО ЗАКАЗЧИКА ---");
                String address = input.readString("Введите адрес проведения мероприятия: ");
                selectedPlace = new EventPlace("На территории заказчика", address, 0);
                return true;
            default:
                System.out.println("Неверный выбор");
                return selectPlace();
        }
    }

    private boolean selectDuration() {
        int hours = input.readInt("Введите длительность в часах (0 - отмена): ");
        if (hours == 0) return false;
        if (hours <= 0) {
            System.out.println("Длительность должна быть положительной");
            return selectDuration();
        }
        this.durationHours = hours;
        return true;
    }

    private void initResources() {
        if (isCustomType() || customEventName != null) {
            currentStaff = new HashMap<>();
            currentEquipment = new HashMap<>();
        } else {
            currentStaff = new HashMap<>(getDefaultStaff());
            currentEquipment = new HashMap<>(getDefaultEquipment());
        }
        decorators = new ArrayList<>();
    }

    private void renderCurrentState() {
        String typeName = (customEventName != null) ? customEventName : getDefaultEventTypeName();
        int totalCost = calculateTotalCost();
        renderer.showCurrentState(selectedDate, selectedPlace, durationHours, typeName,
                currentStaff, currentEquipment, decorators, totalCost);
    }

    /**
     * Создаёт временный Event и навешивает декораторы, чтобы показать пользователю
     * предварительную стоимость ДО подтверждения мероприятия.
     * Не сохраняет этот объект в расписание.
     */
    private int calculateTotalCost() {

        Event tempEvent = createTempEvent();
        EventComponent component = new BaseEvent(tempEvent);

        for (String decoratorName : decorators) {
            component = applyDecorator(component, decoratorName);
        }

        return component.getCost();
    }

    private Event createTempEvent() {
        Event tempEvent = new Event();

        if (!isCustomType() && getEventCategory() != null) {
            tempEvent.setCategory(getEventCategory());
        }

        tempEvent.setDate(selectedDate);
        tempEvent.setDurationHours(durationHours);
        tempEvent.setPlace(selectedPlace);
        tempEvent.setStaffNeeded(new HashMap<>(currentStaff));
        tempEvent.setEquipmentNeeded(new HashMap<>(currentEquipment));

        return tempEvent;
    }

    private EventComponent applyDecorator(EventComponent component, String decoratorName) {
        if (CateringDecorator.NAME.equals(decoratorName)) {
            return new CateringDecorator(component);
        } else if (FireworksDecorator.NAME.equals(decoratorName)) {
            return new FireworksDecorator(component);
        } else if (FlowerDecorator.NAME.equals(decoratorName)) {
            return new FlowerDecorator(component);
        }
        return component;
    }

    private boolean processEditMenu() {
        while (true) {
            renderer.showEditMenu();
            int choice = input.readInt("");

            switch (choice) {
                case 1:
                    editStaff();
                    break;
                case 2:
                    editEquipment();
                    break;
                case 3:
                    editDecorators();
                    break;
                case 4:
                    renderCurrentState();
                    break;
                case 5:
                    return true;
                case 6:
                    return false;
                default:
                    System.out.println("Неверный выбор");
            }
        }
    }

    private void editStaff() {
        StaffType[] types = StaffType.values();
        Map<StaffType, Integer> available = new HashMap<>();
        for (StaffType type : types) {
            available.put(type, manager.getStaffPool().getTotal(type));
        }

        while (true) {
            renderer.showStaffEditMenu(types, currentStaff, available);
            int choice = input.readInt("Выберите тип: ");
            if (choice == 0) break;
            if (choice < 1 || choice > types.length) {
                System.out.println("Неверный выбор");
                continue;
            }

            StaffType type = types[choice - 1];
            int maxAvailable = available.get(type);
            int count = input.readInt("Введите новое количество (0-" + maxAvailable + "): ");

            if (count < 0 || count > maxAvailable) {
                System.out.println("Некорректное количество");
                continue;
            }

            if (count == 0) {
                currentStaff.remove(type);
            } else {
                currentStaff.put(type, count);
            }
            System.out.println("Обновлено");
        }
    }

    private void editEquipment() {
        EquipmentType[] types = EquipmentType.values();
        Map<EquipmentType, Integer> available = new HashMap<>();
        for (EquipmentType type : types) {
            available.put(type, manager.getEquipmentPool().getTotal(type));
        }

        while (true) {
            renderer.showEquipmentEditMenu(types, currentEquipment, available);
            int choice = input.readInt("Выберите тип: ");
            if (choice == 0) break;
            if (choice < 1 || choice > types.length) {
                System.out.println("Неверный выбор");
                continue;
            }

            EquipmentType type = types[choice - 1];
            int maxAvailable = available.get(type);
            int count = input.readInt("Введите новое количество (0-" + maxAvailable + "): ");

            if (count < 0 || count > maxAvailable) {
                System.out.println("Некорректное количество");
                continue;
            }

            if (count == 0) {
                currentEquipment.remove(type);
            } else {
                currentEquipment.put(type, count);
            }
            System.out.println("Обновлено");
        }
    }

    private void editDecorators() {
        while (true) {
            boolean hasCatering = decorators.contains(CateringDecorator.NAME);
            boolean hasFireworks = decorators.contains(FireworksDecorator.NAME);
            boolean hasFlowers = decorators.contains(FlowerDecorator.NAME);

            renderer.showDecoratorMenu(hasCatering, hasFireworks, hasFlowers);
            int choice = input.readInt("Ваш выбор: ");
            if (choice == 0) break;

            String decorator;
            switch (choice) {
                case 1: decorator = CateringDecorator.NAME; break;
                case 2: decorator = FireworksDecorator.NAME; break;
                case 3: decorator = FlowerDecorator.NAME; break;
                default:
                    System.out.println("Неверный выбор");
                    continue;
            }

            if (decorators.contains(decorator)) {
                decorators.remove(decorator);
                System.out.println("Опция удалена");
            } else {
                decorators.add(decorator);
                System.out.println("Опция добавлена");
            }
        }
    }

    private boolean isConfirmed() {
        return input.readYesNo("\nПодтвердить мероприятие");
    }

    private Event buildAndConfirm() {
        EventBuilder builder = new ConcreteEventBuilder();

        if (!isCustomType() && getEventCategory() != null) {
            builder.setCategory(getEventCategory());
        }

        builder.setDate(selectedDate, durationHours);
        builder.setPlace(selectedPlace);

        for (var entry : currentStaff.entrySet()) {
            builder.addStaff(entry.getKey(), entry.getValue());
        }
        for (var entry : currentEquipment.entrySet()) {
            builder.addEquipment(entry.getKey(), entry.getValue());
        }

        Event event = builder.build();

        if (customEventName != null && !customEventName.isEmpty()) {
            event.setCustomName(customEventName);
        }

        if (decorators != null && !decorators.isEmpty()) {
            event.setDecorators(decorators);
        }

        event.confirm();

        System.out.println("Мероприятие успешно создано и подтверждено!");
        return event;
    }
}
