package de.tiostitch.casamentos.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AoCasarEvent extends Event {
    private final HandlerList handlerList = new HandlerList();
    private Player casado;
    private Player casada;

    public AoCasarEvent(Player player, Player other) {
        this.casada = player;
        this.casado = other;
    }

    public Player getCasada() {
        return casada;
    }

    public Player getCasado() {
        return casado;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
