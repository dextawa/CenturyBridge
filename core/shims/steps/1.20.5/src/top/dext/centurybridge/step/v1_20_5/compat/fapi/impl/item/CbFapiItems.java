package top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.item;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.item.v1.ModifyItemAttributeModifiersCallback;
import top.dext.centurybridge.step.v1_20_5.rt.CbItemProps;
import top.dext.centurybridge.step.v1_20_5.rt.CbTools;
import top.dext.centurybridge.step.v1_20_5.rt.Guard;

/**
 * Fabric API 1.20.4's stack-aware item hooks (FabricItem / FabricItemStack) on 1.20.5. 1.20.5's FabricItem
 * kept allowContinuingBlockBreaking, getRecipeRemainder and canBeEnchantedWith, renamed allowNbtUpdateAnimation
 * (allowComponentsUpdateAnimation: wiring renames), and dropped the rest -- 1.20.5 reads food and attribute
 * modifiers as stack components and asks Item.isCorrectForDrops(stack, state). What 1.20.4 made of them:
 * <ul>
 * <li>getAttributeModifiers(stack, slot): ItemStack.getAttributeModifiers(slot) asked it (the default: the
 *     item's getAttributeModifiers(slot)) when the stack had no modifiers of its own, then fired
 *     ModifyItemAttributeModifiersCallback on a copy -- {@link #attributeModifiers}, from CbFapiItemStackMixin
 *     at ItemStack.applyAttributeModifiers;</li>
 * <li>getFoodComponent(stack): every food check of a stack asked it (the default: the item's food) --
 *     {@link #food}, the FOOD of the stack's component view (CbFapiItemStackMixin at getComponents), for an
 *     item whose class overrides it;</li>
 * <li>isSuitableFor(stack, state): ItemStack.isSuitableFor(state) asked it (the default: the item's
 *     isSuitableFor(state)) -- 1.20.5's isCorrectForDrops(stack, state), which a 1.20.4 override answers
 *     through a guarded override hook ({@link #superIsSuitableFor});</li>
 * <li>getIntrinsicEnchantments(stack): EnchantmentHelper's level of an enchantment, and the levels it walks
 *     over a stack, were at least the intrinsic ones -- CbFapiEnchantmentHelperMixin ({@link #intrinsicLevel}).</li>
 * </ul>
 * A mod class declaring one of these 1.20.4 methods implements the matching interface here (adapterHooks),
 * which is how 1.20.5's side reaches it; 1.20.4 code's calls of them reach these statics (instanceRedirects),
 * and an override's super call 1.20.4's default (superRedirects). The vanilla 1.20.4 methods the defaults
 * call are s4item's: CbTools.getDefaultAttributeModifiers, CbItemProps.getFoodProperties.
 */
public final class CbFapiItems {

    private CbFapiItems() {
    }

    /** a mod item class declaring 1.20.4's FabricItem.getAttributeModifiers(stack, slot) */
    public interface StackModifiers {
        Multimap<net.minecraft.class_1320, net.minecraft.class_1322> getAttributeModifiers(net.minecraft.class_1799 stack, net.minecraft.class_1304 slot);
    }

    /** a mod item class declaring 1.20.4's FabricItem.getFoodComponent(stack) */
    public interface StackFood {
        net.minecraft.class_4174 getFoodComponent(net.minecraft.class_1799 stack);
    }

    /** a mod item class declaring 1.20.4's FabricItem.getIntrinsicEnchantments(stack) */
    public interface StackIntrinsic {
        Object2IntMap<net.minecraft.class_1887> getIntrinsicEnchantments(net.minecraft.class_1799 stack);
    }

    /** a mod item class declaring 1.20.4's FabricItem.isSuitableFor(stack, state) */
    public interface StackSuitable {
        boolean isSuitableFor(net.minecraft.class_1799 stack, net.minecraft.class_2680 state);
    }

    /** a mod class implementing 1.20.4's FabricItemStack itself and declaring its getFoodComponent() */
    public interface OwnFood {
        net.minecraft.class_4174 getFoodComponent();
    }

    /** a mod item class declaring 1.20.4's vanilla Item.isSuitableFor(state) (method_7856) */
    public interface Suitable {
        boolean method_7856(net.minecraft.class_2680 state);
    }

    private static volatile boolean modifierListeners;

