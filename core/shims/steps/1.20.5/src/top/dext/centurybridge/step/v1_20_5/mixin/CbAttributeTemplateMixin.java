package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbEffects;

/** MobEffect.AttributeTemplate (a package-private record): its id and modifier, for 1.20.4's template view (rt/CbEffects) */
@Mixin(targets = "net.minecraft.class_1291$class_8634")
public abstract class CbAttributeTemplateMixin implements CbEffects.TemplateView {

    /** id() */
    @Shadow
    public abstract java.util.UUID comp_2182();

    /** create(name, amplifier) */
    @Shadow
    public abstract net.minecraft.class_1322 method_52522(String name, int amplifier);

    @Override
    public java.util.UUID cbS4$id() {
        return comp_2182();
    }

    @Override
    public net.minecraft.class_1322 cbS4$create(String name, int amplifier) {
        return method_52522(name, amplifier);
    }
}
