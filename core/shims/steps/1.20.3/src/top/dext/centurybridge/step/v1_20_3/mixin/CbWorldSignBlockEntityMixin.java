package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_1657;
import net.minecraft.class_2625;
import net.minecraft.class_8242;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_3.rt.CbVanillaReads;

/**
 * 1.20.2's SignBlockEntity.getTextFacingPlayer(player), gone in 1.20.3, back with its 1.20.2 body (a cover): the
 * text of the side the player is facing. 1.20.2 itself never called it.
 */
@Mixin(value = class_2625.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWorldSignBlockEntityMixin {

    @Shadow
    public abstract boolean method_49834(class_1657 player);

    @Shadow
    public abstract class_8242 method_49843(boolean front);

    public class_8242 method_49844(class_1657 player) {
        return this.method_49843(this.method_49834(player));
    }
}
