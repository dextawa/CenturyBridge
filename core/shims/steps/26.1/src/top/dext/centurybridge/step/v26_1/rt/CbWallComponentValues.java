package top.dext.centurybridge.step.v26_1.rt;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ProvidesTrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import top.dext.centurybridge.step.v26_1.facade.EitherHolder;

/**
 * The data components whose value type 26.1 changed under the same component type:
 * <ul>
 * <li>CHICKEN_VARIANT, DAMAGE_TYPE, ZOMBIE_NAUTILUS_VARIANT: 1.21.11 EitherHolder, 26.1 the holder;</li>
 * <li>PROVIDES_TRIM_MATERIAL: 1.21.11 ProvidesTrimMaterial (around an EitherHolder), 26.1 the material's holder;</li>
 * <li>PROVIDES_BANNER_PATTERNS: 1.21.11 a TagKey of banner patterns, 26.1 a HolderSet of them (the tag's).</li>
 * </ul>
 * A value 1.21.11 code reads back meets a cast to its 1.21.11 type (explicit, or LambdaMetafactory's): there a
 * 26.1 value of that component becomes the 1.21.11 one (checkCastRedirects; a holder is an EitherHolder of it,
 * a trim material's holder its ProvidesTrimMaterial, a named holder set its tag). A value 1.21.11 code hands in
 * goes through the component setters, which turn it into 26.1's: an EitherHolder its holder (a key resolved in
 * the registries in use, as 1.21.11 resolved it when used), a ProvidesTrimMaterial its material, a banner
 * pattern tag the registries' holder set of it. Item.Properties.component, used before any registries are
 * loaded, gives a key or a tag to 26.1's delayed components (resolved when the item's components are built,
 * as vanilla's own banner patterns and trim materials are).
 */
public final class CbWallComponentValues {

    private CbWallComponentValues() {
    }

    // ---- read back: casts to the 1.21.11 types

    /** a cast to 1.21.11's EitherHolder: a 26.1 component's holder is an EitherHolder of it */
    public static Object castEitherHolder(Object o) {
        return o instanceof Holder<?> h ? new EitherHolder<>(h) : o;
    }

    /** instanceof 1.21.11's EitherHolder (a holder only reaches 1.21.11 code typed as Object from such a component) */
    public static boolean isEitherHolder(Object o) {
        return o instanceof EitherHolder<?> || o instanceof Holder<?>;
    }

    private static boolean trimMaterial(Object o) {
        return o instanceof Holder<?> h && h.isBound() && h.value() instanceof TrimMaterial;
    }

    /** a cast to ProvidesTrimMaterial: 26.1's PROVIDES_TRIM_MATERIAL value (the material's holder) as 1.21.11's record */
    @SuppressWarnings("unchecked")
    public static Object castProvidesTrimMaterial(Object o) {
        return trimMaterial(o) ? new ProvidesTrimMaterial((Holder<TrimMaterial>) o) : o;
    }

    /** instanceof ProvidesTrimMaterial */
    public static boolean isProvidesTrimMaterial(Object o) {
        return o instanceof ProvidesTrimMaterial || trimMaterial(o);
    }

    /**
     * a cast to TagKey: a 26.1 holder set standing for a tag (PROVIDES_BANNER_PATTERNS' value, BlocksAttacks'
     * bypassedBy) as 1.21.11's tag; a holder set of listed elements has no tag and is left to the cast
     */
    public static Object castTagKey(Object o) {
        return o instanceof HolderSet<?> s && s.unwrapKey().isPresent() ? s.unwrapKey().get() : o;
    }

    /** instanceof TagKey */
    public static boolean isTagKey(Object o) {
        return o instanceof TagKey<?> || o instanceof HolderSet<?> s && s.unwrapKey().isPresent();
    }

    /** BlocksAttacks.bypassedBy(): 26.1's holder set as 1.21.11's tag (a listed set has none and stays) */
    public static java.util.Optional<?> tagOf(java.util.Optional<?> set) {
        return set == null ? null : set.map(CbWallComponentValues::castTagKey);
    }

    /** new BlocksAttacks(.., bypassedBy, ..): 1.21.11's tag as 26.1's holder set (CbWallTagSet) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static java.util.Optional<?> tagSet(java.util.Optional<?> tag) {
        return tag == null ? null : tag.map(v -> v instanceof TagKey k ? CbWallTagSet.of(k) : v);
    }

    // ---- handed in: the component setters

    /** a 1.21.11 value of a component as 26.1's (every other value as it is) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object in(DataComponentType<?> type, Object value) {
        if (value instanceof EitherHolder<?> e) {
            return CbWallEitherHolders.holder(e);
        }
        if (value instanceof ProvidesTrimMaterial p && type == DataComponents.PROVIDES_TRIM_MATERIAL) {
            return p.material();
        }
        if (value instanceof TagKey k && type == DataComponents.PROVIDES_BANNER_PATTERNS) {
            return CbWallTagSet.of(k);
        }
        return value;
    }

    /** ItemStack.set(type, value) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object set(ItemStack stack, DataComponentType type, Object value) {
        return stack.set(type, in(type, value));
    }

    /** ItemStack.update(type, default, op): the operator sees 26.1's value (read through the casts) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object update(ItemStack stack, DataComponentType type, Object fallback, UnaryOperator op) {
        return stack.update(type, in(type, fallback), v -> in(type, op.apply(v)));
    }

    /** ItemStack.update(type, default, arg, op) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object update(ItemStack stack, DataComponentType type, Object fallback, Object arg, BiFunction op) {
        return stack.update(type, in(type, fallback), arg, (v, a) -> in(type, op.apply(v, a)));
    }

    /** DataComponentMap.Builder.set(type, value) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static DataComponentMap.Builder set(DataComponentMap.Builder builder, DataComponentType type, Object value) {
        return builder.set(type, in(type, value));
    }

    /** DataComponentPatch.Builder.set(type, value) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static DataComponentPatch.Builder set(DataComponentPatch.Builder builder, DataComponentType type, Object value) {
        return builder.set(type, in(type, value));
    }

    /** PatchedDataComponentMap.set(type, value) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object set(PatchedDataComponentMap map, DataComponentType type, Object value) {
        return map.set(type, in(type, value));
    }

    /** Entity.setComponent(type, value) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void setComponent(Entity entity, DataComponentType type, Object value) {
        entity.setComponent(type, in(type, value));
    }

    /**
     * Item.Properties.component(type, value), set while an item is made -- before any registries are loaded: an
     * EitherHolder's key and a banner pattern tag go to 26.1's delayed components, resolved when the item's
     * components are built (vanilla's banner pattern items name their tags the same way)
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Item.Properties component(Item.Properties properties, DataComponentType type, Object value) {
        if (value instanceof EitherHolder<?> e && e.comp_3636.right().isPresent()) {
            return properties.delayedHolderComponent(type, e.comp_3636.right().get());
        }
        if (value instanceof TagKey k && type == DataComponents.PROVIDES_BANNER_PATTERNS) {
            return properties.delayedComponent(type, lookup -> lookup.getOrThrow(k));
        }
        return properties.component(type, in(type, value));
    }
}
