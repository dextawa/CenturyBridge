package top.dext.centurybridge.rt.v26_2;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import top.dext.centurybridge.shims.v26_2.ComponentInitializerEntryAccessor;
import top.dext.centurybridge.shims.v26_2.ComponentInitializersAccessor;

/**
 * 26.x binds an item's components only when a data pack loads (they are built from the
 * initializers its constructor registered, against the server's registries), and an
 * ItemStack reads them at construction. 1.20.1 items carried their properties from the
 * moment they were built, and mods made stacks of them right away -- creative tab
 * icons, a list of default stacks while registering.
 *
 * A component map asked for before binding is built here from the same initializers, the
 * holder's own entries only, against the built-in (static) registries; an initializer
 * that needs a data-driven registry is skipped and said so once. The data pack load binds
 * the full map over it as usual -- a stack made earlier keeps the provisional prototype.
 */
public final class CbProvisionalComponents {

    private static HolderLookup.Provider registries;
    private static boolean warned;

    /** the built-in (static) registries as a lookup, for code that runs before any level has one */
    public static synchronized HolderLookup.Provider staticRegistries() {
        if (registries == null) {
            registries = HolderLookup.Provider.create(BuiltInRegistries.REGISTRY.stream()
                .map(r -> (HolderLookup.RegistryLookup<?>) r));
        }
        return registries;
    }

    public static synchronized DataComponentMap of(Holder.Reference<?> holder) {
        if (holder.key() == null) {
            return null;
        }
        staticRegistries();
        DataComponentMap.Builder builder = DataComponentMap.builder();
        boolean any = false;
        for (Object e : ((ComponentInitializersAccessor) BuiltInRegistries.DATA_COMPONENT_INITIALIZERS).cbInitializers()) {
            ComponentInitializerEntryAccessor entry = (ComponentInitializerEntryAccessor) e;
            if (!holder.key().equals(entry.cbKey())) {
                continue;
            }
            any = true;
            try {
                entry.cbRun(builder, registries);
            } catch (RuntimeException ex) {
                if (!warned) {
                    warned = true;
                    org.slf4j.LoggerFactory.getLogger("CenturyBridge").warn(
                        "components of {} asked for before data packs loaded; an initializer needing a data-driven "
                            + "registry was left out until they do ({})", holder.key(), ex.toString());
                }
            }
        }
        return any ? builder.build() : null;
    }

    private CbProvisionalComponents() {
    }
}
