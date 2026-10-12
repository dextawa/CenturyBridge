package top.dext.centurybridge.step.v1_20_5.rt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * An item stack's 1.20.4 NBT and its 1.20.5 components, converted both ways.
 *
 * <p>1.20.4 -> 1.20.5 is the game's own upgrade: the stack's 1.20.4 NBT ({id, Count, tag}) goes through
 * 1.20.5's DataFixer from data version 3700 (1.20.4) to the running one -- ItemStackComponentizationFix
 * and every other item fix of the step, exactly what 1.20.5 does to a 1.20.4 world's items -- and the
 * components it answers are decoded with their own codecs. Keys the game never owned end in
 * minecraft:custom_data, as the fix puts them.
 *
 * <p>1.20.5 -> 1.20.4 is that fix read backwards, on the same NBT layouts: each component the fix
 * makes is encoded with its codec and laid out under the key(s) and in the NBT types 1.20.4 wrote
 * (Damage int, Enchantments [{id, lvl short}], display {Name, Lore, color, MapColor, LocName}, HideFlags
 * from the components' tooltip flags, BlockEntityTag rebuilt from block_entity_data and the components
 * the fix split off it, nested item stacks as {id, Count byte, tag}, ...). A component 1.20.4 had no
 * NBT for (max stack size, food, rarity, a tool, ...) has no key.
 *
 * <p>What does not survive the trip back: HideFlags bits for a part the stack does not have (1.20.5
 * keeps the flag on the component, so a bit with no component under it is gone), and 1.20.5 tooltip
 * flags on a part 1.20.4 had no bit for.
 */
public final class CbItemLegacy {

    /** 1.20.4's data version */
    static final int DATA_VERSION_1_20_4 = 3700;

    /** the components the fix makes, by id: the ones a 1.20.4 tag speaks for */
    static final List<String> MAPPED = List.of(
        "minecraft:custom_data", "minecraft:damage", "minecraft:repair_cost", "minecraft:custom_model_data",
        "minecraft:block_state", "minecraft:entity_data", "minecraft:block_entity_data", "minecraft:unbreakable",
        "minecraft:enchantments", "minecraft:stored_enchantments", "minecraft:enchantment_glint_override",
        "minecraft:custom_name", "minecraft:item_name", "minecraft:lore", "minecraft:dyed_color", "minecraft:map_color",
        "minecraft:can_break", "minecraft:can_place_on", "minecraft:attribute_modifiers", "minecraft:trim",
        "minecraft:hide_additional_tooltip", "minecraft:charged_projectiles", "minecraft:bundle_contents",
        "minecraft:map_id", "minecraft:map_decorations", "minecraft:potion_contents",
        "minecraft:writable_book_content", "minecraft:written_book_content", "minecraft:suspicious_stew_effects",
        "minecraft:debug_stick_state", "minecraft:bucket_entity_data", "minecraft:instrument", "minecraft:recipes",
        "minecraft:lodestone_tracker", "minecraft:fireworks", "minecraft:firework_explosion", "minecraft:profile",
        "minecraft:lock", "minecraft:container_loot", "minecraft:container", "minecraft:bees",
        "minecraft:banner_patterns", "minecraft:base_color", "minecraft:pot_decorations", "minecraft:note_block_sound");

    /**
     * Top-level 1.20.4 keys the fix may read (for some item or other): a change to one of them needs the
     * fix; a change to any other key is custom data only.
     */
    static final Set<String> VANILLA_KEYS = Set.of(
        "HideFlags", "Damage", "RepairCost", "CustomModelData", "BlockStateTag", "EntityTag", "BlockEntityTag",
        "Unbreakable", "Enchantments", "StoredEnchantments", "display", "CanDestroy", "CanPlaceOn",
        "AttributeModifiers", "Trim", "Charged", "ChargedProjectiles", "Items", "map", "map_scale_direction",
        "map_to_lock", "Decorations", "Potion", "CustomPotionColor", "custom_potion_effects", "CustomPotionEffects",
        "pages", "filtered_pages", "title", "filtered_title", "author", "generation", "resolved", "effects",
        "DebugProperty", "instrument", "Recipes", "LodestonePos", "LodestoneDimension", "LodestoneTracked",
        "Fireworks", "Explosion", "SkullOwner", "BucketVariantTag", "NoAI", "Silent", "NoGravity", "Glowing",
        "Invulnerable", "Health", "Age", "Variant", "HuntingCooldown");

