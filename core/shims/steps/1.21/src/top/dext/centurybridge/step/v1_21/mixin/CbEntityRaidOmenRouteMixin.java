package top.dext.centurybridge.step.v1_21.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.dext.centurybridge.step.v1_21.rt.CbMixedMembers;
import top.dext.centurybridge.step.v1_21.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * RaidOmenMobEffect (class_9457) applyEffectTick: Raids.createOrExtendRaid(player, raidOmenPosition) is where 1.21 starts
 * the raid 1.20.6 started from Bad Omen (CbEntityBadOmenRestoredMixin). When a converted mod mixes the restored
 * BadOmenMobEffect.method_58613 (CbMixedMembers), the call runs that member in its place; it calls createOrExtendRaid
 * itself, at the player's block, and its answer stands for the raid made: none when it kept Bad Omen, else the raid at the
 * player's block (ServerLevel.getRaidAt, the raid createOrExtendRaid made or extended there). Unmixed, 1.21's call runs
 * untouched. The route wraps the call (MixinExtras @WrapOperation) at CbVanillaReads.ROUTE_FIRST, the innermost wrapper,
 * so a native 1.21 mod's @WrapOperation of the same call wraps it and keeps running either way; 1.21 clears the Raid Omen
 * position after the call in both cases.
 */
@Mixin(targets = "net.minecraft.class_9457", priority = CbVanillaReads.ROUTE_FIRST)
public abstract class CbEntityRaidOmenRouteMixin {

    private static final String BAD_OMEN_RAID = "net/minecraft/class_8631.method_58613(Lnet/minecraft/class_3222;Lnet/minecraft/class_3218;)Z";

    @WrapOperation(method = "method_5572(Lnet/minecraft/class_1309;I)Z", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_3767;method_16540(Lnet/minecraft/class_3222;Lnet/minecraft/class_2338;)Lnet/minecraft/class_3765;"))
    private net.minecraft.class_3765 cb$v1_21$badOmenRaid(net.minecraft.class_3767 raids, net.minecraft.class_3222 player,
                                                          net.minecraft.class_2338 pos, Operation<net.minecraft.class_3765> original) {
        if (CbMixedMembers.mixed(BAD_OMEN_RAID)
            && net.minecraft.class_1294.field_16595.comp_349() instanceof CbPrioritySplit.BadOmenRaid badOmen) {
            net.minecraft.class_3218 level = player.method_51469();
            return badOmen.cb$v1_21$badOmenRaid(player, level) ? null : level.method_19502(player.method_24515());
        }
        return original.call(raids, player, pos);
    }
}
