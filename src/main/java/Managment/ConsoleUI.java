package Managment;

import Command.*;
import Decorators.EventComponent;
import Model.*;
import Template_Method.*;
import org.springframework.context.ApplicationContext;

import java.util.*;

/**
 * Основной класс консольного интерфейса пользователя.3
 * Реализует цикл: главное меню → обработка ввода → вызов соответствующих команд.
 * Использует ScheduleManager для работы с расписанием и ресурсами,
 * CommandHistory для поддержки Undo.
 */
public class ConsoleUI {
    private final ScheduleManager manager;
    private final Scanner scanner;
    private final ConsoleInput consoleInput;
    private final ApplicationContext context;

    /**
     * Конструктор с внедрением зависимостей.
     * Зависимости получаются явно в Application.main()
     * Контекст используется для получения прототипов Process'ов
     */
    public ConsoleUI(ScheduleManager manager, Scanner scanner, ConsoleInput consoleInput, ApplicationContext context) {
        this.manager = manager;
        this.scanner = scanner;
        this.consoleInput = consoleInput;
        this.context = context;
    }

    public void run() {
        while (true) {
            showMainMenu();
            int choice = readInt();
            switch (choice) {
                case 1 -> createEvent();
                case 2 -> cancelEvent();
                case 3 -> showSchedule();
                case 4 -> editEvent();
                case 5 -> editStaffResources();
                case 6 -> editEquipmentResources();
                case 7 -> undoLast();
                case 8 -> {
                    System.out.println("До свидания!");
                    System.exit(0);
                }
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private void showMainMenu() {
        System.out.println("\n=== СИСТЕМА УПРАВЛЕНИЯ МЕРОПРИЯТИЯМИ ===");
        System.out.println("1. Создать мероприятие");
        System.out.println("2. Отменить мероприятие");
        System.out.println("3. Показать расписание");
        System.out.println("4. Редактировать мероприятие");
        System.out.println("5. Редактировать количество сотрудников");
        System.out.println("6. Редактировать количество оборудования");
        System.out.println("7. Отменить последнее действие (Undo)");
        System.out.println("8. Выход");
        System.out.print("Ваш выбор: ");
    }

    private int readInt() {
        while (!scanner.hasNextInt()) {
            System.out.print("Введите число: ");
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }

    private String readString() {
        return scanner.nextLine().trim();
    }

    /**
     * Выбирает конкретную реализацию EventCreationProcess
     * (свадьба/др/корпоратив/открытие/свой тип) в зависимости от выбора пользователя.
     * После создания оборачивает Event в CreateEventCommand и добавляет в историю для Undo.
     */
    private void createEvent() {
        System.out.println("\n--- СОЗДАНИЕ НОВОГО МЕРОПРИЯТИЯ ---");

        System.out.println("Выберите тип мероприятия:");
        System.out.println("1. Свадьба");
        System.out.println("2. День рождения");
        System.out.println("3. Новогодний корпоратив");
        System.out.println("4. Открытие ТЦ");
        System.out.println("5. Свой тип");
        System.out.println("0. Отмена");

        int choice = consoleInput.readInt("Ваш выбор: ");
        if (choice == 0) return;

        EventCreationProcess process;
        switch (choice) {
            case 1 -> process = context.getBean(WeddingProcess.class);
            case 2 -> process = context.getBean(BirthdayProcess.class);
            case 3 -> process = context.getBean(NewYearPartyProcess.class);
            case 4 -> process = context.getBean(MallOpeningProcess.class);
            case 5 -> {
                process = context.getBean(CustomEventProcess.class);
                String name = consoleInput.readString("Введите название мероприятия: ");
                process.setCustomEventName(name);
            }
            default -> {
                System.out.println("Неверный выбор");
                return;
            }
        }

        Event event = process.create();
        if (event != null) {
            CreateEventCommand command = new CreateEventCommand(
                    event, manager, manager.getStaffPool(), manager.getEquipmentPool()
            );
            command.execute();
            manager.getHistory().push(command);
            System.out.println("Мероприятие добавлено в расписание!");
        } else {
            System.out.println("Создание мероприятия отменено");
        }
    }

    /**
     * Отмена мероприятия с предварительной проверкой:
     * - мероприятие существует
     * - не отменено уже (canCancel())
     * - не в прошлом (нельзя отменить прошедшее)
     * Подтверждение через "да/нет" перед выполнением.
     */
    private void cancelEvent() {

        System.out.println("\n--- ОТМЕНА МЕРОПРИЯТИЯ ---");
        System.out.print("Введите дату мероприятия (день месяц год через пробел или точку): ");
        String line = scanner.nextLine();
        String[] parts = line.split("[ .]");

        if (parts.length != 3) {
            System.out.println("Ошибка: нужно ввести три числа (день месяц год)");
            System.out.print("Нажмите Enter для продолжения...");
            scanner.nextLine();
            return;
        }

        try {
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);

            EventDate date = new EventDate(day, month, year);

            EventComponent toCancel = manager.findByDate(date);
            if (toCancel != null) {
                Event event = toCancel.getEvent();

                System.out.println("Найдено мероприятие: " + event.getDisplayName());

                if (!event.getState().canCancel()) {
                    System.out.println("Мероприятие уже отменено или не может быть отменено");
                    System.out.print("Нажмите Enter для продолжения...");
                    scanner.nextLine();
                    return;
                }

                if (event.getDate().isInPast()) {
                    System.out.println("Нельзя отменить прошедшее мероприятие");
                    return;
                }

                System.out.print("Подтвердите отмену (да/нет): ");
                String confirm = readString();
                if (confirm.equalsIgnoreCase("да")) {

                    CancelEventCommand cmd = new CancelEventCommand(
                            event,
                            manager.getStaffPool(),
                            manager.getEquipmentPool(),
                            manager
                    );
                    cmd.execute();
                    manager.getHistory().push(cmd);
                    System.out.println("Мероприятие отменено");
                } else {
                    System.out.println("Отмена отменена");
                }
            } else {
                System.out.println("Мероприятие на дату " + date + " не найдено");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите числа");
        }
        System.out.print("Нажмите Enter для продолжения...");
        scanner.nextLine();
    }

    private void showSchedule() {
        System.out.println("\n--- РАСПИСАНИЕ МЕРОПРИЯТИЙ ---");

        List<EventComponent> schedule = manager.getSchedule();

        if (schedule.isEmpty()) {
            System.out.println("Расписание пусто");
        } else {
            for (EventComponent component : schedule) {
                Event event = component.getEvent();

                System.out.println(component.getDate() + " | " + event.getDisplayName() + " | " +
                        "Статус: " + event.getState().getStatusName() + " | " +
                        "Стоимость: " + component.getCost() + " руб.");

                System.out.println("Место: " + event.getPlace().getName() +
                        " (" + event.getPlace().getAddress() + ")");

                System.out.println("Сотрудники:");
                if (event.getStaffNeeded().isEmpty()) {
                    System.out.println("      (не требуются)");
                } else {
                    for (var entry : event.getStaffNeeded().entrySet()) {
                        System.out.println("      - " + entry.getKey().getDisplayName() + ": " + entry.getValue() + " шт.");
                    }
                }

                System.out.println("Оборудование:");
                if (event.getEquipmentNeeded().isEmpty()) {
                    System.out.println("      (не требуется)");
                } else {
                    for (var entry : event.getEquipmentNeeded().entrySet()) {
                        System.out.println("      - " + entry.getKey().getDisplayName() + ": " + entry.getValue() + " шт.");
                    }
                }

                if (!event.getDecorators().isEmpty()) {
                    System.out.println("Дополнительно:");
                    for (String d : event.getDecorators()) {
                        System.out.println("      - " + d);
                    }
                }
            }
        }

        System.out.print("\nНажмите Enter для продолжения...");
        scanner.nextLine();
    }

    /**
     * Редактирование с сохранением старого состояния до начала изменений.
     * Сохраняет копии всех полей (старые ресурсы, дата, место, декораторы),
     * запускает EditEventProcess, затем создаёт EditEventCommand со старыми и новыми значениями.
     * Это позволяет откатить изменения даже после нескольких правок.
     */
    private void editEvent() {
        System.out.println("\n--- РЕДАКТИРОВАНИЕ МЕРОПРИЯТИЯ ---");
        System.out.print("Введите дату мероприятия (день месяц год через пробел или точку): ");
        String line = scanner.nextLine();
        String[] parts = line.split("[ .]");

        if (parts.length != 3) {
            System.out.println("Ошибка: нужно ввести три числа (день месяц год)");
            System.out.print("Нажмите Enter для продолжения...");
            scanner.nextLine();
            return;
        }

        try {
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);
            EventDate date = new EventDate(day, month, year);

            EventComponent toEdit = manager.findByDate(date);
            if (toEdit == null) {
                System.out.println("Мероприятие не найдено");
                System.out.print("Нажмите Enter для продолжения...");
                scanner.nextLine();
                return;
            }

            Event event = toEdit.getEvent();

            if (event.getDate().isInPast()) {
                System.out.println("Нельзя редактировать прошедшее мероприятие");
                return;
            }

            if (!event.canEdit()) {
                System.out.println("Невозможно редактировать отменённое мероприятие");
                System.out.print("Нажмите Enter для продолжения...");
                scanner.nextLine();
                return;
            }

            System.out.println("Редактирование мероприятия: " + event.getDisplayName());

            Map<StaffType, Integer> oldStaff = new HashMap<>(event.getStaffNeeded());
            Map<EquipmentType, Integer> oldEquipment = new HashMap<>(event.getEquipmentNeeded());
            EventDate oldDate = event.getDate();
            EventPlace oldPlace = event.getPlace();
            int oldDurationHours = event.getDurationHours();
            List<String> oldDecorators = new ArrayList<>(event.getDecorators());

            // Получаем прототип EditEventProcess из Spring контекста
            EditEventProcess editProcess = context.getBean(EditEventProcess.class);
            editProcess.init(event);
            Event updatedEvent = editProcess.edit();

            if (updatedEvent != null) {
                EditEventCommand command = new EditEventCommand(
                        manager, updatedEvent,
                        oldStaff, oldEquipment,
                        new HashMap<>(updatedEvent.getStaffNeeded()),
                        new HashMap<>(updatedEvent.getEquipmentNeeded()),
                        oldDate, oldPlace, oldDurationHours, oldDecorators,
                        updatedEvent.getDate(), updatedEvent.getPlace(),
                        updatedEvent.getDurationHours(), new ArrayList<>(updatedEvent.getDecorators())
                );
                command.execute();
                manager.getHistory().push(command);

                System.out.println("Мероприятие успешно отредактировано!");
            } else {
                System.out.println("Редактирование отменено");
            }

        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите числа");
        }

        System.out.print("Нажмите Enter для продолжения...");
        scanner.nextLine();
    }

    /**
     * Редактирование глобальных ресурсов через команду.
     * Сохраняет старое значение ДО ввода нового, чтобы undo мог восстановить именно его.
     * После выполнения команды принудительно сохраняет в файл.
     */
    private void editStaffResources() {
        System.out.println("\n--- РЕДАКТИРОВАНИЕ СОТРУДНИКОВ ---");

        StaffType[] types = StaffType.values();
        for (int i = 0; i < types.length; i++) {
            System.out.println((i + 1) + ". " + types[i].getDisplayName() +
                    " (всего: " + manager.getStaffPool().getTotal(types[i]) + ")");
        }

        System.out.print("Ваш выбор: ");
        int choice = readInt();
        if (choice < 1 || choice > types.length) {
            System.out.println("Неверный выбор");
            return;
        }

        StaffType selectedType = types[choice - 1];
        int oldTotal = manager.getStaffPool().getTotal(selectedType);
        System.out.print("Введите новое общее количество " + selectedType.getDisplayName() + ": ");
        int newTotal = readInt();

        if (newTotal < 0) {
            System.out.println("Количество не может быть отрицательным");
            return;
        }

        try {
            EditStaffCommand command = new EditStaffCommand(manager, selectedType, oldTotal, newTotal);
            command.execute();
            manager.getHistory().push(command);
            manager.saveToFiles();

            System.out.println("Обновлено: " + selectedType.getDisplayName() + " -> " + newTotal);
        } catch (IllegalStateException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }

        System.out.print("\nНажмите Enter для продолжения...");
        scanner.nextLine();
    }


    /**
     * Редактирование глобальных ресурсов через команду.
     * Сохраняет старое значение ДО ввода нового, чтобы undo мог восстановить именно его.
     * После выполнения команды принудительно сохраняет в файл.
     */
    private void editEquipmentResources() {
        System.out.println("\n--- РЕДАКТИРОВАНИЕ ОБОРУДОВАНИЯ ---");

        EquipmentType[] types = EquipmentType.values();
        for (int i = 0; i < types.length; i++) {
            System.out.println((i + 1) + ". " + types[i].getDisplayName() +
                    " (всего: " + manager.getEquipmentPool().getTotal(types[i]) + ")");
        }

        System.out.print("Ваш выбор: ");
        int choice = readInt();
        if (choice < 1 || choice > types.length) {
            System.out.println("Неверный выбор");
            return;
        }

        EquipmentType selectedType = types[choice - 1];
        int oldTotal = manager.getEquipmentPool().getTotal(selectedType);  // сохраняем старое значение
        System.out.print("Введите новое общее количество " + selectedType.getDisplayName() + ": ");
        int newTotal = readInt();

        if (newTotal < 0) {
            System.out.println("Количество не может быть отрицательным");
            return;
        }

        try {
            EditEquipmentCommand command = new EditEquipmentCommand(manager, selectedType, oldTotal, newTotal);
            command.execute();
            manager.getHistory().push(command);
            manager.saveToFiles();

            System.out.println("Обновлено: " + selectedType.getDisplayName() + " -> " + newTotal);
        } catch (IllegalStateException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }

        System.out.print("\nНажмите Enter для продолжения...");
        scanner.nextLine();
    }

    private void undoLast() {
        System.out.println("\n--- ОТМЕНА ПОСЛЕДНЕГО ДЕЙСТВИЯ ---");
        manager.getHistory().undoLast();
        System.out.print("Нажмите Enter для продолжения...");
        scanner.nextLine();
    }
}