    // 1.20.4 HideFlags bits (ItemStack.TooltipPart masks)
    static final int HIDE_ENCHANTMENTS = 1;
    static final int HIDE_MODIFIERS = 2;
    static final int HIDE_UNBREAKABLE = 4;
    static final int HIDE_CAN_DESTROY = 8;
    static final int HIDE_CAN_PLACE = 16;
    static final int HIDE_ADDITIONAL = 32;
    static final int HIDE_DYE = 64;
    static final int HIDE_UPGRADES = 128;

    /** map decoration types by their 1.20.4 byte, as the fix's fixMapDecorationType reads them */
    static final List<String> DECORATIONS = List.of("player", "frame", "red_marker", "blue_marker", "target_x",
        "target_point", "player_off_map", "player_off_limits", "mansion", "monument", "banner_white", "banner_orange",
        "banner_magenta", "banner_light_blue", "banner_yellow", "banner_lime", "banner_pink", "banner_gray",
        "banner_light_gray", "banner_cyan", "banner_purple", "banner_blue", "banner_brown", "banner_green",
        "banner_red", "banner_black", "red_x", "village_desert", "village_plains", "village_savanna",
        "village_snowy", "village_taiga", "jungle_temple", "swamp_hut");

    /** firework shapes by their 1.20.4 byte (FireworkRocketItem.Shape ids) */
    static final List<String> SHAPES = List.of("small_ball", "large_ball", "star", "creeper", "burst");

    /** dye colours by their 1.20.4 id (DyeColor) */
    static final List<String> DYES = List.of("white", "orange", "magenta", "light_blue", "yellow", "lime", "pink",
        "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black");

    /** 1.20.4's banner pattern codes by pattern id (shared: rt/CbBannerPatterns) */
    static final Map<String, String> PATTERN_CODES = CbBannerPatterns.CODES;

    // NBT type ids
    private static final int NUMERIC = 99;
    private static final int STRING = 8;
    private static final int LIST = 9;
    private static final int COMPOUND = 10;

    // ---------------------------------------------------------------- 1.20.5 -> 1.20.4

    /** a stack's 1.20.4 tag computed from its components, and whether 1.20.4 would have had one */
    record Legacy(net.minecraft.class_2487 tag, boolean present) {
    }

    /** the stack's 1.20.4 tag, read off its component map (direct: no hook of the stack runs) */
    static Legacy of(net.minecraft.class_1799 stack, net.minecraft.class_9335 components) {
        Set<String> removed = new HashSet<>();
        net.minecraft.class_2487 added = encode(components.method_57940(), removed);
        boolean damageable = components.method_57832(net.minecraft.class_9334.field_50072);
        Integer damage = components.method_57829(net.minecraft.class_9334.field_49629);
        return legacy(added, removed, damageable, damage == null ? 0 : damage);
    }

    /** each added component of a patch encoded with its codec, by id; removed ones noted in removed */
    @SuppressWarnings({"rawtypes", "unchecked"})
    static net.minecraft.class_2487 encode(net.minecraft.class_9326 patch, Set<String> removed) {
        net.minecraft.class_2487 out = new net.minecraft.class_2487();
        if (patch.method_57848()) {
            return out;
        }
        com.mojang.serialization.DynamicOps<net.minecraft.class_2520> ops =
            net.minecraft.class_6903.method_46632(net.minecraft.class_2509.field_11560, CbRegistries.current());
        for (Object o : patch.method_57846()) {
            Map.Entry e = (Map.Entry) o;
            net.minecraft.class_9331 type = (net.minecraft.class_9331) e.getKey();
            net.minecraft.class_2960 id = net.minecraft.class_7923.field_49658.method_10221(type);
            if (id == null) {
                continue;
            }
            Optional value = (Optional) e.getValue();
            if (value.isEmpty()) {
                removed.add(id.toString());
                continue;
            }
            Codec codec = type.method_57875();
            if (codec == null) {
                continue; // a transient component (never saved, never NBT)
            }
            ((com.mojang.serialization.DataResult<net.minecraft.class_2520>) codec.encodeStart(ops, value.get()))
                .resultOrPartial(err -> CbItemTags.warn("encoding " + id + " for the 1.20.4 NBT view: " + err))
                .ifPresent(t -> out.method_10566(id.toString(), t));
        }
        return out;
    }

