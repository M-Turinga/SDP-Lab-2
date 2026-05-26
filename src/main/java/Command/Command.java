/**
 * Базовый интерфейс для всех команд (паттерн Command).
 * Поддерживает выполнение и отмену действия (undo).
 * Используется совместно с CommandHistory.
 */
package Command;

public interface Command {
    void execute();
    void undo();
}
