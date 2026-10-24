package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderCtors;

/** ItemInHandRenderer's ItemRenderer, for the layer constructors 1.21.2 hands it to (CbRenderCtors) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_759.class)
public abstract class CbRenderItemInHandAccess implements CbRenderCtors.ItemInHand {

    @Shadow
    @Final
    private net.minecraft.class_918 field_4044;

    @Override
    public net.minecraft.class_918 cb$itemRenderer() {
        return this.field_4044;
    }
}