    /** ModifyItemAttributeModifiersCallback's invoker factory: whether it has listeners now */
    public static void modifierListeners(boolean any) {
        modifierListeners = any;
    }

    private static net.minecraft.class_1792 item(Object fabricItem) {
        return (net.minecraft.class_1792) fabricItem;
    }

    // ---------------------------------------------------------------- attribute modifiers

    /** item.getAttributeModifiers(stack, slot) (1.20.4's FabricItem): the class's own, else the default */
    public static Multimap<net.minecraft.class_1320, net.minecraft.class_1322> getAttributeModifiers(
            net.minecraft.class_1792 item, net.minecraft.class_1799 stack, net.minecraft.class_1304 slot) {
        return item instanceof StackModifiers m ? m.getAttributeModifiers(stack, slot) : CbTools.getDefaultAttributeModifiers(item, slot);
    }

    /** ... called through FabricItem */
    public static Multimap<net.minecraft.class_1320, net.minecraft.class_1322> getAttributeModifiers(
            net.fabricmc.fabric.api.item.v1.FabricItem item, net.minecraft.class_1799 stack, net.minecraft.class_1304 slot) {
        return item instanceof StackModifiers m ? m.getAttributeModifiers(stack, slot) : CbTools.getDefaultAttributeModifiers(item(item), slot);
    }

    /** FabricItem's default (a super call): the item's 1.20.4 getAttributeModifiers(slot) */
    public static Multimap<net.minecraft.class_1320, net.minecraft.class_1322> superGetAttributeModifiers(
            net.fabricmc.fabric.api.item.v1.FabricItem item, net.minecraft.class_1799 stack, net.minecraft.class_1304 slot) {
        return CbTools.getDefaultAttributeModifiers(item(item), slot);
    }

    /**
     * A stack's modifiers in a slot as 1.20.4's ItemStack.getAttributeModifiers(slot) made them past its own
     * (NBT) ones: the item's stack-aware answer on a modifiable copy, ModifyItemAttributeModifiersCallback
     * fired on it. Null when 1.20.4's Fabric API has no say -- the item's class has no stack-aware override
     * and the callback no listener -- so 1.20.5's path stands.
     */
    public static Multimap<net.minecraft.class_1320, net.minecraft.class_1322> attributeModifiers(
            net.minecraft.class_1799 stack, net.minecraft.class_1304 slot) {
        net.minecraft.class_1792 item = stack.method_7909();
        if (!(item instanceof StackModifiers) && !modifierListeners) {
            return null;
        }
        Multimap<net.minecraft.class_1320, net.minecraft.class_1322> modifiers = LinkedHashMultimap.create(getAttributeModifiers(item, stack, slot));
        ModifyItemAttributeModifiersCallback.EVENT.invoker().modifyAttributeModifiers(stack, slot, modifiers);
        return modifiers;
    }

    /**
     * CbFapiItemStackMixin, ItemStack.applyAttributeModifiers(slot, consumer): for a stack whose component patch
     * sets no attribute modifiers, 1.20.4's {@link #attributeModifiers} handed to the consumer, each attribute
     * as its registry entry. Whether it answered (else vanilla goes on).
     */
    public static boolean feedAttributeModifiers(net.minecraft.class_1799 stack, net.minecraft.class_1304 slot,
                                                 BiConsumer<net.minecraft.class_6880<net.minecraft.class_1320>, net.minecraft.class_1322> consumer) {
        if (stack.method_7960() || stack.method_57380().method_57845(net.minecraft.class_9334.field_49636) != null) {
            return false;
        }
        Multimap<net.minecraft.class_1320, net.minecraft.class_1322> modifiers = attributeModifiers(stack, slot);
        if (modifiers == null) {
            return false;
        }
        for (Map.Entry<net.minecraft.class_1320, net.minecraft.class_1322> e : modifiers.entries()) {
            consumer.accept(net.minecraft.class_7923.field_41190.method_47983(e.getKey()), e.getValue());
        }
        return true;
    }

    // ---------------------------------------------------------------- food

    /** item.getFoodComponent(stack) (1.20.4's FabricItem): the class's own, else the default */
    public static net.minecraft.class_4174 getFoodComponent(net.minecraft.class_1792 item, net.minecraft.class_1799 stack) {
        return item instanceof StackFood f ? f.getFoodComponent(stack) : CbItemProps.getFoodProperties(item);
    }

