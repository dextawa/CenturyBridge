package top.dext.centurybridge.step.v1_21.facade;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.6's RefreshableSearchTree (class_1123, gone in 1.21): a SearchTree that can rebuild what it searches
 * (refresh(), a no-op unless the tree overrides it), and the empty one. 1.21's IdSearchTree (and so
 * FullTextSearchTree) is one again (CbClientIdSearchTreeMixin), as 1.20.6's were; FullTextSearchTree's refresh
 * rebuilds its text index (CbClientFullTextSearchTreeMixin).
 */
@Environment(EnvType.CLIENT)
public interface RefreshableSearchTree<T> extends net.minecraft.class_1129<T> {

    /** empty(): finds nothing */
    static <T> RefreshableSearchTree<T> method_43798() {
        return text -> List.of();
    }

    /** refresh() */
    default void method_4799() {
    }
}
