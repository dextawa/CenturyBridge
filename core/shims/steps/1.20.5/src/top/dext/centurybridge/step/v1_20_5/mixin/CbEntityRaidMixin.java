package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * Raid: 1.20.4's absorbBadOmen(player) -- the player's bad omen raises the raid's omen level by its amplifier plus
 * one (up to the maximum) and is removed -- which 1.20.5 replaced with absorbRaidOmen(ServerPlayer) for its raid
 * omen effect. 1.20.4's body over 1.20.5's effect holders.
 */
@Mixin(value = net.minecraft.class_3765.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbEntityRaidMixin {

    @Shadow
    private int field_16623;

    @Shadow
    public abstract int method_16514();

    /** was absorbBadOmen(player) */
    public void method_16518(net.minecraft.class_1657 player) {
        if (player.method_6059(net.minecraft.class_1294.field_16595)) {
            this.field_16623 += player.method_6112(net.minecraft.class_1294.field_16595).method_5578() + 1;
            this.field_16623 = net.minecraft.class_3532.method_15340(this.field_16623, 0, this.method_16514());
        }
        player.method_6016(net.minecraft.class_1294.field_16595);
    }
}
