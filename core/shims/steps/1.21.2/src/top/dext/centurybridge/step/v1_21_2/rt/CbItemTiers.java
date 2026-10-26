package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_1322;
import net.minecraft.class_1766;
import net.minecraft.class_1792;
import net.minecraft.class_1829;
import net.minecraft.class_1856;
import net.minecraft.class_2248;
import net.minecraft.class_2246;
import net.minecraft.class_3481;
import net.minecraft.class_5134;
import net.minecraft.class_6862;
import net.minecraft.class_6885;
import net.minecraft.class_7923;
import net.minecraft.class_9274;
import net.minecraft.class_9285;
import net.minecraft.class_9334;
import net.minecraft.class_9424;
import net.minecraft.class_9886;
import net.minecraft.class_9889;
import net.minecraft.class_9890;
import top.dext.centurybridge.step.v1_21_2.facade.Tier;
import top.dext.centurybridge.step.v1_21_2.facade.TieredItem;
import top.dext.centurybridge.step.v1_21_2.facade.Tiers;

/**
 * 1.21.1's Tier / TieredItem / tool items on 1.21.2.
 *
 * <p>1.21.1 built a tool from a Tier (an interface mods implement, usually as an enum: uses, speed, attack damage
 * bonus, the blocks it cannot drop, enchantment value, repair ingredient, the Tool component it makes):
 * TieredItem set the durability to the tier's uses and answered the enchantment value and repair check from the
 * tier; DiggerItem / SwordItem put the tier's Tool component; the attribute modifiers were whatever the caller's
 * Properties carried (createAttributes helped). 1.21.2 builds tools from a ToolMaterial record (a repair tag
 * instead of an ingredient, no overridable Tool) and sets durability, REPAIRABLE, ENCHANTABLE, TOOL and the
 * attribute modifiers itself.
 * <ul>
 * <li>{@link #toW}: a 1.21.1 tier as a 1.21.2 material, once per tier, <em>legacy</em>: applyToolProperties /
 *     applySwordProperties (CbItemToolMaterialMixin) then do what 1.21.1's constructors did -- durability = uses,
 *     ENCHANTABLE only for a positive value (1.21.1's 0 was "not enchantable"; 1.21.2's Enchantable refuses it),
 *     REPAIRABLE = the repair ingredient's items (read when first asked, as 1.21.1 read its supplier), TOOL = the
 *     tier's createToolProperties(mineable) or 1.21.1's sword tool, and the caller's attribute modifiers left as
 *     they are.</li>
 * <li>{@link #fromW}: the tier behind a 1.21.2 material: the one it was made from, 1.21.1's Tiers constant for a
 *     1.21.2 vanilla one, else a tier read off the material.</li>
 * </ul>
 */
public final class CbItemTiers {

    private static final Map<Tier, class_9886> TO_W = new IdentityHashMap<>();
    private static final Map<class_9886, Tier> LEGACY = new IdentityHashMap<>();
    private static final Map<class_9886, Tier> DERIVED = new IdentityHashMap<>();

    /** the 1.21.2 material a tool is built from for a 1.21.1 tier (one per tier) */
    public static synchronized class_9886 toW(Tier tier) {
        class_9886 w = TO_W.get(tier);
        if (w == null) {
            // the repair tag is unused for a legacy material (CbItemToolMaterialMixin)
            w = new class_9886(tier.method_58419(), tier.method_8025(), tier.method_8027(), tier.method_8028(),
                tier.method_8026(), null);
            TO_W.put(tier, w);
            LEGACY.put(w, tier);
        }
        return w;
    }

    /** the 1.21.1 tier a legacy material was made from, null for any other */
    public static synchronized Tier legacy(class_9886 w) {
        return LEGACY.get(w);
    }

    /** the 1.21.1 tier behind a 1.21.2 material */
    public static synchronized Tier fromW(class_9886 w) {
        Tier t = LEGACY.get(w);
        if (t != null) {
            return t;
        }
        if (w == class_9886.field_52585) {
            return Tiers.WOOD;
        } else if (w == class_9886.field_52586) {
            return Tiers.STONE;
        } else if (w == class_9886.field_52587) {
            return Tiers.IRON;
        } else if (w == class_9886.field_52588) {
            return Tiers.DIAMOND;
        } else if (w == class_9886.field_52589) {
            return Tiers.GOLD;
        } else if (w == class_9886.field_52590) {
            return Tiers.NETHERITE;
        }
        return DERIVED.computeIfAbsent(w, Derived::new);
    }

