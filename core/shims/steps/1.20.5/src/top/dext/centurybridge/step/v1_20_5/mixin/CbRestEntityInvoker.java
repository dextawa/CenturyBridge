package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * 1.20.5's Entity.getPassengerAttachmentPoint(passenger, dimensions, scale) -- answering a Vec3 -- for
 * mixin/CbRestEntityMixin, whose 1.20.4 cover of the same name and parameters answers a Vector3f (one
 * Java class cannot declare both). The invoker calls it virtually. The entity's random (protected) for
 * covers on its subclasses (mixin/CbRestPlayerMixin).
 */
@Mixin(net.minecraft.class_1297.class)
public interface CbRestEntityInvoker {

    @Accessor("field_5974")
    net.minecraft.class_5819 cbRest$random();

    @Invoker("method_52533")
    net.minecraft.class_243 cbRest$attachmentPoint(net.minecraft.class_1297 passenger, net.minecraft.class_4048 dimensions, float scale);
}
