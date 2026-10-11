package top.dext.centurybridge.facade.v26_2;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.CustomModelData;

/**
 * 1.20.1 net/minecraft/class_5272, ModelPredicateProviderRegistry. 1.21.4 removed the
 * runtime predicate registry for data-driven item model definitions; this is the old
 * registry itself, with the old lookup order (damage / damaged on damageable items, then
 * the global providers, then the item's own), so the providers mods register -- through
 * Fabric API's transitive access widener, register is private in vanilla -- are kept and
 * answered exactly as before. What consumed them, the "overrides" of the old item model
 * JSON, 26.x no longer reads: the converter carries them into item model definitions of
 * the centurybridge:legacy_overrides type, which asks this registry (CbLegacyItemModels).
 *
 * Vanilla's own per-item providers are kept where 26.x still has what they read (bow and
 * crossbow, the ones mods copy onto their own items); the rest answer null as an
 * unregistered predicate did, and say so once.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class class_5272 {

    /** GLOBAL */
    public static final Map<Identifier, class_1800> field_24443 = new HashMap<>();
    /** CUSTOM_MODEL_DATA_KEY */
    public static final String field_32947 = "CustomModelData";
    /** DAMAGED_ID */
    public static final Identifier field_24444 = Identifier.withDefaultNamespace("damaged");
    /** DAMAGE_ID */
    public static final Identifier field_24445 = Identifier.withDefaultNamespace("damage");
    /** DAMAGED_PROVIDER */
    public static final class_6395 field_24446 = (stack, world, entity, seed) -> stack.isDamaged() ? 1.0F : 0.0F;
    /** DAMAGE_PROVIDER */
    public static final class_6395 field_24447 = (stack, world, entity, seed) ->
        Mth.clamp((float) stack.getDamageValue() / (float) stack.getMaxDamage(), 0.0F, 1.0F);
    /** ITEM_SPECIFIC */
    public static final Map<Item, Map<Identifier, class_1800>> field_24448 = new HashMap<>();

    private static final Set<String> UNPORTED = Set.of(
        "brushing", "time", "angle", "cast", "blocking", "throwing", "level", "filled", "broken", "tooting",
        "honey_level", "trim_type");
    private static final Set<String> WARNED = ConcurrentHashMap.newKeySet();

    static {
        method_27881(Identifier.withDefaultNamespace("lefthanded"), (stack, world, entity, seed) ->
            entity != null && entity.getMainArm() != HumanoidArm.RIGHT ? 1.0F : 0.0F);
        method_27881(Identifier.withDefaultNamespace("cooldown"), (stack, world, entity, seed) ->
            entity instanceof Player player ? player.getCooldowns().getCooldownPercent(stack, 0.0F) : 0.0F);
        // the int CustomModelData tag is the first float of the custom_model_data
        // component -- the data fixer carries it there
        method_37106((stack, world, entity, seed) -> {
            CustomModelData cmd = stack.get(DataComponents.CUSTOM_MODEL_DATA);
            Float f = cmd == null ? null : cmd.getFloat(0);
            return f == null ? 0.0F : (float) f.intValue();
        });

        Identifier pull = Identifier.withDefaultNamespace("pull");
        Identifier pulling = Identifier.withDefaultNamespace("pulling");
        method_27879(Items.BOW, pull, (stack, world, entity, seed) ->
            entity == null || entity.getUseItem() != stack ? 0.0F
                : (float) (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F);
        method_27879(Items.BOW, pulling, (stack, world, entity, seed) ->
            entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
        method_27879(Items.CROSSBOW, pull, (stack, world, entity, seed) ->
            entity == null || CrossbowItem.isCharged(stack) ? 0.0F
                : (float) (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks())
                    / (float) CrossbowItem.getChargeDuration(stack, entity));
        method_27879(Items.CROSSBOW, pulling, (stack, world, entity, seed) ->
            entity != null && entity.isUsingItem() && entity.getUseItem() == stack
                && !CrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
        method_27879(Items.CROSSBOW, Identifier.withDefaultNamespace("charged"),
            (stack, world, entity, seed) -> CrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
        method_27879(Items.CROSSBOW, Identifier.withDefaultNamespace("firework"),
            (stack, world, entity, seed) -> {
                ChargedProjectiles loaded = stack.get(DataComponents.CHARGED_PROJECTILES);
                return loaded != null && loaded.contains(Items.FIREWORK_ROCKET) ? 1.0F : 0.0F;
            });
    }

    public class_5272() {
    }

    /** register(id, provider): a provider for every item */
    public static class_6395 method_27881(Identifier id, class_6395 provider) {
        synchronized (field_24443) {
            field_24443.put(id, provider);
        }
        return provider;
    }

    /** registerCustomModelData(provider) */
    public static void method_37106(class_1800 provider) {
        synchronized (field_24443) {
            field_24443.put(Identifier.withDefaultNamespace("custom_model_data"), provider);
        }
    }

    /** register(item, id, provider) */
    public static void method_27879(Item item, Identifier id, class_6395 provider) {
        synchronized (field_24448) {
            field_24448.computeIfAbsent(item, k -> new HashMap<>()).put(id, provider);
        }
    }

    /** get(item, id), the 1.20.1 shape */
    public static class_1800 method_27878(Item item, Identifier id) {
        return lookup(item, item.components().getOrDefault(DataComponents.MAX_DAMAGE, 0) > 0, id);
    }

    /** get(stack, id), the 1.21.3 shape */
    public static class_1800 method_27878(ItemStack stack, Identifier id) {
        return lookup(stack.getItem(), stack.getMaxDamage() > 0, id);
    }

    private static class_1800 lookup(Item item, boolean damageable, Identifier id) {
        if (damageable) {
            if (field_24445.equals(id)) {
                return field_24447;
            }
            if (field_24444.equals(id)) {
                return field_24446;
            }
        }
        class_1800 global;
        synchronized (field_24443) {
            global = field_24443.get(id);
        }
        if (global != null) {
            return global;
        }
        class_1800 p;
        synchronized (field_24448) {
            Map<Identifier, class_1800> own = field_24448.get(item);
            p = own == null ? null : own.get(id);
        }
        if (p == null && id.getNamespace().equals("minecraft") && UNPORTED.contains(id.getPath())
                && WARNED.add(id.getPath())) {
            org.slf4j.LoggerFactory.getLogger("CenturyBridge").warn(
                "vanilla model predicate minecraft:{} is not carried to 26.x (it reads state 26.x moved into "
                    + "its own item model definitions); asked for {}, answering as unregistered",
                id.getPath(), item);
        }
        return p;
    }
}
