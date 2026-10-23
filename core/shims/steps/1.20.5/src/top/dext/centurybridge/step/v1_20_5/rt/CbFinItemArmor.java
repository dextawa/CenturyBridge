package top.dext.centurybridge.step.v1_20_5.rt;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import top.dext.centurybridge.step.v1_20_5.facade.ArmorMaterial;
import top.dext.centurybridge.step.v1_20_5.facade.ArmorMaterials;

/**
 * 1.20.4's armor API on 1.20.5's (area s4fin, world/item).
 *
 * <p>Materials. 1.20.4's ArmorMaterial was an interface a mod implemented (or the vanilla ArmorMaterials enum,
 * facade/ArmorMaterials); 1.20.5's is a registered record whose items hold its Holder. A 1.20.4 material
 * handed to 1.20.5 becomes the holder of the record standing for it ({@link #holder}): a vanilla constant's
 * own holder, else a record built from the material's 1.20.4 answers -- defense per piece, enchantment value,
 * equip sound, repair ingredient, toughness, knockback resistance, and one texture layer named as 1.20.4's
 * armor renderer named it (getName(): "textures/models/armor/<name>_layer_<n>.png", the namespace before a
 * ':' as Fabric API 1.20.4's renderer read it) -- registered in ARMOR_MATERIAL under that name while the
 * registry is open, else held directly. A holder 1.20.4 code receives goes back to the 1.20.4 material
 * ({@link #material}): the mod's own object for one it handed in, the vanilla constant for a vanilla record,
 * otherwise a read-only view of the record ({@link View}).
 *
 * <p>Durability. 1.20.4's ArmorItem took a piece's durability from its material (Properties.defaultDurability,
 * a no-op once durability was set); 1.20.5 moved it to the item's properties, so the constructor bridge sets
 * it the 1.20.4 way before calling 1.20.5's. Knockback resistance: 1.20.4 gave it netherite only in vanilla
 * (Fabric API 1.20.4 to any material with one, which is the behaviour 1.20.4 mods saw); 1.20.5 gives it to any
 * material with one.
 *
 * <p>Dyeing. 1.20.4's DyeableLeatherItem (an interface on the item) is 1.20.5's dyed_color component and the
 * minecraft:dyeable item tag. An item whose class implements it (facade/DyeableLeatherItem) is put in that
 * tag as the tags load ({@link #dyeableItems}, CbFinItemDyeableTagMixin), so 1.20.5's dye recipe, cauldron
 * washing and armor tint treat it as 1.20.4 did; its color methods read and write the stack's 1.20.4 NBT view
 * (display.color), which the dyed_color component mirrors. 1.20.4's instanceof DyeableLeatherItem is
 * {@link #cbInstanceOf}.
 */
public final class CbFinItemArmor {

    private CbFinItemArmor() {
    }

    // ---------------------------------------------------------------- materials

    private static final Map<ArmorMaterial, net.minecraft.class_6880<net.minecraft.class_1741>> HOLDERS =
        java.util.Collections.synchronizedMap(new java.util.IdentityHashMap<>());
    private static final Map<net.minecraft.class_1741, ArmorMaterial> MATERIALS =
        java.util.Collections.synchronizedMap(new java.util.IdentityHashMap<>());

    /** the 1.20.5 holder standing for a 1.20.4 material */
    public static net.minecraft.class_6880<net.minecraft.class_1741> holder(ArmorMaterial material) {
        if (material == null) {
            return null;
        }
        if (material instanceof ArmorMaterials vanilla) {
            return vanilla.cbHolder();
        }
        if (material instanceof View view) {
            return view.holder;
        }
        return HOLDERS.computeIfAbsent(material, CbFinItemArmor::register);
    }

