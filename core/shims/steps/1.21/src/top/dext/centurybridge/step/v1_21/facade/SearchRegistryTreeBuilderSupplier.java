package top.dext.centurybridge.step.v1_21.facade;

import java.util.List;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.6's SearchRegistry.TreeBuilderSupplier (class_1124$class_7456, gone in 1.21): what a search tree is built
 * from its contents with, registered under a key (CbClientSearchTreesMixin).
 */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface SearchRegistryTreeBuilderSupplier<T> extends Function<List<T>, RefreshableSearchTree<T>> {
}
