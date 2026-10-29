package cb.smx6c.mixin;

import cb.smx6c.Counters;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.6's FaceBakery.bakeQuad(from, to, face, sprite, direction, state, rotation, shade, modelLocation): HEAD counts
 * the calls and records the location; RETURN replaces the quads baked for minecraft:block/stone and
 * minecraft:block/stone_mirrored (the stone blockstate's weighted variants) by ones with the same sprite, direction, tint
 * index and shade and an all-zero vertex array of the same length (stone draws no faces).
 */
@Mixin(class_796.class)
public abstract class FaceBakeryMixin {
    private static final String BAKE = "method_3468(Lorg/joml/Vector3f;Lorg/joml/Vector3f;Lnet/minecraft/class_783;"
        + "Lnet/minecraft/class_1058;Lnet/minecraft/class_2350;Lnet/minecraft/class_3665;Lnet/minecraft/class_789;"
        + "ZLnet/minecraft/class_2960;)Lnet/minecraft/class_777;";

    @Inject(method = BAKE, at = @At("HEAD"))
    private void cb$count(Vector3f from, Vector3f to, class_783 face, class_1058 sprite, class_2350 dir, class_3665 state,
                          class_789 rot, boolean shade, class_2960 location, CallbackInfoReturnable<class_777> cir) {
        Counters.CALLS.incrementAndGet();
        if (location == null) {
            Counters.NULLS.incrementAndGet();
        } else {
            Counters.LOCATIONS.merge(location.toString(), 1, Integer::sum);
        }
    }

    /** a handler that never reads the id: smx6.json's mixinTargets moves it onto 1.21's bakeQuad; still once per face */
    @Inject(method = BAKE, at = @At("HEAD"))
    private void cb$countNoId(CallbackInfoReturnable<class_777> cir) {
        Counters.CALLS_NO_ID.incrementAndGet();
    }

    @Inject(method = BAKE, at = @At("RETURN"), cancellable = true)
    private void cb$hideStone(Vector3f from, Vector3f to, class_783 face, class_1058 sprite, class_2350 dir, class_3665 state,
                              class_789 rot, boolean shade, class_2960 location, CallbackInfoReturnable<class_777> cir) {
        if (location != null && ("minecraft:block/stone".equals(location.toString())
                || "minecraft:block/stone_mirrored".equals(location.toString()))) {
            class_777 q = cir.getReturnValue();
            cir.setReturnValue(new class_777(new int[q.method_3357().length], q.method_3359(), q.method_3358(), q.method_35788(), q.method_24874()));
            Counters.REPLACED.incrementAndGet();
        }
    }
}