    private static net.minecraft.class_6880<net.minecraft.class_1741> register(ArmorMaterial m) {
        EnumMap<net.minecraft.class_1738.class_8051, Integer> defense = new EnumMap<>(net.minecraft.class_1738.class_8051.class);
        for (net.minecraft.class_1738.class_8051 type : net.minecraft.class_1738.class_8051.values()) {
            if (type != net.minecraft.class_1738.class_8051.field_48838) {
                defense.put(type, m.method_48403(type));
            }
        }
        String name = m.method_7694();
        net.minecraft.class_2960 texture = name.indexOf(':') >= 0 ? new net.minecraft.class_2960(name) : new net.minecraft.class_2960("minecraft", name);
        net.minecraft.class_1741 record = new net.minecraft.class_1741(defense, m.method_7699(),
            net.minecraft.class_7923.field_41172.method_47983(m.method_7698()), m::method_7695,
            List.of(new net.minecraft.class_1741.class_9196(texture)), m.method_7700(), m.method_24355());
        MATERIALS.put(record, m);
        net.minecraft.class_2378<net.minecraft.class_1741> registry = net.minecraft.class_7923.field_48976;
        if (registry instanceof CbCodecRegistries.Frozen f && !f.cbS4$frozen() && !registry.method_10250(texture)) {
            return net.minecraft.class_2378.method_47985(registry, texture, record);
        }
        return net.minecraft.class_6880.method_40223(record);
    }

    /** the 1.20.4 material a 1.20.5 holder stands for */
    public static ArmorMaterial material(net.minecraft.class_6880<net.minecraft.class_1741> holder) {
        if (holder == null) {
            return null;
        }
        net.minecraft.class_1741 record = holder.comp_349();
        ArmorMaterial own = MATERIALS.get(record);
        if (own != null) {
            return own;
        }
        for (ArmorMaterials v : ArmorMaterials.values()) {
            if (v.cbHolder().comp_349() == record) {
                return v;
            }
        }
        return new View(holder);
    }

    /** a 1.20.5 material 1.20.4 code receives that it did not make and that is not a vanilla 1.20.4 one */
    public static final class View implements ArmorMaterial {
        final net.minecraft.class_6880<net.minecraft.class_1741> holder;

        View(net.minecraft.class_6880<net.minecraft.class_1741> holder) {
            this.holder = holder;
        }

        private net.minecraft.class_1741 r() {
            return holder.comp_349();
        }

        /** the record does not know durability (1.20.5 keeps it on the item): the piece's base times 1.20.5's own use for unknown materials, none */
        @Override
        public int method_48402(net.minecraft.class_1738.class_8051 type) {
            return 0;
        }

        @Override
        public int method_48403(net.minecraft.class_1738.class_8051 type) {
            return r().method_48403(type);
        }

        @Override
        public int method_7699() {
            return r().comp_2299();
        }

        @Override
        public net.minecraft.class_3414 method_7698() {
            return r().comp_2300().comp_349();
        }

        @Override
        public net.minecraft.class_1856 method_7695() {
            return r().comp_2301().get();
        }

        @Override
        public String method_7694() {
            net.minecraft.class_2960 id = holder.method_40230().map(net.minecraft.class_5321::method_29177).orElse(null);
            return id == null ? "" : id.method_12836().equals("minecraft") ? id.method_12832() : id.toString();
        }

        @Override
        public float method_7700() {
            return r().comp_2303();
        }

        @Override
        public float method_24355() {
            return r().comp_2304();
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof View v && v.holder.comp_349() == holder.comp_349();
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(holder.comp_349());
        }
    }

    // ---------------------------------------------------------------- ArmorMaterial calls (interface redirects)

    public static int method_48402(ArmorMaterial m, net.minecraft.class_1738.class_8051 type) {
        return m.method_48402(type);
    }

    // ---------------------------------------------------------------- TrimMaterial's override map (generic drift)

