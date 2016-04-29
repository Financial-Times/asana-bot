package com.ft.tasks;

public interface TaskRunnerFactory {
    TaskRunner getTaskRunner(final String taskName);
}
