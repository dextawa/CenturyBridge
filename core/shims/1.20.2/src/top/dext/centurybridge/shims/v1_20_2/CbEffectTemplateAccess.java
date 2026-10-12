package top.dext.centurybridge.shims.v1_20_2;

import java.util.UUID;
import net.minecraft.class_1291;
import net.minecraft.class_1322;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * The vanilla attribute-modifier template of an effect (MobEffect's private template record, made
 * by addAttributeModifier): its effect, id, base amount and operation -- what 1.20.1 stored as
 * the effect's modifier (CbEffects.baseModifier).
 */
@Mixin(targets = "net.minecraft.class_1291$class_8634")
public interface CbEffectTemplateAccess {

    @Accessor("field_45104")
    class_1291 cb$effect();

    @Accessor("field_45105")
    UUID cb$id();

    @Accessor("field_45106")
    double cb$amount();

    @Accessor("field_45107")
    class_1322.class_1323 cb$operation();
}
