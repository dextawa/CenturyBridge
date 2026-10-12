package top.dext.centurybridge.step.v1_20_5.rt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 1.20.4's ItemStack API that 1.20.5 dropped or reshaped (the tag itself is CbItemTags): each one
 * does what 1.20.4's did, on the component that now holds the data -- the same component the stack's
 * 1.20.4 tag key maps to, so the tag view and these agree.
 */
public final class CbItemStacks {

    // ---------------------------------------------------------------- names, repair cost, flags

    /** ItemStack.setHoverName(name): the custom name (display.Name in 1.20.4); null clears it */
    public static net.minecraft.class_1799 setHoverName(net.minecraft.class_1799 stack, net.minecraft.class_2561 name) {
        if (name != null) {
            stack.method_57379(net.minecraft.class_9334.field_49631, name);
        } else {
            stack.method_57381(net.minecraft.class_9334.field_49631);
        }
        return stack;
    }

    /** ItemStack.resetHoverName() */
    public static void resetHoverName(net.minecraft.class_1799 stack) {
        stack.method_57381(net.minecraft.class_9334.field_49631);
    }

    /** ItemStack.hasCustomHoverName() */
    public static boolean hasCustomHoverName(net.minecraft.class_1799 stack) {
        return stack.method_57826(net.minecraft.class_9334.field_49631);
    }

    /** ItemStack.getBaseRepairCost(): RepairCost, 0 when unset */
    public static int getBaseRepairCost(net.minecraft.class_1799 stack) {
        Integer cost = stack.method_57824(net.minecraft.class_9334.field_49639);
        return cost == null ? 0 : cost;
    }

    /** ItemStack.setRepairCost(cost): 1.20.4 kept a positive cost and removed any other */
    public static void setRepairCost(net.minecraft.class_1799 stack, int cost) {
        stack.method_57379(net.minecraft.class_9334.field_49639, cost > 0 ? cost : 0);
    }

    /**
     * ItemStack.hideTooltipPart(part): 1.20.4 set the part's bit in HideFlags; done the same way, on the
     * tag view, which hands the flags to the game's upgrade (each part's tooltip flag, or
     * hide_additional_tooltip).
     */
    public static void hideTooltipPart(net.minecraft.class_1799 stack, top.dext.centurybridge.step.v1_20_5.facade.TooltipPart part) {
        net.minecraft.class_2487 tag = CbItemTags.getOrCreateTag(stack);
        tag.method_10569("HideFlags", tag.method_10550("HideFlags") | part.method_30269());
    }

    /** ItemStack.isEdible(): the stack has food properties */
    public static boolean isEdible(net.minecraft.class_1799 stack) {
        return stack.method_57826(net.minecraft.class_9334.field_50075);
    }

    // ---------------------------------------------------------------- attribute modifiers

    /**
     * ItemStack.getAttributeModifiers(slot): the stack's modifiers for the slot -- the stack's own when it
     * has them, else its item's defaults, as 1.20.4 chose between the AttributeModifiers tag and
     * Item.getDefaultAttributeModifiers(slot) (1.20.5's forEachModifier makes the same choice).
     */
    public static com.google.common.collect.Multimap<net.minecraft.class_1320, net.minecraft.class_1322> getAttributeModifiers(
            net.minecraft.class_1799 stack, net.minecraft.class_1304 slot) {
        com.google.common.collect.Multimap<net.minecraft.class_1320, net.minecraft.class_1322> out =
            com.google.common.collect.LinkedHashMultimap.create();
        stack.method_57354(slot, (attribute, modifier) -> out.put(attribute.comp_349(), modifier));
        return out;
    }

