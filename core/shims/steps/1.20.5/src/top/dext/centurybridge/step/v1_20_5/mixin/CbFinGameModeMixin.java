package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * MultiPlayerGameMode: 1.20.4's getPickRange() (5 blocks in creative, 4.5 otherwise) and hasFarPickRange()
 * (creative), which 1.20.5 replaced with the player's interaction range attributes; 1.20.4's bodies.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_636.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbFinGameModeMixin {

    @Shadow
    private net.minecraft.class_1934 field_3719;

    /** was getPickRange() */
    public float method_2904() {
        return this.field_3719.method_8386() ? 5.0F : 4.5F;
    }

    /** was hasFarPickRange() */
    public boolean method_2926() {
        return this.field_3719.method_8386();
    }
}
