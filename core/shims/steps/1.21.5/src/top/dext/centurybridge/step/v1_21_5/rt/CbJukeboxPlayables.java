package top.dext.centurybridge.step.v1_21_5.rt;

import com.google.common.collect.MapMaker;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import net.minecraft.class_10712;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1836;
import net.minecraft.class_2561;
import net.minecraft.class_9323;
import net.minecraft.class_9331;
import net.minecraft.class_9334;
import net.minecraft.class_9791;
import net.minecraft.class_9792;
import net.minecraft.class_9793;

/**
 * 1.21.4's JukeboxPlayable(song, showInTooltip) is JukeboxPlayable(song) since 1.21.5: whether the component's tooltip
 * line shows is the stack's TooltipDisplay (TOOLTIP_DISPLAY hiding JUKEBOX_PLAYABLE; ItemStack.addToTooltip skips a
 * hidden component, javap 1.21.5 class_1799.method_57369), no longer the value's own flag (1.21.4 class_9792.method_57409
 * printed only when showInTooltip).
 * <ul>
 * <li>{@code new JukeboxPlayable(song, false)} builds 1.21.5's value and remembers it (by identity) as hidden;
 *     showInTooltip() answers that, withTooltip(b) builds a value marked by b.</li>
 * <li>A 1.21.4 mod hands its value to 1.21.5 through the component setters (Item.Properties.component, ItemStack.set /
 *     update): beside a marked value they put TOOLTIP_DISPLAY hiding JUKEBOX_PLAYABLE, beside an unmarked one they
 *     show it again, as 1.21.4's flag travelled with the value. The setters themselves are vanilla's, so native mixins
 *     on them still run. The TooltipDisplay persists with the stack (saved, synced).</li>
 * <li>1.21.4's addToTooltip(context, lines, flag) on such a value prints only when it shows.</li>
 * </ul>
 * Loss: a value read back from a stack (saved and loaded, or copied by vanilla) is not the instance the mod built, so
 * its showInTooltip() answers true even when the stack hides it.
 */
public final class CbJukeboxPlayables {

    /** values a 1.21.4 mod built with showInTooltip false */
    private static final Set<class_9792> HIDDEN = Collections.newSetFromMap(new MapMaker().weakKeys().makeMap());
    /** the TooltipDisplay each converted mod's Item.Properties carries (1.21.4 code cannot set one itself) */
    private static final Map<class_1792.class_1793, class_10712> DISPLAYS = new MapMaker().weakKeys().makeMap();

    private CbJukeboxPlayables() {
    }

    /** new JukeboxPlayable(song, showInTooltip) */
    public static class_9792 newJukeboxPlayable(class_9791<class_9793> song, boolean showInTooltip) {
        class_9792 value = new class_9792(song);
        if (!showInTooltip) {
            HIDDEN.add(value);
        }
        return value;
    }

    /** JukeboxPlayable.showInTooltip() */
    public static boolean showInTooltip(class_9792 value) {
        return !HIDDEN.contains(value);
    }

    /** JukeboxPlayable.withTooltip(showInTooltip) */
    public static class_9792 withTooltip(class_9792 value, boolean showInTooltip) {
        return newJukeboxPlayable(value.comp_2833(), showInTooltip);
    }

    /** 1.21.4's JukeboxPlayable.addToTooltip(context, lines, flag): 1.21.5's body, only when the value shows */
    public static void addToTooltip(class_9792 value, class_1792.class_9635 context, Consumer<class_2561> lines, class_1836 flag) {
        if (showInTooltip(value)) {
            value.method_57409(context, lines, flag, class_9323.field_49584);
        }
    }

    private static boolean hides(class_9331<?> type, Object value) {
        return type == class_9334.field_52175 && value instanceof class_9792 jp && !showInTooltip(jp);
    }

    /** Item.Properties.component(type, value) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_1792.class_1793 component(class_1792.class_1793 properties, class_9331 type, Object value) {
        properties.method_57349(type, value);
        if (type == class_9334.field_52175 && value instanceof class_9792) {
            class_10712 display;
            boolean had;
            synchronized (DISPLAYS) {
                had = DISPLAYS.containsKey(properties);
                display = DISPLAYS.getOrDefault(properties, class_10712.field_56318).method_67215(type, hides(type, value));
                DISPLAYS.put(properties, display);
            }
            // an item whose value shows keeps 1.21.5's default components (no TooltipDisplay of its own)
            if (had || !display.equals(class_10712.field_56318)) {
                properties.method_57349(class_9334.field_56400, display);
            }
        }
        return properties;
    }

    /** the stack's TooltipDisplay hides JUKEBOX_PLAYABLE exactly when the value handed in does */
    private static void display(class_1799 stack, class_9331<?> type, Object value) {
        if (type == class_9334.field_52175 && value instanceof class_9792) {
            class_10712 old = stack.method_58695(class_9334.field_56400, class_10712.field_56318);
            class_10712 now = old.method_67215(type, hides(type, value));
            if (!now.equals(old)) {
                stack.method_57379(class_9334.field_56400, now);
            }
        }
    }

    /** ItemStack.set(type, value) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object set(class_1799 stack, class_9331 type, Object value) {
        Object old = stack.method_57379(type, value);
        display(stack, type, value);
        return old;
    }

    /** ItemStack.update(type, default, op) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object update(class_1799 stack, class_9331 type, Object fallback, UnaryOperator op) {
        Object now = stack.method_57368(type, fallback, op);
        display(stack, type, now);
        return now;
    }

    /** ItemStack.update(type, default, arg, op) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object update(class_1799 stack, class_9331 type, Object fallback, Object arg, BiFunction op) {
        Object now = stack.method_57367(type, fallback, arg, op);
        display(stack, type, now);
        return now;
    }
}