    /**
     * TrimMaterial's overrideArmorMaterials: 1.20.4 keyed it by the ArmorMaterials constant, 1.20.5 by the
     * material's holder. Adaptive both ways (a map already keyed the wanted way passes through), each the
     * other's inverse: a key that is a 1.20.4 material goes to its holder, a holder to its 1.20.4 material.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map materialHolderMap(Map map) {
        if (map == null || map.isEmpty() || !(map.keySet().iterator().next() instanceof ArmorMaterial)) {
            return map;
        }
        Map out = new java.util.LinkedHashMap();
        map.forEach((k, v) -> out.put(k instanceof ArmorMaterial m ? holder(m) : k, v));
        return out;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map holderMaterialMap(Map map) {
        if (map == null || map.isEmpty() || !(map.keySet().iterator().next() instanceof net.minecraft.class_6880)) {
            return map;
        }
        Map out = new java.util.LinkedHashMap();
        map.forEach((k, v) -> out.put(k instanceof net.minecraft.class_6880 h ? material(h) : k, v));
        return out;
    }

    // ---------------------------------------------------------------- ArmorItem

    /** new ArmorItem(material, type, properties) / super(..): 1.20.4's defaultDurability first, the material's holder */
    public static net.minecraft.class_1792.class_1793 properties(ArmorMaterial material, net.minecraft.class_1738.class_8051 type,
            net.minecraft.class_1792.class_1793 properties) {
        return CbItemProps.defaultDurability(properties, material.method_48402(type));
    }

    /** ArmorItem.getMaterial() (the item's holder, as 1.20.4's material) */
    public static ArmorMaterial method_7686(net.minecraft.class_1738 item) {
        return material(item.method_7686());
    }

    /** ArmorItem.material (protected field): the material */
    public static ArmorMaterial field_7881(net.minecraft.class_1738 item) {
        return material(((top.dext.centurybridge.step.v1_20_5.mixin.CbFinItemArmorAccess) item).cbFin$material());
    }

    /** ArmorItem.knockbackResistance (protected field): the material's */
    public static float field_21976(net.minecraft.class_1738 item) {
        return ((top.dext.centurybridge.step.v1_20_5.mixin.CbFinItemArmorAccess) item).cbFin$material().comp_349().comp_2304();
    }

    // ---------------------------------------------------------------- ArmorTrim textures

    /** ArmorTrim.innerTexture(material) */
    public static net.minecraft.class_2960 method_48434(net.minecraft.class_8053 trim, ArmorMaterial material) {
        return trim.method_48434(holder(material));
    }

    /** ArmorTrim.outerTexture(material) */
    public static net.minecraft.class_2960 method_48436(net.minecraft.class_8053 trim, ArmorMaterial material) {
        return trim.method_48436(holder(material));
    }

    // ---------------------------------------------------------------- dyeing

