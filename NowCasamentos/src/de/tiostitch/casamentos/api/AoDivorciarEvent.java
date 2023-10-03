package de.tiostitch.casamentos.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AoDivorciarEvent extends Event {
    private final HandlerList handlerList = new HandlerList();
    private Player divorciador;
    private Player divorciado;

    public AoDivorciarEvent(Player player, Player other) {
        this.divorciador = player;
        this.divorciado = other;
    }

    public Player getDivorciador() {
        return divorciador;
    }

    public Player getDivorciado() {
        return divorciado;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
