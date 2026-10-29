package top.dext.centurybridge.step.v1_21.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_1058;
import net.minecraft.class_2350;
import net.minecraft.class_3665;
import net.minecraft.class_777;
import net.minecraft.class_783;
import net.minecraft.class_789;
import net.minecraft.class_796;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.dext.centurybridge.step.v1_21.rt.CbMixedMembers;
import top.dext.centurybridge.step.v1_21.rt.CbRenderModelBake;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * BlockModel.bakeFace (class_793.method_3447): 1.21's one call of FaceBakery.bakeQuad, where 1.20.6 called
 * bakeQuad(.., id) with the id of the model being baked. When a converted mod mixes the restored bakeQuad(.., id)
 * (CbRenderFaceBakeryMixin; CbMixedMembers), the call runs that member with the id 1.21's model baker is baking
 * (CbRenderModelBake.bakingId, set by CbRenderModelBakerMixin around each model's bake); it bakes through 1.21's
 * bakeQuad, so a native 1.21 mod's injectors in bakeQuad still run once per face. Unmixed, 1.21's call runs untouched.
 * Priority CbVanillaReads.ROUTE_FIRST: the innermost wrapper of the call, so a native mod's @WrapOperation of it wraps
 * the route and runs either way.
 */
@Mixin(value = net.minecraft.class_793.class, priority = CbVanillaReads.ROUTE_FIRST)
public abstract class CbRenderFaceBakeryRouteMixin {

    @WrapOperation(method = "method_3447(Lnet/minecraft/class_785;Lnet/minecraft/class_783;Lnet/minecraft/class_1058;Lnet/minecraft/class_2350;Lnet/minecraft/class_3665;)Lnet/minecraft/class_777;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_796;method_3468(Lorg/joml/Vector3f;Lorg/joml/Vector3f;Lnet/minecraft/class_783;Lnet/minecraft/class_1058;Lnet/minecraft/class_2350;Lnet/minecraft/class_3665;Lnet/minecraft/class_789;Z)Lnet/minecraft/class_777;"))
    private static class_777 cb$v1_21$bakeQuad(class_796 bakery, Vector3f from, Vector3f to, class_783 face, class_1058 sprite, class_2350 direction,
                                               class_3665 state, class_789 rotation, boolean shade, Operation<class_777> original) {
        if (CbMixedMembers.mixed(CbRenderModelBake.BAKE_QUAD)) {
            return ((CbRenderModelBake.LegacyFaceBakery) bakery).method_3468(from, to, face, sprite, direction, state, rotation, shade,
                CbRenderModelBake.bakingId());
        }
        return original.call(bakery, from, to, face, sprite, direction, state, rotation, shade);
    }
}