    /** a tier read off a 1.21.2 material no 1.21.1 tier was made from */
    private record Derived(class_9886 w) implements Tier {
        @Override
        public int method_8025() {
            return w.comp_2931();
        }

        @Override
        public float method_8027() {
            return w.comp_2932();
        }

        @Override
        public float method_8028() {
            return w.comp_2933();
        }

        @Override
        public class_6862<class_2248> method_58419() {
            return w.comp_2930();
        }

        @Override
        public int method_8026() {
            return w.comp_2934();
        }

        @Override
        public class_1856 method_8023() {
            return class_1856.method_8106(top.dext.centurybridge.step.v1_21_2.rt.CbItemTags.lookup(class_7923.field_41178).method_46735(w.comp_2935()));
        }
    }

    /**
     * The properties 1.21.1's TieredItem and DiggerItem / SwordItem constructors gave a tool of a legacy material:
     * durability, enchantability (positive only), repair ingredient, and the Tool component; attribute modifiers
     * untouched. {@code tool} is the tier's own (DiggerItem) or 1.21.1's sword tool.
     */
    public static class_1792.class_1793 legacyProperties(class_9886 w, class_1792.class_1793 props, class_9424 tool) {
        Tier tier = legacy(w);
        props.method_7895(tier.method_8025());
        int enchantment = tier.method_8026();
        if (enchantment > 0) {
            props.method_57349(class_9334.field_53695, new class_9889(enchantment));
        }
        props.method_57349(class_9334.field_53696,
            new class_9890(new CbItemArmor.IngredientItems(tier::method_8023)));
        return props.method_57349(class_9334.field_50077, tool);
    }

    /** SwordItem.createToolProperties(): cobweb mined and dropped at 15, sword-efficient blocks at 1.5, 2 damage per block */
    public static class_9424 swordTool() {
        return new class_9424(List.of(
            class_9424.class_9425.method_58431(class_6885.method_40246(class_2246.field_10343.method_40142()), 15.0F),
            class_9424.class_9425.method_58434(top.dext.centurybridge.step.v1_21_2.rt.CbItemTags.lookup(class_7923.field_41175).method_46735(class_3481.field_44469), 1.5F)),
            1.0F, 2);
    }

    // ---- Tool.Rule's 1.21.1 factories (tag / block list where 1.21.2 takes a holder set) ----

    private static class_6885<class_2248> blocks(class_6862<class_2248> tag) {
        return top.dext.centurybridge.step.v1_21_2.rt.CbItemTags.lookup(class_7923.field_41175).method_46735(tag);
    }

    private static class_6885<class_2248> blocks(List<class_2248> list) {
        return class_6885.method_40244(class_2248::method_40142, list);
    }

    /** Rule.minesAndDrops(blocks, speed) */
    public static class_9424.class_9425 method_58431(List<class_2248> list, float speed) {
        return class_9424.class_9425.method_58431(blocks(list), speed);
    }

    /** Rule.minesAndDrops(tag, speed) */
    public static class_9424.class_9425 method_58428(class_6862<class_2248> tag, float speed) {
        return class_9424.class_9425.method_58431(blocks(tag), speed);
    }

    /** Rule.deniesDrops(tag) */
    public static class_9424.class_9425 method_58427(class_6862<class_2248> tag) {
        return class_9424.class_9425.method_58427(blocks(tag));
    }

    /** Rule.overrideSpeed(tag, speed) */
    public static class_9424.class_9425 method_58433(class_6862<class_2248> tag, float speed) {
        return class_9424.class_9425.method_58434(blocks(tag), speed);
    }

    /** Rule.overrideSpeed(blocks, speed) */
    public static class_9424.class_9425 method_58434(List<class_2248> list, float speed) {
        return class_9424.class_9425.method_58434(blocks(list), speed);
    }

    // ---- createAttributes (1.21.1's bodies) ----