    /**
     * ItemStack.addAttributeModifier(attribute, modifier, slot): appended to the stack's own modifier
     * list. As in 1.20.4, the first one a stack gets starts a list of its own (the AttributeModifiers
     * tag), which then stands in for the item's defaults instead of adding to them. A null slot is
     * every slot.
     */
    public static void addAttributeModifier(net.minecraft.class_1799 stack, net.minecraft.class_1320 attribute,
                                            net.minecraft.class_1322 modifier, net.minecraft.class_1304 slot) {
        Optional<?> own = stack.method_57380().method_57845(net.minecraft.class_9334.field_49636);
        net.minecraft.class_9285 base = own != null && own.isPresent()
            ? (net.minecraft.class_9285) own.get() : net.minecraft.class_9285.field_49326;
        net.minecraft.class_9274 group = slot == null ? net.minecraft.class_9274.field_49216 : net.minecraft.class_9274.method_59524(slot);
        stack.method_57379(net.minecraft.class_9334.field_49636,
            base.method_57484(CbItemHolders.attributeHolder(attribute), modifier, group));
    }

    // ---------------------------------------------------------------- adventure mode

    /** ItemStack.hasAdventureModePlaceTagForBlock(registry, block): 1.20.5 parsed the predicates already */
    public static boolean hasAdventureModePlaceTagForBlock(net.minecraft.class_1799 stack, net.minecraft.class_2378<?> blocks,
                                                           net.minecraft.class_2694 block) {
        return stack.method_57357(block);
    }

    /** ItemStack.hasAdventureModeBreakTagForBlock(registry, block) */
    public static boolean hasAdventureModeBreakTagForBlock(net.minecraft.class_1799 stack, net.minecraft.class_2378<?> blocks,
                                                           net.minecraft.class_2694 block) {
        return stack.method_57373(block);
    }

    // ---------------------------------------------------------------- damage

    /**
     * ItemStack.hurt(amount, random, player): 1.20.5's hurtAndBreak(amount, random, player, onBreak) is
     * 1.20.4's hurt with the answer turned into a callback (same unbreaking rolls, durability trigger,
     * damage write): true exactly when that callback runs.
     */
    public static boolean hurt(net.minecraft.class_1799 stack, int amount, net.minecraft.class_5819 random,
                               net.minecraft.class_3222 player) {
        boolean[] broke = {false};
        stack.method_7956(amount, random, player, () -> broke[0] = true);
        return broke[0];
    }

    /**
     * ItemStack.hurtAndBreak(amount, entity, onBroken), 1.20.4's body: nothing on the client or for a
     * creative player; when the stack breaks, onBroken runs, the stack shrinks by one, the player gets
     * the broken-item stat and the damage is reset.
     */
    public static <T extends net.minecraft.class_1309> void hurtAndBreak(net.minecraft.class_1799 stack, int amount, T entity,
                                                                        Consumer<T> onBroken) {
        if (entity.method_37908().field_9236
                || entity instanceof net.minecraft.class_1657 p && p.method_31549().field_7477) {
            return;
        }
        if (!stack.method_7963()) {
            return;
        }
        if (hurt(stack, amount, entity.method_6051(), entity instanceof net.minecraft.class_3222 sp ? sp : null)) {
            onBroken.accept(entity);
            net.minecraft.class_1792 item = stack.method_7909();
            stack.method_7934(1);
            if (entity instanceof net.minecraft.class_1657 p) {
                p.method_7259(net.minecraft.class_3468.field_15383.method_14956(item));
            }
            stack.method_7974(0);
        }
    }

    // ---------------------------------------------------------------- tooltips

    /**
     * ItemStack.getTooltipLines(player, flag): 1.20.4 handed the item the player's level (null without a
     * player); 1.20.5 takes the tooltip context for it.
     */
    public static net.minecraft.class_1792.class_9635 tooltipContext(net.minecraft.class_1657 player) {
        return CbTooltips.fromLevel(player == null ? null : player.method_37908());
    }

    /** ItemStack.appendEnchantmentNames(lines, enchantments): each {id, lvl} entry's full name */
    public static void appendEnchantmentNames(List<net.minecraft.class_2561> lines, net.minecraft.class_2499 enchantments) {
        for (int i = 0; i < enchantments.size(); i++) {
            net.minecraft.class_2487 e = enchantments.method_10602(i);
            net.minecraft.class_2960 id = net.minecraft.class_2960.method_12829(e.method_10558("id"));
            net.minecraft.class_1887 en = id == null ? null : net.minecraft.class_7923.field_41176.method_10223(id);
            if (en != null) {
                lines.add(en.method_8179(net.minecraft.class_3532.method_15340(e.method_10550("lvl"), 0, 255)));
            }
        }
    }

