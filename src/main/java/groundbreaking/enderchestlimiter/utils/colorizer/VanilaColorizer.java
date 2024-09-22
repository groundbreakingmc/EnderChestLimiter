package groundbreaking.enderchestlimiter.utils.colorizer;

public class VanilaColorizer implements IColorizer {

    @Override
    public String colorize(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }

        return ColorCodesTranslator.translateAlternateColorCodes('&', message);
    }
}
