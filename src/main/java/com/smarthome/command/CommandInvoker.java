package com.smarthome.command;

import com.smarthome.persistence.dao.CommandsLogDAO;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


// Executes commands, stores undo history, and optionally writes command audit logs.
public class CommandInvoker {
    private final Deque<DeviceCommand> history = new ArrayDeque<>();
    private final CommandsLogDAO auditLog;

    
    public CommandInvoker() {
        this(null);
    }

    
    public CommandInvoker(CommandsLogDAO auditLog) {
        this.auditLog = auditLog;
    }

    
    public void execute(DeviceCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        command.execute();
        history.push(command);
        if (auditLog != null) {
            auditLog.insert(
                UUID.randomUUID().toString(),
                null,                       // device_id resolution belongs in commands; keep null here
                command.describe(),
                "{}",
                "OK"
            );
        }
    }

    
    public boolean canUndo() {
        return !history.isEmpty();
    }

    
    public DeviceCommand undo() {
        if (history.isEmpty()) {
            return null;
        }
        DeviceCommand last = history.pop();
        last.undo();
        return last;
    }

    
    public List<DeviceCommand> getHistory() {
        return Collections.unmodifiableList(List.copyOf(history));
    }

    
    public void clearHistory() {
        history.clear();
    }
}
