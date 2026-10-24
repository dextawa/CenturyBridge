package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.21.1 ParrotModel.renderOnShoulder(poseStack, consumer, light, overlay, limbSwing, limbSwingAmount, netHeadYaw,
 * headPitch, tickCount), gone in 1.21.2 (ParrotOnShoulderLayer poses the model through a render state), put back with
 * 1.21.1's body: the shoulder pose set up from those values with no wing flap (1.21.1's setupAnim(ON_SHOULDER, tickCount,
 * limbSwing, limbSwingAmount, 0, netHeadYaw, headPitch); 1.21.2's setupAnim reads them from a ParrotRenderState, filled
 * as ParrotOnShoulderLayer fills its own), then the whole model drawn.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_584.class)
public abstract class CbRenderParrotShoulderMixin {

    /** 1.21.1 ParrotModel.renderOnShoulder */
    public void method_17106(net.minecraft.class_4587 poseStack, net.minecraft.class_4588 consumer, int light, int overlay, float limbSwing,
                             float limbSwingAmount, float netHeadYaw, float headPitch, int tickCount) {
        net.minecraft.class_584 model = (net.minecraft.class_584) (Object) this;
        net.minecraft.class_10051 state = new net.minecraft.class_10051();
        state.field_53512 = net.minecraft.class_584.class_585.field_3464;
        state.field_53328 = (float) tickCount;
        state.field_53450 = limbSwing;
        state.field_53451 = limbSwingAmount;
        state.field_53511 = 0.0F;
        state.field_53448 = netHeadYaw;
        state.field_53447 = headPitch;
        model.method_17112(state);
        model.method_63512().method_22698(poseStack, consumer, light, overlay);
    }
}
