package cn.mcres.luckyfish.antileakaccount;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class MessageHolder {
    private final ConfigurationSection cs;

    public MessageHolder(ConfigurationSection cs) {
        this.cs = cs;
    }

    public void sendMessage(Player player, String messageKey, BiFunction<Player, String, String> replacer) {
        player.sendMessage(getMessage(player, messageKey, replacer));
    }

    public String getMessage(Player player, String messageKey, BiFunction<Player, String, String> replacer) {
        String message = cs.getString(messageKey);
        if (replacer != null) {
            message = replacer.apply(player, message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public List<String> getMessages(Player player, String messageKey, BiFunction<Player, String, String> replacer) {
        List<String> result = new ArrayList<>();

        for (String msg : cs.getStringList(messageKey)) {
            String read = msg;
            if (replacer != null) {
                read = replacer.apply(player, read);
            }
            result.add(ChatColor.translateAlternateColorCodes('&', read));
        }

        return result;
    }
}