    /** a 1.20.5 stack in NBT ({id, count, components}) as 1.20.4's ({id, Count, tag}) */
    static net.minecraft.class_2487 legacyStack(net.minecraft.class_2520 encoded) {
        net.minecraft.class_2487 out = new net.minecraft.class_2487();
        if (!(encoded instanceof net.minecraft.class_2487 s)) {
            return out;
        }
        String id = s.method_10558("id");
        out.method_10582("id", id);
        out.method_10567("Count", (byte) (s.method_10573("count", NUMERIC) ? s.method_10550("count") : 1));
        net.minecraft.class_2487 comps = s.method_10562("components");
        Set<String> removed = new HashSet<>();
        net.minecraft.class_2487 added = new net.minecraft.class_2487();
        for (String k : comps.method_10541()) {
            if (k.startsWith("!")) {
                removed.add(k.substring(1));
            } else {
                added.method_10566(k, comps.method_10580(k));
            }
        }
        net.minecraft.class_2960 rl = net.minecraft.class_2960.method_12829(id);
        net.minecraft.class_1792 item = rl == null ? null : net.minecraft.class_7923.field_41178.method_10223(rl);
        boolean damageable = added.method_10545("minecraft:max_damage") && !removed.contains("minecraft:max_damage")
            || item != null && item.method_57347().method_57832(net.minecraft.class_9334.field_50072)
            && !removed.contains("minecraft:max_damage");
        Legacy l = legacy(added, removed, damageable,
            added.method_10573("minecraft:damage", NUMERIC) ? added.method_10550("minecraft:damage") : 0);
        if (l.present()) {
            out.method_10566("tag", l.tag());
        }
        return out;
    }

