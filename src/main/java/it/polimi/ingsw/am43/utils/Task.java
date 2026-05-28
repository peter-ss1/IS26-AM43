package it.polimi.ingsw.am43.utils;

public interface Task<T> {
    public void execute(T target);
}
