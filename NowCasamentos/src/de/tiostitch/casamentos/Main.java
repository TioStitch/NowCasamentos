package de.tiostitch.casamentos;

import de.tiostitch.casamentos.commands.CasamentoCMD;
import de.tiostitch.casamentos.database.YAMLData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class Main extends JavaPlugin implements Listener {

    public static Main plugin;

    @Override
    public void onEnable() {
        plugin = this;

        Bukkit.getConsoleSender().sendMessage("§a------------------");
        Bukkit.getConsoleSender().sendMessage("  §b§lNowCasamentos");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(" §fEstado:");
        Bukkit.getConsoleSender().sendMessage("  §aIniciado com sucesso!");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§a------------------");
        saveDefaultConfig();

        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginCommand("cs").setExecutor(new CasamentoCMD());
    }

    @EventHandler
    public void onPlayerEnter(PlayerJoinEvent e) throws IOException {
        YAMLData.createPlayer(e.getPlayer());
    }
}
