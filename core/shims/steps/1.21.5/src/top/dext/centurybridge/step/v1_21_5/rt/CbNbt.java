package top.dext.centurybridge.step.v1_21_5.rt;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.class_2338;
import net.minecraft.class_2479;
import net.minecraft.class_2483;
import net.minecraft.class_2487;
import net.minecraft.class_2489;
import net.minecraft.class_2494;
import net.minecraft.class_2495;
import net.minecraft.class_2497;
import net.minecraft.class_2499;
import net.minecraft.class_2501;
import net.minecraft.class_2516;
import net.minecraft.class_2519;
import net.minecraft.class_2520;
import net.minecraft.class_2522;
import net.minecraft.class_4844;

/**
 * 1.21.4's NBT accessors on 1.21.5 (nbt area). 1.21.5 turned CompoundTag's and ListTag's typed getters into Optionals
 * (getInt(key) -> Optional&lt;Integer&gt;, with getIntOr(key, default) for 1.21.4's body), made ListTag hold elements
 * of any type (no element type), and dropped the UUID helpers, getTagType / contains(key, type), the List-taking
 * array puts and Tag.getAsString. Each member here is 1.21.4's body on 1.21.5's API: a getter answers what 1.21.4's
 * answered for every tag 1.21.4 could hold (its type tests are the tag ids 1.21.4 compared), with 1.21.4's fallback.
 * The getters whose 1.21.4 body is exactly a 1.21.5 *Or(key, default) are wired to it (callReshapes); these are the rest.
 */
public final class CbNbt {

    private CbNbt() {
    }

    // ------------------------------------------------------------------ CompoundTag getters (callReshapes' converters)

    public static byte optByte(Optional<Byte> v) {
        return v.orElse((byte) 0);
    }

    public static short optShort(Optional<Short> v) {
        return v.orElse((short) 0);
    }

    public static int optInt(Optional<Integer> v) {
        return v.orElse(0);
    }

    public static long optLong(Optional<Long> v) {
        return v.orElse(0L);
    }

    public static float optFloat(Optional<Float> v) {
        return v.orElse(0.0F);
    }

    public static double optDouble(Optional<Double> v) {
        return v.orElse(0.0D);
    }

    public static String optString(Optional<String> v) {
        return v.orElse("");
    }

    public static boolean optBoolean(Optional<Boolean> v) {
        return v.orElse(false);
    }

    /** getCompound(key): the compound, or a new (unattached) one, as 1.21.4 */
    public static class_2487 optCompound(Optional<class_2487> v) {
        return v.orElseGet(class_2487::new);
    }

    // ------------------------------------------------------------------ CompoundTag

    /** getByteArray(key): the array, or an empty one (1.21.4) */
    public static byte[] byteArray(Optional<byte[]> value) {
        return value.orElseGet(() -> new byte[0]);
    }

    /** getIntArray(key): the array, or an empty one (1.21.4) */
    public static int[] intArray(Optional<int[]> value) {
        return value.orElseGet(() -> new int[0]);
    }

    /** getLongArray(key): the array, or an empty one (1.21.4) */
    public static long[] longArray(Optional<long[]> value) {
        return value.orElseGet(() -> new long[0]);
    }

    /** getTagType(key): the tag's id, 0 (END) when absent */
    public static byte method_10540(class_2487 tag, String key) {
        class_2520 t = tag.method_10580(key);
        return t == null ? 0 : t.method_10711();
    }

    /** contains(key, type): the tag's id is type, or type is 99 (any number) and the tag is numeric (ids 1..6) */
    public static boolean method_10573(class_2487 tag, String key, int type) {
        int id = method_10540(tag, key);
        if (id == type) {
            return true;
        }
        return type == 99 && id >= 1 && id <= 6;
    }

    /**
     * getList(key, type): the list under key when it is one whose elements are of that type (or which is empty), else a
     * new empty list. 1.21.4's lists held one element type; 1.21.5's may mix, and a mixed list is no list of that type.
     */
    public static class_2499 method_10554(class_2487 tag, String key, int type) {
        if (tag.method_10580(key) instanceof class_2499 list && ofType(list, type)) {
            return list;
        }
        return new class_2499();
    }

