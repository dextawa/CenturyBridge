package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_7225;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** ReloadableServerRegistries.Holder's registries (private) for CbRestServer.holderRegistries */
@Mixin(targets = "net.minecraft.class_9383$class_9385")
public interface CbRestRegistriesHolderAccessor {

    @Accessor("field_49920")
    class_7225.class_7874 cb$registries();
}
