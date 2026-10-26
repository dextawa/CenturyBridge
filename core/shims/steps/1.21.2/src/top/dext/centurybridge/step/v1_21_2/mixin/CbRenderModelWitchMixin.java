package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderModelFields;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1 WitchModel.setHoldingItem(boolean): sets the model's holdingItem, which its setupAnim read (the nose pose).
 * 1.21.2 reads WitchRenderState.isHoldingItem instead: the flag is kept on the model and carried into the state while
 * a 1.21.1 call poses it (CbRenderModelFields).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_622.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderModelWitchMixin implements CbRenderModelFields.Witch {

    @Unique
    private boolean cb$holdingItem;

    public void method_2840(boolean holdingItem) {
        this.cb$holdingItem = holdingItem;
    }

    @Override
    public void cb$holdingItem(boolean holdingItem) {
        this.cb$holdingItem = holdingItem;
    }

    @Override
    public boolean cb$holdingItem() {
        return this.cb$holdingItem;
    }
}
