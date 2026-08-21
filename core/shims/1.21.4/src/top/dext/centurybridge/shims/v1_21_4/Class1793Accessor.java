package top.dext.centurybridge.shims.v1_21_4;

import net.minecraft.class_1792;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_9323;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Key plumbing on Item Settings for the pending-key repair (see CbPending). */
@Mixin(class_1792.class_1793.class)
public interface Class1793Accessor {

    @Accessor("field_54117")
    class_5321<class_1792> cbGetKey();

    @Accessor("field_54117")
    void cbSetKey(class_5321<class_1792> key);

    @Invoker("method_63689")
    String cbTranslationKey();

    @Invoker("method_63691")
    class_2960 cbModelId();

    @Invoker("method_58406")
    class_9323 cbBuildComponents(class_2561 name, class_2960 modelId);
}
