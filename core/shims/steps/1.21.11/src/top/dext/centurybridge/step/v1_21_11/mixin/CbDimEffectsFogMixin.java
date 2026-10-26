package top.dext.centurybridge.step.v1_21_11.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11398;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import net.minecraft.class_7285;
import net.minecraft.class_9779;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_11.rt.CbDimensionEffectsRoutes;

/**
 * AtmosphericFogEnvironment, 1.21.11's air fog (1.21.10's AirBasedFogEnvironment / DimensionOrBossFogEnvironment), with
 * 1.21.10's effects consulted where 1.21.10 read them (rt/CbDimensionEffectsRoutes; otherwise 1.21.11 exactly). Every
 * hook sits on one of 1.21.11's own calls (an argument it is handed), never on a local slot:
 * <ul>
 * <li>getBaseColor: the fog color (1.21.11's FOG_COLOR attribute, where its two blends take it; 1.21.10
 * getBrightnessDependentFogColor of the biome fog) and the sunrise tint (1.21.11's SUNRISE_SUNSET_COLOR, where
 * ARGB.alphaFloat and ARGB.opaque take it; 1.21.10 isSunriseOrSunset / getSunriseOrSunsetColor).</li>
 * <li>setupFog: 1.21.10 drew a dimension whose effects are foggy at the camera's column with the dimension-or-boss
 * fog; 1.21.11 draws that fog for the boss overlay only, at the end of setupFog (bytecode 184-238: start
 * min(start, 10), end min(end, 96), sky and cloud end = end). When the boss flag is false and 1.21.10's isFoggyAt
 * says foggy, the same four lines run after 1.21.11's body.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
@Mixin(class_11398.class)
public abstract class CbDimEffectsFogMixin {
    private static final String BASE_COLOR = "method_71654(Lnet/minecraft/class_638;Lnet/minecraft/class_4184;IF)I";

    /** this getBaseColor call's level when its effects are consulted (render thread), else null; its camera, tick */
    @Unique
    private class_638 cb$level;
    @Unique
    private class_4184 cb$camera;
    @Unique
    private float cb$partialTick;
    @Unique
    private int cb$sunrise;
    /** whether the sunrise blend (the first srgbLerp) already took 1.21.10's fog color */
    @Unique
    private boolean cb$fogTaken;

    @Inject(method = BASE_COLOR, at = @At("HEAD"))
    private void cb$consult(class_638 level, class_4184 camera, int renderDistance, float partialTick,
            CallbackInfoReturnable<Integer> cir) {
        boolean consulted = CbDimensionEffectsRoutes.consulted(level, CbDimensionEffectsRoutes.FOG_COLOR) != null;
        this.cb$level = consulted ? level : null;
        this.cb$camera = camera;
        this.cb$partialTick = partialTick;
        this.cb$fogTaken = false;
    }

    /**
     * the fog color: 1.21.11 reads FOG_COLOR and blends it first with the sunrise tint (ARGB.srgbLerp, first call,
     * only when there is a tint), then with the sky color (second call); 1.21.10's color goes in at whichever of the two
     * takes the attribute's value
     */
    @ModifyArg(method = BASE_COLOR,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_9848;method_61319(FII)I", ordinal = 0), index = 1)
    private int cb$fogAtSunrise(int own) {
        if (this.cb$level == null) {
            return own;
        }
        this.cb$fogTaken = true;
        return CbDimensionEffectsRoutes.fogColor(this.cb$level, this.cb$camera, this.cb$partialTick, own);
    }

    @ModifyArg(method = BASE_COLOR,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_9848;method_61319(FII)I", ordinal = 1), index = 1)
    private int cb$fogAtSky(int own) {
        if (this.cb$level == null || this.cb$fogTaken) {
            return own;
        }
        return CbDimensionEffectsRoutes.fogColor(this.cb$level, this.cb$camera, this.cb$partialTick, own);
    }

    /**
     * the sunrise tint: 1.21.11 reads SUNRISE_SUNSET_COLOR only when the camera faces the sun (1.21.10 asked
     * isSunriseOrSunset only then) and hands it to ARGB.alphaFloat, then, when that alpha is above 0, to ARGB.opaque
     * for the blend; 1.21.10's answer is asked once, at the first
     */
    @ModifyArg(method = BASE_COLOR,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_9848;method_65100(I)F", ordinal = 0))
    private int cb$sunriseAlpha(int own) {
        if (this.cb$level == null) {
            return own;
        }
        this.cb$sunrise = CbDimensionEffectsRoutes.fogSunriseColor(this.cb$level, this.cb$camera, this.cb$partialTick, own);
        return this.cb$sunrise;
    }

    @ModifyArg(method = BASE_COLOR,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_9848;method_61334(I)I", ordinal = 0))
    private int cb$sunriseColor(int own) {
        return this.cb$level == null ? own : this.cb$sunrise;
    }

    @Inject(method = "method_42591(Lnet/minecraft/class_7285;Lnet/minecraft/class_4184;Lnet/minecraft/class_638;FLnet/minecraft/class_9779;)V",
        at = @At("RETURN"))
    private void cb$dimensionFog(class_7285 fog, class_4184 camera, class_638 level, float renderDistance, class_9779 delta,
            CallbackInfo ci) {
        // 1.21.10's answer first: without a consulted effects object nothing below runs (no extra vanilla call)
        if (!CbDimensionEffectsRoutes.worldFog(level, camera, false)
                || class_310.method_1551().field_1705.method_1740().method_1800()) {
            return; // not foggy by 1.21.10's effects, or 1.21.11's own boss branch already ran
        }
        fog.field_60582 = Math.min(fog.field_60582, 10.0F);
        fog.field_60584 = Math.min(fog.field_60584, 96.0F);
        fog.field_60099 = fog.field_60584;
        fog.field_60100 = fog.field_60584;
    }
}
