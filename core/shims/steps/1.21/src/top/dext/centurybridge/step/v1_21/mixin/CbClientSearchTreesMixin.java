package top.dext.centurybridge.step.v1_21.mixin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21.facade.RefreshableSearchTree;
import top.dext.centurybridge.step.v1_21.facade.SearchRegistryKey;
import top.dext.centurybridge.step.v1_21.facade.SearchRegistryTreeBuilderSupplier;
import top.dext.centurybridge.step.v1_21.rt.CbClientSearch;

/**
 * 1.20.6's SearchRegistry on 1.21's SessionSearchTrees (CbClientSearch): register / getTree / populate /
 * onResourceManagerReload, and a ResourceManagerReloadListener again, as 1.20.6's was. A registered key is
 * 1.20.6's entry -- its supplier, its tree (empty until populated; populate builds it from the contents and
 * refreshes it; reload refreshes it). CREATIVE_NAMES / CREATIVE_TAGS / RECIPE_COLLECTIONS answer 1.21's trees of
 * this object (the connection's, for the game's registry), and populating one builds it with 1.20.6's supplier
 * where 1.21's screens read it. An unregistered other key is 1.20.6's "Tree builder not registered".
 */
@Mixin(net.minecraft.class_1124.class)
public abstract class CbClientSearchTreesMixin implements CbClientSearch.CbClientSearchRegistry, net.minecraft.class_4013 {

    /** creativeByNameSearch */
    @Shadow
    private CompletableFuture<net.minecraft.class_1129<net.minecraft.class_1799>> field_51826;

    /** creativeByTagSearch */
    @Shadow
    private CompletableFuture<net.minecraft.class_1129<net.minecraft.class_1799>> field_51827;

    /** recipeSearch */
    @Shadow
    private CompletableFuture<net.minecraft.class_1129<net.minecraft.class_516>> field_51828;

    /** creativeNameSearch() */
    @Shadow
    public abstract net.minecraft.class_1129<net.minecraft.class_1799> method_60372();

    /** creativeTagSearch() */
    @Shadow
    public abstract net.minecraft.class_1129<net.minecraft.class_1799> method_60370();

    /** recipes() */
    @Shadow
    public abstract net.minecraft.class_1129<net.minecraft.class_516> method_60364();

    /** 1.20.6's entries: the supplier and the tree it last built */
    @Unique
    private final Map<SearchRegistryKey<?>, Object[]> cb$entries = new HashMap<>();

    @Unique
    private CbClientSearchTreesMixin cb$fixed() {
        return (CbClientSearchTreesMixin) (Object) CbClientSearch.cbFixedTreesOf((net.minecraft.class_1124) (Object) this);
    }

    /** register(key, supplier) */
    @Override
    public <T> void method_4801(SearchRegistryKey<T> key, SearchRegistryTreeBuilderSupplier<T> supplier) {
        this.cb$entries.put(key, new Object[] {supplier, RefreshableSearchTree.method_43798()});
    }

    /** getTree(key) */
    @Override
    @SuppressWarnings("unchecked")
    public <T> net.minecraft.class_1129<T> method_4800(SearchRegistryKey<T> key) {
        Object[] entry = this.cb$entries.get(key);
        if (entry != null) {
            return (net.minecraft.class_1129<T>) entry[1];
        }
        CbClientSearchTreesMixin fixed = this.cb$fixed();
        if (key == CbClientSearch.field_5495) {
            return (net.minecraft.class_1129<T>) (Object) fixed.method_60372();
        }
        if (key == CbClientSearch.field_5494) {
            return (net.minecraft.class_1129<T>) (Object) fixed.method_60370();
        }
        if (key == CbClientSearch.field_46200) {
            return (net.minecraft.class_1129<T>) (Object) fixed.method_60364();
        }
        throw new IllegalStateException("Tree builder not registered");
    }

    /** populate(key, contents) */
    @Override
    @SuppressWarnings("unchecked")
    public <T> void method_43804(SearchRegistryKey<T> key, List<T> contents) {
        Object[] entry = this.cb$entries.get(key);
        if (entry != null) {
            RefreshableSearchTree<T> tree = ((SearchRegistryTreeBuilderSupplier<T>) entry[0]).apply(contents);
            entry[1] = tree;
            tree.method_4799();
            return;
        }
        CbClientSearchTreesMixin fixed = this.cb$fixed();
        if (key == CbClientSearch.field_5495) {
            fixed.field_51826 = CompletableFuture.completedFuture(CbClientSearch.cbCreativeNames((List<net.minecraft.class_1799>) contents));
            return;
        }
        if (key == CbClientSearch.field_5494) {
            fixed.field_51827 = CompletableFuture.completedFuture(CbClientSearch.cbCreativeTags((List<net.minecraft.class_1799>) contents));
            return;
        }
        if (key == CbClientSearch.field_46200) {
            fixed.field_51828 = CompletableFuture.completedFuture(CbClientSearch.cbRecipeCollections((List<net.minecraft.class_516>) contents));
            return;
        }
        throw new IllegalStateException("Tree builder not registered");
    }

    /** onResourceManagerReload(resources): every tree refreshed, as 1.20.6's were */
    @Override
    public void method_14491(net.minecraft.class_3300 resources) {
        for (Object[] entry : this.cb$entries.values()) {
            ((RefreshableSearchTree<?>) entry[1]).method_4799();
        }
        CbClientSearchTreesMixin fixed = this.cb$fixed();
        for (Object tree : new Object[] {fixed.method_60372(), fixed.method_60370(), fixed.method_60364()}) {
            if (tree instanceof RefreshableSearchTree<?> refreshable) {
                refreshable.method_4799();
            }
        }
    }
}
