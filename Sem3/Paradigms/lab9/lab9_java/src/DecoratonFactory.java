public class DecoratonFactory {
    public static IDecoration getDecoration(String name) throws UnknownDecorationException {
        if (name == null) throw new UnknownDecorationException("Decoration name empty");

        String normalized = name.toUpperCase().trim();

        try {
            DecorationType type = DecorationType.valueOf(normalized);

            return switch (type) {
                case BAUBLES -> new BaublesDecoration();
                case LIGHTS -> new LightsDecoration();
                case ANGEL -> new AngelDecoration();
                case SNOWFLAKES -> new SnowflakesDecoration();
                default -> throw new UnknownDecorationException("No implementation for: " + name);
            };
        } catch (IllegalArgumentException e) {
            throw new UnknownDecorationException("Unknown decoration: " + name);
        }
    }
}
