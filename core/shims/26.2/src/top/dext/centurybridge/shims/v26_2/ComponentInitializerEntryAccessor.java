package top.dext.centurybridge.shims.v26_2;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** One registered initializer (a package-private record): its key, and running it. */
@Mixin(targets = "net.minecraft.core.component.DataComponentInitializers$InitializerEntry")
public interface ComponentInitializerEntryAccessor {

    @Invoker("key")
    ResourceKey<?> cbKey();

    @Invoker("run")
    void cbRun(DataComponentMap.Builder builder, HolderLookup.Provider registries);
}
