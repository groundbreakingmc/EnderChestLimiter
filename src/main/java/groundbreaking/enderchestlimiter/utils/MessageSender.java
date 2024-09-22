package groundbreaking.enderchestlimiter.utils;

import org.bukkit.command.CommandSender;

import java.util.List;

public class MessageSender {

    public static void send(CommandSender sender, List<String> messages) {
        for (int i = 0; i < messages.size() - 1; i++) {
            sender.sendMessage(messages.get(i));
        }
    }

    public static void send(CommandSender sender, List<String> messages, String replacementTarget, String replacement) {
        for (int i = 0; i < messages.size() - 1; i++) {
            sender.sendMessage(messages.get(i).replace(replacementTarget, replacement));
        }
    }
}
