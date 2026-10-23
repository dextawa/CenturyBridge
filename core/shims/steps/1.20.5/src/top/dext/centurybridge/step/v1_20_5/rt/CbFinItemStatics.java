package top.dext.centurybridge.step.v1_20_5.rt;

import java.util.List;
import java.util.Optional;

/**
 * 1.20.4's static item helpers that 1.20.5 took out (area s4fin, world/item): BlockItem, CompassItem,
 * CrossbowItem, FireworkRocketItem, FireworkStarItem, MapItem, ShieldItem, WritableBookItem,
 * WrittenBookItem, ArmorTrim, SmithingTemplateItem, Ingredient, ShapedRecipePattern, MerchantOffers.
 *
 * <p>Each answers what 1.20.4's body did. 1.20.4 read and wrote a stack's NBT; these read and write the
 * stack's 1.20.4 NBT view (CbItemTags), which stays in step with the stack's 1.20.5 components both ways
 * (a write to the view is upgraded into the components the 1.20.4 key speaks for). Where 1.20.4's body
 * was a network read or write, it is 1.20.5's stream codec for the same value through the buffer's
 * registry view (CbNetBufs.cbRegistryBuf), the wire form 1.20.5 uses where 1.20.4 used its own.
 */
public final class CbFinItemStatics {

    private CbFinItemStatics() {
    }

    // ---------------------------------------------------------------- BlockItem

    /** BlockItem.getBlockEntityData(stack): the tag's BlockEntityTag compound, or null */
    public static net.minecraft.class_2487 method_38072(net.minecraft.class_1799 stack) {
        return CbItemTags.getTagElement(stack, "BlockEntityTag");
    }

    /**
     * BlockItem.setBlockEntityData(stack, type, tag): an empty tag removes BlockEntityTag; otherwise the
     * block entity's id goes in (BlockEntity.addEntityType) and the compound becomes the tag's
     * BlockEntityTag -- the reference 1.20.4 code keeps writing into.
     */
    public static void method_38073(net.minecraft.class_1799 stack, net.minecraft.class_2591<?> type, net.minecraft.class_2487 tag) {
        if (tag.method_33133()) {
            CbItemTags.removeTagKey(stack, "BlockEntityTag");
        } else {
            net.minecraft.class_2586.method_38238(tag, type);
            CbItemTags.addTagElement(stack, "BlockEntityTag", tag);
        }
    }

    // ---------------------------------------------------------------- CompassItem

    /** CompassItem.isLodestoneCompass(stack): the tag has LodestoneDimension or LodestonePos */
    public static boolean method_26365(net.minecraft.class_1799 stack) {
        net.minecraft.class_2487 tag = CbItemTags.getTag(stack);
        return tag != null && (tag.method_10545("LodestoneDimension") || tag.method_10545("LodestonePos"));
    }

    /**
     * CompassItem.getLodestonePosition(tag): the GlobalPos of LodestonePos (an {X, Y, Z} compound,
     * NbtUtils.readBlockPos of 1.20.4) in LodestoneDimension (Level.RESOURCE_KEY_CODEC), or null when
     * either is missing or the dimension does not parse.
     */
    public static net.minecraft.class_4208 method_43124(net.minecraft.class_2487 tag) {
        boolean pos = tag.method_10545("LodestonePos");
        boolean dim = tag.method_10545("LodestoneDimension");
        if (pos && dim) {
            Optional<net.minecraft.class_5321<net.minecraft.class_1937>> d = net.minecraft.class_1937.field_25178
                .parse(net.minecraft.class_2509.field_11560, tag.method_10580("LodestoneDimension")).result();
            if (d.isPresent()) {
                net.minecraft.class_2487 p = tag.method_10562("LodestonePos");
                net.minecraft.class_2338 bp = new net.minecraft.class_2338(p.method_10550("X"), p.method_10550("Y"), p.method_10550("Z"));
                return net.minecraft.class_4208.method_19443(d.get(), bp);
            }
        }
        return null;
    }

    // ---------------------------------------------------------------- CrossbowItem

    /**
     * CrossbowItem.setCharged(stack, charged): the tag's Charged byte. 1.20.5 has no such flag (a
     * crossbow is charged when its charged_projectiles are not empty), so the key stays the mod's own
     * data in the view, as 1.20.4's ItemStackComponentizationFix drops Charged and keeps the projectiles.
     */
    public static void method_7782(net.minecraft.class_1799 stack, boolean charged) {
        CbItemTags.getOrCreateTag(stack).method_10556("Charged", charged);
    }

