package top.dext.centurybridge.step.v1_21_4.facade;

import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.3 ItemProperties (class_5272, gone in 1.21.4): the model predicates item models' "overrides" switched on --
 * the generic ones every item answers, and per-item ones (vanilla's bows, crossbows, compasses ... and every
 * predicate a mod registers, through Fabric API's FabricModelPredicateProviderRegistry). 1.21.3's registrations and
 * lookup, member for member; the values read 1.21.4's components where 1.21.3's changed shape (custom model data,
 * trim materials). CenturyBridge's legacy_overrides item model reads its predicates here.
 */
@Environment(EnvType.CLIENT)
public class ItemProperties {

    /** GENERIC_PROPERTIES */
    public static final Map<net.minecraft.class_2960, ItemPropertyFunction> field_24443 = new HashMap<>();
    /** DAMAGED */
    public static final net.minecraft.class_2960 field_24444 = net.minecraft.class_2960.method_60656("damaged");
    /** DAMAGE */
    public static final net.minecraft.class_2960 field_24445 = net.minecraft.class_2960.method_60656("damage");
    /** PROPERTY_DAMAGED */
    public static final ClampedItemPropertyFunction field_24446 = (stack, level, entity, seed) -> stack.method_7986() ? 1.0F : 0.0F;
    /** PROPERTY_DAMAGE */
    public static final ClampedItemPropertyFunction field_24447 = (stack, level, entity, seed) ->
        net.minecraft.class_3532.method_15363((float) stack.method_7919() / (float) stack.method_7936(), 0.0F, 1.0F);
    /** PROPERTIES */
    public static final Map<net.minecraft.class_1792, Map<net.minecraft.class_2960, ItemPropertyFunction>> field_24448 = new HashMap<>();

    public ItemProperties() {
    }

    /** registerGeneric */
    public static ClampedItemPropertyFunction method_27881(net.minecraft.class_2960 id, ClampedItemPropertyFunction property) {
        field_24443.put(id, property);
        return property;
    }

    /** registerCustomModelData */
    public static void method_37106(ItemPropertyFunction property) {
        field_24443.put(net.minecraft.class_2960.method_60656("custom_model_data"), property);
    }

    /** register */
    public static void method_27879(net.minecraft.class_1792 item, net.minecraft.class_2960 id, ClampedItemPropertyFunction property) {
        field_24448.computeIfAbsent(item, k -> new HashMap<>()).put(id, property);
    }

    /** getProperty */
    public static ItemPropertyFunction method_27878(net.minecraft.class_1799 stack, net.minecraft.class_2960 id) {
        if (stack.method_7936() > 0) {
            if (field_24445.equals(id)) {
                return field_24447;
            }
            if (field_24444.equals(id)) {
                return field_24446;
            }
        }
        ItemPropertyFunction generic = field_24443.get(id);
        if (generic != null) {
            return generic;
        }
        Map<net.minecraft.class_2960, ItemPropertyFunction> perItem = field_24448.get(stack.method_7909());
        return perItem == null ? null : perItem.get(id);
    }

    /** getProperty by item (before 1.21.2): the same lookup for the item's default stack */
    public static ItemPropertyFunction method_27878(net.minecraft.class_1792 item, net.minecraft.class_2960 id) {
        return method_27878(new net.minecraft.class_1799(item), id);
    }

    /** TAG_CUSTOM_MODEL_DATA (before 1.20.5, when custom model data was the stack tag's "CustomModelData") */
    public static final String field_32947 = "CustomModelData";

    /** a beehive / bee nest stack's honey: 1 when its block state says the hive is full (honey level 5) */
    public static int method_63532(net.minecraft.class_1799 stack) {
        net.minecraft.class_9275 props = stack.method_57825(net.minecraft.class_9334.field_49623, net.minecraft.class_9275.field_49284);
        Integer level = props.method_57418(net.minecraft.class_4481.field_20420);
        return level != null && level == 5 ? 1 : 0;
    }

    // ---------------------------------------------------------------- 1.21.3's own registrations

    private static boolean using(net.minecraft.class_1309 entity, net.minecraft.class_1799 stack) {
        return entity != null && entity.method_6115() && entity.method_6030() == stack;
    }

    /**
     * 1.21.3 trim materials carried an item model index (the trim_type predicate); 1.21.4's carry an asset name.
     * Vanilla's ten materials answer their 1.21.3 index; a data-pack material had its own, which 1.21.4 no longer
     * loads, and reads as no trim.
     */
    private static float trimIndex(String assetName) {
        return switch (assetName) {
            case "quartz" -> 0.1F;
            case "iron" -> 0.2F;
            case "netherite" -> 0.3F;
            case "redstone" -> 0.4F;
            case "copper" -> 0.5F;
            case "gold" -> 0.6F;
            case "emerald" -> 0.7F;
            case "diamond" -> 0.8F;
            case "lapis" -> 0.9F;
            case "amethyst" -> 1.0F;
            default -> Float.NEGATIVE_INFINITY;
        };
    }

    static {
        method_27881(net.minecraft.class_2960.method_60656("lefthanded"), (stack, level, entity, seed) ->
            entity != null && entity.method_6068() != net.minecraft.class_1306.field_6183 ? 1.0F : 0.0F);
        method_27881(net.minecraft.class_2960.method_60656("cooldown"), (stack, level, entity, seed) ->
            entity instanceof net.minecraft.class_1657 player ? player.method_7357().method_7905(stack, 0.0F) : 0.0F);
        method_27881(net.minecraft.class_2960.method_60656("trim_type"), (stack, level, entity, seed) -> {
            net.minecraft.class_8053 trim = stack.method_57824(net.minecraft.class_9334.field_49607);
            return trim != null ? trimIndex(trim.comp_3179().comp_349().comp_1208()) : Float.NEGATIVE_INFINITY;
        });
        method_27881(net.minecraft.class_2960.method_60656("broken"), (stack, level, entity, seed) ->
            stack.method_63692() ? 1.0F : 0.0F);
        // 1.21.3 custom model data was one int; 1.21.4 upgrades it to the first entry of its float list
        method_37106((stack, level, entity, seed) -> {
            Float f = stack.method_57825(net.minecraft.class_9334.field_49637, net.minecraft.class_9280.field_55212).method_65362(0);
            return f != null ? f : 0.0F;
        });
        method_27879(net.minecraft.class_1802.field_8102, net.minecraft.class_2960.method_60656("pull"), (stack, level, entity, seed) -> {
            if (entity == null || entity.method_6030() != stack) {
                return 0.0F;
            }
            return (float) (stack.method_7935(entity) - entity.method_6014()) / 20.0F;
        });
        method_27879(net.minecraft.class_1802.field_42716, net.minecraft.class_2960.method_60656("brushing"), (stack, level, entity, seed) ->
            entity != null && entity.method_6030() == stack ? (float) (entity.method_6014() % 10) / 10.0F : 0.0F);
        method_27879(net.minecraft.class_1802.field_8102, net.minecraft.class_2960.method_60656("pulling"), (stack, level, entity, seed) ->
            using(entity, stack) ? 1.0F : 0.0F);
        for (net.minecraft.class_1792 bundle : net.minecraft.class_5537.method_64975()) {
            method_27879(bundle, net.minecraft.class_2960.method_60656("filled"), (stack, level, entity, seed) ->
                net.minecraft.class_5537.method_31561(stack));
        }
        method_27879(net.minecraft.class_1802.field_8557, net.minecraft.class_2960.method_60656("time"), new ClampedItemPropertyFunction() {
            /** rotation */
            private double field_7911;
            /** rota */
            private double field_7910;
            /** lastUpdateTick */
            private long field_7913;

            @Override
            public float unclampedCall(net.minecraft.class_1799 stack, net.minecraft.class_638 level, net.minecraft.class_1309 living, int seed) {
                net.minecraft.class_1297 entity = living != null ? living : stack.method_27319();
                if (entity == null) {
                    return 0.0F;
                }
                if (level == null && entity.method_37908() instanceof net.minecraft.class_638 cl) {
                    level = cl;
                }
                if (level == null) {
                    return 0.0F;
                }
                double target = level.method_8597().comp_645() ? (double) level.method_30274(1.0F) : Math.random();
                return (float) this.wobble(level, target);
            }

            private double wobble(net.minecraft.class_1937 level, double target) {
                if (level.method_8510() != this.field_7913) {
                    this.field_7913 = level.method_8510();
                    double d = target - this.field_7911;
                    d = net.minecraft.class_3532.method_15367(d + 0.5, 1.0) - 0.5;
                    this.field_7910 += d * 0.1;
                    this.field_7910 *= 0.9;
                    this.field_7911 = net.minecraft.class_3532.method_15367(this.field_7911 + this.field_7910, 1.0);
                }
                return this.field_7911;
            }
        });
        method_27879(net.minecraft.class_1802.field_8251, net.minecraft.class_2960.method_60656("angle"), new CompassItemPropertyFunction((level, stack, entity) -> {
            net.minecraft.class_9291 tracker = stack.method_57824(net.minecraft.class_9334.field_49614);
            if (tracker != null) {
                return tracker.comp_2402().orElse(null);
            }
            // 1.21.3 CompassItem.getSpawnPosition(level), gone in 1.21.4: the shared spawn in a natural dimension
            return level.method_8597().comp_645() ? net.minecraft.class_4208.method_19443(level.method_27983(), level.method_43126()) : null;
        }));
        method_27879(net.minecraft.class_1802.field_38747, net.minecraft.class_2960.method_60656("angle"), new CompassItemPropertyFunction((level, stack, entity) ->
            entity instanceof net.minecraft.class_1657 player ? player.method_43122().orElse(null) : null));
        method_27879(net.minecraft.class_1802.field_8399, net.minecraft.class_2960.method_60656("pull"), (stack, level, entity, seed) -> {
            if (entity == null || net.minecraft.class_1764.method_7781(stack)) {
                return 0.0F;
            }
            return (float) (stack.method_7935(entity) - entity.method_6014()) / (float) net.minecraft.class_1764.method_7775(stack, entity);
        });
        method_27879(net.minecraft.class_1802.field_8399, net.minecraft.class_2960.method_60656("pulling"), (stack, level, entity, seed) ->
            using(entity, stack) && !net.minecraft.class_1764.method_7781(stack) ? 1.0F : 0.0F);
        method_27879(net.minecraft.class_1802.field_8399, net.minecraft.class_2960.method_60656("charged"), (stack, level, entity, seed) ->
            net.minecraft.class_1764.method_7781(stack) ? 1.0F : 0.0F);
        method_27879(net.minecraft.class_1802.field_8399, net.minecraft.class_2960.method_60656("firework"), (stack, level, entity, seed) -> {
            net.minecraft.class_9278 projectiles = stack.method_57824(net.minecraft.class_9334.field_49649);
            return projectiles != null && projectiles.method_57438(net.minecraft.class_1802.field_8639) ? 1.0F : 0.0F;
        });
        method_27879(net.minecraft.class_1802.field_8378, net.minecraft.class_2960.method_60656("cast"), (stack, level, entity, seed) -> {
            if (entity == null) {
                return 0.0F;
            }
            boolean main = entity.method_6047() == stack;
            boolean off = entity.method_6079() == stack;
            if (entity.method_6047().method_7909() instanceof net.minecraft.class_1787) {
                off = false;
            }
            return (main || off) && entity instanceof net.minecraft.class_1657 player && player.field_7513 != null ? 1.0F : 0.0F;
        });
        method_27879(net.minecraft.class_1802.field_8255, net.minecraft.class_2960.method_60656("blocking"), (stack, level, entity, seed) ->
            using(entity, stack) ? 1.0F : 0.0F);
        method_27879(net.minecraft.class_1802.field_8547, net.minecraft.class_2960.method_60656("throwing"), (stack, level, entity, seed) ->
            using(entity, stack) ? 1.0F : 0.0F);
        method_27879(net.minecraft.class_1802.field_30904, net.minecraft.class_2960.method_60656("level"), (stack, level, entity, seed) -> {
            net.minecraft.class_9275 props = stack.method_57825(net.minecraft.class_9334.field_49623, net.minecraft.class_9275.field_49284);
            Integer light = props.method_57418(net.minecraft.class_6089.field_31187);
            return light != null ? (float) light / 16.0F : 1.0F;
        });
        method_27879(net.minecraft.class_1802.field_39057, net.minecraft.class_2960.method_60656("tooting"), (stack, level, entity, seed) ->
            using(entity, stack) ? 1.0F : 0.0F);
        method_27879(net.minecraft.class_1802.field_20415, net.minecraft.class_2960.method_60656("honey_level"), (stack, level, entity, seed) ->
            (float) method_63532(stack));
        method_27879(net.minecraft.class_1802.field_20416, net.minecraft.class_2960.method_60656("honey_level"), (stack, level, entity, seed) ->
            (float) method_63532(stack));
    }
}
