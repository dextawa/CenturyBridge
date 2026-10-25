package cb.s5c;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.ModInitializer;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1810;
import net.minecraft.class_1829;
import net.minecraft.class_1836;
import net.minecraft.class_1937;
import net.minecraft.class_2378;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_5321;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import net.minecraft.class_9886;

/** The mod's items (1.21.4 classes): a SwordItem subclass with s5's overrides and a PickaxeItem with none. */
public class S5cItems implements ModInitializer {
    /** one inventoryTick call: side, slot, selected, whether the holder is a player */
    public record Tick(boolean client, int slot, boolean selected, boolean player) {
        @Override
        public String toString() {
            return (client ? "C" : "S") + "(" + slot + "," + selected + (player ? "" : ",nonplayer") + ")";
        }
    }

    public static final List<Tick> TICKS = new ArrayList<>();
    public static volatile boolean breakingSoundAsked;
    public static class_1792 SWORD;
    public static class_1792 PICK;

    public static class TestSword extends class_1829 {
        public TestSword(class_1792.class_1793 props) {
            super(class_9886.field_52588, 3.0F, -2.4F, props);
        }

        @Override
        public void method_7851(class_1799 stack, class_1792.class_9635 ctx, List<class_2561> lines, class_1836 flag) {
            super.method_7851(stack, ctx, lines, flag);
            lines.add(class_2561.method_43470("cb-s5 tooltip"));
        }

        @Override
        public void method_7888(class_1799 stack, class_1937 level, class_1297 entity, int slot, boolean selected) {
            synchronized (TICKS) {
                TICKS.add(new Tick(level.field_9236, slot, selected, entity instanceof class_1657));
            }
            super.method_7888(stack, level, entity, slot, selected);
        }

        @Override
        public class_3414 method_57336() {
            breakingSoundAsked = true;
            return class_3417.field_14833; // ANVIL_LAND
        }
    }

    public static class TestPick extends class_1810 {
        public TestPick(class_1792.class_1793 props) {
            super(class_9886.field_52587, 1.0F, -2.8F, props);
        }
    }

    @Override
    public void onInitialize() {
        class_5321<class_1792> sk = class_5321.method_29179(class_7924.field_41197, class_2960.method_60655("cb_s5c", "sword"));
        SWORD = class_2378.method_39197(class_7923.field_41178, sk, new TestSword(new class_1792.class_1793().method_63686(sk)));
        class_5321<class_1792> pk = class_5321.method_29179(class_7924.field_41197, class_2960.method_60655("cb_s5c", "pick"));
        PICK = class_2378.method_39197(class_7923.field_41178, pk, new TestPick(new class_1792.class_1793().method_63686(pk)));
    }
}