    /**
     * The 1.20.4 tag for components in NBT (the fix, backwards). damageable: the item has a durability
     * (1.20.4 kept "Damage" on every such stack, writing it in the constructor and on every setTag).
     */
    static Legacy legacy(net.minecraft.class_2487 c, Set<String> removed, boolean damageable, int damage) {
        net.minecraft.class_2487 t = new net.minecraft.class_2487();
        int hide = 0;
        boolean present = false;
        // keys the game never owned (and vanilla-named keys the fix left there for an item they mean
        // nothing on): first, so a component's own key wins as it did before the fix moved it out
        if (c.method_10580("minecraft:custom_data") instanceof net.minecraft.class_2487 cd) {
            t.method_10543(cd);
            present = true;
        }
        // a bucketed mob's own keys lived at the top level of the bucket's tag
        if (c.method_10580("minecraft:bucket_entity_data") instanceof net.minecraft.class_2487 b) {
            t.method_10543(b);
        }
        if (num(c, "minecraft:damage") != null) {
            t.method_10569("Damage", num(c, "minecraft:damage").method_10701());
        } else if (damageable) {
            t.method_10569("Damage", damage);
        }
        if (num(c, "minecraft:repair_cost") != null) {
            t.method_10569("RepairCost", num(c, "minecraft:repair_cost").method_10701());
        }
        if (num(c, "minecraft:custom_model_data") != null) {
            t.method_10569("CustomModelData", num(c, "minecraft:custom_model_data").method_10701());
        }
        if (c.method_10580("minecraft:unbreakable") instanceof net.minecraft.class_2487 u) {
            t.method_10556("Unbreakable", true);
            if (!shown(u)) {
                hide |= HIDE_UNBREAKABLE;
            }
        }
        boolean enchanted = false;
        if (c.method_10580("minecraft:enchantments") instanceof net.minecraft.class_2487 en) {
            net.minecraft.class_2499 list = enchantments(en);
            if (!list.isEmpty()) {
                t.method_10566("Enchantments", list);
                enchanted = true;
            }
            if (!shown(en)) {
                hide |= HIDE_ENCHANTMENTS;
            }
        }
        if (c.method_10580("minecraft:stored_enchantments") instanceof net.minecraft.class_2487 st) {
            t.method_10566("StoredEnchantments", enchantments(st));
            if (!shown(st)) {
                hide |= HIDE_ADDITIONAL;
            }
        }
        // a present but empty (or unreadable) Enchantments list made 1.20.4 draw the glint; the fix turns
        // that into the glint override
        if (!enchanted && c.method_10580("minecraft:enchantment_glint_override") instanceof net.minecraft.class_2514 g
                && g.method_10698() != 0) {
            net.minecraft.class_2499 list = new net.minecraft.class_2499();
            list.add(new net.minecraft.class_2487());
            t.method_10566("Enchantments", list);
        }
        net.minecraft.class_2487 display = new net.minecraft.class_2487();
        if (c.method_10580("minecraft:custom_name") instanceof net.minecraft.class_2519 name) {
            display.method_10582("Name", name.method_10714());
        }
        if (c.method_10580("minecraft:lore") instanceof net.minecraft.class_2499 lore) {
            display.method_10566("Lore", lore.method_10612());
        }
        net.minecraft.class_2520 dyed = c.method_10580("minecraft:dyed_color");
        if (dyed instanceof net.minecraft.class_2487 d) {
            display.method_10569("color", d.method_10550("rgb"));
            if (!shown(d)) {
                hide |= HIDE_DYE;
            }
        } else if (dyed instanceof net.minecraft.class_2514 rgb) {
            display.method_10569("color", rgb.method_10701());
        }
        if (num(c, "minecraft:map_color") != null) {
            display.method_10569("MapColor", num(c, "minecraft:map_color").method_10701());
        }
        if (c.method_10580("minecraft:item_name") instanceof net.minecraft.class_2519 itemName) {
            String key = translationKey(itemName.method_10714());
            if (key != null) {
                display.method_10582("LocName", key);
            }
        }
        if (!display.method_33133()) {
            t.method_10566("display", display);
        }
        hide |= adventure(c, "minecraft:can_break", "CanDestroy", HIDE_CAN_DESTROY, t);
        hide |= adventure(c, "minecraft:can_place_on", "CanPlaceOn", HIDE_CAN_PLACE, t);
        if (c.method_10580("minecraft:attribute_modifiers") instanceof net.minecraft.class_2487 am) {
            t.method_10566("AttributeModifiers", modifiers(am));
            if (!shown(am)) {
                hide |= HIDE_MODIFIERS;
            }
        } else if (removed.contains("minecraft:attribute_modifiers")) {
            // 1.20.4 cleared an item's modifiers with an empty list
            t.method_10566("AttributeModifiers", new net.minecraft.class_2499());
        }
        if (c.method_10580("minecraft:trim") instanceof net.minecraft.class_2487 tr) {
            net.minecraft.class_2487 trim = new net.minecraft.class_2487();
            copy(tr, "material", trim, "material");
            copy(tr, "pattern", trim, "pattern");
            t.method_10566("Trim", trim);
            if (!shown(tr)) {
                hide |= HIDE_UPGRADES;
            }
        }
        if (c.method_10545("minecraft:hide_additional_tooltip")) {
            hide |= HIDE_ADDITIONAL;
        }
        if (c.method_10580("minecraft:charged_projectiles") instanceof net.minecraft.class_2499 cp) {
            net.minecraft.class_2499 items = stacks(cp);
            t.method_10566("ChargedProjectiles", items);
            t.method_10556("Charged", !items.isEmpty());
        }
        if (c.method_10580("minecraft:bundle_contents") instanceof net.minecraft.class_2499 bc) {
            t.method_10566("Items", stacks(bc));
        }
        if (num(c, "minecraft:map_id") != null) {
            t.method_10569("map", num(c, "minecraft:map_id").method_10701());
        }
        if (c.method_10580("minecraft:map_decorations") instanceof net.minecraft.class_2487 md) {
            t.method_10566("Decorations", decorations(md));
        }
        net.minecraft.class_2520 pc = c.method_10580("minecraft:potion_contents");
        if (pc instanceof net.minecraft.class_2519 potion) {
            t.method_10582("Potion", potion.method_10714());
        } else if (pc instanceof net.minecraft.class_2487 p) {
            copy(p, "potion", t, "Potion");
            copy(p, "custom_color", t, "CustomPotionColor");
            copy(p, "custom_effects", t, "custom_potion_effects");
        }
        if (c.method_10580("minecraft:writable_book_content") instanceof net.minecraft.class_2487 wb) {
            pages(wb.method_10580("pages"), t, false);
        }
        if (c.method_10580("minecraft:written_book_content") instanceof net.minecraft.class_2487 wr) {
            filtered(wr.method_10580("title"), t, "title", "filtered_title");
            copy(wr, "author", t, "author");
            copy(wr, "generation", t, "generation");
            copy(wr, "resolved", t, "resolved");
            pages(wr.method_10580("pages"), t, true);
        }
        copy(c, "minecraft:suspicious_stew_effects", t, "effects");
        copy(c, "minecraft:debug_stick_state", t, "DebugProperty");
        copy(c, "minecraft:entity_data", t, "EntityTag");
        copy(c, "minecraft:block_state", t, "BlockStateTag");
        copy(c, "minecraft:instrument", t, "instrument");
        copy(c, "minecraft:recipes", t, "Recipes");
        if (c.method_10580("minecraft:lodestone_tracker") instanceof net.minecraft.class_2487 lt) {
            if (lt.method_10580("target") instanceof net.minecraft.class_2487 target) {
                int[] pos = target.method_10561("pos");
                if (pos.length == 3) {
                    net.minecraft.class_2487 p = new net.minecraft.class_2487();
                    p.method_10569("X", pos[0]);
                    p.method_10569("Y", pos[1]);
                    p.method_10569("Z", pos[2]);
                    t.method_10566("LodestonePos", p);
                }
                copy(target, "dimension", t, "LodestoneDimension");
            }
            t.method_10556("LodestoneTracked", !lt.method_10545("tracked") || lt.method_10577("tracked"));
        }
        if (c.method_10580("minecraft:firework_explosion") instanceof net.minecraft.class_2487 fe) {
            t.method_10566("Explosion", explosion(fe));
        }
        if (c.method_10580("minecraft:fireworks") instanceof net.minecraft.class_2487 fw) {
            net.minecraft.class_2487 f = new net.minecraft.class_2487();
            f.method_10567("Flight", fw.method_10571("flight_duration"));
            net.minecraft.class_2499 ex = new net.minecraft.class_2499();
            for (net.minecraft.class_2520 e : fw.method_10554("explosions", COMPOUND)) {
                ex.add(explosion((net.minecraft.class_2487) e));
            }
            f.method_10566("Explosions", ex);
            t.method_10566("Fireworks", f);
        }
        net.minecraft.class_2520 profile = c.method_10580("minecraft:profile");
        if (profile != null) {
            t.method_10566("SkullOwner", profile(profile));
        }
        net.minecraft.class_2487 be = blockEntity(c);
        if (be != null) {
            t.method_10566("BlockEntityTag", be);
        }
        if (hide != 0) {
            t.method_10569("HideFlags", hide);
        }
        return new Legacy(t, present || !t.method_33133());
    }

