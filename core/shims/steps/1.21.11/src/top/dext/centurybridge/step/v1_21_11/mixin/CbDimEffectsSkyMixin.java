package top.dext.centurybridge.step.v1_21_11.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12076;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import net.minecraft.class_9975;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_11.rt.CbDimensionEffectsRoutes;
import top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads;

/**
 * SkyRenderer.extractRenderState: 1.21.10 set the state's sky type from effects().skyType() and its sunrise color
 * from isSunriseOrSunset / getSunriseOrSunsetColor; 1.21.11 sets skybox from dimensionType().skybox() and
 * sunriseAndSunsetColor from the SUNRISE_SUNSET_COLOR attribute. Right after each of 1.21.11's writes, 1.21.10's
 * answer may replace it (rt/CbDimensionEffectsRoutes); the rest of the method (the early return for NONE, the end
 * flash for END) reads the field as written.
 * Route order: CbVanillaReads.ROUTE_FIRST, so a native 1.21.11 mod's injector at the same point runs after the route and
 * sees (and may change) the routed value instead of having its own replaced.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = class_9975.class, priority = CbVanillaReads.ROUTE_FIRST)
public abstract class CbDimEffectsSkyMixin {
    private static final String EXTRACT =
        "method_74926(Lnet/minecraft/class_638;FLnet/minecraft/class_4184;Lnet/minecraft/class_12076;)V";

    @Inject(method = EXTRACT, at = @At(value = "FIELD", opcode = org.objectweb.asm.Opcodes.PUTFIELD,
        target = "Lnet/minecraft/class_12076;field_64464:Lnet/minecraft/class_2874$class_12326;", shift = At.Shift.AFTER))
    private void cb$skybox(class_638 level, float partialTick, class_4184 camera, class_12076 state, CallbackInfo ci) {
        state.field_64464 = CbDimensionEffectsRoutes.skybox(level, state.field_64464);
    }

    @Inject(method = EXTRACT, at = @At(value = "FIELD", opcode = org.objectweb.asm.Opcodes.PUTFIELD,
        target = "Lnet/minecraft/class_12076;field_63095:I", shift = At.Shift.AFTER))
    private void cb$sunrise(class_638 level, float partialTick, class_4184 camera, class_12076 state, CallbackInfo ci) {
        state.field_63095 = CbDimensionEffectsRoutes.skySunriseColor(level, state.field_63091, state.field_63095);
    }
}
