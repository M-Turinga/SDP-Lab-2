package Template_Method;

import java.util.Scanner;

/**
 * Класс для безопасного чтения ввода с консоли.
 * Обрабатывает ошибки ввода чисел и пустые строки.
 * Используется в шаблонных методах создания/редактирования.
 */
public class ConsoleInput {

    private final Scanner scanner;

    public ConsoleInput(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Безопасное чтение целого числа с консоли.
     * При некорректном вводе (не число) запрашивает повторно, не вылетая с исключением.
     * Вызывает scanner.nextLine() после успешного чтения, чтобы очистить буфер.
     */
    public int readInt(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("Ошибка: введите число: ");
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }

    public String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public boolean readYesNo(String prompt) {
        String answer = readString(prompt + " (да/нет): ");
        return answer.equalsIgnoreCase("да");
    }
}