    private static net.minecraft.class_2514 num(net.minecraft.class_2487 c, String key) {
        return c.method_10580(key) instanceof net.minecraft.class_2514 n ? n : null;
    }

    /** show_in_tooltip, true when absent (every component's default) */
    private static boolean shown(net.minecraft.class_2487 component) {
        return !component.method_10545("show_in_tooltip") || component.method_10577("show_in_tooltip");
    }

    private static void copy(net.minecraft.class_2487 from, String key, net.minecraft.class_2487 to, String as) {
        net.minecraft.class_2520 v = from.method_10580(key);
        if (v != null) {
            to.method_10566(as, v.method_10707());
        }
    }

    /** {levels: {id: level}} -> [{id, lvl short}] */
    private static net.minecraft.class_2499 enchantments(net.minecraft.class_2487 component) {
        net.minecraft.class_2499 list = new net.minecraft.class_2499();
        net.minecraft.class_2487 levels = component.method_10562("levels");
        for (String id : levels.method_10541()) {
            net.minecraft.class_2487 e = new net.minecraft.class_2487();
            e.method_10582("id", id);
            e.method_10575("lvl", (short) levels.method_10550(id));
            list.add(e);
        }
        return list;
    }

    /** the translation key of a component that is only that translation, else null */
    private static String translationKey(String json) {
        try {
            com.google.gson.JsonElement e = com.google.gson.JsonParser.parseString(json);
            if (e.isJsonObject() && e.getAsJsonObject().size() == 1 && e.getAsJsonObject().has("translate")) {
                return e.getAsJsonObject().get("translate").getAsString();
            }
        } catch (RuntimeException ex) {
            // not JSON: not a translation
        }
        return null;
    }

    /** can_break / can_place_on -> the 1.20.4 list of block predicate strings; answers the hide bit */
    private static int adventure(net.minecraft.class_2487 c, String component, String key, int bit,
                                 net.minecraft.class_2487 t) {
        if (!(c.method_10580(component) instanceof net.minecraft.class_2487 a)) {
            return 0;
        }
        net.minecraft.class_2499 out = new net.minecraft.class_2499();
        net.minecraft.class_2520 preds = a.method_10580("predicates");
        net.minecraft.class_2499 list = new net.minecraft.class_2499();
        if (preds instanceof net.minecraft.class_2499 l) {
            list = l;
        } else if (preds instanceof net.minecraft.class_2487 single) {
            list.add(single);
        }
        for (net.minecraft.class_2520 p : list) {
            if (p instanceof net.minecraft.class_2487 pred) {
                for (String s : predicateStrings(pred)) {
                    out.add(net.minecraft.class_2519.method_23256(s));
                }
            }
        }
        t.method_10566(key, out);
        return shown(a) ? 0 : bit;
    }

