package top.dext.centurybridge.step.v1_21_6.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_1792;
import net.minecraft.class_2248;
import net.minecraft.class_2474;
import net.minecraft.class_3495;
import net.minecraft.class_3497;
import net.minecraft.class_6862;
import net.minecraft.class_7225;
import net.minecraft.class_7784;
import net.minecraft.class_7889;
import net.minecraft.class_7924;

/**
 * 1.21.5's ItemTagsProvider (net/minecraft/class_7805), which 1.21.6 removed (its vanilla item tags fill block-and-item
 * tags pairs through BlockItemTagsProvider, class_11387, instead of copying). A 1.21.5 mod's references are renamed here
 * (classRenames, s6h-tags.json), including the climbed 1.20.6 UpdateOneTwentyOneItemTagsProvider (v1_21 class_8908) and
 * 1.21.4 WinterDropItemTagsProvider facades. The bodies are 1.21.5's (javap). It is an IntrinsicHolderTagsProvider of
 * items keyed by their built-in holder. copy(block tag, item tag) records a pair. getHolderLookup (1.21.6 still calls it
 * from TagsProvider.run, the same protected method) waits for the block tags and, for each recorded pair, adds every
 * entry of the block tag's builder to the item tag's builder. A missing block tag fails with 1.21.5's message, which
 * names the item tag.
 * <p>Its members are public, as every facade standing in for a dead class must be: access wideners never reach a
 * CenturyBridge class. 1.21.5 declared the fields private and copy / getHolderLookup protected.
 */
public abstract class ItemTagsProvider extends class_7889<class_1792> {

    /** blockTags */
    public final CompletableFuture<class_2474.class_8211<class_2248>> field_40664;

    /** tagsToCopy */
    public final Map<class_6862<class_2248>, class_6862<class_1792>> field_43092 = new HashMap<>();

    public ItemTagsProvider(class_7784 output, CompletableFuture<class_7225.class_7874> lookupProvider,
            CompletableFuture<class_2474.class_8211<class_2248>> blockTags) {
        super(output, class_7924.field_41197, lookupProvider, item -> item.method_40131().method_40237());
        this.field_40664 = blockTags;
    }

    public ItemTagsProvider(class_7784 output, CompletableFuture<class_7225.class_7874> lookupProvider,
            CompletableFuture<class_2474.class_8211<class_1792>> parentProvider, CompletableFuture<class_2474.class_8211<class_2248>> blockTags) {
        super(output, class_7924.field_41197, lookupProvider, parentProvider, item -> item.method_40131().method_40237());
        this.field_40664 = blockTags;
    }

    /** copy(TagKey&lt;Block&gt;, TagKey&lt;Item&gt;) */
    public void method_46218(class_6862<class_2248> blockTag, class_6862<class_1792> itemTag) {
        this.field_43092.put(blockTag, itemTag);
    }

    /** getHolderLookup */
    @Override
    public CompletableFuture<class_7225.class_7874> method_49651() {
        return super.method_49651().thenCombine(this.field_40664, (lookup, blockLookup) -> {
            this.field_43092.forEach((blockTag, itemTag) -> this.cb$copy(blockLookup, blockTag, itemTag));
            return lookup;
        });
    }

    /** 1.21.5's lambda of getHolderLookup (method_49650) */
    private void cb$copy(class_2474.class_8211<class_2248> blockLookup, class_6862<class_2248> blockTag, class_6862<class_1792> itemTag) {
        class_3495 itemBuilder = this.method_27169(itemTag);
        Optional<class_3495> blockBuilder = blockLookup.apply(blockTag);
        List<class_3497> entries = blockBuilder.orElseThrow(() -> new IllegalStateException("Missing block tag " + itemTag.comp_327())).method_26782();
        entries.forEach(itemBuilder::method_27064);
    }
}
