package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21.facade.RefreshableSearchTree;

/** IdSearchTree (and so FullTextSearchTree) is a RefreshableSearchTree again, as 1.20.6's was */
@Mixin(net.minecraft.class_1121.class)
public abstract class CbClientIdSearchTreeMixin<T> implements RefreshableSearchTree<T> {
}
