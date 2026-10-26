package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_2513;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_20_3.facade.StemGrownBlock;
import top.dext.centurybridge.step.v1_20_3.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_20_3.rt.CbVanillaReads;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldStemAccess;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldStems;

/**
 * A StemBlock built through the 1.20.2 constructor (see CbWorldStems) grows its fruit, attaches and
 * picks its seed through the 1.20.2 fruit and seed supplier (CbWorldStemMixinHooks, default priority), and answers
 * 1.20.2's getFruit().
 */
@Mixin(value = class_2513.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWorldStemMixin implements CbWorldStemAccess, CbPrioritySplit.StemLegacy {

    @Unique
    private CbWorldStems.LegacyStem cb$legacy;

    @Override
    public void cb$setLegacyStem(CbWorldStems.LegacyStem legacy) {
        this.cb$legacy = legacy;
    }

    @Override
    public CbWorldStems.LegacyStem cb$v1_20_3$legacyStem() {
        return this.cb$legacy;
    }

    /**
     * 1.20.2 StemBlock.getFruit(). 1.20.3's own stems name their fruit by key, and that fruit (Blocks.MELON,
     * Blocks.PUMPKIN) is a plain Block, not a StemGrownBlock: there is nothing of the 1.20.2 type to return.
     */
    public StemGrownBlock method_10694() {
        if (this.cb$legacy != null) {
            return this.cb$legacy.fruit();
        }
        throw new IllegalStateException("StemBlock.getFruit(): " + this + " is a 1.20.3 stem; its fruit is a plain Block,"
            + " not a StemGrownBlock");
    }
}
