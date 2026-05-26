package Managment;

import Model.Event;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;

/**
 * AOP Aspect для логирования методов Spring Bean'ов.
 */
@Aspect
@Component
public class LoggingAspect implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Логирование выполнения команд
    @Before("execution(* Command.Command.execute(..))")
    public void logCommandExecute(JoinPoint joinPoint) {
        String commandName = joinPoint.getTarget().getClass().getSimpleName();
        System.out.println("[ВЫПОЛНЕНИЕ] " + commandName);
    }

    // Логирование отмены команд
    @Before("execution(* Command.Command.undo(..))")
    public void logCommandUndo(JoinPoint joinPoint) {
        String commandName = joinPoint.getTarget().getClass().getSimpleName();
        System.out.println("[ОТМЕНА] " + commandName);
    }

    // Логирование создания мероприятия
    @AfterReturning(
            pointcut = "execution(* Template_Method.EventCreationProcess.create(..))",
            returning = "event"
    )
    public void logEventCreation(Event event) {
        if (event != null) {
            System.out.println("[СОЗДАНО] " + event.getDisplayName());
        } else {
            System.out.println("[СОЗДАНИЕ] Отменено пользователем");
        }
    }

    // Логирование сохранения
    @Before("execution(* Managment.ScheduleManager.saveToFiles(..))")
    public void logSave() {
        System.out.println("[СОХРАНЕНИЕ] Данные сохранены в файлы");
    }

    // Логирование ошибок
    @AfterThrowing(
            pointcut = "execution(* Command.*Command.execute(..))",
            throwing = "exception"
    )
    public void logError(Exception exception) {
        System.out.println("[ОШИБКА] " + exception.getMessage());
    }
}
