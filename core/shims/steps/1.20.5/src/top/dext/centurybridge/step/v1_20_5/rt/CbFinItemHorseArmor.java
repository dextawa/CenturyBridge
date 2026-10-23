package top.dext.centurybridge.step.v1_20_5.rt;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 1.20.4's HorseArmorItem(protection, identifier, properties) on 1.20.5's AnimalArmorItem (class_4059, same
 * name) (area s4fin, world/item).
 *
 * <p>1.20.4: a horse armor item carried its protection (getProtection(), what AbstractHorse added as the
 * ARMOR attribute) and a texture named "textures/entity/horse/armor/horse_armor_<identifier>.png" (minecraft
 * namespace). 1.20.5: an AnimalArmorItem holds an armor material -- its defense for the BODY slot is the
 * armor value the horse gets -- and an equestrian body type that names the texture
 * "<material namespace>:textures/entity/horse/armor/horse_armor_<material path>.png". So a 1.20.4 horse armor
 * gets a material of its own named minecraft:<identifier> with BODY defense = protection (registered while
 * ARMOR_MATERIAL is open, else a direct holder whose name 1.20.5 cannot ask -- then its textures cannot be
 * derived and the 1.20.4 one is set), the same texture as 1.20.4's; getProtection() answers the protection.
 */
public final class CbFinItemHorseArmor {

    private CbFinItemHorseArmor() {
    }

    private static final Map<net.minecraft.class_4059, Integer> PROTECTION =
        java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());
    private static final Map<String, net.minecraft.class_6880<net.minecraft.class_1741>> MATERIALS = new java.util.concurrent.ConcurrentHashMap<>();

    /** the material a 1.20.4 horse armor of this protection and texture name stands on */
    public static net.minecraft.class_6880<net.minecraft.class_1741> material(int protection, String identifier) {
        return MATERIALS.computeIfAbsent(identifier + "#" + protection, k -> {
            EnumMap<net.minecraft.class_1738.class_8051, Integer> defense = new EnumMap<>(net.minecraft.class_1738.class_8051.class);
            defense.put(net.minecraft.class_1738.class_8051.field_48838, protection);
            net.minecraft.class_2960 id = new net.minecraft.class_2960("minecraft", identifier);
            net.minecraft.class_1741 record = new net.minecraft.class_1741(defense, 0, net.minecraft.class_3417.field_14883,
                () -> net.minecraft.class_1856.field_9017, List.of(new net.minecraft.class_1741.class_9196(id)), 0.0F, 0.0F);
            net.minecraft.class_2378<net.minecraft.class_1741> registry = net.minecraft.class_7923.field_48976;
            if (registry instanceof CbCodecRegistries.Frozen f && !f.cbS4$frozen() && !registry.method_10250(id)) {
                return net.minecraft.class_2378.method_47985(registry, id, record);
            }
            net.minecraft.class_2960 shared = registry.method_10250(id) ? id : null;
            if (shared != null && registry.method_10223(id).method_48403(net.minecraft.class_1738.class_8051.field_48838) == protection) {
                return registry.method_40264(net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_48977, id)).orElseThrow();
            }
            throw new IllegalStateException("1.20.4 horse armor '" + identifier + "' made after the armor material registry froze: "
                + "1.20.5's AnimalArmorItem needs a registered material to name its texture");
        });
    }

    /** a 1.20.4 horse armor's protection */
    public static void remember(net.minecraft.class_4059 item, int protection, String identifier) {
        PROTECTION.put(item, protection);
    }

    /** HorseArmorItem.getProtection(): 1.20.4's protection, else the material's body defense (1.20.5's armor value) */
    public static int method_18455(net.minecraft.class_4059 item) {
        Integer p = PROTECTION.get(item);
        return p != null ? p : item.method_7686().comp_349().method_48403(net.minecraft.class_1738.class_8051.field_48838);
    }

    /** HorseArmorItem.getProtection()'s body: the protection the item was made with */
    public static int superGetProtection(net.minecraft.class_4059 item) {
        Integer p = PROTECTION.get(item);
        return p != null ? p : item.method_7686().comp_349().method_48403(net.minecraft.class_1738.class_8051.field_48838);
    }

    /** a 1.20.4 horse armor class declaring getProtection() */
    public interface LegacyProtection {
        int method_18455();
    }

    /**
     * getDefaultAttributeModifiers() of a 1.20.4 horse armor class declaring getProtection() (guarded adapter):
     * 1.20.4's AbstractHorse gave the horse ARMOR = getProtection() of its armor item; 1.20.5 gives it the body
     * armor item's modifiers, whose ARMOR entry is the material's body defense. The item's own modifiers with
     * that entry's amount set to its getProtection().
     */
    public static net.minecraft.class_9285 modifiers(LegacyProtection item) {
        net.minecraft.class_9285 base = ((net.minecraft.class_1792) (Object) item).method_7844();
        net.minecraft.class_9285.class_9286 b = net.minecraft.class_9285.method_57480();
        for (net.minecraft.class_9285.class_9287 e : base.comp_2393()) {
            net.minecraft.class_1322 m = e.comp_2396();
            if (e.comp_2395().equals(net.minecraft.class_5134.field_23724) && m.comp_2450() == net.minecraft.class_1322.class_1323.field_6328) {
                m = new net.minecraft.class_1322(m.comp_2447(), m.comp_2448(), item.method_18455(), m.comp_2450());
            }
            b.method_57487(e.comp_2395(), m, e.comp_2397());
        }
        return b.method_57486();
    }

    /** super(protection, identifier, properties) of a 1.20.4 HorseArmorItem subclass: 1.20.5's constructor arguments */
    public static net.minecraft.class_6880<net.minecraft.class_1741> superMaterial(int protection, String identifier) {
        return material(protection, identifier);
    }

    /** the body type of a 1.20.4 HorseArmorItem: a horse's */
    public static net.minecraft.class_4059.class_9076 equestrian() {
        return net.minecraft.class_4059.class_9076.field_47825;
    }

    /** 1.20.4's HorseArmorItem had one texture, no dye overlay (a dyeable one tinted that texture) */
    public static boolean noOverlay() {
        return false;
    }

    /** a 1.20.4 HorseArmorItem (new, or a subclass after super(..)): its protection */
    public static void post(net.minecraft.class_4059 item, int protection, String identifier) {
        remember(item, protection, identifier);
    }
}
