package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_5.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * 1.20.4 AbstractHorse.getPassengersRidingOffsetY(dimensions, scale): the height 1.20.4's horse put its
 * rider at (before the rearing offset), which mods override. 1.20.5's horse takes that height from its
 * passenger attachment instead. The cover answers that attachment's height (the first passenger's,
 * 1.20.4's horses carried one), so a vanilla horse answers 1.20.5's own value; where a 1.20.4 override
 * answers another height, 1.20.5's point is moved to it, keeping the rearing offset 1.20.5 adds
 * (CbRestAbstractHorseMixinHooks, default priority).
 */
@Mixin(value = net.minecraft.class_1496.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRestAbstractHorseMixin implements CbPrioritySplit.HorseRiding {

    /** 1.20.4 AbstractHorse.getPassengersRidingOffsetY(EntityDimensions, float) */
    protected float method_52550(net.minecraft.class_4048 dimensions, float scale) {
        return cb$v1_20_5$attachmentHeight(dimensions);
    }

    @Override
    public float cb$v1_20_5$attachmentHeight(net.minecraft.class_4048 dimensions) {
        net.minecraft.class_1297 self = (net.minecraft.class_1297) (Object) this;
        return (float) dimensions.comp_2188().method_55679(net.minecraft.class_9064.field_47743, 0, self.method_36454()).field_1351;
    }

    /** 1.20.4's getPassengersRidingOffsetY called virtually, for 1.20.5's getPassengerAttachmentPoint (CbRestAbstractHorseMixinHooks) */
    @Override
    public float cb$v1_20_5$passengersRidingOffsetY(net.minecraft.class_4048 dimensions, float scale) {
        return this.method_52550(dimensions, scale);
    }
}