    private static boolean ofType(class_2499 list, int type) {
        for (int i = 0; i < list.size(); i++) {
            if (list.method_10534(i).method_10711() != type) {
                return false;
            }
        }
        return true;
    }

    /** getUUID(key): the 4-int array under key as a UUID; anything else throws as 1.21.4's NbtUtils.loadUUID did */
    public static UUID method_25926(class_2487 tag, String key) {
        return method_25930(tag.method_10580(key));
    }

    /** putUUID(key, uuid) */
    public static void method_25927(class_2487 tag, String key, UUID uuid) {
        tag.method_10566(key, method_25929(uuid));
    }

    /** hasUUID(key): an int array of length 4 under key */
    public static boolean method_25928(class_2487 tag, String key) {
        return tag.method_10580(key) instanceof class_2495 a && a.method_10588().length == 4;
    }

    /** putByteArray(key, List&lt;Byte&gt;) */
    public static void method_36110(class_2487 tag, String key, List<Byte> values) {
        tag.method_10566(key, new class_2479(bytes(values)));
    }

    /** putIntArray(key, List&lt;Integer&gt;) */
    public static void method_10572(class_2487 tag, String key, List<Integer> values) {
        tag.method_10566(key, new class_2495(ints(values)));
    }

    /** putLongArray(key, List&lt;Long&gt;) */
    public static void method_10538(class_2487 tag, String key, List<Long> values) {
        tag.method_10566(key, new class_2501(longs(values)));
    }

    // ------------------------------------------------------------------ ListTag: 1.21.4 answered only for the exact element type

    private static class_2520 at(class_2499 list, int i) {
        return i >= 0 && i < list.size() ? list.method_10534(i) : null;
    }

    /** getCompound(i) */
    public static net.minecraft.class_2487 method_10602(class_2499 list, int i) {
        return at(list, i) instanceof class_2487 c ? c : new class_2487();
    }

    /** getList(i) */
    public static class_2499 method_10603(class_2499 list, int i) {
        return at(list, i) instanceof class_2499 l ? l : new class_2499();
    }

    /** getShort(i): a ShortTag's value, else 0 */
    public static short method_10609(class_2499 list, int i) {
        return at(list, i) instanceof class_2516 t ? t.method_10696() : 0;
    }

    /** getInt(i): an IntTag's value, else 0 */
    public static int method_10600(class_2499 list, int i) {
        return at(list, i) instanceof class_2497 t ? t.method_10701() : 0;
    }

    /** getIntArray(i) */
    public static int[] method_36111(class_2499 list, int i) {
        return at(list, i) instanceof class_2495 t ? t.method_10588() : new int[0];
    }

    /** getLongArray(i) */
    public static long[] method_36112(class_2499 list, int i) {
        return at(list, i) instanceof class_2501 t ? t.method_10615() : new long[0];
    }

    /** getDouble(i): a DoubleTag's value, else 0 */
    public static double method_10611(class_2499 list, int i) {
        return at(list, i) instanceof class_2489 t ? t.method_10697() : 0.0D;
    }

    /** getFloat(i): a FloatTag's value, else 0 */
    public static float method_10604(class_2499 list, int i) {
        return at(list, i) instanceof class_2494 t ? t.method_10700() : 0.0F;
    }

    /** getString(i): a StringTag's value, another tag's string form, "" out of range */
    public static String method_10608(class_2499 list, int i) {
        class_2520 t = at(list, i);
        return t == null ? "" : method_10714(t);
    }

    /**
     * CollectionTag.getElementType(): the id of the elements -- for a list its elements' (0 when empty; 1.21.4's lists
     * held one type, a 1.21.5 list mixing types answers its first element's), for the array tags their number type
     */
    public static byte method_10601(class_2483 tag) {
        if (tag instanceof class_2499 list) {
            return list.isEmpty() ? 0 : list.method_10534(0).method_10711();
        }
        if (tag instanceof class_2479) {
            return 1;
        }
        if (tag instanceof class_2495) {
            return 3;
        }
        return 4;
    }

