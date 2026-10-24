package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import top.dext.centurybridge.step.v1_21_4.rt.CbRenderLayers;

/** PlayerItemInHandLayer's protected renderArmWithItem (1.21.4 shape), for a 1.21.3 call of the old shape (CbRenderLayers) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_5697.class)
public interface CbLayerPlayerItemInHandAccess extends CbRenderLayers.CbPlayerArmWithItem {
    @Override
    @Invoker("method_62594")
    void cb$renderArmWithItem(net.minecraft.class_10055 state, net.minecraft.class_10444 item, net.minecraft.class_1306 arm, net.minecraft.class_4587 pose,
            net.minecraft.class_4597 buffers, int light);
}
