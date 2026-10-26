package top.dext.centurybridge.step.v1_20_3.mixin;

import java.util.function.Supplier;
import net.minecraft.class_1322;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_20_3.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_20_3.rt.CbVanillaReads;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldAttributeModifierName;

/**
 * AttributeModifier's 1.20.2 name: getName() comes back (a cover), answering the name supplier a 1.20.2
 * constructor gave (CbWorldAttributes.attachName) and else the 1.20.3 field. save() asks getName() as
 * 1.20.2's did, so a 1.20.2 subclass's override names the saved modifier again; toString() reads the
 * supplier directly, as 1.20.2's did; the 1.20.3 codec writes what save() writes (CbWorldAttributeModifierMixinHooks,
 * default priority).
 */
@Mixin(value = class_1322.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWorldAttributeModifierMixin implements CbWorldAttributeModifierName, CbPrioritySplit.AttributeModifierName {

    @Shadow
    @Final
    private String field_46248;

    @Unique
    private Supplier<String> cb$nameGetter;

    @Override
    public void cb$setNameGetter(Supplier<String> nameGetter) {
        this.cb$nameGetter = nameGetter;
    }

    @Override
    public String cb$v1_20_3$name() {
        return this.cb$nameGetter != null ? this.cb$nameGetter.get() : this.field_46248;
    }

    /** 1.20.2 getName() */
    @Override
    public String method_6185() {
        return this.cb$v1_20_3$name();
    }
}
