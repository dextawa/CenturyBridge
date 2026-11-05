package top.dext.centurybridge.step.v26_1.facade;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.trading.MerchantOffer;
import org.apache.commons.lang3.tuple.Pair;
import top.dext.centurybridge.step.v26_1.rt.CbFapiTrades;
import top.dext.centurybridge.step.v26_1.rt.CbWallEnchantBookPicks;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.ItemCost;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 1.21.11's VillagerTrades (net/minecraft/class_3853) on 26.1, whose trades are data (VillagerTrade / TradeSet
 * registries). The three tables are 1.21.11's, in 1.21.11's shape: profession -> level -> listings, and the wandering
 * trader's pools with their counts. Each vanilla level (and pool) starts as one placeholder listing for the 26.1 trade
 * set the profession names for that level; once a server's registries and tags are loaded the placeholders become one
 * listing per trade of the set (rt/CbFapiTrades), with every edit mods made to the tables kept on top. Villagers and
 * wandering traders pick their offers from these tables as 1.21.11 did. Member names are 1.21.11's intermediary
 * names; the tables are not final (1.21.11's Fabric API replaced two of them with mutable copies).
 */
public class VillagerTrades {

    /** TRADES */
    public static Map<ResourceKey<VillagerProfession>, Int2ObjectMap<ItemListing[]>> field_17067 = CbFapiTrades.initialTrades();
    /** WANDERING_TRADER_TRADES */
    public static List<Pair<ItemListing[], Integer>> field_17724 = CbFapiTrades.initialWanderingTrades();
    /** EXPERIMENTAL_TRADES */
    public static Map<ResourceKey<VillagerProfession>, Int2ObjectMap<ItemListing[]>> field_45128 = CbFapiTrades.initialExperimentalTrades();

    public VillagerTrades() {
    }

    /** 1.21.11's VillagerTrades.ItemListing (net/minecraft/class_3853$class_1652) */
    @FunctionalInterface
    public interface ItemListing {
        /** getOffer: the offer, or null for none */
        MerchantOffer method_7246(ServerLevel level, Entity trader, RandomSource random);
    }

    /**
     * 1.21.11's VillagerTrades.SuspiciousStewForEmerald (net/minecraft/class_3853$class_4166): fields, constructors and
     * getOffer are 1.21.11's (javap of client-1.21.11-intermediary-code.jar) over 26.1's same item, component and offer
     * classes.
     */
    public static class SuspiciousStewForEmerald implements ItemListing {
        /** effects */
        public final SuspiciousStewEffects field_45756;
        /** xp */
        public final int field_18579;
        /** priceMultiplier */
        public final float field_18580;

        public SuspiciousStewForEmerald(Holder<MobEffect> effect, int duration, int xp) {
            this(new SuspiciousStewEffects(List.of(new SuspiciousStewEffects.Entry(effect, duration))), xp, 0.05f);
        }

        public SuspiciousStewForEmerald(SuspiciousStewEffects effects, int xp, float priceMultiplier) {
            this.field_45756 = effects;
            this.field_18579 = xp;
            this.field_18580 = priceMultiplier;
        }

        @Override
        public MerchantOffer method_7246(ServerLevel level, Entity trader, RandomSource random) {
            ItemStack stew = new ItemStack(Items.SUSPICIOUS_STEW, 1);
            stew.set(DataComponents.SUSPICIOUS_STEW_EFFECTS, this.field_45756);
            return new MerchantOffer(new ItemCost(Items.EMERALD), stew, 12, this.field_18579, this.field_18580);
        }
    }

    /**
     * 1.21.11's VillagerTrades.ItemsAndEmeraldsToItems (net/minecraft/class_3853$class_4164): fields, constructors and
     * getOffer are 1.21.11's (javap of client-1.21.11-intermediary-code.jar) over 26.1's same item, cost, offer and
     * enchantment-provider classes. Every constructor is public (a facade's surface is; 1.21.11's ItemStack one was
     * private and its provider one package-private).
     */
    public static class ItemsAndEmeraldsToItems implements ItemListing {
        /** fromItem */
        public final ItemCost field_18563;
        /** emeraldCost */
        public final int field_18565;
        /** toItem */
        public final ItemStack field_18566;
        /** maxUses */
        public final int field_18568;
        /** villagerXp */
        public final int field_18569;
        /** priceMultiplier */
        public final float field_18570;
        /** enchantmentProvider */
        public final Optional<ResourceKey<EnchantmentProvider>> field_51618;

        public ItemsAndEmeraldsToItems(ItemLike fromItem, int fromCount, int emeraldCost, Item toItem, int toCount,
                int maxUses, int villagerXp, float priceMultiplier) {
            this(fromItem, fromCount, emeraldCost, new ItemStack(toItem), toCount, maxUses, villagerXp, priceMultiplier);
        }

        public ItemsAndEmeraldsToItems(ItemLike fromItem, int fromCount, int emeraldCost, ItemStack toItem, int toCount,
                int maxUses, int villagerXp, float priceMultiplier) {
            this(new ItemCost(fromItem, fromCount), emeraldCost, toItem.copyWithCount(toCount), maxUses, villagerXp,
                priceMultiplier, Optional.empty());
        }

        public ItemsAndEmeraldsToItems(ItemLike fromItem, int fromCount, int emeraldCost, ItemLike toItem, int toCount,
                int maxUses, int villagerXp, float priceMultiplier, ResourceKey<EnchantmentProvider> enchantmentProvider) {
            this(new ItemCost(fromItem, fromCount), emeraldCost, new ItemStack(toItem, toCount), maxUses, villagerXp,
                priceMultiplier, Optional.of(enchantmentProvider));
        }

        public ItemsAndEmeraldsToItems(ItemCost fromItem, int emeraldCost, ItemStack toItem, int maxUses, int villagerXp,
                float priceMultiplier, Optional<ResourceKey<EnchantmentProvider>> enchantmentProvider) {
            this.field_18563 = fromItem;
            this.field_18565 = emeraldCost;
            this.field_18566 = toItem;
            this.field_18568 = maxUses;
            this.field_18569 = villagerXp;
            this.field_18570 = priceMultiplier;
            this.field_51618 = enchantmentProvider;
        }

        @Override
        public MerchantOffer method_7246(ServerLevel level, Entity trader, RandomSource random) {
            ItemStack result = this.field_18566.copy();
            this.field_51618.ifPresent(provider -> EnchantmentHelper.enchantItemFromProvider(result, level.registryAccess(),
                provider, level.getCurrentDifficultyAt(trader.blockPosition()), random));
            return new MerchantOffer(new ItemCost(Items.EMERALD, this.field_18565), Optional.of(this.field_18563), result, 0,
                this.field_18568, this.field_18569, this.field_18570);
        }
    }

    /**
     * 1.21.11's VillagerTrades.EnchantBookForEmeralds (net/minecraft/class_3853$class_1648): fields, constructors and
     * getOffer are 1.21.11's (javap of client-1.21.11-intermediary-code.jar). The pick is 1.21.11's
     * registry.getRandomElementOf(tag, random) (on HolderGetter in 26.1); when a climbing 1.21 layer gives back
     * 1.20.6's getRandomEnchantment and explicit lists it installs that pick (rt/CbWallEnchantBookPicks) and the book's
     * enchantment comes from method_59891, so a mod's call and the villager's pick are the same.
     */
    public static class EnchantBookForEmeralds implements ItemListing, CbWallEnchantBookPicks.Explicit {
        /** villagerXp */
        public final int field_18557;
        /** tradeableEnchantments */
        public final TagKey<Enchantment> field_45131;
        /** minLevel */
        public final int field_45132;
        /** maxLevel */
        public final int field_45133;
        /** a 1.20.6 constructor's enchantment list (the 1.21 layer's objects), null for a trade on the tag */
        private List<?> cb$explicit;

        public EnchantBookForEmeralds(int villagerXp, TagKey<Enchantment> tradeableEnchantments) {
            this(villagerXp, 0, Integer.MAX_VALUE, tradeableEnchantments);
        }

        public EnchantBookForEmeralds(int villagerXp, int minLevel, int maxLevel, TagKey<Enchantment> tradeableEnchantments) {
            this.field_18557 = villagerXp;
            this.field_45131 = tradeableEnchantments;
            this.field_45132 = minLevel;
            this.field_45133 = maxLevel;
        }

        @Override
        public void cb$setEnchantments(List enchantments) {
            this.cb$explicit = List.copyOf(enchantments);
        }

        /**
         * 1.20.6's offerable list (the explicit list, else the tag's members in order) as the 1.21 layer gives it back;
         * empty without a 1.21 layer, which is the only caller (its field_45131 getter, CbEnchantBookTrades)
         */
        @Override
        public List cb$tradeable() {
            CbWallEnchantBookPicks.Hook hook = CbWallEnchantBookPicks.hook();
            return hook == null ? List.of() : hook.cb$tradeable(this.cb$explicit, this.field_45131, null);
        }

        /**
         * 1.20.6's getRandomEnchantment as the 1.21 layer gives it back (answering the 1.21 layer's enchantment object,
         * which a converted call casts back to: callReshapes "ret ... as"); null without a 1.21 layer, which never
         * calls it
         */
        public Object cb$pick(RandomSource random, FeatureFlagSet features, Registry<Enchantment> registry) {
            CbWallEnchantBookPicks.Hook hook = CbWallEnchantBookPicks.hook();
            return hook == null ? null : hook.cb$pick(this.cb$explicit, this.field_45131, registry, random, features);
        }

        /**
         * the 1.21 layer's restored EnchantBookForTrades.getRandomEnchantment(random, features): a converted mod's call
         * reaches it through callReshapes (answer cast back to the 1.21 layer's enchantment class, "ret as"); the same
         * pick the villager's getOffer makes. Null without a 1.21 layer (no converted call exists then)
         */
        public Object method_59891(RandomSource random, FeatureFlagSet features) {
            return this.cb$pick(random, features, null);
        }

        @Override
        public MerchantOffer method_7246(ServerLevel level, Entity trader, RandomSource random) {
            Registry<Enchantment> registry = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            Optional<Holder<Enchantment>> picked;
            CbWallEnchantBookPicks.Hook hook = CbWallEnchantBookPicks.hook();
            if (hook != null) {
                Object e = this.cb$pick(random, level.enabledFeatures(), registry);
                picked = e == null ? Optional.empty() : Optional.ofNullable(hook.cb$holder(e));
            } else {
                picked = registry.getRandomElementOf(this.field_45131, random);
            }
            int cost;
            ItemStack book;
            if (picked.isEmpty()) {
                cost = 1;
                book = new ItemStack(Items.BOOK);
            } else {
                Holder<Enchantment> h = picked.get();
                Enchantment enchantment = h.value();
                int lvl = Mth.nextInt(random, Math.max(enchantment.getMinLevel(), this.field_45132),
                    Math.min(enchantment.getMaxLevel(), this.field_45133));
                book = EnchantmentHelper.createBook(new EnchantmentInstance(h, lvl));
                cost = 2 + random.nextInt(5 + lvl * 10) + 3 * lvl;
                if (h.is(EnchantmentTags.DOUBLE_TRADE_PRICE)) {
                    cost *= 2;
                }
                if (cost > 64) {
                    cost = 64;
                }
            }
            return new MerchantOffer(new ItemCost(Items.EMERALD, cost), Optional.of(new ItemCost(Items.BOOK)), book, 12,
                this.field_18557, 0.2f);
        }
    }
}
