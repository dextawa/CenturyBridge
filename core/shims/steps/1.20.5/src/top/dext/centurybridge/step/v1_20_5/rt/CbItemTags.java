package top.dext.centurybridge.step.v1_20_5.rt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * An item stack's 1.20.4 NBT ("tag"), kept on its 1.20.5 components.
 *
 * <p>1.20.4 code holds the stack's tag as a live CompoundTag: it reads it, writes into it and into its
 * nested compounds and lists, keeps the reference, hands over its own compound with setTag. Here the
 * tag a 1.20.4 call gets is one compound per stack (the "view"), and the components stay the stack's
 * truth for everything else:
 * <ul>
 * <li>the view is made from the components the first time 1.20.4 code asks (CbItemLegacy: the
 *     componentization fix read backwards; custom data at the top level as before);</li>
 * <li>before vanilla reads the stack's components -- getComponents, the patch, copies, comparisons,
 *     the codecs and stream codec -- whatever 1.20.4 code changed in the view since it was last in
 *     step is written to the components ({@link #flush}): a change to keys only the mod owns goes to
 *     custom_data directly; any change to a key the game owned goes through the game's own upgrade of
 *     the whole tag (CbItemLegacy.upgrade), which sets every component a 1.20.4 tag speaks for;</li>
 * <li>after vanilla writes a component, the view is brought up to date in place ({@link #refresh}),
 *     so a reference 1.20.4 code holds sees the write, as it saw vanilla's writes into the tag.</li>
 * </ul>
 * A change is found by comparing the view with a snapshot of it from the last sync, so a change deep
 * inside a nested compound, or made through a compound the mod put in and kept, is found too.
 *
 * <p>"No tag" and "an empty tag" stay apart as they were in 1.20.4 (an empty tag made a stack differ
 * from a tagless one): an empty view is kept as an empty minecraft:custom_data, the one component that
 * says "this stack has 1.20.4 data, and it is empty".
 */
public final class CbItemTags {

    /** what a stack keeps for its view (CbItemStackMixin) */
    public interface State {
        /** the view handed to 1.20.4 code; null until it first asks */
        net.minecraft.class_2487 cb$s4Tag();

        void cb$s4Tag(net.minecraft.class_2487 tag);

        /** the view as of the last sync with the components; null forces the next flush to upgrade */
        net.minecraft.class_2487 cb$s4Snap();

        void cb$s4Snap(net.minecraft.class_2487 snap);

        /** set while the view and the components are being synchronized */
        boolean cb$s4Busy();

        void cb$s4Busy(boolean busy);

        /** the stack's component map */
        net.minecraft.class_9335 cb$s4Components();
    }

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger("CenturyBridge");
    private static final Set<String> WARNED = java.util.concurrent.ConcurrentHashMap.newKeySet();

    static void warn(String message) {
        if (WARNED.size() < 512 && WARNED.add(message)) {
            LOG.warn("[centurybridge 1.20.4 item NBT] {}", message);
        }
    }

    private static State state(net.minecraft.class_1799 stack) {
        return (State) (Object) stack;
    }

    // ---------------------------------------------------------------- the hooks (CbItemStackMixin)

    /** before vanilla reads the stack's components: write the view's changes to them */
    public static void flush(net.minecraft.class_1799 stack) {
        if (stack == null) {
            return;
        }
        State s = state(stack);
        net.minecraft.class_2487 tag = s.cb$s4Tag();
        if (tag == null || s.cb$s4Busy() || tag.equals(s.cb$s4Snap())) {
            return;
        }
        s.cb$s4Busy(true);
        try {
            push(stack, s, tag);
        } finally {
            s.cb$s4Busy(false);
        }
    }

    /** after vanilla wrote a component: bring the view up to date in place */
    public static void refresh(net.minecraft.class_1799 stack) {
        State s = state(stack);
        net.minecraft.class_2487 tag = s.cb$s4Tag();
        if (tag == null || s.cb$s4Busy()) {
            return;
        }
        s.cb$s4Busy(true);
        try {
            pullInto(stack, s, tag);
        } finally {
            s.cb$s4Busy(false);
        }
    }

    private static void push(net.minecraft.class_1799 stack, State s, net.minecraft.class_2487 tag) {
        net.minecraft.class_2487 snap = s.cb$s4Snap();
        net.minecraft.class_9335 comps = s.cb$s4Components();
        boolean vanilla = snap == null;
        Set<String> changed = new HashSet<>();
        if (!vanilla) {
            for (String k : tag.method_10541()) {
                if (!Objects.equals(tag.method_10580(k), snap.method_10580(k))) {
                    changed.add(k);
                }
            }
            for (String k : snap.method_10541()) {
                if (!tag.method_10545(k)) {
                    changed.add(k);
                }
            }
            for (String k : changed) {
                if (CbItemLegacy.VANILLA_KEYS.contains(k)) {
                    vanilla = true;
                    break;
                }
            }
        }
        if (!vanilla) {
            // only keys the game never owned changed: they live in custom_data, nothing else moves
            net.minecraft.class_9279 cd = comps.method_57829(net.minecraft.class_9334.field_49628);
            net.minecraft.class_2487 custom = cd == null ? new net.minecraft.class_2487() : cd.method_57461();
            for (String k : changed) {
                if (tag.method_10545(k)) {
                    custom.method_10566(k, tag.method_10580(k).method_10707());
                } else {
                    custom.method_10551(k);
                }
            }
            setCustom(stack, comps, custom, tag.method_33133());
            s.cb$s4Snap(tag.method_10553());
            return;
        }
        // a key the game owned changed: the game's upgrade of the whole tag decides every component
        Map<net.minecraft.class_9331<?>, Object> up = CbItemLegacy.upgrade(stack.method_7909(), tag);
        net.minecraft.class_9323 proto = stack.method_58658();
        for (String id : CbItemLegacy.MAPPED) {
            net.minecraft.class_9331<?> type = type(id);
            if (type == null) {
                continue;
            }
            if (up.containsKey(type)) {
                set(comps, type, up.get(type));
            } else {
                reset(comps, proto, type);
            }
        }
        for (Map.Entry<net.minecraft.class_9331<?>, Object> e : up.entrySet()) {
            net.minecraft.class_2960 id = net.minecraft.class_7923.field_49658.method_10221(e.getKey());
            if (id != null && !CbItemLegacy.MAPPED.contains(id.toString())) {
                set(comps, e.getKey(), e.getValue()); // anything else the upgrade made
            }
        }
        if (tag.method_33133()) {
            comps.method_57938(net.minecraft.class_9334.field_49628, net.minecraft.class_9279.field_49302);
        }
        pullInto(stack, s, tag);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void set(net.minecraft.class_9335 comps, net.minecraft.class_9331 type, Object value) {
        comps.method_57938(type, value);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void reset(net.minecraft.class_9335 comps, net.minecraft.class_9323 proto, net.minecraft.class_9331 type) {
        if (proto.method_57832(type)) {
            comps.method_57938(type, proto.method_57829(type)); // the prototype's value: no patch entry
        } else {
            comps.method_57939(type);
        }
    }

    private static void setCustom(net.minecraft.class_1799 stack, net.minecraft.class_9335 comps,
                                  net.minecraft.class_2487 custom, boolean tagEmpty) {
        if (!custom.method_33133()) {
            comps.method_57938(net.minecraft.class_9334.field_49628, net.minecraft.class_9279.method_57456(custom));
        } else if (tagEmpty) {
            comps.method_57938(net.minecraft.class_9334.field_49628, net.minecraft.class_9279.field_49302);
        } else {
            reset(comps, stack.method_58658(), net.minecraft.class_9334.field_49628);
        }
    }

    private static net.minecraft.class_9331<?> type(String id) {
        net.minecraft.class_2960 rl = net.minecraft.class_2960.method_12829(id);
        return rl == null ? null : net.minecraft.class_7923.field_49658.method_10223(rl);
    }

    /** the view rewritten in place to what the components say, the snapshot taken */
    private static void pullInto(net.minecraft.class_1799 stack, State s, net.minecraft.class_2487 tag) {
        net.minecraft.class_2487 fresh = CbItemLegacy.of(stack, s.cb$s4Components()).tag();
        for (String k : new ArrayList<>(tag.method_10541())) {
            if (!fresh.method_10545(k)) {
                tag.method_10551(k);
            }
        }
        for (String k : fresh.method_10541()) {
            net.minecraft.class_2520 v = fresh.method_10580(k);
            if (!v.equals(tag.method_10580(k))) {
                tag.method_10566(k, v);
            }
        }
        s.cb$s4Snap(tag.method_10553());
    }

    /** the stack's view, made on first use; present: 1.20.4 would have had a tag */
    private static net.minecraft.class_2487 view(net.minecraft.class_1799 stack) {
        State s = state(stack);
        net.minecraft.class_2487 tag = s.cb$s4Tag();
        if (tag == null) {
            flush(stack);
            tag = CbItemLegacy.of(stack, s.cb$s4Components()).tag();
            s.cb$s4Tag(tag);
            s.cb$s4Snap(tag.method_10553());
        }
        return tag;
    }

    /** whether the stack has 1.20.4 data at all (a view with keys, or the custom_data that says so) */
    private static boolean present(net.minecraft.class_1799 stack, net.minecraft.class_2487 view) {
        return !view.method_33133() || state(stack).cb$s4Components().method_57832(net.minecraft.class_9334.field_49628);
    }

    // ---------------------------------------------------------------- 1.20.4's ItemStack tag API

    /** ItemStack.getTag(): the stack's tag, null when it has none */
    public static net.minecraft.class_2487 getTag(net.minecraft.class_1799 stack) {
        if (stack.method_7960() && !hasView(stack)) {
            return null;
        }
        net.minecraft.class_2487 tag = view(stack);
        return present(stack, tag) ? tag : null;
    }

    private static boolean hasView(net.minecraft.class_1799 stack) {
        return state(stack).cb$s4Tag() != null;
    }

    /** ItemStack.hasTag(): not an empty stack, and a tag with something in it */
    public static boolean hasTag(net.minecraft.class_1799 stack) {
        if (stack.method_7960()) {
            return false;
        }
        net.minecraft.class_2487 tag = getTag(stack);
        return tag != null && !tag.method_33133();
    }

    /**
     * ItemStack.getOrCreateTag(): the tag, an empty one made first when there is none. On an empty
     * stack (the shared ItemStack.EMPTY among them) 1.20.4 wrote the new tag into that shared stack;
     * here such a stack hands out a compound of its own and keeps nothing.
     */
    public static net.minecraft.class_2487 getOrCreateTag(net.minecraft.class_1799 stack) {
        if (stack.method_7960()) {
            return new net.minecraft.class_2487();
        }
        net.minecraft.class_2487 tag = view(stack);
        if (!present(stack, tag)) {
            State s = state(stack);
            s.cb$s4Busy(true);
            try {
                // 1.20.4: setTag(new CompoundTag()) -- the stack now has a tag, an empty one
                s.cb$s4Components().method_57938(net.minecraft.class_9334.field_49628, net.minecraft.class_9279.field_49302);
                s.cb$s4Snap(tag.method_10553());
            } finally {
                s.cb$s4Busy(false);
            }
        }
        return tag;
    }

    /**
     * ItemStack.setTag(tag): the compound becomes the stack's tag (1.20.4 kept the reference: later
     * writes into it are the stack's), every component a 1.20.4 tag speaks for is set from it; then, as
     * 1.20.4 did, the item checks what it loaded (1.20.5's verifyComponentsAfterLoad).
     */
    public static void setTag(net.minecraft.class_1799 stack, net.minecraft.class_2487 tag) {
        if (stack.method_7960()) {
            return;
        }
        State s = state(stack);
        if (tag == null) {
            s.cb$s4Busy(true);
            try {
                net.minecraft.class_9323 proto = stack.method_58658();
                for (String id : CbItemLegacy.MAPPED) {
                    net.minecraft.class_9331<?> type = type(id);
                    if (type != null) {
                        reset(s.cb$s4Components(), proto, type);
                    }
                }
            } finally {
                s.cb$s4Busy(false);
            }
            s.cb$s4Tag(null);
            s.cb$s4Snap(null);
            return;
        }
        s.cb$s4Tag(tag);
        s.cb$s4Snap(null);
        flush(stack);
        stack.method_7909().method_7860(stack);
    }

    /** ItemStack.getTagElement(key): the compound under key, or null */
    public static net.minecraft.class_2487 getTagElement(net.minecraft.class_1799 stack, String key) {
        net.minecraft.class_2487 tag = getTag(stack);
        return tag != null && tag.method_10573(key, 10) ? tag.method_10562(key) : null;
    }

    /** ItemStack.getOrCreateTagElement(key): the compound under key, put in (empty) when missing */
    public static net.minecraft.class_2487 getOrCreateTagElement(net.minecraft.class_1799 stack, String key) {
        net.minecraft.class_2487 tag = getTag(stack);
        if (tag != null && tag.method_10573(key, 10)) {
            return tag.method_10562(key);
        }
        net.minecraft.class_2487 c = new net.minecraft.class_2487();
        addTagElement(stack, key, c);
        return c;
    }

    /** ItemStack.addTagElement(key, value): put into the tag (made when missing) */
    public static void addTagElement(net.minecraft.class_1799 stack, String key, net.minecraft.class_2520 value) {
        getOrCreateTag(stack).method_10566(key, value);
    }

    /** ItemStack.removeTagKey(key): remove it; a tag left empty is no tag (1.20.4 dropped it) */
    public static void removeTagKey(net.minecraft.class_1799 stack, String key) {
        net.minecraft.class_2487 tag = getTag(stack);
        if (tag != null && tag.method_10545(key)) {
            tag.method_10551(key);
            flush(stack);
            if (tag.method_33133()) {
                // nothing left: no tag at all, not the empty one
                State s = state(stack);
                s.cb$s4Busy(true);
                try {
                    reset(s.cb$s4Components(), stack.method_58658(), net.minecraft.class_9334.field_49628);
                } finally {
                    s.cb$s4Busy(false);
                }
            }
        }
    }

    /** ItemStack.getEnchantmentTags(): the tag's Enchantments list (the live one), or a new empty list */
    public static net.minecraft.class_2499 getEnchantmentTags(net.minecraft.class_1799 stack) {
        net.minecraft.class_2487 tag = getTag(stack);
        return tag != null ? tag.method_10554("Enchantments", 10) : new net.minecraft.class_2499();
    }

    /** ItemStack.save(tag): 1.20.4's stack NBT -- id, Count (a byte), and the tag when there is one */
    public static net.minecraft.class_2487 save(net.minecraft.class_1799 stack, net.minecraft.class_2487 out) {
        net.minecraft.class_2960 id = net.minecraft.class_7923.field_41178.method_10221(stack.method_7909());
        out.method_10582("id", id == null ? "minecraft:air" : id.toString());
        out.method_10567("Count", (byte) stack.method_7947());
        net.minecraft.class_2487 tag = getTag(stack);
        if (tag != null) {
            out.method_10566("tag", tag.method_10553());
        }
        return out;
    }

    /**
     * ItemStack.of(nbt): a stack from 1.20.4's stack NBT, upgraded by the game's own data fixer exactly as
     * 1.20.5 loads a 1.20.4 world's items. NBT already in 1.20.5's form ({id, count, components}: what
     * vanilla now writes, handed to 1.20.4 code) is read as it is. Like 1.20.4, an unreadable stack is
     * the empty stack.
     */
    public static net.minecraft.class_1799 of(net.minecraft.class_2487 nbt) {
        try {
            net.minecraft.class_2487 modern = nbt;
            if (!nbt.method_10545("components") && !nbt.method_10545("count")) {
                int now = net.minecraft.class_155.method_16673().method_37912().method_38494();
                com.mojang.serialization.Dynamic<net.minecraft.class_2520> up = net.minecraft.class_3551.method_15450().update(
                    net.minecraft.class_1208.field_5712, new com.mojang.serialization.Dynamic<>(net.minecraft.class_2509.field_11560, nbt),
                    CbItemLegacy.DATA_VERSION_1_20_4, now);
                if (!(up.getValue() instanceof net.minecraft.class_2487 c)) {
                    return net.minecraft.class_1799.field_8037;
                }
                modern = c;
            }
            return net.minecraft.class_1799.method_57359(CbRegistries.current(), modern);
        } catch (RuntimeException e) {
            warn("an unreadable 1.20.4 item stack: " + e);
            return net.minecraft.class_1799.field_8037;
        }
    }

    private CbItemTags() {
    }
}
