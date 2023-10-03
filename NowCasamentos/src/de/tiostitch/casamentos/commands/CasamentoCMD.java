package de.tiostitch.casamentos.commands;

import de.tiostitch.casamentos.Main;
import de.tiostitch.casamentos.api.AoCasarEvent;
import de.tiostitch.casamentos.api.AoDivorciarEvent;
import de.tiostitch.casamentos.api.AoPedirCasamentoEvent;
import de.tiostitch.casamentos.api.AoRecusarCasamentoEvent;
import de.tiostitch.casamentos.database.YAMLData;
import de.tiostitch.casamentos.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CasamentoCMD implements CommandExecutor {

    private HashMap<Player, List<String>> pedidos_recebidos = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("§b§lLista de Comandos - §eNowCasamentos");
            player.sendMessage("");
            player.sendMessage("§e/cs aceitar <jogador> - Aceita um pedido de casamento.");
            player.sendMessage("§e/cs recusar <jogador> - Recusa um pedido de casamento.");
            player.sendMessage("");
            player.sendMessage("§e/cs casar <jogador> - Envia um pedido de casamento.");
            player.sendMessage("§e/cs divorciar <jogador> - Envia um pedido de divórcio.");
            player.sendMessage("§e/cs verstatus <jogador> - Vê os status de um jogador.");
            player.sendMessage("");
            return false;
        }

        if (args[0].equalsIgnoreCase("casar")) {
            if (!player.hasPermission("NowCasamentos.casar")) {
                player.sendMessage("§cVocê não tem permissão para usar este comando!");
                return false;
            }

            if (args.length == 1) {
                return false;
            }
            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                player.sendMessage("§cO jogador é inválido ou está offline!");
                return false;
            }

            //Check bloqueador de limitador.
            if (checkCasarPlayer(player, target)) {
                return false;
            }

            List<String> pedidos = pedidos_recebidos.getOrDefault(target, new ArrayList<>());
            if (pedidos.contains(player.getName())) {
                player.sendMessage("§cVocê já enviou um pedido de casamento para este jogador!");
                return false;
            }


            String pedidoMessage = Main.plugin.getConfig().getString("messages.receber-pedido-casamento")
                    .replace("&", "§")
                    .replace("%pedidoPor%", player.getName());
            String reciboMessage = Main.plugin.getConfig().getString("messages.enviado-pedido-casamento")
                    .replace("&", "§")
                    .replace("%enviadoPara%", target.getName());

            player.sendMessage(reciboMessage);

            Bukkit.getPluginManager().callEvent(new AoPedirCasamentoEvent(player, target));

            pedidos.add(player.getName());
            pedidos_recebidos.put(target, pedidos);
            target.sendMessage(pedidoMessage);
            runScheduler(player, target);
        } else if (args[0].equalsIgnoreCase("divorciar")) {
            if (!player.hasPermission("NowCasamentos.divorciar")) {
                player.sendMessage("§cVocê não tem permissão para usar este comando!");
                return false;
            }

            if (args.length == 1) {
                return false;
            }
            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                player.sendMessage("§cO jogador é inválido ou está offline!");
                return false;
            }

            if (!PlayerUtils.isCasado(player)) {
                String casadoMessage = Main.plugin.getConfig().getString("messages.nao-esta-casado").replace("&", "§");
                player.sendMessage(casadoMessage);
                return true;
            }

            if (!PlayerUtils.isCasado(target)) {
                String casadoMessage = Main.plugin.getConfig().getString("messages.nao-outro-casado").replace("&", "§");
                player.sendMessage(casadoMessage);
                return true;
            }

            String pedidoMessage = Main.plugin.getConfig().getString("messages.divorciar")
                    .replace("&", "§");
            String reciboMessage = Main.plugin.getConfig().getString("messages.eu-divorciei")
                    .replace("&", "§");
            String divorcioGeral = Main.plugin.getConfig().getString("messages.divorcio-geral")
                    .replace("&", "§")
                    .replace("%divorciadoPor%", player.getName())
                    .replace("%divorciado%", target.getName());

            player.sendMessage(reciboMessage);
            target.sendMessage(pedidoMessage);

            try {
                YAMLData.setConjuge(player.getName(), "ninguém");
                YAMLData.setConjuge(target.getName(), "ninguém");
                YAMLData.addDivorciado(player.getName(), target.getName());
                YAMLData.addDivorciado(target.getName(), player.getName());

                Bukkit.getPluginManager().callEvent(new AoDivorciarEvent(player, target));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Bukkit.broadcastMessage(divorcioGeral);


        } else if (args[0].equalsIgnoreCase("aceitar")) {
            if (!player.hasPermission("NowCasamentos.aceitar")) {
                player.sendMessage("§cVocê não tem permissão para usar este comando!");
                return false;
            }

            if (args.length == 1) {
                return false;
            }
            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                player.sendMessage("§cO jogador é inválido ou está offline!");
                return false;
            }

            //Check bloqueador de limitador.
            if (checkCasarPlayer(player, target)) {
                return false;
            }

            String pedidoMessage = Main.plugin.getConfig().getString("messages.aceito-pedido-casamento")
                    .replace("&", "§");
            String pedidoGeral = Main.plugin.getConfig().getString("messages.casamento-geral")
                    .replace("&", "§")
                    .replace("%casado%", target.getName())
                    .replace("%casada%", player.getName());

            List<String> pedidos = pedidos_recebidos.getOrDefault(player, new ArrayList<>());
            if (pedidos.contains(target.getName())) {
                player.sendMessage(pedidoMessage);
                try {
                    YAMLData.setConjuge(player.getName(), target.getName());
                    YAMLData.setConjuge(target.getName(), player.getName());

                    Bukkit.getPluginManager().callEvent(new AoCasarEvent(player, target));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Bukkit.broadcastMessage(pedidoGeral);
            } else {
                player.sendMessage("§cVocê não tem pedidos de casamento deste jogador!");
            }
        } else if (args[0].equalsIgnoreCase("recusar")) {
            if (!player.hasPermission("NowCasamentos.recusar")) {
                player.sendMessage("§cVocê não tem permissão para usar este comando!");
                return false;
            }

            if (args.length == 1) {
                return false;
            }
            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                player.sendMessage("§cO jogador é inválido ou está offline!");
                return false;
            }

            List<String> pedidos = pedidos_recebidos.getOrDefault(player, new ArrayList<>());
            if (pedidos.contains(target.getName())) {
                String negadoMessage = Main.plugin.getConfig().getString("messages.negado-pedido-casamento").replace("&", "§");
                player.sendMessage(negadoMessage);
                target.sendMessage("§cO pedido de casamento foi negado!");

                Bukkit.getPluginManager().callEvent(new AoRecusarCasamentoEvent(player, target));

                pedidos.remove(target.getName());
                pedidos_recebidos.put(player, pedidos);
            } else {
                player.sendMessage("§cVocê não tem pedidos de casamento deste jogador!");
            }
        } else if (args[0].equalsIgnoreCase("verstatus")) {
            if (!player.hasPermission("NowCasamentos.verstatus")) {
                player.sendMessage("§cVocê não tem permissão para usar este comando!");
                return false;
            }

            if (args.length == 1) {
                return false;
            }
            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                player.sendMessage("§cO jogador é inválido ou está offline!");
                return false;
            }

            //NÃO TERMINADO ATUALMENTE!
            //ISTO NÃO ESTÁ OTIMIZADO NEM FUNCIONAL DIREITO.
            String[] divorcios = new String[3];
            List<String> statusJogador = Main.plugin.getConfig().getStringList("messages.status");
            List<String> div = YAMLData.getDivorcios(target.getName());

            for (int i = 0; i < 3 && i < div.size(); i++) {
                divorcios[i] = div.get(i);
            }

            String status = PlayerUtils.isCasado(player) ? "§aCasado" : "§cSolteiro";

            for (String string : statusJogador) {
                String mensagem = string
                        .replace("&", "§")
                        .replace("%name%", target.getName())
                        .replace("%status%", status)
                        .replace("%divorcios%", String.join("\n", divorcios)); // Adicione "\n" para separar as mensagens

                player.sendMessage(mensagem);
            }
        }
        return false;
    }


    public static boolean checkCasarPlayer(Player player, Player target) {
        if (target.getName().equals(player.getName())) {
            player.sendMessage("§cVocê não pode enviar pedidos de casamento para sí mesmo!");
            return true;
        }

        if (PlayerUtils.isCasado(player)) {
            String casadoMessage = Main.plugin.getConfig().getString("messages.esta-casado").replace("&", "§");
            player.sendMessage(casadoMessage);
            return true;
        }

        if (PlayerUtils.isCasado(target)) {
            String casadoMessage = Main.plugin.getConfig().getString("messages.outro-casado").replace("&", "§");
            player.sendMessage(casadoMessage);
            return true;
        }

        return false;
    }

    int DELAY = Main.plugin.getConfig().getInt("settings.expirar-pedido");
    public void runScheduler(Player player, Player target) {
        Bukkit.getScheduler().runTaskLater(Main.plugin, () -> {
            if (pedidos_recebidos.containsKey(target)) {
                player.sendMessage("§cO pedido de casamento expirou ou negou!");
                target.sendMessage("§cO pedido de casamento expirou ou negou!");
            }

            List<String> pedidos = pedidos_recebidos.getOrDefault(target, new ArrayList<>());
            pedidos.remove(player.getName());
            pedidos_recebidos.put(target, pedidos);
        }, DELAY * 20L);
    }
}
