package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.serialization.Dynamic;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * NBT statics 26.1 dropped or made instance members:
 * <ul>
 * <li>NbtUtils.addCurrentDataVersion(Dynamic): 1.21.11's body, addDataVersion(dynamic, the current data version)
 * (26.1 keeps addDataVersion and the CompoundTag form).</li>
 * <li>TextComponentTagVisitor.handleEscapePretty(text), a protected static in 1.21.11: a key as aqua text when it is a
 * simple value, else quoted with its quotes plain and its inside aqua. 26.1's is a private instance method styled by
 * the visitor's own styling; 1.21.11's fixed styling is kept here, with 1.21.11's pattern (26.1's is the same).</li>
 * </ul>
 */
public final class CbWallNbt {

    /** 1.21.11 TextComponentTagVisitor.SIMPLE_VALUE */
    private static final Pattern SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");

    private CbWallNbt() {
    }

    /** 1.21.11 NbtUtils.addCurrentDataVersion(dynamic) */
    public static <T> Dynamic<T> addCurrentDataVersion(Dynamic<T> dynamic) {
        return NbtUtils.addDataVersion(dynamic, SharedConstants.getCurrentVersion().dataVersion().version());
    }

    /** 1.21.11 TextComponentTagVisitor.handleEscapePretty(text) (its key colour was ChatFormatting.AQUA) */
    public static Component handleEscapePretty(String text) {
        if (SIMPLE_VALUE.matcher(text).matches()) {
            return Component.literal(text).withStyle(ChatFormatting.AQUA);
        }
        String quoted = StringTag.quoteAndEscape(text);
        String quote = quoted.substring(0, 1);
        MutableComponent inner = Component.literal(quoted.substring(1, quoted.length() - 1)).withStyle(ChatFormatting.AQUA);
        return Component.literal(quote).append(inner).append(quote);
    }
}