    /**
     * CollectionTag.set(i, tag) (1.21.4's AbstractList set): the old element. A list takes any tag since 1.21.5; an
     * array tag only its number type -- another throws ClassCastException, as 1.21.4's erased bridge method did
     */
    public static class_2520 method_10606(class_2483 tag, int i, class_2520 value) {
        if (tag instanceof class_2499 list) {
            return list.method_68585(i, value);
        }
        class_2520 old = tag.method_10534(i);
        if (!tag.method_10535(i, value)) {
            throw new ClassCastException(value.getClass().getName() + " is no element of " + tag.getClass().getName());
        }
        return old;
    }

    /** CollectionTag.add(i, tag) (1.21.4's AbstractList add): as {@link #method_10606} */
    public static void method_10531(class_2483 tag, int i, class_2520 value) {
        if (tag instanceof class_2499 list) {
            list.method_68586(i, value);
        } else if (!tag.method_10533(i, value)) {
            throw new ClassCastException(value.getClass().getName() + " is no element of " + tag.getClass().getName());
        }
    }

    /** ByteArrayTag.set(i, ByteTag): the old value as a tag */
    public static net.minecraft.class_2481 method_17803(class_2479 tag, int i, net.minecraft.class_2481 value) {
        net.minecraft.class_2481 old = tag.method_10523(i);
        tag.method_10535(i, value);
        return old;
    }

    /** ByteArrayTag.add(i, ByteTag) */
    public static void method_17805(class_2479 tag, int i, net.minecraft.class_2481 value) {
        tag.method_10533(i, value);
    }

    /** IntArrayTag.set(i, IntTag): the old value as a tag */
    public static class_2497 method_17806(class_2495 tag, int i, class_2497 value) {
        class_2497 old = tag.method_10589(i);
        tag.method_10535(i, value);
        return old;
    }

    /** IntArrayTag.add(i, IntTag) */
    public static void method_17808(class_2495 tag, int i, class_2497 value) {
        tag.method_10533(i, value);
    }

    /** LongArrayTag.set(i, LongTag): the old value as a tag */
    public static net.minecraft.class_2503 method_17810(class_2501 tag, int i, net.minecraft.class_2503 value) {
        net.minecraft.class_2503 old = tag.method_10616(i);
        tag.method_10535(i, value);
        return old;
    }

    /** LongArrayTag.add(i, LongTag) */
    public static void method_17812(class_2501 tag, int i, net.minecraft.class_2503 value) {
        tag.method_10533(i, value);
    }

    /** Tag.TAG_ANY_NUMERIC: the type 1.21.4's contains(key, type) took for any number */
    public static byte field_33263() {
        return 99;
    }

    // ------------------------------------------------------------------ Tag, NbtUtils, TagParser

    /** Tag.getAsString(): a string tag's value, any other tag's SNBT (1.21.4: StringTagVisitor, which toString uses) */
    public static String method_10714(class_2520 tag) {
        return tag instanceof class_2519 s ? s.comp_3831() : tag.toString();
    }

    /** NbtUtils.createUUID(uuid) */
    public static class_2495 method_25929(UUID uuid) {
        return new class_2495(class_4844.method_26275(uuid));
    }

    /** NbtUtils.loadUUID(tag): 1.21.4's checks and messages */
    public static UUID method_25930(class_2520 tag) {
        if (!(tag instanceof class_2495 a)) {
            throw new IllegalArgumentException("Expected UUID-Tag to be of type " + class_2495.field_21036.method_23259() + ", but found "
                + tag.method_23258().method_23259() + ".");
        }
        int[] v = a.method_10588();
        if (v.length != 4) {
            throw new IllegalArgumentException("Expected UUID-Array to be of length 4, but found " + v.length + ".");
        }
        return class_4844.method_26276(v);
    }

    /** NbtUtils.readBlockPos(tag, key): a 3-int array under key */
    public static Optional<class_2338> method_10691(class_2487 tag, String key) {
        int[] v = intArray(tag.method_10561(key));
        return v.length == 3 ? Optional.of(new class_2338(v[0], v[1], v[2])) : Optional.empty();
    }

