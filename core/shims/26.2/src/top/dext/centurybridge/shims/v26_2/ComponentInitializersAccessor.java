package top.dext.centurybridge.shims.v26_2;

import java.util.List;
import net.minecraft.core.component.DataComponentInitializers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** The registered component initializers, read by CbProvisionalComponents. */
@Mixin(DataComponentInitializers.class)
public interface ComponentInitializersAccessor {

    @Accessor("initializers")
    List<Object> cbInitializers();
}
