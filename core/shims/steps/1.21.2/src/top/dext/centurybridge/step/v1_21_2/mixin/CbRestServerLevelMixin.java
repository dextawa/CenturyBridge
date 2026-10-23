package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_3218;
import net.minecraft.class_3695;
import net.minecraft.class_8921;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestServer;

/**
 * 1.21.1's ServerLevel discarded, at the start of each entity's tick, an animal while the server did not
 * spawn animals and an npc while npcs were off (shouldDiscardEntity: MinecraftServer.isSpawningAnimals /
 * areNpcsEnabled, which a 1.21.1 server subclass or the dedicated server's spawn-animals / spawn-npcs
 * properties decide). 1.21.2 removed the check with the switches; this is it again, on the entity tick
 * lambda that 1.21.2's tick runs per entity (method_31420), before anything else of the tick -- where
 * 1.21.1 made it. With the switches at their default (true) it discards nothing.
 */
@Mixin(class_3218.class)
public abstract class CbRestServerLevelMixin {

    @Shadow
    @Final
    private MinecraftServer field_13959;

    @Inject(method = "method_31420(Lnet/minecraft/class_8921;Lnet/minecraft/class_3695;Lnet/minecraft/class_1297;)V",
        at = @At("HEAD"), cancellable = true)
    private void cb$discardBySpawnFlags(class_8921 tickRate, class_3695 profiler, class_1297 entity, CallbackInfo ci) {
        if (!entity.method_31481() && CbRestServer.shouldDiscard(this.field_13959, entity)) {
            entity.method_31472();
            ci.cancel();
        }
    }
}
