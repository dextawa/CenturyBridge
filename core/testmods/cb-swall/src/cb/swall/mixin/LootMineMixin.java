package cb.swall.mixin;

import cb.swall.SwallTestMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.21.11's private LootCommand.dropBlockLoot(ctx, pos, tool, callback) (class_3039.method_13219), as 1.21.11-era mods
 * mixed it: counts "/loot ... mine" runs. No captured arguments (the callback type class_3041 is private). On 26.1 the
 * selector follows dropBlockLoot(.., ItemInstance, ..) (callReshapes + mixinTargets).
 */
@Mixin(net.minecraft.class_3039.class)
public abstract class LootMineMixin {
    @Inject(method = "method_13219(Lcom/mojang/brigadier/context/CommandContext;Lnet/minecraft/class_2338;Lnet/minecraft/class_1799;Lnet/minecraft/class_3039$class_3041;)I",
        at = @At("HEAD"), remap = false)
    private static void cbswall$countMine(CallbackInfoReturnable<Integer> cir) {
        SwallTestMod.LOOT_MINE_CALLS.incrementAndGet();
    }
}
