package dev.tenacity.utils.render;

import dev.tenacity.NIXWARE;
import dev.tenacity.utils.tuples.Pair;
import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.module.settings.impl.ModeSetting;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum Theme {
    NIX("NIXWARE", new Color(0, 149, 185), new Color(5, 26, 45)),
    SPEARMINT("Spearmint", new Color(97, 194, 162), new Color(65, 130, 108)),
    AUBERGINE("Aubergine", new Color(170, 7, 107), new Color(97, 4, 95)),
    AQUA("Aqua", new Color(185, 250, 255), new Color(79, 199, 200)),
    BANANA("Banana", new Color(253, 236, 177), new Color(255, 255, 255)),
    BLASTER("Blaster", new Color(255, 172, 94), new Color(199, 121, 208)),
    BLEND("Blend", new Color(71, 148, 253), new Color(71, 253, 160)),
    BLOSSOM("Blossom", new Color(226, 208, 249), new Color(49, 119, 115)),
    BUBBLEGUM("Bubblegum", new Color(243, 145, 216), new Color(152, 165, 243)),
    CANDY_CANE("Candy Cane", new Color(255, 0, 0), new Color(255, 255, 255)),
    CANTALOUPE("Cantaloupe", new Color(248, 255, 174), new Color(67, 198, 172)),
    CARBON_FIBRE("Carbon Fibre", new Color(77, 79, 81), new Color(35, 37, 38)),
    CHERRY("Cherry", new Color(187, 55, 125), new Color(251, 211, 233)),
    CHRISTMAS("Christmas", new Color(255, 64, 64), new Color(255, 255, 255)),
    CORAL("Coral", new Color(244, 168, 150), new Color(52, 133, 151)),
    DIGITAL_HORIZON("Digital Horizon", new Color(95, 195, 228), new Color(229, 93, 135)),
    EXPRESS("Express", new Color(173, 83, 137), new Color(60, 16, 83)),
    JELLO_SHOT("Jello Shot", new Color(36, 254, 65), new Color(253, 252, 71)),
    LATTE("Latte", new Color(158, 118, 118), new Color(255, 248, 234)),
    LIME_WATER("Lime Water", new Color(18, 255, 247), new Color(179, 255, 171)),
    LUSH("Lush", new Color(168, 224, 99), new Color(86, 171, 47)),
    HALOGEN("Halogen", new Color(255, 65, 108), new Color(255, 75, 43)),
    HAZARD("Hazard", new Color(255, 250, 0), new Color(255, 255, 255)),
    HYPER("Hyper", new Color(236, 110, 173), new Color(52, 148, 230)),
    MAGIC("Magic", new Color(74, 0, 224), new Color(142, 45, 226)),
    MAY("May", new Color(238, 79, 238), new Color(253, 219, 245)),
    MINTY("Minty", new Color(148, 235, 194), new Color(31, 64, 55)),
    ORANGE_JUICE("Orange Juice", new Color(252, 74, 26), new Color(247, 183, 51)),
    OUTRUN("Outrun", new Color(239, 77, 160), new Color(7, 0, 82)),
    OVERDRIVE("Overdrive", new Color(131, 57, 179), new Color(253, 29, 29)),
    PASTEL("Pastel", new Color(243, 155, 178), new Color(207, 196, 243)),
    PINE("Pine", new Color(206, 212, 106), new Color(7, 85, 59)),
    PUMPKIN("Pumpkin", new Color(241, 166, 98), new Color(255, 216, 169)),
    POLARIZED("Polarized", new Color(173, 239, 209), new Color(0, 32, 64)),
    SATIN("Satin", new Color(215, 60, 67), new Color(140, 23, 39)),
    SNOWY_SKY("Snowy Sky", new Color(1, 171, 179), new Color(234, 234, 234)),
    STEEL_FADE("Steel Fade", new Color(66, 134, 244), new Color(55, 59, 68)),
    SUNDAE("Sundae", new Color(206, 74, 126), new Color(28, 28, 27)),
    SUNKIST("Sunkist", new Color(242, 201, 76), new Color(242, 153, 74)),
    SWEET_MORNING("Sweet Morning", new Color(255, 95, 109), new Color(255, 195, 113)),
    SYNCHRONIZED("Synchronized", new Color(247, 255, 0), new Color(219, 54, 164)),
    TERMINAL("Terminal", new Color(15, 155, 15), new Color(25, 30, 25)),
    TITANIUM("Titanium", new Color(133, 147, 152), new Color(40, 48, 72)),
    WATER("Water", new Color(12, 232, 199), new Color(12, 163, 232)),
    WATERMELON("Watermelon", new Color(236, 68, 155), new Color(153, 244, 67)),
    WINTER_STORM("Winter Storm", new Color(230, 218, 218), new Color(39, 64, 70)),
    WOOD("Wood", new Color(79, 109, 81), new Color(170, 139, 87)),
    JADE_GREEN("Jade Green", new Color(0, 168, 107), new Color(0, 105, 66)),
    VAPE("VAPE V5", new Color(26, 25, 26), new Color(1, 254, 170)),
    REMIX_COLOR("Remix Color", new Color(46, 204, 133), new Color(46, 204, 133)),
    Cyan_Purple("Cyan_Purple", new Color(140, 40, 255), new Color(46, 234, 255), true),
    DEV("Dev", NIXWARE.INSTANCE.getClientColor(), NIXWARE.INSTANCE.getAlternateClientColor(), true),
    CUSTOM_THEME("Custom Theme", HUDMod.color1.getColor(), HUDMod.color2.getColor());

    @Getter
    @AllArgsConstructor
    public enum KeyColors {
        RED(new Color(255, 50, 50)),
        ORANGE(new Color(255, 128, 50)),
        YELLOW(new Color(255, 255, 50)),
        LIME(new Color(128, 255, 50)),
        DARK_GREEN(new Color(50, 128, 50)),
        AQUA(new Color(50, 200, 255)),
        DARK_BLUE(new Color(50, 100, 200)),
        PURPLE(new Color(128, 50, 255)),
        PINK(new Color(255, 128, 255)),
        GRAY(new Color(100, 100, 110));

        private final Color color;
    }

    private static final Map<String, Theme> themeMap = new HashMap<>();

    private final String name;
    private final Pair<Color, Color> colors;
    private final boolean gradient;

    Theme(String name, Color color, Color colorAlt) {
        this(name, color, colorAlt, false);
    }

    Theme(String name, Color color, Color colorAlt, boolean gradient) {
        this.name = name;
        colors = Pair.of(color, colorAlt);
        this.gradient = gradient;
    }

    public static void init() {
        Arrays.stream(values()).forEach(theme -> themeMap.put(theme.getName(), theme));
    }

    public Pair<Color, Color> getColors() {
        if (this.equals(Theme.CUSTOM_THEME)) {
            if (HUDMod.color1.isRainbow()) {
                return Pair.of(HUDMod.color1.getColor(), HUDMod.color1.getAltColor());
            } else return Pair.of(HUDMod.color1.getColor(), HUDMod.color2.getColor());
        } else return colors;
    }

    public static Pair<Color, Color> getThemeColors(String name) {
        return get(name).getColors();
    }

    public static ModeSetting getModeSetting(String name, String defaultValue) {
        return new ModeSetting(name, defaultValue, Arrays.stream(Theme.values()).map(Theme::getName).toArray(String[]::new));
    }

    public static Theme get(String name) {
        return themeMap.get(name);
    }

    public static Theme getCurrentTheme() {
        return get(HUDMod.theme.getMode());
    }
}