    /** CrossbowItem.containsChargedProjectile(stack, item): one of the charged projectiles is that item */
    public static boolean method_7772(net.minecraft.class_1799 crossbow, net.minecraft.class_1792 item) {
        net.minecraft.class_9278 c = crossbow.method_57825(net.minecraft.class_9334.field_49649, net.minecraft.class_9278.field_49298);
        return c.method_57438(item);
    }

    // ---------------------------------------------------------------- FireworkRocketItem / FireworkStarItem

    /** FireworkRocketItem.setDuration(stack, flight): Fireworks.Flight */
    public static void method_47368(net.minecraft.class_1799 stack, byte flight) {
        CbItemTags.getOrCreateTagElement(stack, "Fireworks").method_10567("Flight", flight);
    }

    /**
     * FireworkStarItem.appendHoverText(explosionTag, lines): 1.20.4's lines for an Explosion compound --
     * the shape, the colours, the fade colours, trail and twinkle, grey -- with 1.20.4's translation keys.
     */
    public static void method_7809(net.minecraft.class_2487 tag, List<net.minecraft.class_2561> lines) {
        net.minecraft.class_9283.class_1782 shape = net.minecraft.class_9283.class_1782.method_7813(tag.method_10571("Type"));
        lines.add(net.minecraft.class_2561.method_43471("item.minecraft.firework_star.shape." + shape.method_15434())
            .method_27692(net.minecraft.class_124.field_1080));
        int[] colors = tag.method_10561("Colors");
        if (colors.length > 0) {
            lines.add(colorNames(net.minecraft.class_2561.method_43473().method_27692(net.minecraft.class_124.field_1080), colors));
        }
        int[] fade = tag.method_10561("FadeColors");
        if (fade.length > 0) {
            lines.add(colorNames(net.minecraft.class_2561.method_43471("item.minecraft.firework_star.fade_to")
                .method_10852(net.minecraft.class_5244.field_41874).method_27692(net.minecraft.class_124.field_1080), fade));
        }
        if (tag.method_10577("Trail")) {
            lines.add(net.minecraft.class_2561.method_43471("item.minecraft.firework_star.trail").method_27692(net.minecraft.class_124.field_1080));
        }
        if (tag.method_10577("Flicker")) {
            lines.add(net.minecraft.class_2561.method_43471("item.minecraft.firework_star.flicker").method_27692(net.minecraft.class_124.field_1080));
        }
    }

    /** 1.20.4 FireworkStarItem.appendColors */
    private static net.minecraft.class_2561 colorNames(net.minecraft.class_5250 line, int[] colors) {
        for (int i = 0; i < colors.length; i++) {
            if (i > 0) {
                line.method_27693(", ");
            }
            line.method_10852(colorName(colors[i]));
        }
        return line;
    }

    /** 1.20.4 FireworkStarItem.getColorName: the dye's name for a firework colour, else "custom" */
    private static net.minecraft.class_2561 colorName(int color) {
        net.minecraft.class_1767 dye = net.minecraft.class_1767.method_7786(color);
        return dye == null ? net.minecraft.class_2561.method_43471("item.minecraft.firework_star.custom_color")
            : net.minecraft.class_2561.method_43471("item.minecraft.firework_star." + dye.method_7792());
    }

    // ---------------------------------------------------------------- MapItem

    /** MapItem.getMapId(stack): the tag's "map" number, or null */
    public static Integer method_8003(net.minecraft.class_1799 stack) {
        net.minecraft.class_2487 tag = CbItemTags.getTag(stack);
        return tag != null && tag.method_10573("map", 99) ? tag.method_10550("map") : null;
    }

    /** MapItem.makeKey(id): "map_" + id, the saved data name (1.20.5's MapId.key()) */
    public static String method_17440(int id) {
        return "map_" + id;
    }

    /** MapItem.getTooltipForId(stack): "Id #n" for the stack's map id (1.20.4 unboxed it: no id throws, as it did) */
    public static net.minecraft.class_2561 method_53837(net.minecraft.class_1799 stack) {
        return net.minecraft.class_1806.method_53836(new net.minecraft.class_9209(method_8003(stack).intValue()));
    }

