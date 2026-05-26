package Command;

import java.util.Stack;

/**
 * История выполненных команд (стек).
 * Позволяет отменить последнее действие (Ctrl+Z).
 */
public class CommandHistory {
    private final Stack<Command> history = new Stack<>();

    public void push(Command cmd) { history.push(cmd); }

    public void undoLast() {
        if (!history.isEmpty()) {
        Command cmd = history.pop();
        cmd.undo();
        System.out.println("Отмена последней команды выполнена");
        } else {
            System.out.println("Нет команд для отмены");
        }
    }
}
