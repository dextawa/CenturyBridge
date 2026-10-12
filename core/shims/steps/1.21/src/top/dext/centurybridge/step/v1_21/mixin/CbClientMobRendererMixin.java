package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21.rt.CbClientModels;

/**
 * 1.20.6's MobRenderer declared shouldRender(mob, ...) and render(mob, ...) (LivingEntityRenderer's, plus the
 * leash); 1.21's draws and culls the leash of any Leashable in EntityRenderer and MobRenderer declares
 * neither. A 1.20.6 subclass's super.shouldRender / super.render reach what MobRenderer inherits now.
 */
@Mixin(net.minecraft.class_927.class)
public abstract class CbClientMobRendererMixin<T extends net.minecraft.class_1308, M extends net.minecraft.class_583<T>>
        extends net.minecraft.class_922<T, M> implements CbClientModels.CbMobRendererSuper {

    protected CbClientMobRendererMixin(net.minecraft.class_5617.class_5618 context, M model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean cb$superShouldRender(net.minecraft.class_1308 mob, net.minecraft.class_4604 frustum, double x, double y, double z) {
        return super.method_3933((T) mob, frustum, x, y, z);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void cb$superRender(net.minecraft.class_1308 mob, float yaw, float partialTick, net.minecraft.class_4587 pose,
                               net.minecraft.class_4597 buffers, int light) {
        super.method_4054((T) mob, yaw, partialTick, pose, buffers, light);
    }
}
