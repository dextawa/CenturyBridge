package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's MerchantOffer / MerchantOffers API on 1.20.5's (area s4fin, world/item/trading).
 *
 * <p>Costs. 1.20.4 priced an offer in item stacks (costA, costB -- an empty stack for none) and a stack
 * satisfied a cost when it was the same item with a tag containing the cost's tag (NbtUtils.compareNbt);
 * 1.20.5 prices it in ItemCosts (item, count, a component predicate). The cost standing for a 1.20.4 stack
 * ({@link #cost}) has the stack's item and count and, as predicate, exactly the components the stack adds
 * to its item's defaults -- a 1.20.5 stack satisfies it when it carries those (1.20.4's "contains the cost's
 * tag" over the components the tag speaks for). costB empty is no second cost.
 *
 * <p>NBT. 1.20.4's offer NBT (buy, buyB, sell, uses, maxUses, rewardExp, xp, priceMultiplier, specialPrice,
 * demand) and MerchantOffers' {Recipes: [...]} are 1.20.5's MerchantOffer / MerchantOffers codecs, which
 * write those keys (buy / buyB as ItemCosts, sell as an item stack) -- encoded and decoded through them with
 * the running world's registries.
 */
public final class CbFinItemTrading {

    private CbFinItemTrading() {
    }

    /** the ItemCost of a 1.20.4 cost stack */
    public static net.minecraft.class_9306 cost(net.minecraft.class_1799 stack) {
        net.minecraft.class_9329.class_9330 b = net.minecraft.class_9329.method_57862();
        for (java.util.Map.Entry<net.minecraft.class_9331<?>, java.util.Optional<?>> e : stack.method_57380().method_57846()) {
            if (e.getValue().isPresent()) {
                put(b, e.getKey(), e.getValue().get());
            }
        }
        return new net.minecraft.class_9306(stack.method_41409(), stack.method_7947(), b.method_57871());
    }

    @SuppressWarnings("unchecked")
    private static <T> void put(net.minecraft.class_9329.class_9330 b, net.minecraft.class_9331<T> type, Object value) {
        b.method_57872(type, (T) value);
    }

    /** an optional second cost: none for an empty stack */
    public static java.util.Optional<net.minecraft.class_9306> costB(net.minecraft.class_1799 stack) {
        return stack == null || stack.method_7960() ? java.util.Optional.empty() : java.util.Optional.of(cost(stack));
    }

    // ---------------------------------------------------------------- NBT

    private static <T> com.mojang.serialization.DynamicOps<net.minecraft.class_2520> ops() {
        return net.minecraft.class_6903.method_46632(net.minecraft.class_2509.field_11560, CbRegistries.current());
    }

    /** a 1.20.4 MerchantOffer subclass declaring createTag() */
    public interface LegacyOfferTag {
        net.minecraft.class_2487 method_8251();
    }

    /** a 1.20.4 MerchantOffers subclass declaring createTag() */
    public interface LegacyOffersTag {
        net.minecraft.class_2487 method_8268();
    }

    /** a 1.20.4 MerchantOffers subclass declaring writeToStream(buf) */
    public interface LegacyOffersStream {
        void method_8270(net.minecraft.class_2540 buf);
    }

    /** offer.createTag(): the offer's own when its class declares one (1.20.4's virtual call), else 1.20.4's */
    public static net.minecraft.class_2487 method_8251(net.minecraft.class_1914 offer) {
        return offer instanceof LegacyOfferTag own ? own.method_8251() : superOfferTag(offer);
    }

    /** MerchantOffer.createTag()'s body: 1.20.5's offer codec writes 1.20.4's keys */
    public static net.minecraft.class_2487 superOfferTag(net.minecraft.class_1914 offer) {
        net.minecraft.class_2520 t = net.minecraft.class_1914.field_48849.encodeStart(ops(), offer).getOrThrow();
        return t instanceof net.minecraft.class_2487 c ? c : new net.minecraft.class_2487();
    }

    /** new MerchantOffer(tag): 1.20.5's offer codec */
    public static net.minecraft.class_1914 fromTag(net.minecraft.class_2487 tag) {
        return net.minecraft.class_1914.field_48849.parse(ops(), tag).getOrThrow();
    }

    /** offers.createTag(): the list's own when its class declares one, else 1.20.4's */
    public static net.minecraft.class_2487 method_8268(net.minecraft.class_1916 offers) {
        return offers instanceof LegacyOffersTag own ? own.method_8268() : superOffersTag(offers);
    }

    /** MerchantOffers.createTag()'s body: {Recipes: [each offer's createTag()]} */
    public static net.minecraft.class_2487 superOffersTag(net.minecraft.class_1916 offers) {
        net.minecraft.class_2487 out = new net.minecraft.class_2487();
        net.minecraft.class_2499 list = new net.minecraft.class_2499();
        for (int i = 0; i < offers.size(); i++) {
            list.add(method_8251(offers.get(i)));
        }
        out.method_10566("Recipes", list);
        return out;
    }

    /** offers.writeToStream(buf): the list's own when its class declares one, else 1.20.4's (1.20.5's offers stream codec) */
    public static void method_8270(net.minecraft.class_1916 offers, net.minecraft.class_2540 buf) {
        if (offers instanceof LegacyOffersStream own) {
            own.method_8270(buf);
        } else {
            CbFinItemStatics.method_8270(offers, buf);
        }
    }

    /** new MerchantOffers(tag): the offers of its Recipes list */
    public static net.minecraft.class_1916 offersFromTag(net.minecraft.class_2487 tag) {
        net.minecraft.class_1916 out = new net.minecraft.class_1916();
        net.minecraft.class_2499 list = tag.method_10554("Recipes", 10);
        for (int i = 0; i < list.size(); i++) {
            out.add(fromTag(list.method_10602(i)));
        }
        return out;
    }

    /** a 1.20.4 MerchantOffers subclass's super(tag): its offers, added after the list is made */
    public static void offersPost(net.minecraft.class_1916 offers, net.minecraft.class_2487 tag) {
        offers.addAll(offersFromTag(tag));
    }
}
