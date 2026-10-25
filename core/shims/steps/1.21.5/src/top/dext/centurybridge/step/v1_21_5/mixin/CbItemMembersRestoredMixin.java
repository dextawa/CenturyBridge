package top.dext.centurybridge.step.v1_21_5.mixin;

import java.util.List;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1836;
import net.minecraft.class_2561;
import net.minecraft.class_3414;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_5.rt.CbItemSounds;
import top.dext.centurybridge.step.v1_21_5.rt.CbItemTick;
import top.dext.centurybridge.step.v1_21_5.rt.CbItemTooltip;
import top.dext.centurybridge.step.v1_21_5.rt.CbVanillaReads;

/**
 * 1.21.4's overridable Item members that 1.21.5 dropped or reshaped, restored on Item: a 1.21.4 call links to them,
 * a 1.21.4 class's override overrides them. Priority below every mod's (CbVanillaReads.PRIORITY).
 */
@Mixin(value = class_1792.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbItemMembersRestoredMixin implements CbItemSounds.ItemBreakingSound {

    /**
     * 1.21.4's Item.appendHoverText(stack, context, lines, flag), restored: the item's 1.21.5 appendHoverText
     * (CbItemTooltip). 1.21.5's tooltip path calls the new shape directly, so this member carries no vanilla read;
     * a 1.21.4 override is reached through its override hook instead.
     */
    public void method_7851(class_1799 stack, class_1792.class_9635 context, List<class_2561> lines, class_1836 flag) {
        CbItemTooltip.restored((class_1792) (Object) this, stack, context, lines, flag);
    }

    /**
     * 1.21.4's Item.inventoryTick(stack, level, entity, slot, selected), restored: the item's 1.21.5 inventoryTick on the
     * server (CbItemTick). 1.21.5's tick path calls the new shape directly; a 1.21.4 override is reached through its
     * override hook (and on the client CbItemStackTickMixin).
     */
    public void method_7888(class_1799 stack, net.minecraft.class_1937 level, net.minecraft.class_1297 entity, int slot, boolean selected) {
        CbItemTick.restored((class_1792) (Object) this, stack, level, entity, slot, selected);
    }

    /** 1.21.4's Item.getBreakingSound(), restored: what 1.21.5 plays for the item (CbItemSounds) */
    @CbVanillaReads("CbItemBreakSoundMixin: LivingEntity.breakItem plays the sound this member answers for every item whose "
        + "class overrides it; the others play the BREAK_SOUND it answers")
    @Override
    public class_3414 method_57336() {
        return CbItemSounds.defaultBreakingSound((class_1792) (Object) this);
    }
}