    /** MapItem.getColor(stack): display.MapColor with full alpha, else 1.20.4's default -12173266 */
    public static int method_7999(net.minecraft.class_1799 stack) {
        net.minecraft.class_2487 display = CbItemTags.getTagElement(stack, "display");
        if (display != null && display.method_10573("MapColor", 99)) {
            return 0xFF000000 | display.method_10550("MapColor") & 0xFFFFFF;
        }
        return -12173266;
    }

    // ---------------------------------------------------------------- ShieldItem

    /** ShieldItem.getColor(stack): BlockEntityTag.Base as a dye colour, white without one */
    public static net.minecraft.class_1767 method_8013(net.minecraft.class_1799 stack) {
        net.minecraft.class_2487 be = method_38072(stack);
        return be != null ? net.minecraft.class_1767.method_7791(be.method_10550("Base")) : net.minecraft.class_1767.field_7952;
    }

    // ---------------------------------------------------------------- books

    /** WritableBookItem.makeSureTagIsValid(tag): a "pages" list of strings each at most 32767 long */
    public static boolean method_8047(net.minecraft.class_2487 tag) {
        if (tag == null) {
            return false;
        }
        if (!tag.method_10573("pages", 9)) {
            return false;
        }
        net.minecraft.class_2499 pages = tag.method_10554("pages", 8);
        for (int i = 0; i < pages.size(); i++) {
            if (pages.method_10608(i).length() > 32767) {
                return false;
            }
        }
        return true;
    }

    /** WrittenBookItem.makeSureTagIsValid(tag): valid pages, a title of at most 32, an author */
    public static boolean method_8053(net.minecraft.class_2487 tag) {
        if (!method_8047(tag)) {
            return false;
        }
        if (!tag.method_10573("title", 8)) {
            return false;
        }
        if (tag.method_10558("title").length() > 32) {
            return false;
        }
        return tag.method_10573("author", 8);
    }

    /** WrittenBookItem.getGeneration(stack): the tag's generation (1.20.4 read a missing tag as a crash; here 0) */
    public static int method_8052(net.minecraft.class_1799 stack) {
        net.minecraft.class_2487 tag = CbItemTags.getTag(stack);
        if (tag == null) {
            throw new NullPointerException("1.20.4 WrittenBookItem.getGeneration on a stack with no tag");
        }
        return tag.method_10550("generation");
    }

    /** WrittenBookItem.getPageCount(stack): the size of the tag's string pages list, 0 without a tag */
    public static int method_17443(net.minecraft.class_1799 stack) {
        net.minecraft.class_2487 tag = CbItemTags.getTag(stack);
        return tag != null ? tag.method_10554("pages", 8).size() : 0;
    }

    // ---------------------------------------------------------------- ArmorTrim

    private static final net.minecraft.class_2561 UPGRADE_TITLE = net.minecraft.class_2561.method_43471(
        net.minecraft.class_156.method_646("item", new net.minecraft.class_2960("smithing_template.upgrade")))
        .method_27692(net.minecraft.class_124.field_1080);

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger("CenturyBridge");

    /**
     * ArmorTrim.setTrim(registries, stack, trim): on trimmable armour, the trim encoded into the tag's
     * "Trim" (ArmorTrim.CODEC with the registries), true; false otherwise.
     */
    public static boolean method_48429(net.minecraft.class_5455 registries, net.minecraft.class_1799 stack, net.minecraft.class_8053 trim) {
        if (stack.method_31573(net.minecraft.class_3489.field_41890)) {
            net.minecraft.class_2520 nbt = net.minecraft.class_8053.field_41994.encodeStart(
                net.minecraft.class_6903.method_46632(net.minecraft.class_2509.field_11560, registries), trim).result().orElseThrow();
            CbItemTags.addTagElement(stack, "Trim", nbt);
            return true;
        }
        return false;
    }

    /**
     * ArmorTrim.getTrim(registries, stack, suppressErrors): on trimmable armour with a "Trim", the trim
     * it parses to (errors logged unless suppressed), else empty.
     */
    public static Optional<net.minecraft.class_8053> method_48428(net.minecraft.class_5455 registries, net.minecraft.class_1799 stack, boolean suppressErrors) {
        if (stack.method_31573(net.minecraft.class_3489.field_41890)) {
            net.minecraft.class_2487 tag = CbItemTags.getTag(stack);
            if (tag != null && tag.method_10545("Trim")) {
                net.minecraft.class_2487 t = CbItemTags.getTagElement(stack, "Trim");
                net.minecraft.class_8053 trim = net.minecraft.class_8053.field_41994.parse(
                        net.minecraft.class_6903.method_46632(net.minecraft.class_2509.field_11560, registries), t)
                    .resultOrPartial(e -> {
                        if (!suppressErrors) {
                            LOG.warn(e);
                        }
                    }).orElse(null);
                return Optional.ofNullable(trim);
            }
        }
        return Optional.empty();
    }

