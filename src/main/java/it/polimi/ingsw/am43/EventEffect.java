package it.polimi.ingsw.am43;

@FunctionalInterface
public interface EventEffect<T extends Event> {
    void manifest(Player player, T event);
}