    /** ... called through FabricItem */
    public static net.minecraft.class_4174 getFoodComponent(net.fabricmc.fabric.api.item.v1.FabricItem item, net.minecraft.class_1799 stack) {
        return item instanceof StackFood f ? f.getFoodComponent(stack) : CbItemProps.getFoodProperties(item(item));
    }

    /** FabricItem's default (a super call): the item's 1.20.4 getFoodComponent() */
    public static net.minecraft.class_4174 superGetFoodComponent(net.fabricmc.fabric.api.item.v1.FabricItem item, net.minecraft.class_1799 stack) {
        return CbItemProps.getFoodProperties(item(item));
    }

    /**
     * stack.getFoodComponent() (1.20.4's FabricItemStack): the food eating the stack gives -- the stack's FOOD
     * component, which for an item overriding the stack-aware method is that method's answer (the component
     * view, {@link #withFood}).
     */
    public static net.minecraft.class_4174 stackFood(net.minecraft.class_1799 stack) {
        return stack.method_57824(net.minecraft.class_9334.field_50075);
    }

    /** ... called through FabricItemStack: a mod's own implementation answers itself, a stack as above */
    public static net.minecraft.class_4174 stackFood(net.fabricmc.fabric.api.item.v1.FabricItemStack stack) {
        return stack instanceof OwnFood f ? f.getFoodComponent() : stackFood((net.minecraft.class_1799) (Object) stack);
    }

    /** FabricItemStack's default (a super call): the stack's item asked with the stack */
    public static net.minecraft.class_4174 superStackFood(net.fabricmc.fabric.api.item.v1.FabricItemStack self) {
        net.minecraft.class_1799 stack = (net.minecraft.class_1799) (Object) self;
        return getFoodComponent(stack.method_7909(), stack);
    }

    /** the stack-aware food of a stack of a StackFood item (null: not food, as 1.20.4's isFood said) */
    public static net.minecraft.class_4174 food(net.minecraft.class_1799 stack) {
        return ((StackFood) stack.method_7909()).getFoodComponent(stack);
    }

    /**
     * CbFapiItemStackMixin, ItemStack.getComponents(): for a stack of a StackFood item, its components with FOOD
     * as {@link #food} answers -- asked only when FOOD is (get / contains), or when the whole set is (types,
     * size, iteration). Writes go to the stack's own components, and its codecs read its patch, so the view is
     * never saved or sent; the client works it out the same way.
     */
    public static net.minecraft.class_9323 withFood(net.minecraft.class_1799 stack, net.minecraft.class_9323 components) {
        if (components == null || stack.method_7960() || !(stack.method_7909() instanceof StackFood)) {
            return components;
        }
        return new FoodView(stack, components);
    }

    /** a stack's components with the stack-aware FOOD */
    private static final class FoodView implements net.minecraft.class_9323 {
        private final net.minecraft.class_1799 stack;
        private final net.minecraft.class_9323 base;

        FoodView(net.minecraft.class_1799 stack, net.minecraft.class_9323 base) {
            this.stack = stack;
            this.base = base;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T method_57829(net.minecraft.class_9331<? extends T> type) {
            if (type == net.minecraft.class_9334.field_50075) {
                return (T) food(stack);
            }
            return base.method_57829(type);
        }

        @Override
        public Set<net.minecraft.class_9331<?>> method_57831() {
            Set<net.minecraft.class_9331<?>> types = base.method_57831();
            boolean has = types.contains(net.minecraft.class_9334.field_50075);
            boolean food = food(stack) != null;
            if (has == food) {
                return types;
            }
            Set<net.minecraft.class_9331<?>> out = new it.unimi.dsi.fastutil.objects.ReferenceArraySet<>(types);
            if (food) {
                out.add(net.minecraft.class_9334.field_50075);
            } else {
                out.remove(net.minecraft.class_9334.field_50075);
            }
            return out;
        }

        // contains, getOrDefault, copy, iterator, stream, size, isEmpty, filtered: ComponentMap's defaults,
        // which answer through get and getTypes above, so every view of FOOD agrees
    }

    // ---------------------------------------------------------------- intrinsic enchantments