    /** one block predicate ({blocks, state, nbt}) as 1.20.4's strings: one per block id or tag */
    private static List<String> predicateStrings(net.minecraft.class_2487 pred) {
        StringBuilder suffix = new StringBuilder();
        if (pred.method_10580("state") instanceof net.minecraft.class_2487 state && !state.method_33133()) {
            suffix.append('[');
            boolean first = true;
            for (String k : state.method_10541()) {
                if (!first) {
                    suffix.append(',');
                }
                first = false;
                suffix.append(k).append('=').append(state.method_10580(k).method_10714());
            }
            suffix.append(']');
        }
        if (pred.method_10580("nbt") instanceof net.minecraft.class_2519 nbt) {
            suffix.append(nbt.method_10714());
        }
        List<String> out = new java.util.ArrayList<>();
        net.minecraft.class_2520 blocks = pred.method_10580("blocks");
        if (blocks instanceof net.minecraft.class_2519 s) {
            out.add(s.method_10714() + suffix);
        } else if (blocks instanceof net.minecraft.class_2499 l) {
            for (net.minecraft.class_2520 b : l) {
                out.add(b.method_10714() + suffix);
            }
        }
        return out;
    }

    /** {modifiers: [{type, slot, uuid, name, amount, operation}]} -> [{AttributeName, Slot, UUID, Name, Amount, Operation}] */
    private static net.minecraft.class_2499 modifiers(net.minecraft.class_2487 component) {
        net.minecraft.class_2499 out = new net.minecraft.class_2499();
        for (net.minecraft.class_2520 e : component.method_10554("modifiers", COMPOUND)) {
            net.minecraft.class_2487 m = (net.minecraft.class_2487) e;
            net.minecraft.class_2487 o = new net.minecraft.class_2487();
            copy(m, "type", o, "AttributeName");
            String slot = m.method_10558("slot");
            if (!slot.isEmpty() && !slot.equals("any")) {
                o.method_10582("Slot", slot);
            }
            copy(m, "uuid", o, "UUID");
            copy(m, "name", o, "Name");
            copy(m, "amount", o, "Amount");
            o.method_10569("Operation", switch (m.method_10558("operation")) {
                case "add_multiplied_base" -> 1;
                case "add_multiplied_total" -> 2;
                default -> 0;
            });
            out.add(o);
        }
        return out;
    }

    /** a list of encoded 1.20.5 stacks as 1.20.4 stacks */
    private static net.minecraft.class_2499 stacks(net.minecraft.class_2499 encoded) {
        net.minecraft.class_2499 out = new net.minecraft.class_2499();
        for (net.minecraft.class_2520 s : encoded) {
            out.add(legacyStack(s));
        }
        return out;
    }

    /** {key: {type, x, z, rotation}} -> [{id, type byte, x, z, rot}] */
    private static net.minecraft.class_2499 decorations(net.minecraft.class_2487 component) {
        net.minecraft.class_2499 out = new net.minecraft.class_2499();
        for (String key : component.method_10541()) {
            if (!(component.method_10580(key) instanceof net.minecraft.class_2487 d)) {
                continue;
            }
            String type = d.method_10558("type");
            if (type.startsWith("minecraft:")) {
                type = type.substring("minecraft:".length());
            }
            int idx = DECORATIONS.indexOf(type);
            net.minecraft.class_2487 o = new net.minecraft.class_2487();
            o.method_10582("id", key);
            o.method_10567("type", (byte) Math.max(idx, 0));
            o.method_10549("x", d.method_10574("x"));
            o.method_10549("z", d.method_10574("z"));
            o.method_10549("rot", d.method_10583("rotation"));
            out.add(o);
        }
        return out;
    }

    /** a Filterable ({raw, filtered?} or the raw value alone) into key and filteredKey */
    private static void filtered(net.minecraft.class_2520 f, net.minecraft.class_2487 t, String key, String filteredKey) {
        if (f instanceof net.minecraft.class_2487 o) {
            copy(o, "raw", t, key);
            copy(o, "filtered", t, filteredKey);
        } else if (f != null) {
            t.method_10566(key, f.method_10707());
        }
    }

