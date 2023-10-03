package de.tiostitch.casamentos.database;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class YAMLData {

    public static void setConjuge(String playerName, String i) throws IOException {
        File file = new File("plugins/NowCasamentos/data/" + playerName + "/data.yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        yamlConfiguration.set("principal.conjuge", String.valueOf(i));
        yamlConfiguration.save(file);
    }

    public static String getConjuge(String playerName) {
        File file = new File("plugins/NowCasamentos/data/" + playerName + "/data.yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        return yamlConfiguration.getString("principal.conjuge");
    }

    public static void addDivorciado(String playerName, String i) throws IOException {
        File file = new File("plugins/NowCasamentos/data/" + playerName + "/data.yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        List<String> divorciados = yamlConfiguration.getStringList("principal.divorciados");
        if (divorciados.contains(i)) {
            return;
        }
        divorciados.add(i);
        yamlConfiguration.set("principal.divorciados", divorciados);
        yamlConfiguration.save(file);
    }

    public static List<String> getDivorcios(String playerName) {
        File file = new File("plugins/NowCasamentos/data/" + playerName + "/data.yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        return yamlConfiguration.getStringList("principal.divorciados");
    }

    public static void createPlayer(Player p) throws IOException {
        File folder = new File("plugins/NowCasamentos/data/" + p.getName());
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File playerFile = new File("plugins/NowCasamentos/data/" + p.getName() + "/data.yml");
        if (!playerFile.exists()) {
            playerFile.createNewFile();
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(playerFile);

            yamlConfiguration.set("principal.conjuge", "ninguém");
            yamlConfiguration.set("principal.divorciados", null);

            yamlConfiguration.save(playerFile);
        }
    }
}


