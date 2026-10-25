package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21.rt.CbMobEffectTemplates;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * MobEffect.AttributeTemplate (class_1291$class_8634): restores 1.20.6's create(name, amplifier) (method_52522(String, int)).
 * 1.20.6 named each modifier "name amplifier" under the template's UUID; 1.21's modifier has neither name nor UUID (its id is
 * the template's ResourceLocation) and 1.21's create takes the amplifier alone. The restored method answers 1.21's modifier
 * for the amplifier; CbMobEffectMixin routes MobEffect's calls through it with the name 1.20.6 passed
 * (MobEffect.getDescriptionId()), so a 1.20.6 mod's mixin on create sees the call vanilla makes and decides its answer.
 */
@Mixin(targets = "net.minecraft.class_1291$class_8634", priority = 1)
public abstract class CbMobEffectTemplateMixin implements CbMobEffectTemplates.Restored {

    @Shadow
    public abstract net.minecraft.class_1322 method_52522(int amplifier);

    /** 1.20.6 MobEffect.AttributeTemplate.create(name, amplifier) */
    @Override
    @CbVanillaReads(value = "CbMobEffectMixin: MobEffect.addAttributeModifiers (method_5555) and createModifiers' per-template "
        + "step (method_55651) take their modifier from this method (they called 1.21's create(amplifier)), with 1.20.6's name "
        + "argument, the effect's description id. The body is 1.21's: 1.20.6's built the modifier from the UUID and the name, which "
        + "1.21's modifier does not have, so only what wraps the call is carried, not anchors inside it",
        carries = {"Inject@HEAD", "Inject@RETURN", "Inject@TAIL", "ModifyReturnValue@RETURN"})
    public net.minecraft.class_1322 method_52522(String name, int amplifier) {
        return this.method_52522(amplifier);
    }
}