    // ---------------------------------------------------------------- construction

    /** new ItemStack(item, count, Optional tag): 1.20.4 made the stack, then set the tag when there was one */
    public static net.minecraft.class_1799 cbNew(net.minecraft.class_6880<net.minecraft.class_1792> item, int count,
                                                 Optional<net.minecraft.class_2487> tag) {
        net.minecraft.class_1799 stack = new net.minecraft.class_1799(item, count);
        tag.ifPresent(t -> CbItemTags.setTag(stack, t));
        return stack;
    }

    // ---------------------------------------------------------------- 1.20.4's stack codecs

    private static Codec<net.minecraft.class_1792> nonAirItem() {
        return net.minecraft.class_7923.field_41178.method_39673().validate(item -> item == net.minecraft.class_1802.field_8162
            ? DataResult.error(() -> "Item must not be minecraft:air") : DataResult.success(item));
    }

    private static Codec<net.minecraft.class_1799> itemWithCount;
    private static Codec<net.minecraft.class_1799> singleItem;
    private static Codec<net.minecraft.class_1799> advancementIcon;
    private static MapCodec<net.minecraft.class_1799> result;

    /** ItemStack.ITEM_WITH_COUNT_CODEC: {item (not air), count (default 1)} */
    public static Codec<net.minecraft.class_1799> itemWithCountCodec() {
        if (itemWithCount == null) {
            itemWithCount = RecordCodecBuilder.create(i -> i.group(
                nonAirItem().fieldOf("item").forGetter(net.minecraft.class_1799::method_7909),
                Codec.INT.optionalFieldOf("count", 1).forGetter(net.minecraft.class_1799::method_7947)
            ).apply(i, net.minecraft.class_1799::new));
        }
        return itemWithCount;
    }

    /** ItemStack.SINGLE_ITEM_CODEC: an item id (not air) as a stack of one */
    public static Codec<net.minecraft.class_1799> singleItemCodec() {
        if (singleItem == null) {
            singleItem = nonAirItem().xmap(net.minecraft.class_1799::new, net.minecraft.class_1799::method_7909);
        }
        return singleItem;
    }

    /** ItemStack.ADVANCEMENT_ICON_CODEC: {item, nbt (SNBT, optional)}; the nbt is the stack's 1.20.4 tag */
    public static Codec<net.minecraft.class_1799> advancementIconCodec() {
        if (advancementIcon == null) {
            advancementIcon = RecordCodecBuilder.create(i -> i.group(
                net.minecraft.class_7923.field_41178.method_40294().fieldOf("item").forGetter(net.minecraft.class_1799::method_41409),
                net.minecraft.class_2522.field_45952.optionalFieldOf("nbt").forGetter(s -> Optional.ofNullable(CbItemTags.getTag(s)))
            ).apply(i, (item, nbt) -> cbNew(item, 1, nbt)));
        }
        return advancementIcon;
    }

    /** ItemStack.RESULT_CODEC: {result (item id), count} */
    public static MapCodec<net.minecraft.class_1799> resultCodec() {
        if (result == null) {
            result = RecordCodecBuilder.mapCodec(i -> i.group(
                net.minecraft.class_7923.field_41178.method_39673().fieldOf("result").forGetter(net.minecraft.class_1799::method_7909),
                Codec.INT.fieldOf("count").forGetter(net.minecraft.class_1799::method_7947)
            ).apply(i, net.minecraft.class_1799::new));
        }
        return result;
    }

    /** ItemStack.ATTRIBUTE_MODIFIER_FORMAT: 1.20.5 keeps the same "#.##" (ROOT) format on ItemAttributeModifiers */
    public static java.text.DecimalFormat attributeModifierFormat() {
        return net.minecraft.class_9285.field_49329;
    }

    private CbItemStacks() {
    }
}
