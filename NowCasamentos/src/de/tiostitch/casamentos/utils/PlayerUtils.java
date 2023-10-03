package de.tiostitch.casamentos.utils;

import de.tiostitch.casamentos.database.YAMLData;
import org.bukkit.entity.Player;

public class PlayerUtils {

    public static boolean isCasado(Player player) {
        return !YAMLData.getConjuge(player.getName()).equals("ninguém");
    }
}