    /** NbtUtils.writeBlockPos(pos): a 3-int array */
    public static class_2520 method_10692(class_2338 pos) {
        return new class_2495(new int[] {pos.method_10263(), pos.method_10264(), pos.method_10260()});
    }

    /** TagParser.parseTag(snbt): a whole SNBT compound (1.21.5's parser, the same contract: nothing may follow it) */
    public static class_2487 method_10718(String snbt) throws CommandSyntaxException {
        return class_2522.method_67315(snbt);
    }

    // ------------------------------------------------------------------ array tags from lists (1.21.4 constructors: unboxing, a null element throws as there)

    public static byte[] bytes(List<Byte> values) {
        byte[] a = new byte[values.size()];
        for (int i = 0; i < a.length; i++) {
            a[i] = values.get(i);
        }
        return a;
    }

    public static int[] ints(List<Integer> values) {
        int[] a = new int[values.size()];
        for (int i = 0; i < a.length; i++) {
            a[i] = values.get(i);
        }
        return a;
    }

    public static long[] longs(List<Long> values) {
        long[] a = new long[values.size()];
        for (int i = 0; i < a.length; i++) {
            a[i] = values.get(i);
        }
        return a;
    }

    public static long[] longs(it.unimi.dsi.fastutil.longs.LongSet values) {
        return values.toLongArray();
    }

    /** {@code new ByteArrayTag(List)} */
    public static class_2479 newByteArray(List<Byte> values) {
        return new class_2479(bytes(values));
    }

    /** {@code new IntArrayTag(List)} */
    public static class_2495 newIntArray(List<Integer> values) {
        return new class_2495(ints(values));
    }

    /** {@code new LongArrayTag(List)} */
    public static class_2501 newLongArray(List<Long> values) {
        return new class_2501(longs(values));
    }

    /** {@code new LongArrayTag(LongSet)} */
    public static class_2501 newLongArraySet(it.unimi.dsi.fastutil.longs.LongSet values) {
        return new class_2501(longs(values));
    }

    // ------------------------------------------------------------------ the same members keyed on each collection tag (instanceRedirects take the owner)

    public static byte method_10601(net.minecraft.class_2499 tag) {
        return method_10601((class_2483) tag);
    }

    public static class_2520 method_10606(net.minecraft.class_2499 tag, int i, class_2520 value) {
        return method_10606((class_2483) tag, i, value);
    }

    public static void method_10531(net.minecraft.class_2499 tag, int i, class_2520 value) {
        method_10531((class_2483) tag, i, value);
    }

    public static byte method_10601(net.minecraft.class_2479 tag) {
        return method_10601((class_2483) tag);
    }

    public static class_2520 method_10606(net.minecraft.class_2479 tag, int i, class_2520 value) {
        return method_10606((class_2483) tag, i, value);
    }

    public static void method_10531(net.minecraft.class_2479 tag, int i, class_2520 value) {
        method_10531((class_2483) tag, i, value);
    }

    public static byte method_10601(net.minecraft.class_2495 tag) {
        return method_10601((class_2483) tag);
    }

    public static class_2520 method_10606(net.minecraft.class_2495 tag, int i, class_2520 value) {
        return method_10606((class_2483) tag, i, value);
    }

    public static void method_10531(net.minecraft.class_2495 tag, int i, class_2520 value) {
        method_10531((class_2483) tag, i, value);
    }

    public static byte method_10601(net.minecraft.class_2501 tag) {
        return method_10601((class_2483) tag);
    }

    public static class_2520 method_10606(net.minecraft.class_2501 tag, int i, class_2520 value) {
        return method_10606((class_2483) tag, i, value);
    }

    public static void method_10531(net.minecraft.class_2501 tag, int i, class_2520 value) {
        method_10531((class_2483) tag, i, value);
    }

    /** StringTag.getAsString(): its value */
    public static String method_10714(class_2519 tag) {
        return tag.comp_3831();
    }
}