    /** book pages ([{raw, filtered?}]) -> pages [raw] + filtered_pages {"index": filtered} */
    private static void pages(net.minecraft.class_2520 pagesTag, net.minecraft.class_2487 t, boolean written) {
        if (!(pagesTag instanceof net.minecraft.class_2499 list)) {
            return;
        }
        net.minecraft.class_2499 pages = new net.minecraft.class_2499();
        net.minecraft.class_2487 filteredPages = new net.minecraft.class_2487();
        for (int i = 0; i < list.size(); i++) {
            net.minecraft.class_2520 p = list.method_10534(i);
            if (p instanceof net.minecraft.class_2487 o) {
                net.minecraft.class_2520 raw = o.method_10580("raw");
                pages.add(raw == null ? net.minecraft.class_2519.method_23256("") : raw.method_10707());
                if (o.method_10580("filtered") instanceof net.minecraft.class_2520 fl) {
                    filteredPages.method_10566(String.valueOf(i), fl.method_10707());
                }
            } else {
                pages.add(p.method_10707());
            }
        }
        t.method_10566("pages", pages);
        if (!filteredPages.method_33133()) {
            t.method_10566("filtered_pages", filteredPages);
        }
    }

    /** {shape, colors, fade_colors, has_trail, has_twinkle} -> {Type, Colors, FadeColors, Trail, Flicker} */
    private static net.minecraft.class_2487 explosion(net.minecraft.class_2487 e) {
        net.minecraft.class_2487 o = new net.minecraft.class_2487();
        o.method_10567("Type", (byte) Math.max(SHAPES.indexOf(e.method_10558("shape")), 0));
        copy(e, "colors", o, "Colors");
        copy(e, "fade_colors", o, "FadeColors");
        if (e.method_10577("has_trail")) {
            o.method_10556("Trail", true);
        }
        if (e.method_10577("has_twinkle")) {
            o.method_10556("Flicker", true);
        }
        return o;
    }

    /** a profile ({name?, id?, properties?} or a name) -> 1.20.4's SkullOwner compound */
    private static net.minecraft.class_2520 profile(net.minecraft.class_2520 p) {
        net.minecraft.class_2487 o = new net.minecraft.class_2487();
        if (p instanceof net.minecraft.class_2519 name) {
            o.method_10582("Name", name.method_10714());
            return o;
        }
        if (!(p instanceof net.minecraft.class_2487 c)) {
            return o;
        }
        copy(c, "name", o, "Name");
        copy(c, "id", o, "Id");
        net.minecraft.class_2487 props = new net.minecraft.class_2487();
        for (net.minecraft.class_2520 e : c.method_10554("properties", COMPOUND)) {
            net.minecraft.class_2487 prop = (net.minecraft.class_2487) e;
            net.minecraft.class_2487 v = new net.minecraft.class_2487();
            copy(prop, "value", v, "Value");
            copy(prop, "signature", v, "Signature");
            String name = prop.method_10558("name");
            net.minecraft.class_2499 l = props.method_10554(name, COMPOUND);
            l.add(v);
            props.method_10566(name, l);
        }
        if (!props.method_33133()) {
            o.method_10566("Properties", props);
        }
        return o;
    }

