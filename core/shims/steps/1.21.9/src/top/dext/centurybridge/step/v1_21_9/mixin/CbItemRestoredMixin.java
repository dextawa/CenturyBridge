package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_1792;
import net.minecraft.class_1799;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_9.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * Item.verifyComponentsAfterLoad(stack), which 1.21.9 dropped (javap: absent from 1.21.9's class_1792). 1.21.8's Item
 * body was empty; its one vanilla override, PlayerHeadItem's, resolved a partial profile component and stored the
 * result back, which 1.21.9 does where the profile is used (ResolvableProfile's resolve(resolver)), so PlayerHeadItem
 * inherits this empty body. 1.21.8's ItemStack and EntityEquipment called it where 1.21.9 no longer does; those calls
 * come back in CbItemVerifyHooksMixin / CbItemVerifyEquipmentHooksMixin (default priority), through
 * {@link #cb$v1_21_9$verifyComponentsAfterLoad}, so a mod's override or mixin of it answers.
 */
@Mixin(value = class_1792.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbItemRestoredMixin implements CbPrioritySplit.ItemVerify {

    /** Item.verifyComponentsAfterLoad(stack) */
    public void method_7860(class_1799 stack) {
    }

    @Override
    public void cb$v1_21_9$verifyComponentsAfterLoad(class_1799 stack) {
        this.method_7860(stack);
    }
}
