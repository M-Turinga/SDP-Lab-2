package Managment;

import Template_Method.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Точка входа приложения. Здесь создается и управляется Spring контекст.
 * Контекст создается в main, используется для получения необходимых bean'ов,
 * а затем закрывается при завершении программы.
 */
public class Application {
    public static void main(String[] args) {
        // Установка UTF-8 кодировки для консоли
        System.setProperty("file.encoding", "UTF-8");
        System.setOut(new java.io.PrintStream(System.out, true, StandardCharsets.UTF_8));

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        try {
            ScheduleManager scheduleManager = context.getBean(ScheduleManager.class);
            Scanner scanner = context.getBean(Scanner.class);
            ConsoleInput consoleInput = context.getBean(ConsoleInput.class);

            ConsoleUI consoleUI = new ConsoleUI(scheduleManager, scanner, consoleInput, context);
            consoleUI.run();

        } finally {
            context.close();
        }
    }
}

