package Managment;

import Command.*;
import Model.*;
import Template_Method.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Scope;

import java.util.Scanner;

/**
 * Java-конфигурация Spring приложения.
 * Определяет бины для синглтонов и прототипов.
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AppConfig {

    // Синглтоны
    @Bean
    public StaffPool staffPool() {
        return new StaffPool();
    }

    @Bean
    public EquipmentPool equipmentPool() {
        return new EquipmentPool();
    }

    @Bean
    public CommandHistory commandHistory() {
        return new CommandHistory();
    }

    @Bean
    public ScheduleManager scheduleManager(StaffPool staffPool, EquipmentPool equipmentPool, CommandHistory commandHistory) {
        return new ScheduleManager(staffPool, equipmentPool, commandHistory);
    }

    @Bean
    public ConsoleInput consoleInput(Scanner scanner) {
        return new ConsoleInput(scanner);
    }

    @Bean
    public EventRenderer eventRenderer() {
        return new EventRenderer();
    }

    @Bean
    public Scanner scanner() {
        return new Scanner(System.in);
    }

    // Прототипы
    @Bean
    @Scope("prototype")
    public BirthdayProcess birthdayProcess(ConsoleInput input, EventRenderer renderer, ScheduleManager manager) {
        return new BirthdayProcess(input, renderer, manager);
    }

    @Bean
    @Scope("prototype")
    public WeddingProcess weddingProcess(ConsoleInput input, EventRenderer renderer, ScheduleManager manager) {
        return new WeddingProcess(input, renderer, manager);
    }

    @Bean
    @Scope("prototype")
    public NewYearPartyProcess newYearPartyProcess(ConsoleInput input, EventRenderer renderer, ScheduleManager manager) {
        return new NewYearPartyProcess(input, renderer, manager);
    }

    @Bean
    @Scope("prototype")
    public MallOpeningProcess mallOpeningProcess(ConsoleInput input, EventRenderer renderer, ScheduleManager manager) {
        return new MallOpeningProcess(input, renderer, manager);
    }

    @Bean
    @Scope("prototype")
    public CustomEventProcess customEventProcess(ConsoleInput input, EventRenderer renderer, ScheduleManager manager) {
        return new CustomEventProcess(input, renderer, manager);
    }

    @Bean
    @Scope("prototype")
    public EditEventProcess editEventProcess(ConsoleInput input, EventRenderer renderer, ScheduleManager manager) {
        return new EditEventProcess(input, renderer, manager);
    }


    // LoggingAspect для AOP
    @Bean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }


}
