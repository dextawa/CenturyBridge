package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_5.rt.CbRestEntityApi;

/**
 * Where 1.20.4's EntityRenderer.renderNameTag put the name: getNameTagOffsetY() above the entity, which
 * mods override. 1.20.5 puts it at the name tag attachment (plus 0.5). The height comes from the entity's
 * getNameTagOffsetY again -- the cover (mixin/CbRestEntityMixin) answers 1.20.5's own height, so only a
 * 1.20.4 override moves it; across, the attachment stays where 1.20.5 has it.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_897.class)
public abstract class CbRestEntityRendererMixin {

    @Redirect(method = "method_3926", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_9066;method_55675(Lnet/minecraft/class_9064;IF)Lnet/minecraft/class_243;"))
    private net.minecraft.class_243 cbRest$nameTag(net.minecraft.class_9066 attachments, net.minecraft.class_9064 attachment, int index,
                                                   float yRot, net.minecraft.class_1297 entity, net.minecraft.class_2561 name,
                                                   net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light,
                                                   float partialTick) {
        net.minecraft.class_243 at = attachments.method_55675(attachment, index, yRot);
        double y = ((CbRestEntityApi) entity).cbRest$nameTagOffsetY() - 0.5D;
        return at == null ? new net.minecraft.class_243(0, y, 0) : new net.minecraft.class_243(at.field_1352, y, at.field_1350);
    }
}
