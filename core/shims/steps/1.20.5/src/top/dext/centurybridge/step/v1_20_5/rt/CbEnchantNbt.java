package top.dext.centurybridge.step.v1_20_5.rt;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 1.20.4's NBT side of enchantments, which 1.20.5 dropped with the NBT form of a stack's enchantments
 * (EnchantmentHelper.getEnchantmentId / getEnchantmentLevel / setEnchantmentLevel / storeEnchantment /
 * deserializeEnchantments / getEnchantments / setEnchantments, EnchantedBookItem.getEnchantments /
 * addEnchantment). Each body is 1.20.4's, read off its bytecode; a stack's enchantments are read and
 * written through the stack's 1.20.4 tag (CbItemTags), so the "Enchantments" / "StoredEnchantments"
 * lists a mod sees and edits are the ones the components hold, mapped by the same rules as every other
 * tag key.
 */
public final class CbEnchantNbt {

    private static final String ENCHANTMENTS = "Enchantments";
    private static final String STORED = "StoredEnchantments";

    /** EnchantmentHelper.getEnchantmentId(enchantment): its registry id */
    public static net.minecraft.class_2960 getEnchantmentId(net.minecraft.class_1887 e) {
        return net.minecraft.class_7923.field_41176.method_10221(e);
    }

    /** EnchantmentHelper.getEnchantmentId(tag): the entry's "id" as a location, null when it is none */
    public static net.minecraft.class_2960 getEnchantmentIdOf(net.minecraft.class_2487 tag) {
        return net.minecraft.class_2960.method_12829(tag.method_10558("id"));
    }

    /** EnchantmentHelper.getEnchantmentLevel(tag): the entry's "lvl", clamped to 0..255 */
    public static int getEnchantmentLevel(net.minecraft.class_2487 tag) {
        return net.minecraft.class_3532.method_15340(tag.method_10550("lvl"), 0, 255);
    }

    /** EnchantmentHelper.setEnchantmentLevel(tag, level): "lvl" as a short */
    public static void setEnchantmentLevel(net.minecraft.class_2487 tag, int level) {
        tag.method_10575("lvl", (short) level);
    }

    /** EnchantmentHelper.storeEnchantment(id, level): {id: "&lt;id&gt;", lvl: (short) level} */
    public static net.minecraft.class_2487 storeEnchantment(net.minecraft.class_2960 id, int level) {
        net.minecraft.class_2487 tag = new net.minecraft.class_2487();
        tag.method_10582("id", String.valueOf(id));
        tag.method_10575("lvl", (short) level);
        return tag;
    }

    /**
     * EnchantmentHelper.deserializeEnchantments(list): each entry whose id names a registered enchantment,
     * in list order, with its clamped level (a later entry for the same enchantment replaces the level)
     */
    public static Map<net.minecraft.class_1887, Integer> deserializeEnchantments(net.minecraft.class_2499 list) {
        Map<net.minecraft.class_1887, Integer> map = new LinkedHashMap<>();
        for (int i = 0; i < list.size(); i++) {
            net.minecraft.class_2487 tag = list.method_10602(i);
            net.minecraft.class_7923.field_41176.method_17966(getEnchantmentIdOf(tag))
                .ifPresent(e -> map.put(e, getEnchantmentLevel(tag)));
        }
        return map;
    }

    /** EnchantmentHelper.getEnchantments(stack): an enchanted book's stored enchantments, any other stack's own */
    public static Map<net.minecraft.class_1887, Integer> getEnchantments(net.minecraft.class_1799 stack) {
        return deserializeEnchantments(stack.method_31574(net.minecraft.class_1802.field_8598)
            ? bookEnchantments(stack) : CbItemTags.getEnchantmentTags(stack));
    }

    /**
     * EnchantmentHelper.setEnchantments(map, stack), 1.20.4's body: the entries (a null enchantment
     * skipped) become the stack's "Enchantments" list -- none at all removes it; an enchanted book
     * instead gets each one added to its stored enchantments as EnchantedBookItem.addEnchantment does.
     */
    public static void setEnchantments(Map<net.minecraft.class_1887, Integer> map, net.minecraft.class_1799 stack) {
        net.minecraft.class_2499 list = new net.minecraft.class_2499();
        for (Map.Entry<net.minecraft.class_1887, Integer> en : map.entrySet()) {
            net.minecraft.class_1887 e = en.getKey();
            if (e == null) {
                continue;
            }
            int level = en.getValue();
            list.add(storeEnchantment(getEnchantmentId(e), level));
            if (stack.method_31574(net.minecraft.class_1802.field_8598)) {
                addBookEnchantment(stack, new net.minecraft.class_1889(e, level));
            }
        }
        if (list.isEmpty()) {
            CbItemTags.removeTagKey(stack, ENCHANTMENTS);
        } else if (!stack.method_31574(net.minecraft.class_1802.field_8598)) {
            CbItemTags.addTagElement(stack, ENCHANTMENTS, list);
        }
    }

    /** EnchantedBookItem.getEnchantments(stack): the "StoredEnchantments" list of its tag (a new empty one without a tag) */
    public static net.minecraft.class_2499 bookEnchantments(net.minecraft.class_1799 stack) {
        net.minecraft.class_2487 tag = CbItemTags.getTag(stack);
        return tag != null ? tag.method_10554(STORED, 10) : new net.minecraft.class_2499();
    }

    /**
     * EnchantedBookItem.addEnchantment(stack, instance), 1.20.4's body: an entry for the same enchantment
     * is raised to the instance's level when lower and otherwise left; without one the instance is
     * appended; the list goes back under "StoredEnchantments".
     */
    public static void addBookEnchantment(net.minecraft.class_1799 stack, net.minecraft.class_1889 instance) {
        net.minecraft.class_2499 list = bookEnchantments(stack);
        boolean add = true;
        net.minecraft.class_2960 id = getEnchantmentId(instance.field_9093);
        for (int i = 0; i < list.size(); i++) {
            net.minecraft.class_2487 tag = list.method_10602(i);
            net.minecraft.class_2960 other = getEnchantmentIdOf(tag);
            if (other != null && other.equals(id)) {
                if (getEnchantmentLevel(tag) < instance.field_9094) {
                    setEnchantmentLevel(tag, instance.field_9094);
                }
                add = false;
                break;
            }
        }
        if (add) {
            list.add(storeEnchantment(id, instance.field_9094));
        }
        CbItemTags.getOrCreateTag(stack).method_10566(STORED, list);
    }

    private CbEnchantNbt() {
    }
}
