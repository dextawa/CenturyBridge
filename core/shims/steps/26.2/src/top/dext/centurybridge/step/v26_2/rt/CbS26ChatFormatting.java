package top.dext.centurybridge.step.v26_2.rt;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.util.ExtraCodecs;

/**
 * 26.1.2's ChatFormatting API, which 26.2 cut down to its codes (getByCode, stripFormatting, toString): the
 * enum no longer implements StringRepresentable and dropped its name, id, colour and format data. Each member as
 * 26.1.2 had it (javap of 26.1.2's class): the per-constant data of 26.1.2's <clinit> (ids 0..15 and RGB colours
 * of the sixteen colours, the five formats, RESET with id -1 and no colour), getName = name() in lower case,
 * getByName through 26.1.2's cleanName (lower case, letters only), CODEC as StringRepresentable.fromEnum built it
 * (by serialized name, compressed as the ordinal), COLOR_CODEC refusing formats with 26.1.2's message. getChar
 * reads 26.2's own code (the same char, layer AW).
 */
public final class CbS26ChatFormatting {
    private CbS26ChatFormatting() {}

    private record Data(int id, Integer color, boolean format) {}

    private static final Map<ChatFormatting, Data> DATA = new java.util.EnumMap<>(Map.ofEntries(
        Map.entry(ChatFormatting.BLACK, new Data(0, 0, false)),
        Map.entry(ChatFormatting.DARK_BLUE, new Data(1, 170, false)),
        Map.entry(ChatFormatting.DARK_GREEN, new Data(2, 43520, false)),
        Map.entry(ChatFormatting.DARK_AQUA, new Data(3, 43690, false)),
        Map.entry(ChatFormatting.DARK_RED, new Data(4, 11141120, false)),
        Map.entry(ChatFormatting.DARK_PURPLE, new Data(5, 11141290, false)),
        Map.entry(ChatFormatting.GOLD, new Data(6, 16755200, false)),
        Map.entry(ChatFormatting.GRAY, new Data(7, 11184810, false)),
        Map.entry(ChatFormatting.DARK_GRAY, new Data(8, 5592405, false)),
        Map.entry(ChatFormatting.BLUE, new Data(9, 5592575, false)),
        Map.entry(ChatFormatting.GREEN, new Data(10, 5635925, false)),
        Map.entry(ChatFormatting.AQUA, new Data(11, 5636095, false)),
        Map.entry(ChatFormatting.RED, new Data(12, 16733525, false)),
        Map.entry(ChatFormatting.LIGHT_PURPLE, new Data(13, 16733695, false)),
        Map.entry(ChatFormatting.YELLOW, new Data(14, 16777045, false)),
        Map.entry(ChatFormatting.WHITE, new Data(15, 16777215, false)),
        Map.entry(ChatFormatting.OBFUSCATED, new Data(-1, null, true)),
        Map.entry(ChatFormatting.BOLD, new Data(-1, null, true)),
        Map.entry(ChatFormatting.STRIKETHROUGH, new Data(-1, null, true)),
        Map.entry(ChatFormatting.UNDERLINE, new Data(-1, null, true)),
        Map.entry(ChatFormatting.ITALIC, new Data(-1, null, true)),
        Map.entry(ChatFormatting.RESET, new Data(-1, null, false))));

    private static String cleanName(String name) {
        return name.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
    }

    private static final Map<String, ChatFormatting> FORMATTING_BY_NAME = Arrays.stream(ChatFormatting.values())
        .collect(Collectors.toMap(f -> cleanName(f.name()), f -> f));

    private static final ChatFormatting[] VALUES = ChatFormatting.values();
    private static final Function<String, ChatFormatting> BY_SERIALIZED_NAME = Arrays.stream(VALUES)
        .collect(Collectors.toMap(CbS26ChatFormatting::getSerializedName, f -> f))::get;

    private static final Codec<ChatFormatting> CODEC = ExtraCodecs.orCompressed(
        Codec.stringResolver(CbS26ChatFormatting::getSerializedName, BY_SERIALIZED_NAME),
        ExtraCodecs.idResolverCodec(Enum::ordinal, i -> i >= 0 && i < VALUES.length ? VALUES[i] : null, -1));
    private static final Codec<ChatFormatting> COLOR_CODEC = CODEC.validate(f -> isFormat(f)
        ? DataResult.error(() -> "Formatting was not a valid color: " + f) : DataResult.success(f));

    public static Codec<ChatFormatting> CODEC() {
        return CODEC;
    }

    public static Codec<ChatFormatting> COLOR_CODEC() {
        return COLOR_CODEC;
    }

    public static char getChar(ChatFormatting f) {
        return f.code;
    }

    public static int getId(ChatFormatting f) {
        return DATA.get(f).id();
    }

    public static boolean isFormat(ChatFormatting f) {
        return DATA.get(f).format();
    }

    public static boolean isColor(ChatFormatting f) {
        return !isFormat(f) && f != ChatFormatting.RESET;
    }

    public static Integer getColor(ChatFormatting f) {
        return DATA.get(f).color();
    }

    public static String getName(ChatFormatting f) {
        return f.name().toLowerCase(Locale.ROOT);
    }

    public static String getSerializedName(ChatFormatting f) {
        return getName(f);
    }

    public static ChatFormatting getByName(String name) {
        return name == null ? null : FORMATTING_BY_NAME.get(cleanName(name));
    }

    public static ChatFormatting getById(int id) {
        if (id < 0) {
            return ChatFormatting.RESET;
        }
        for (ChatFormatting f : ChatFormatting.values()) {
            if (getId(f) == id) {
                return f;
            }
        }
        return null;
    }

    public static Collection<String> getNames(boolean colors, boolean formats) {
        List<String> out = Lists.newArrayList();
        for (ChatFormatting f : ChatFormatting.values()) {
            if ((!isColor(f) || colors) && (!isFormat(f) || formats)) {
                out.add(getName(f));
            }
        }
        return out;
    }
}