    /** ArmorTrim.appendUpgradeHoverText(stack, registries, lines): the upgrade title, pattern and material lines */
    public static void method_48425(net.minecraft.class_1799 stack, net.minecraft.class_5455 registries, List<net.minecraft.class_2561> lines) {
        Optional<net.minecraft.class_8053> trim = method_48428(registries, stack, true);
        if (trim.isPresent()) {
            net.minecraft.class_8053 t = trim.get();
            lines.add(UPGRADE_TITLE);
            lines.add(net.minecraft.class_5244.method_48320().method_10852(t.method_48424().comp_349().method_48446(t.method_48431())));
            lines.add(net.minecraft.class_5244.method_48320().method_10852(t.method_48431().comp_349().comp_1212()));
        }
    }

    // ---------------------------------------------------------------- SmithingTemplateItem

    /** SmithingTemplateItem.createArmorTrimTemplate(key): 1.20.5's, required features none (1.20.4 had none) */
    public static net.minecraft.class_8052 method_48418(net.minecraft.class_5321<net.minecraft.class_8056> key) {
        return net.minecraft.class_8052.method_48418(key);
    }

    /** SmithingTemplateItem.createArmorTrimTemplate(id): 1.20.5's, required features none */
    public static net.minecraft.class_8052 method_48419(net.minecraft.class_2960 id) {
        return net.minecraft.class_8052.method_48419(id);
    }

    // ---------------------------------------------------------------- network forms

    /** Ingredient.fromNetwork(buf): 1.20.5's ingredient stream codec, the same list-of-stacks form */
    public static net.minecraft.class_1856 method_8086(net.minecraft.class_2540 buf) {
        return net.minecraft.class_1856.field_48355.decode(CbNetBufs.cbRegistryBuf(buf));
    }

    /** Ingredient.toNetwork(buf): 1.20.5's ingredient stream codec */
    public static void method_8088(net.minecraft.class_1856 ingredient, net.minecraft.class_2540 buf) {
        net.minecraft.class_1856.field_48355.encode(CbNetBufs.cbRegistryBuf(buf), ingredient);
    }

    /** ShapedRecipePattern.fromNetwork(buf): 1.20.5's pattern stream codec (width, height, ingredients) */
    public static net.minecraft.class_8957 method_55090(net.minecraft.class_2540 buf) {
        return net.minecraft.class_8957.field_48359.decode(CbNetBufs.cbRegistryBuf(buf));
    }

    /** ShapedRecipePattern.toNetwork(buf): 1.20.5's pattern stream codec */
    public static void method_55087(net.minecraft.class_8957 pattern, net.minecraft.class_2540 buf) {
        net.minecraft.class_8957.field_48359.encode(CbNetBufs.cbRegistryBuf(buf), pattern);
    }

    /** MerchantOffers.createFromStream(buf): 1.20.5's offers stream codec */
    public static net.minecraft.class_1916 method_8265(net.minecraft.class_2540 buf) {
        return net.minecraft.class_1916.field_48373.decode(CbNetBufs.cbRegistryBuf(buf));
    }

    /** MerchantOffers.writeToStream(buf): 1.20.5's offers stream codec */
    public static void method_8270(net.minecraft.class_1916 offers, net.minecraft.class_2540 buf) {
        net.minecraft.class_1916.field_48373.encode(CbNetBufs.cbRegistryBuf(buf), offers);
    }

    // ---------------------------------------------------------------- FireworkRocketItem.Shape

    /**
     * FireworkRocketItem.Shape.getName(): the shape's name ("small_ball", ...). 1.20.5 moved the enum to
     * FireworkExplosion.Shape (same constants, ids and names; classRenames) where getName() answers the
     * translated component and getSerializedName() the name.
     */
    public static String method_7812(net.minecraft.class_9283.class_1782 shape) {
        return shape.method_15434();
    }
}
