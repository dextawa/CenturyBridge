package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.core.Registry;
import net.minecraft.server.ReloadableServerResources;

/**
 * 1.21.11's ReloadableServerResources.updateStaticRegistryTags(): applied the tags postponed for the static
 * registries (javap class_5350.method_61248: postponedTags.forEach(PendingTags::apply)). 26.1 folds it into
 * updateComponentsAndStaticRegistryTags(), which also binds the new data components; 1.21.11's half alone is
 * kept here (the layer's access widener opens postponedTags).
 */
public final class CbWallServerResources {
    private CbWallServerResources() {
    }

    /** updateStaticRegistryTags() */
    public static void updateStaticRegistryTags(ReloadableServerResources resources) {
        resources.postponedTags.forEach(Registry.PendingTags::apply);
    }
}