    /** BlockEntityTag: block_entity_data and the components the fix split off it, put back */
    private static net.minecraft.class_2487 blockEntity(net.minecraft.class_2487 c) {
        net.minecraft.class_2487 be = c.method_10580("minecraft:block_entity_data") instanceof net.minecraft.class_2487 d
            ? d.method_10553() : new net.minecraft.class_2487();
        boolean any = c.method_10545("minecraft:block_entity_data");
        if (c.method_10580("minecraft:lock") instanceof net.minecraft.class_2519 lock) {
            be.method_10582("Lock", lock.method_10714());
            any = true;
        }
        if (c.method_10580("minecraft:container_loot") instanceof net.minecraft.class_2487 loot) {
            copy(loot, "loot_table", be, "LootTable");
            if (loot.method_10545("seed")) {
                be.method_10544("LootTableSeed", loot.method_10537("seed"));
            }
            any = true;
        }
        if (c.method_10580("minecraft:container") instanceof net.minecraft.class_2499 items) {
            net.minecraft.class_2499 out = new net.minecraft.class_2499();
            for (net.minecraft.class_2520 e : items) {
                net.minecraft.class_2487 slot = (net.minecraft.class_2487) e;
                net.minecraft.class_2487 st = legacyStack(slot.method_10580("item"));
                st.method_10567("Slot", (byte) slot.method_10550("slot"));
                out.add(st);
            }
            be.method_10566("Items", out);
            any = true;
        }
        if (c.method_10580("minecraft:bees") instanceof net.minecraft.class_2499 bees) {
            net.minecraft.class_2499 out = new net.minecraft.class_2499();
            for (net.minecraft.class_2520 e : bees) {
                net.minecraft.class_2487 bee = (net.minecraft.class_2487) e;
                net.minecraft.class_2487 o = new net.minecraft.class_2487();
                copy(bee, "entity_data", o, "EntityData");
                copy(bee, "ticks_in_hive", o, "TicksInHive");
                copy(bee, "min_ticks_in_hive", o, "MinOccupationTicks");
                out.add(o);
            }
            be.method_10566("Bees", out);
            any = true;
        }
        if (c.method_10580("minecraft:banner_patterns") instanceof net.minecraft.class_2499 patterns) {
            net.minecraft.class_2499 out = new net.minecraft.class_2499();
            for (net.minecraft.class_2520 e : patterns) {
                net.minecraft.class_2487 layer = (net.minecraft.class_2487) e;
                String pattern = layer.method_10558("pattern");
                String code = PATTERN_CODES.get(pattern.contains(":") ? pattern : "minecraft:" + pattern);
                net.minecraft.class_2487 o = new net.minecraft.class_2487();
                o.method_10582("Pattern", code != null ? code : pattern);
                o.method_10569("Color", Math.max(DYES.indexOf(layer.method_10558("color")), 0));
                out.add(o);
            }
            be.method_10566("Patterns", out);
            any = true;
        }
        if (c.method_10580("minecraft:base_color") instanceof net.minecraft.class_2519 base) {
            be.method_10569("Base", Math.max(DYES.indexOf(base.method_10714()), 0));
            any = true;
        }
        if (c.method_10580("minecraft:pot_decorations") instanceof net.minecraft.class_2499 sherds) {
            be.method_10566("sherds", sherds.method_10612());
            any = true;
        }
        if (c.method_10580("minecraft:note_block_sound") instanceof net.minecraft.class_2519 sound) {
            be.method_10582("note_block_sound", sound.method_10714());
            any = true;
        }
        return any ? be : null;
    }

    // ---------------------------------------------------------------- 1.20.4 -> 1.20.5

    /**
     * The components a 1.20.4 tag stands for on this item: the game's upgrade of {id, Count, tag},
     * decoded. Ids of the components the upgrade produced -> value; a component that would not decode is
     * reported and left out.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    static Map<net.minecraft.class_9331<?>, Object> upgrade(net.minecraft.class_1792 item, net.minecraft.class_2487 tag) {
        net.minecraft.class_2487 in = new net.minecraft.class_2487();
        in.method_10582("id", net.minecraft.class_7923.field_41178.method_10221(item).toString());
        in.method_10567("Count", (byte) 1);
        in.method_10566("tag", tag.method_10553());
        int now = net.minecraft.class_155.method_16673().method_37912().method_38494();
        Dynamic<net.minecraft.class_2520> out = net.minecraft.class_3551.method_15450().update(
            net.minecraft.class_1208.field_5712, new Dynamic<>(net.minecraft.class_2509.field_11560, in),
            DATA_VERSION_1_20_4, now);
        net.minecraft.class_2487 comps = out.getValue() instanceof net.minecraft.class_2487 s
            ? s.method_10562("components") : new net.minecraft.class_2487();
        com.mojang.serialization.DynamicOps<net.minecraft.class_2520> ops =
            net.minecraft.class_6903.method_46632(net.minecraft.class_2509.field_11560, CbRegistries.current());
        Map<net.minecraft.class_9331<?>, Object> result = new java.util.LinkedHashMap<>();
        for (String key : comps.method_10541()) {
            net.minecraft.class_2960 id = net.minecraft.class_2960.method_12829(key);
            net.minecraft.class_9331 type = id == null ? null : net.minecraft.class_7923.field_49658.method_10223(id);
            Codec codec = type == null ? null : type.method_57875();
            if (codec == null) {
                CbItemTags.warn("the 1.20.4 NBT of " + in.method_10558("id") + " upgrades to an unknown component " + key);
                continue;
            }
            ((com.mojang.serialization.DataResult<Object>) codec.parse(ops, comps.method_10580(key)))
                .resultOrPartial(err -> CbItemTags.warn("decoding " + key + " from 1.20.4 NBT: " + err))
                .ifPresent(v -> result.put(type, v));
        }
        return result;
    }

    private CbItemLegacy() {
    }
}
