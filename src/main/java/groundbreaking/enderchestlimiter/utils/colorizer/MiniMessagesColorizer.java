package groundbreaking.enderchestlimiter.utils.colorizer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MiniMessagesColorizer implements IColorizer {

    @Override
    public String colorize(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }

        Component component = MiniMessage.miniMessage().deserialize(message);
        return LegacyComponentSerializer.legacySection().serialize(component);
    }
}
