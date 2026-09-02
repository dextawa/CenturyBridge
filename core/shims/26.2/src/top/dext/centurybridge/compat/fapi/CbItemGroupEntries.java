package top.dext.centurybridge.compat.fapi;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

/**
 * fabric-item-group-api-v1's FabricItemGroupEntries as 1.20.x listeners receive
 * it, backed by 26.x's {@link FabricCreativeModeTabOutput} (the same object the
 * real event hands out). Old names delegate 1:1: addAfter/addBefore -&gt;
 * insertAfter/insertBefore; the intermediary-era CreativeModeTab.Output methods
 * (add -&gt; accept after the namespace wall) come from implementing Output.
 */
public class CbItemGroupEntries implements CreativeModeTab.Output {

    private final FabricCreativeModeTabOutput out;

    public CbItemGroupEntries(FabricCreativeModeTabOutput out) {
        this.out = out;
    }

    public CreativeModeTab.ItemDisplayParameters getContext() {
        return out.getContext();
    }

    public FeatureFlagSet getEnabledFeatures() {
        return out.getEnabledFeatures();
    }

    public boolean shouldShowOpRestrictedItems() {
        return out.shouldShowOpRestrictedItems();
    }

    public List<ItemStack> getDisplayStacks() {
        return out.getDisplayStacks();
    }

    public List<ItemStack> getSearchTabStacks() {
        return out.getSearchTabStacks();
    }

    /** was method_45417 / accept(ItemStack, TabVisibility): Output's abstract method */
    @Override
    public void accept(ItemStack stack, CreativeModeTab.TabVisibility visibility) {
        out.accept(stack, visibility);
    }

    // ---- 1.20.1 call sites reach ItemGroup.Entries' inherited add/addAll through THIS owner
    // by intermediary name (the namespace wall translates vanilla owners only; a converted
    // jar carries CbItemGroupEntries.method_45421(ItemLike)), so the old ids stay callable ----

    /** was ItemGroup.Entries.add(ItemStack, StackVisibility) */
    public void method_45417(ItemStack stack, CreativeModeTab.TabVisibility visibility) {
        out.accept(stack, visibility);
    }

    /** was ItemGroup.Entries.add(ItemStack) */
    public void method_45420(ItemStack stack) {
        out.accept(stack);
    }

    /** was ItemGroup.Entries.add(ItemConvertible) */
    public void method_45421(ItemLike item) {
        out.accept(item);
    }

    /** was ItemGroup.Entries.add(ItemConvertible, StackVisibility) */
    public void method_45422(ItemLike item, CreativeModeTab.TabVisibility visibility) {
        out.accept(item, visibility);
    }

    /** was ItemGroup.Entries.addAll(Collection) */
    public void method_45423(Collection<ItemStack> stacks) {
        out.acceptAll(stacks);
    }

    /** was ItemGroup.Entries.addAll(Collection, StackVisibility) */
    public void method_45424(Collection<ItemStack> stacks, CreativeModeTab.TabVisibility visibility) {
        out.acceptAll(stacks, visibility);
    }

    public void prepend(ItemStack stack) {
        out.prepend(stack);
    }

    public void prepend(ItemStack stack, CreativeModeTab.TabVisibility visibility) {
        out.prepend(stack, visibility);
    }

    public void prepend(ItemLike item) {
        out.prepend(item);
    }

    public void prepend(ItemLike item, CreativeModeTab.TabVisibility visibility) {
        out.prepend(item, visibility);
    }

    public void addAfter(ItemLike afterLast, ItemStack... newStack) {
        out.insertAfter(afterLast, newStack);
    }

    public void addAfter(ItemStack afterLast, ItemStack... newStack) {
        out.insertAfter(afterLast, newStack);
    }

    public void addAfter(ItemLike afterLast, ItemLike... newItem) {
        out.insertAfter(afterLast, newItem);
    }

    public void addAfter(ItemStack afterLast, ItemLike... newItem) {
        out.insertAfter(afterLast, newItem);
    }

    public void addAfter(ItemLike afterLast, Collection<ItemStack> newStacks) {
        out.insertAfter(afterLast, newStacks);
    }

    public void addAfter(ItemStack afterLast, Collection<ItemStack> newStacks) {
        out.insertAfter(afterLast, newStacks);
    }

    public void addAfter(ItemLike afterLast, Collection<ItemStack> newStacks, CreativeModeTab.TabVisibility visibility) {
        out.insertAfter(afterLast, newStacks, visibility);
    }

    public void addAfter(ItemStack afterLast, Collection<ItemStack> newStacks, CreativeModeTab.TabVisibility visibility) {
        out.insertAfter(afterLast, newStacks, visibility);
    }

    public void addAfter(Predicate<ItemStack> afterLast, Collection<ItemStack> newStacks, CreativeModeTab.TabVisibility visibility) {
        out.insertAfter(afterLast, newStacks, visibility);
    }

    public void addBefore(ItemLike beforeFirst, ItemStack... newStack) {
        out.insertBefore(beforeFirst, newStack);
    }

    public void addBefore(ItemStack beforeFirst, ItemStack... newStack) {
        out.insertBefore(beforeFirst, newStack);
    }

    public void addBefore(ItemLike beforeFirst, ItemLike... newItem) {
        out.insertBefore(beforeFirst, newItem);
    }

    public void addBefore(ItemStack beforeFirst, ItemLike... newItem) {
        out.insertBefore(beforeFirst, newItem);
    }

    public void addBefore(ItemLike beforeFirst, Collection<ItemStack> newStacks) {
        out.insertBefore(beforeFirst, newStacks);
    }

    public void addBefore(ItemStack beforeFirst, Collection<ItemStack> newStacks) {
        out.insertBefore(beforeFirst, newStacks);
    }

    public void addBefore(ItemLike beforeFirst, Collection<ItemStack> newStacks, CreativeModeTab.TabVisibility visibility) {
        out.insertBefore(beforeFirst, newStacks, visibility);
    }

    public void addBefore(ItemStack beforeFirst, Collection<ItemStack> newStacks, CreativeModeTab.TabVisibility visibility) {
        out.insertBefore(beforeFirst, newStacks, visibility);
    }

    public void addBefore(Predicate<ItemStack> beforeFirst, Collection<ItemStack> newStacks, CreativeModeTab.TabVisibility visibility) {
        out.insertBefore(beforeFirst, newStacks, visibility);
    }
}