    /** DiggerItem.createAttributes(tier, attackDamage, attackSpeed): damage + the tier's bonus, speed, main hand */
    public static class_9285 method_57346(Tier tier, float attackDamage, float attackSpeed) {
        return class_9285.method_57480()
            .method_57487(class_5134.field_23721, new class_1322(class_1792.field_8006, attackDamage + tier.method_8028(),
                class_1322.class_1323.field_6328), class_9274.field_49217)
            .method_57487(class_5134.field_23723, new class_1322(class_1792.field_8001, attackSpeed,
                class_1322.class_1323.field_6328), class_9274.field_49217)
            .method_57486();
    }

    /** SwordItem.createAttributes(tier, attackDamage, attackSpeed) */
    public static class_9285 method_57394(Tier tier, int attackDamage, float attackSpeed) {
        return class_9285.method_57480()
            .method_57487(class_5134.field_23721, new class_1322(class_1792.field_8006, (float) attackDamage + tier.method_8028(),
                class_1322.class_1323.field_6328), class_9274.field_49217)
            .method_57487(class_5134.field_23723, new class_1322(class_1792.field_8001, attackSpeed,
                class_1322.class_1323.field_6328), class_9274.field_49217)
            .method_57486();
    }

    // ---- TieredItem.getTier() on 1.21.2's tool classes (no longer TieredItems) ----

    /** a 1.21.2 tool's material (CbItemToolMixins) */
    public interface ToolFields {
        class_9886 cb$toolMaterial();
    }

    /** a 1.21.1 tool subclass overriding getTier() (adapterHooks) */
    public interface LegacyTier {
        Tier method_8022();
    }

    private static Tier tier(class_1792 item) {
        if (item instanceof LegacyTier l) {
            return l.method_8022();
        }
        return item instanceof TieredItem t ? t.method_8022() : super_tier(item);
    }

    /**
     * getTier() through a TieredItem reference (an Item since TieredItem is renamed onto it; CbItemTypes has let only
     * 1.21.1's TieredItems through): the class's override, the facade's tier, or the 1.21.2 tool's
     */
    public static Tier method_8022(class_1792 item) {
        return tier(item);
    }

    /** new TieredItem(tier, properties): the facade, typed as the Item TieredItem references are */
    public static class_1792 newTieredItem(Tier tier, class_1792.class_1793 properties) {
        return new TieredItem(tier, properties);
    }

    /** 1.21.1's TieredItem.getTier(): the tier the tool was built with */
    public static Tier super_tier(class_1792 item) {
        return fromW(((ToolFields) (Object) item).cb$toolMaterial());
    }

    /**
     * getTier() through a DiggerItem / SwordItem reference. Named per class, not only overloaded: 1.21.5 folds both
     * into Item (classRenames), and forms told apart only by those types would collide in the climbed layer
     */
    public static Tier method_8022Digger(class_1766 item) {
        return tier(item);
    }

    public static Tier method_8022Sword(class_1829 item) {
        return tier(item);
    }

    // ---- constructors of 1.21.1's tools: the 1.21.2 class from the converted tier (attribute modifiers: the caller's) ----

    public static net.minecraft.class_1829 newSwordItem(Tier tier, class_1792.class_1793 props) {
        return new net.minecraft.class_1829(toW(tier), 0.0F, 0.0F, props);
    }

    public static net.minecraft.class_1743 newAxeItem(Tier tier, class_1792.class_1793 props) {
        return new net.minecraft.class_1743(toW(tier), 0.0F, 0.0F, props);
    }

    public static net.minecraft.class_1810 newPickaxeItem(Tier tier, class_1792.class_1793 props) {
        return new net.minecraft.class_1810(toW(tier), 0.0F, 0.0F, props);
    }

    public static net.minecraft.class_1821 newShovelItem(Tier tier, class_1792.class_1793 props) {
        return new net.minecraft.class_1821(toW(tier), 0.0F, 0.0F, props);
    }

    public static net.minecraft.class_1794 newHoeItem(Tier tier, class_1792.class_1793 props) {
        return new net.minecraft.class_1794(toW(tier), 0.0F, 0.0F, props);
    }

    private CbItemTiers() {
    }
}
