package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1058;
import net.minecraft.class_2350;
import net.minecraft.class_2960;
import net.minecraft.class_3665;
import net.minecraft.class_777;
import net.minecraft.class_783;
import net.minecraft.class_789;
import net.minecraft.class_796;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21.rt.CbRenderModelBake;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * FaceBakery.bakeQuad with 1.20.6's trailing model id, given back so a 1.20.6 mod's calls, overrides and super
 * calls link (covers in s6fin-client.json): 1.21's bakeQuad, the id dropped -- vanilla 1.20.6 used it only to name the
 * model in the UV-lock warning, which 1.21 no longer prints. A converted mod's injector here follows smx6.json's
 * mixinTargets onto 1.21's bakeQuad (its body is the one 1.21 bakes with) unless its handler reads the id; such an
 * injector stays on this member, and while one does (CbMixedMembers) 1.21's bakeFace bakes every face through it with
 * the id of the model being baked (CbRenderFaceBakeryRouteMixin), as 1.20.6's bakeFace did. Not marked
 * CbVanillaReads: the marker would keep every selector here, the ones mixinTargets carries into 1.21's body too.
 */
@Mixin(value = class_796.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderFaceBakeryMixin implements CbRenderModelBake.LegacyFaceBakery {

    @Shadow
    public abstract class_777 method_3468(Vector3f from, Vector3f to, class_783 face, class_1058 sprite, class_2350 direction, class_3665 state,
                                          class_789 rotation, boolean shade);

    @Override
    public class_777 method_3468(Vector3f from, Vector3f to, class_783 face, class_1058 sprite, class_2350 direction, class_3665 state,
                                 class_789 rotation, boolean shade, class_2960 id) {
        return this.method_3468(from, to, face, sprite, direction, state, rotation, shade);
    }
}
