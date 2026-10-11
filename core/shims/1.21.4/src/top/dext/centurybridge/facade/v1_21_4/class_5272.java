package top.dext.centurybridge.facade.v1_21_4;

/**
 * 1.20.1 net/minecraft/class_5272, ModelPredicateProviderRegistry. 1.21.4 removed the
 * runtime predicate registry for data-driven item model definitions; this is the old
 * registry itself, with the old lookup order (damage / damaged on damageable items, then
 * the global providers, then the item's own), so the providers mods register -- through
 * Fabric API's transitive access widener, register is private in vanilla -- are kept
 * and answered exactly as before. What consumed them, the "overrides" of the old item
 * model JSON, 1.21.4 no longer reads: the converter carries them into item model
 * definitions of the centurybridge:legacy_overrides type, which asks this registry
 * (CbLegacyItemModels).
 *
 * Vanilla's own per-item providers are kept where 1.21.4 still has what they read (bow
 * and crossbow, the ones mods copy onto their own items); the rest answer null as an
 * unregistered predicate did, and say so once.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class class_5272 {

    /** GLOBAL */
    public static final java.util.Map<net.minecraft.class_2960, class_1800> field_24443 = new java.util.HashMap<>();
    /** CUSTOM_MODEL_DATA_KEY */
    public static final String field_32947 = "CustomModelData";
    /** DAMAGED_ID */
    public static final net.minecraft.class_2960 field_24444 = net.minecraft.class_2960.method_60656("damaged");
    /** DAMAGE_ID */
    public static final net.minecraft.class_2960 field_24445 = net.minecraft.class_2960.method_60656("damage");
    /** DAMAGED_PROVIDER */
    public static final class_6395 field_24446 = (stack, world, entity, seed) -> stack.method_7986() ? 1.0F : 0.0F;
    /** DAMAGE_PROVIDER */
    public static final class_6395 field_24447 = (stack, world, entity, seed) ->
        net.minecraft.class_3532.method_15363((float) stack.method_7919() / (float) stack.method_7936(), 0.0F, 1.0F);
    /** ITEM_SPECIFIC */
    public static final java.util.Map<net.minecraft.class_1792, java.util.Map<net.minecraft.class_2960, class_1800>> field_24448 =
        new java.util.HashMap<>();

    private static final java.util.Set<String> UNPORTED = java.util.Set.of(
        "brushing", "time", "angle", "cast", "blocking", "throwing", "level", "filled", "broken", "tooting",
        "honey_level", "trim_type");
    private static final java.util.Set<String> WARNED = java.util.concurrent.ConcurrentHashMap.newKeySet();

    static {
        method_27881(net.minecraft.class_2960.method_60656("lefthanded"), (stack, world, entity, seed) ->
            entity != null && entity.method_6068() != net.minecraft.class_1306.field_6183 ? 1.0F : 0.0F);
        method_27881(net.minecraft.class_2960.method_60656("cooldown"), (stack, world, entity, seed) ->
            entity instanceof net.minecraft.class_1657 player ? player.method_7357().method_7905(stack, 0.0F) : 0.0F);
        // the int CustomModelData tag is the first float of 1.21.4's custom_model_data
        // component -- the data fixer carries it there
        method_37106((stack, world, entity, seed) -> {
            net.minecraft.class_9280 cmd = stack.method_57824(net.minecraft.class_9334.field_49637);
            Float f = cmd == null ? null : cmd.method_65362(0);
            return f == null ? 0.0F : (float) f.intValue();
        });

        net.minecraft.class_2960 pull = net.minecraft.class_2960.method_60656("pull");
        net.minecraft.class_2960 pulling = net.minecraft.class_2960.method_60656("pulling");
        method_27879(net.minecraft.class_1802.field_8102, pull, (stack, world, entity, seed) ->
            entity == null || entity.method_6030() != stack ? 0.0F
                : (float) (stack.method_7935(entity) - entity.method_6014()) / 20.0F);
        method_27879(net.minecraft.class_1802.field_8102, pulling, (stack, world, entity, seed) ->
            entity != null && entity.method_6115() && entity.method_6030() == stack ? 1.0F : 0.0F);
        method_27879(net.minecraft.class_1802.field_8399, pull, (stack, world, entity, seed) ->
            entity == null || net.minecraft.class_1764.method_7781(stack) ? 0.0F
                : (float) (stack.method_7935(entity) - entity.method_6014())
                    / (float) net.minecraft.class_1764.method_7775(stack, entity));
        method_27879(net.minecraft.class_1802.field_8399, pulling, (stack, world, entity, seed) ->
            entity != null && entity.method_6115() && entity.method_6030() == stack
                && !net.minecraft.class_1764.method_7781(stack) ? 1.0F : 0.0F);
        method_27879(net.minecraft.class_1802.field_8399, net.minecraft.class_2960.method_60656("charged"),
            (stack, world, entity, seed) -> net.minecraft.class_1764.method_7781(stack) ? 1.0F : 0.0F);
        method_27879(net.minecraft.class_1802.field_8399, net.minecraft.class_2960.method_60656("firework"),
            (stack, world, entity, seed) -> {
                net.minecraft.class_9278 loaded = stack.method_57824(net.minecraft.class_9334.field_49649);
                return loaded != null && loaded.method_57438(net.minecraft.class_1802.field_8639) ? 1.0F : 0.0F;
            });
    }

    public class_5272() {
    }

    /** register(id, provider): a provider for every item */
    public static class_6395 method_27881(net.minecraft.class_2960 id, class_6395 provider) {
        field_24443.put(id, provider);
        return provider;
    }

    /** registerCustomModelData(provider) */
    public static void method_37106(class_1800 provider) {
        field_24443.put(net.minecraft.class_2960.method_60656("custom_model_data"), provider);
    }

    /** register(item, id, provider) */
    public static void method_27879(net.minecraft.class_1792 item, net.minecraft.class_2960 id, class_6395 provider) {
        field_24448.computeIfAbsent(item, k -> new java.util.HashMap<>()).put(id, provider);
    }

    /** get(item, id), the 1.20.1 shape */
    public static class_1800 method_27878(net.minecraft.class_1792 item, net.minecraft.class_2960 id) {
        return lookup(item, item.method_57347().method_57830(net.minecraft.class_9334.field_50072, 0) > 0, id);
    }

    /** get(stack, id), the 1.21.3 shape */
    public static class_1800 method_27878(net.minecraft.class_1799 stack, net.minecraft.class_2960 id) {
        return lookup(stack.method_7909(), stack.method_7936() > 0, id);
    }

    private static class_1800 lookup(net.minecraft.class_1792 item, boolean damageable, net.minecraft.class_2960 id) {
        if (damageable) {
            if (field_24445.equals(id)) {
                return field_24447;
            }
            if (field_24444.equals(id)) {
                return field_24446;
            }
        }
        class_1800 global = field_24443.get(id);
        if (global != null) {
            return global;
        }
        java.util.Map<net.minecraft.class_2960, class_1800> own = field_24448.get(item);
        class_1800 p = own == null ? null : own.get(id);
        if (p == null && id.method_12836().equals("minecraft") && UNPORTED.contains(id.method_12832())
                && WARNED.add(id.method_12832())) {
            org.slf4j.LoggerFactory.getLogger("CenturyBridge").warn(
                "vanilla model predicate minecraft:{} is not carried to 1.21.4 (it reads state 1.21.4 moved into "
                    + "its own item model definitions); asked for {}, answering as unregistered",
                id.method_12832(), item);
        }
        return p;
    }
}
