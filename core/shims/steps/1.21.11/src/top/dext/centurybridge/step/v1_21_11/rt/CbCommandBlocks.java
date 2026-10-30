package top.dext.centurybridge.step.v1_21_11.rt;

import net.minecraft.class_1918;
import net.minecraft.class_2165;
import net.minecraft.class_2168;
import net.minecraft.class_3218;

/**
 * 1.21.10 BaseCommandBlock's level: getLevel() (abstract; CommandBlockEntity's source answered its block entity's level,
 * MinecartCommandBase its minecart's), which 1.21.11 dropped when onUpdated / createCommandSourceStack started taking
 * the ServerLevel (javap -c both). The layer gives getLevel back (mixin/CbCommandBlockRestoredMixin and the two vanilla
 * source restorers). A converted call of the old onUpdated() / createCommandSourceStack(source) reaches 1.21.11's with
 * the level 1.21.10's own bodies read through getLevel() (instanceRedirects); the same level is the frameDefaults
 * judgment for an old override's super call entered without a frame.
 */
public final class CbCommandBlocks {
    private CbCommandBlocks() {
    }

    /** implemented by CbCommandBlockRestoredMixin: a virtual call of the restored getLevel() */
    public interface Level {
        class_3218 cb$v1_21_11$level();
    }

    /** BaseCommandBlock.getLevel() of the block, as 1.21.10 asked it */
    public static class_3218 level(class_1918 block) {
        return ((Level) block).cb$v1_21_11$level();
    }

    /** BaseCommandBlock.onUpdated() */
    public static void method_8295(class_1918 block) {
        block.method_8295(level(block));
    }

    /** BaseCommandBlock.createCommandSourceStack(source) */
    public static class_2168 method_8303(class_1918 block, class_2165 source) {
        return block.method_8303(level(block), source);
    }
}
