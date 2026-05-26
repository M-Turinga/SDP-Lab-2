package Template_Method;

import Decorators.*;
import Managment.ScheduleManager;
import Model.*;

import java.util.*;

/**
 * Процесс редактирования существующего мероприятия (аналог создания, но без выбора даты/места/длительности).
 * Работает с текущим Event, модифицирует его ресурсы и декораторы.
 * Не создаёт новый объект, а изменяет существующий (в отличие от EventCreationProcess).
 */
public class EditEventProcess {

    private ConsoleInput input;
    private EventRenderer renderer;
    private ScheduleManager manager;
    private Event event;

    private EventDate selectedDate;
    private EventPlace selectedPlace;
    private int durationHours;
    private Map<StaffType, Integer> currentStaff;
    private Map<EquipmentType, Integer> currentEquipment;
    private List<String> decorators;

    public EditEventProcess(ConsoleInput input, EventRenderer renderer, ScheduleManager manager) {
        this.input = input;
        this.renderer = renderer;
        this.manager = manager;
    }

    /**
     * Инициализация перед редактированием (с конкретным мероприятием)
     */
    public void init(Event event) {
        this.event = event;
        this.selectedDate = event.getDate();
        this.selectedPlace = event.getPlace();
        this.durationHours = event.getDurationHours();
        this.currentStaff = new HashMap<>(event.getStaffNeeded());
        this.currentEquipment = new HashMap<>(event.getEquipmentNeeded());
        this.decorators = new ArrayList<>(event.getDecorators());
    }

    /**
     * Процесс редактирования, похож на create() но без выбора даты/места/длительности.
     * Работает напрямую с существующим Event, не создавая новый.
     * Показывает текущее состояние, позволяет менять ресурсы/декораторы,
     * подтверждает изменения или отменяет.
     */
    public Event edit() {
        do {
            renderCurrentState();
            if (!processEditMenu()) {
                return null;
            }
        } while (!isConfirmed());
        return buildUpdatedEvent();
    }

    private void renderCurrentState() {

        String typeName = event.getDisplayName();

        int totalCost = calculateTotalCost();
        renderer.showCurrentState(selectedDate, selectedPlace, durationHours, typeName,
                currentStaff, currentEquipment, decorators, totalCost);
    }

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
        tempEvent.setCategory(event.getCategory());
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
        return input.readYesNo("\nПодтвердить изменения");
    }

    private Event buildUpdatedEvent() {
        event.setDate(selectedDate);
        event.setPlace(selectedPlace);
        event.setDurationHours(durationHours);
        event.setStaffNeeded(new HashMap<>(currentStaff));
        event.setEquipmentNeeded(new HashMap<>(currentEquipment));
        event.setDecorators(new ArrayList<>(decorators));

        System.out.println("Мероприятие успешно обновлено!");
        return event;
    }
}
