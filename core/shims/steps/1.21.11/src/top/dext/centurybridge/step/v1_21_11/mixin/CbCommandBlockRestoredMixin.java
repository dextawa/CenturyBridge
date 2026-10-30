package top.dext.centurybridge.step.v1_21_11.mixin;

import net.minecraft.class_1918;
import net.minecraft.class_3218;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_11.rt.CbCommandBlocks;
import top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads;

/**
 * 1.21.10 BaseCommandBlock.getLevel() (abstract there): every 1.21.10 subclass declared it; the two vanilla ones get
 * their 1.21.10 bodies back (CbCommandBlockEntitySourceRestoredMixin, CbMinecartCommandSourceRestoredMixin), a
 * converted mod's subclass keeps its own. A class that never declared it could not be instantiated on 1.21.10: the base
 * answers AbstractMethodError, as the JVM did. 1.21.11 never calls it.
 */
@Mixin(value = class_1918.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbCommandBlockRestoredMixin implements CbCommandBlocks.Level {

    /** BaseCommandBlock.getLevel() */
    public class_3218 method_8293() {
        throw new AbstractMethodError("1.21.10 BaseCommandBlock.getLevel() is abstract: " + this.getClass().getName());
    }

    @Override
    public class_3218 cb$v1_21_11$level() {
        return this.method_8293();
    }
}
