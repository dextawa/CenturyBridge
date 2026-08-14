package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_1291;
import net.minecraft.class_1309;
import net.minecraft.class_5131;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.1 -> 1.20.2: StatusEffect attribute hooks dropped the entity/amplifier
 * parameters. Old three-arg overloads delegate to the new shapes so legacy
 * super-calls link. Semantic note: vanilla now invokes the NEW shapes, so a
 * legacy subclass override of the old shape is no longer called by vanilla --
 * linkage is restored, bespoke override logic may be dormant.
 */
@Mixin(class_1291.class)
public abstract class StatusEffectBridge {

    public void method_5562(class_1309 entity, class_5131 attributes, int amplifier) {
        ((class_1291) (Object) this).method_5562(attributes);
    }

    public void method_5555(class_1309 entity, class_5131 attributes, int amplifier) {
        ((class_1291) (Object) this).method_5555(attributes, amplifier);
    }

    /** removed in 1.20.2; re-implemented with the legacy default semantics */
    public double method_5563(int amplifier, net.minecraft.class_1322 modifier) {
        return modifier.method_6186() * (amplifier + 1);
    }
}
