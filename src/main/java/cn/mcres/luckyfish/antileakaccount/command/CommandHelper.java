package cn.mcres.luckyfish.antileakaccount.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandHelper {
    static List<String> listPlayers(String[] args) {
        if (args.length > 1) {
            return new ArrayList<>();
        }

        List<String> playerNames = new ArrayList<>();

        for (Player p : Bukkit.getOnlinePlayers()) {
            playerNames.add(p.getName());
        }

        if (args.length == 1) {
            playerNames.removeIf((name) -> !name.startsWith(args[0]));
        }

        return playerNames;
    }
}