    /**
     * Whether an item's own class (not 1.20.5's ArmorItem, which CbFinItemDyeableArmorMixin gives the interface
     * so a 1.20.4 cast of a vanilla leather item holds) implements 1.20.4's DyeableLeatherItem.
     */
    static boolean declaresDyeable(net.minecraft.class_1792 item) {
        for (Class<?> c = item.getClass(); c != null && c != net.minecraft.class_1738.class && c != net.minecraft.class_1792.class; c = c.getSuperclass()) {
            for (Class<?> i : c.getInterfaces()) {
                if (top.dext.centurybridge.step.v1_20_5.facade.DyeableLeatherItem.class.isAssignableFrom(i)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** the ids of the items that are 1.20.4 dyeable by their class (CbFinItemDyeableTagMixin adds them to minecraft:dyeable) */
    public static List<net.minecraft.class_2960> dyeableItems() {
        List<net.minecraft.class_2960> ids = new java.util.ArrayList<>();
        for (net.minecraft.class_1792 item : net.minecraft.class_7923.field_41178) {
            if (declaresDyeable(item)) {
                ids.add(net.minecraft.class_7923.field_41178.method_10221(item));
            }
        }
        return ids;
    }

    /**
     * instanceof DyeableLeatherItem: 1.20.4's truth -- an item whose class implements it (a mod's, or one of the
     * facades), or a 1.20.5 item in minecraft:dyeable (vanilla's leather armor and leather horse armor, which
     * were DyeableArmorItem / DyeableHorseArmorItem in 1.20.4; all are ArmorItems, so a cast that follows holds)
     */
    public static boolean cbInstanceOf(Object o) {
        if (!(o instanceof net.minecraft.class_1792 item)) {
            return o instanceof top.dext.centurybridge.step.v1_20_5.facade.DyeableLeatherItem;
        }
        return declaresDyeable(item) || item instanceof net.minecraft.class_1738 && item.method_40131().method_40220(net.minecraft.class_3489.field_48803);
    }

    /** DyeableLeatherItem.hasCustomColor(stack): display.color is there */
    public static boolean hasCustomColor(net.minecraft.class_1799 stack) {
        net.minecraft.class_2487 d = CbItemTags.getTagElement(stack, "display");
        return d != null && d.method_10573("color", 99);
    }

    /** DyeableLeatherItem.getColor(stack): display.color, else 1.20.4's default 10511680 */
    public static int getColor(net.minecraft.class_1799 stack) {
        net.minecraft.class_2487 d = CbItemTags.getTagElement(stack, "display");
        return d != null && d.method_10573("color", 99) ? d.method_10550("color") : 10511680;
    }

    /** DyeableLeatherItem.clearColor(stack) */
    public static void clearColor(net.minecraft.class_1799 stack) {
        net.minecraft.class_2487 d = CbItemTags.getTagElement(stack, "display");
        if (d != null && d.method_10545("color")) {
            d.method_10551("color");
            CbItemTags.flush(stack);
        }
    }

    /** DyeableLeatherItem.setColor(stack, color) */
    public static void setColor(net.minecraft.class_1799 stack, int color) {
        CbItemTags.getOrCreateTagElement(stack, "display").method_10569("color", color);
        CbItemTags.flush(stack);
    }

    /** a 1.20.4 dyeable item's color methods, as an item declares or inherits them (facade/DyeableLeatherItem) */
    public interface Dyeable {
        boolean method_7801(net.minecraft.class_1799 stack);

        int method_7800(net.minecraft.class_1799 stack);

        void method_7798(net.minecraft.class_1799 stack);

        void method_7799(net.minecraft.class_1799 stack, int color);
    }

    /**
     * DyeableLeatherItem.dyeArmor(stack, dyes): 1.20.4's body -- the stack's item must be a 1.20.4 dyeable
     * one (its own hasCustomColor / getColor / setColor); a 1.20.5 dyeable item (in the tag) takes 1.20.5's
     * DyedItemColor.applyDyes, which is the same mix.
     */
    public static net.minecraft.class_1799 dyeArmor(net.minecraft.class_1799 stack, List<net.minecraft.class_1769> dyes) {
        if (!cbInstanceOf(stack.method_7909())) {
            return net.minecraft.class_1799.field_8037;
        }
        Dyeable item = (Dyeable) stack.method_7909();
        net.minecraft.class_1799 out = stack.method_46651(1);
        int[] rgb = new int[3];
        int brightness = 0;
        int count = 0;
        if (item.method_7801(stack)) {
            int c = item.method_7800(out);
            float r = (float) (c >> 16 & 255) / 255.0F;
            float g = (float) (c >> 8 & 255) / 255.0F;
            float b = (float) (c & 255) / 255.0F;
            brightness += (int) (Math.max(r, Math.max(g, b)) * 255.0F);
            rgb[0] += (int) (r * 255.0F);
            rgb[1] += (int) (g * 255.0F);
            rgb[2] += (int) (b * 255.0F);
            count++;
        }
        for (net.minecraft.class_1769 dye : dyes) {
            float[] f = dye.method_7802().method_7787();
            int r = (int) (f[0] * 255.0F);
            int g = (int) (f[1] * 255.0F);
            int b = (int) (f[2] * 255.0F);
            brightness += Math.max(r, Math.max(g, b));
            rgb[0] += r;
            rgb[1] += g;
            rgb[2] += b;
            count++;
        }
        int r = rgb[0] / count;
        int g = rgb[1] / count;
        int b = rgb[2] / count;
        float avg = (float) brightness / (float) count;
        float max = (float) Math.max(r, Math.max(g, b));
        r = (int) ((float) r * avg / max);
        g = (int) ((float) g * avg / max);
        b = (int) ((float) b * avg / max);
        int color = (r << 8) + g;
        color = (color << 8) + b;
        item.method_7799(out, color);
        return out;
    }
}
