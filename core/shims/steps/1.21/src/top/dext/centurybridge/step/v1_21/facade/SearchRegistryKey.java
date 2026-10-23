package top.dext.centurybridge.step.v1_21.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.6's SearchRegistry.Key (public there; 1.21's SessionSearchTrees.Key is package-private and used only inside
 * it): the identity a search tree is registered under. A 1.20.6 class makes its own; SearchRegistry's
 * CREATIVE_NAMES / CREATIVE_TAGS / RECIPE_COLLECTIONS are the three of CbClientSearch, which answer 1.21's own trees.
 */
@Environment(EnvType.CLIENT)
public class SearchRegistryKey<T> {

    public SearchRegistryKey() {
    }
}
