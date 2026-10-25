package top.dext.centurybridge.step.v26_1.rt;

import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.InstrumentComponent;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.ProvidesTrimMaterial;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.advancements.criterion.EntityTypePredicate;

/**
 * 1.21.11 data component (and component-like record) members 26.1 dropped or reshaped, 1.21.11's bodies on 26.1's:
 * <ul>
 * <li>ItemContainerContents stream() / nonEmptyStream() / nonEmptyItemsCopy(): copies of every slot / of the
 *     non-empty ones -- 26.1's allItemsCopyStream / nonEmptyItemCopyStream, and stacks of its non-empty templates;</li>
 * <li>InstrumentComponent / ProvidesTrimMaterial built from a key, and unwrap(registries): 26.1's hold the holder
 *     (CbWallEitherHolders resolves the key as 1.21.11's EitherHolder did), so unwrap answers it;</li>
 * <li>TransmuteResult (26.1's ItemStackTemplate) apply(input) / isResultUnchanged(input) / display();</li>
 * <li>ChargedProjectiles.of(stack), JukeboxSong.fromStack(registries, stack), DataComponentPatch.get(type),
 *     Equippable.canBeEquippedBy(type), EntityTypePredicate.matches(type), Difficulty.getKey().</li>
 * </ul>
 */
public final class CbWallComponentMembers {

    private CbWallComponentMembers() {
    }

    /** 1.21.11 ItemContainerContents.stream(): a copy of every slot, empty ones included */
    public static Stream<ItemStack> stream(ItemContainerContents contents) {
        return contents.allItemsCopyStream();
    }

    /** 1.21.11 ItemContainerContents.nonEmptyStream() */
    public static Stream<ItemStack> nonEmptyStream(ItemContainerContents contents) {
        return contents.nonEmptyItemCopyStream();
    }

    /** 1.21.11 ItemContainerContents.nonEmptyItemsCopy() */
    public static Iterable<ItemStack> nonEmptyItemsCopy(ItemContainerContents contents) {
        return Iterables.transform(contents.nonEmptyItems(), ItemStackTemplate::create);
    }

    /** 1.21.11 new InstrumentComponent(key) */
    public static InstrumentComponent instrumentComponent(ResourceKey<Instrument> key) {
        return new InstrumentComponent(CbWallEitherHolders.holder(new top.dext.centurybridge.step.v26_1.facade.EitherHolder<>(key)));
    }

    /** 1.21.11 InstrumentComponent.unwrap(registries) */
    public static Optional<Holder<Instrument>> unwrap(InstrumentComponent component, HolderLookup.Provider registries) {
        return Optional.of(component.instrument());
    }

    /** 1.21.11 new ProvidesTrimMaterial(key) */
    public static ProvidesTrimMaterial providesTrimMaterial(ResourceKey<TrimMaterial> key) {
        return new ProvidesTrimMaterial(CbWallEitherHolders.holder(new top.dext.centurybridge.step.v26_1.facade.EitherHolder<>(key)));
    }

    /** 1.21.11 ProvidesTrimMaterial.unwrap(registries) */
    public static Optional<Holder<TrimMaterial>> unwrap(ProvidesTrimMaterial component, HolderLookup.Provider registries) {
        return Optional.of(component.material());
    }

    /** 1.21.11 TransmuteResult.apply(input): the input turned into the result item and count, the result's patch on it */
    public static ItemStack apply(ItemStackTemplate result, ItemStack input) {
        ItemStack stack = input.transmuteCopy(result.item().value(), result.count());
        stack.applyComponents(result.components());
        return stack;
    }

    /** 1.21.11 TransmuteResult.isResultUnchanged(input) */
    public static boolean isResultUnchanged(ItemStackTemplate result, ItemStack input) {
        ItemStack stack = apply(result, input);
        return stack.getCount() == 1 && ItemStack.isSameItemSameComponents(input, stack);
    }

    /** 1.21.11 TransmuteResult.display() */
    public static SlotDisplay display(ItemStackTemplate result) {
        return new SlotDisplay.ItemStackSlotDisplay(result);
    }

    /** 1.21.11 ChargedProjectiles.of(stack): the stack's copy (26.1 keeps no empty projectile) */
    public static ChargedProjectiles of(ItemStack stack) {
        return ChargedProjectiles.ofNonEmpty(List.of(stack));
    }

    /** 1.21.11 JukeboxSong.fromStack(registries, stack) */
    public static Optional<Holder<JukeboxSong>> fromStack(HolderLookup.Provider registries, ItemStack stack) {
        JukeboxPlayable playable = stack.get(DataComponents.JUKEBOX_PLAYABLE);
        return playable != null ? Optional.of(playable.song()) : Optional.empty();
    }

    /** 1.21.11 DataComponentPatch.get(type): null when the patch leaves the type alone, empty when it removes it */
    public static Optional<?> get(DataComponentPatch patch, DataComponentType<?> type) {
        for (Map.Entry<DataComponentType<?>, Optional<?>> entry : patch.entrySet()) {
            if (entry.getKey() == type) {
                return entry.getValue();
            }
        }
        return null;
    }

    /** 1.21.11 Equippable.canBeEquippedBy(type) */
    public static boolean canBeEquippedBy(Equippable equippable, EntityType<?> type) {
        return equippable.allowedEntities().isEmpty() || equippable.allowedEntities().get().contains(type.builtInRegistryHolder());
    }

    /** 1.21.11 EntityTypePredicate.matches(type) */
    public static boolean matches(EntityTypePredicate predicate, EntityType<?> type) {
        return predicate.matches(type.builtInRegistryHolder());
    }

    /** 1.21.11 Difficulty.getKey() */
    public static String getKey(Difficulty difficulty) {
        return difficulty.getSerializedName();
    }
}