    /** item.getIntrinsicEnchantments(stack) (1.20.4's FabricItem): the class's own, else none */
    public static Object2IntMap<net.minecraft.class_1887> getIntrinsicEnchantments(net.minecraft.class_1792 item, net.minecraft.class_1799 stack) {
        return item instanceof StackIntrinsic i ? i.getIntrinsicEnchantments(stack) : Object2IntMaps.emptyMap();
    }

    /** ... called through FabricItem */
    public static Object2IntMap<net.minecraft.class_1887> getIntrinsicEnchantments(net.fabricmc.fabric.api.item.v1.FabricItem item, net.minecraft.class_1799 stack) {
        return item instanceof StackIntrinsic i ? i.getIntrinsicEnchantments(stack) : Object2IntMaps.emptyMap();
    }

    /** FabricItem's default (a super call): none */
    public static Object2IntMap<net.minecraft.class_1887> superGetIntrinsicEnchantments(net.fabricmc.fabric.api.item.v1.FabricItem item, net.minecraft.class_1799 stack) {
        return Object2IntMaps.emptyMap();
    }

    /** the intrinsic level of an enchantment on a stack (0 for an item no 1.20.4 class gives any) */
    public static int intrinsicLevel(net.minecraft.class_1799 stack, net.minecraft.class_1887 enchantment) {
        if (!(stack.method_7909() instanceof StackIntrinsic i)) {
            return 0;
        }
        return i.getIntrinsicEnchantments(stack).getOrDefault(enchantment, 0);
    }

    /**
     * CbFapiEnchantmentHelperMixin, EnchantmentHelper.forEachEnchantment(consumer, stack): the stack's
     * enchantments each at least at its intrinsic level -- 1.20.4 walked the stack's own enchantments so
     * (intrinsic ones the stack lacks were seen only by getLevel).
     */
    public static net.minecraft.class_9304 withIntrinsic(net.minecraft.class_1799 stack, net.minecraft.class_9304 enchantments) {
        if (!(stack.method_7909() instanceof StackIntrinsic i)) {
            return enchantments;
        }
        Object2IntMap<net.minecraft.class_1887> intrinsic = i.getIntrinsicEnchantments(stack);
        if (intrinsic.isEmpty()) {
            return enchantments;
        }
        net.minecraft.class_9304.class_9305 builder = null;
        for (Object2IntMap.Entry<net.minecraft.class_6880<net.minecraft.class_1887>> e : enchantments.method_57539()) {
            net.minecraft.class_1887 ench = e.getKey().comp_349();
            int level = intrinsic.getOrDefault(ench, 0);
            if (level > e.getIntValue()) {
                if (builder == null) {
                    builder = new net.minecraft.class_9304.class_9305(enchantments);
                }
                builder.method_57547(ench, level);
            }
        }
        return builder == null ? enchantments : builder.method_57549();
    }

    // ---------------------------------------------------------------- suitable for a block

    /** item.isSuitableFor(stack, state) (1.20.4's FabricItem): the class's own, else 1.20.5's isCorrectForDrops */
    public static boolean isSuitableFor(net.minecraft.class_1792 item, net.minecraft.class_1799 stack, net.minecraft.class_2680 state) {
        return item instanceof StackSuitable s ? s.isSuitableFor(stack, state) : item.method_58405(stack, state);
    }

    /** ... called through FabricItem */
    public static boolean isSuitableFor(net.fabricmc.fabric.api.item.v1.FabricItem item, net.minecraft.class_1799 stack, net.minecraft.class_2680 state) {
        return item instanceof StackSuitable s ? s.isSuitableFor(stack, state) : item(item).method_58405(stack, state);
    }

    /**
     * FabricItem's default (a super call of a 1.20.4 override): the item's vanilla isSuitableFor(state) -- the
     * class's own 1.20.4 one, else what its superclass answers for 1.20.5's isCorrectForDrops(stack, state),
     * reached past this class's synthesized one through the hook's guard.
     */
    public static boolean superIsSuitableFor(net.fabricmc.fabric.api.item.v1.FabricItem fabricItem, net.minecraft.class_1799 stack, net.minecraft.class_2680 state) {
        net.minecraft.class_1792 item = item(fabricItem);
        if (item instanceof Suitable s) {
            return s.method_7856(state);
        }
        boolean entered = Guard.cbEnter(item);
        try {
            return item.method_58405(stack, state);
        } finally {
            if (entered) {
                Guard.cbExit(item);
            }
        }
    }
}
