package it.polimi.ingsw.am43.utils;

/**
 * Represents a command or action designed to be executed on a given target object type.
 * It is used to assign elements to an Executor queue.
 *
 * @param <T> The type of the target object on which the task operates.
 */
public interface Task<T> {
    public void execute(T target);
}
