package de.tiostitch.casamentos.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AoRecusarCasamentoEvent extends Event {
    private final HandlerList handlerList = new HandlerList();
    private Player enviador;
    private Player recebedor;

    public AoRecusarCasamentoEvent(Player player, Player other) {
        this.enviador = player;
        this.recebedor = other;
    }

    public Player getEnviador() {
        return enviador;
    }

    public Player getRecebedor() {
        return recebedor;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
