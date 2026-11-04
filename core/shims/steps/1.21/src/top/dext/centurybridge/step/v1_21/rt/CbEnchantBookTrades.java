package top.dext.centurybridge.step.v1_21.rt;

import top.dext.centurybridge.step.v1_21.facade.class_1887;

/**
 * 1.20.6's constructors of VillagerTrades.EnchantBookForEmeralds (class_3853$class_1648), as factories over 1.21's
 * (ctorRedirects; a 1.20.6 mod's constructor @Invoker comes here through MixinBridge's constructor-invoker bridging).
 * 1.20.6 kept a list of offerable enchantments; 1.21 keeps an enchantment tag:
 * <ul>
 * <li>(villagerXp): 1.20.6 offered every enchantment whose isTradeable said so; 1.21's equivalent is the #tradeable tag
 *     (EnchantmentTags.TRADEABLE), which 1.21's own default librarian trade uses.</li>
 * <li>(villagerXp, enchantments...) and (villagerXp, minLevel, maxLevel, enchantments...): the trade is built on the
 *     #tradeable tag and keeps the given list (CbVillagerEnchantBookMixin.cb$explicit), which its restored 1.20.6 pick
 *     (getRandomEnchantment, method_59891) draws from in the order given, as 1.20.6 drew from its list.</li>
 * </ul>
 */
public final class CbEnchantBookTrades {
    private CbEnchantBookTrades() {
    }

    /** a trade carrying a 1.20.6 enchantment list (CbVillagerEnchantBookMixin) */
    public interface Explicit {
        void cb$setEnchantments(java.util.List<class_1887> enchantments);

        /** the enchantments the trade offers, as 1.20.6's list: the explicit list a 1.20.6 constructor gave, else the tag's members in order */
        java.util.List<class_1887> cb$tradeable();
    }

    /**
     * 1.20.6 EnchantBookForEmeralds.tradeableEnchantments (field_45131, a List of enchantments; 1.21 keeps the TagKey under
     * the same id): read by a converted mod through its access widener (instanceFieldGetters). The list the restored
     * pick draws from, unmodifiable as 1.20.6's (List.of / Arrays.asList of the constructor).
     */
    public static java.util.List<class_1887> cbTradeableEnchantments(net.minecraft.class_3853.class_1648 trade) {
        return java.util.Collections.unmodifiableList(((Explicit) (Object) trade).cb$tradeable());
    }

    /**
     * 1.20.6 VillagerTrades.enchantedItem helper (private static method_53832(item, enchantment, level)): 1.20.6's body,
     * new ItemStack(item) then enchant(enchantment, level); the enchant takes the enchantment's world holder as the step's
     * ItemStack.enchant reshape does (CbEnchantments.cbHolder). 1.21 dropped it with the trades that called it (their
     * enchantments now come from enchantment providers).
     */
    public static net.minecraft.class_1799 method_53832(net.minecraft.class_1792 item, class_1887 enchantment, int level) {
        net.minecraft.class_1799 stack = new net.minecraft.class_1799(item);
        stack.method_7978(CbEnchantments.cbHolder(enchantment), level);
        return stack;
    }

    /**
     * 1.20.6 ItemsAndEmeraldsToItems(cost, emeraldCost, result, maxUses, xp, priceMultiplier) (class_3853$class_4164): 1.21's
     * constructor with no enchantment provider (Optional.empty(): getOffer then enchants nothing, as 1.20.6 did not)
     */
    public static net.minecraft.class_3853.class_4164 cbNewItemsAndEmeralds(net.minecraft.class_9306 cost, int emeraldCost,
                                                                          net.minecraft.class_1799 result, int maxUses, int xp,
                                                                          float priceMultiplier) {
        return new net.minecraft.class_3853.class_4164(cost, emeraldCost, result, maxUses, xp, priceMultiplier, java.util.Optional.empty());
    }

    /** 1.20.6 EnchantBookForEmeralds(villagerXp) */
    public static net.minecraft.class_3853.class_1648 cbNew(int villagerXp) {
        return new net.minecraft.class_3853.class_1648(villagerXp, net.minecraft.class_9636.field_51545);
    }

    /** 1.20.6 EnchantBookForEmeralds(villagerXp, enchantments...) */
    public static net.minecraft.class_3853.class_1648 cbNewListed(int villagerXp, class_1887[] enchantments) {
        return cbNewRanged(villagerXp, 0, Integer.MAX_VALUE, enchantments);
    }

    /** 1.20.6 EnchantBookForEmeralds(villagerXp, minLevel, maxLevel, enchantments...) */
    public static net.minecraft.class_3853.class_1648 cbNewRanged(int villagerXp, int minLevel, int maxLevel, class_1887[] enchantments) {
        net.minecraft.class_3853.class_1648 trade = new net.minecraft.class_3853.class_1648(villagerXp, minLevel, maxLevel,
            net.minecraft.class_9636.field_51545);
        ((Explicit) (Object) trade).cb$setEnchantments(java.util.Arrays.asList(enchantments));
        return trade;
    }
}
