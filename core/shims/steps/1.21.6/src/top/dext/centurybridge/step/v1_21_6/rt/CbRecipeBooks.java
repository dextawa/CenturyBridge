package top.dext.centurybridge.step.v1_21_6.rt;

import java.util.function.Predicate;
import net.minecraft.class_1860;
import net.minecraft.class_2487;
import net.minecraft.class_3441;
import net.minecraft.class_5321;
import net.minecraft.class_5411;
import net.minecraft.class_5421;

/**
 * 1.21.5's CompoundTag form of the server recipe book (javap of both releases). 1.21.5's ServerRecipeBook.toNbt()
 * wrote RecipeBookSettings.write(tag) -- for each of the four book types its two booleans under fixed keys
 * ("isGuiOpen" / "isFilteringCraftable", "isFurnaceGuiOpen" ...) -- then "recipes" and "toBeDisplayed", lists of the
 * recipe ids as strings; fromNbt(tag, isRecognized) replaced the settings with RecipeBookSettings.read(tag) (missing
 * keys false) and added the ids of both lists that parse (Recipe.KEY_CODEC list, unreadable lists empty) and pass the
 * predicate. 1.21.6 keeps the same state and stores it through ServerRecipeBook.Packed's codec (the same keys; false
 * settings left out); its loadUntrusted(packed, isRecognized) does what fromNbt did after parsing.
 * <p>
 * The 1.21.5 members are restored on the classes (CbRecipeBookRestoredMixin, CbRecipeBookSettingsRestoredMixin).
 * The route (CbRecipeBookRouteMixin) consults them only when a converted mod mixed one ({@link CbMixedMembers}) or the
 * book is a subclass (whose override 1.21.6's codec path never asks); otherwise 1.21.6's save and load run alone.
 */
public final class CbRecipeBooks {

    private CbRecipeBooks() {
    }

    /** the restored members' keys in the conversion's mixed-member signal (CbMixedMembers) */
    public static final String TO_NBT = "net/minecraft/class_3441.method_14902()Lnet/minecraft/class_2487;";
    public static final String FROM_NBT = "net/minecraft/class_3441.method_14901(Lnet/minecraft/class_2487;Ljava/util/function/Predicate;)V";
    public static final String SETTINGS_WRITE = "net/minecraft/class_5411.method_30189(Lnet/minecraft/class_2487;)V";

    /** the restored ServerRecipeBook members, as the route calls them, and the layer's own reads of the book */
    public interface LegacyBook {
        /** toNbt() */
        class_2487 method_14902();

        /** fromNbt(tag, isRecognized) */
        void method_14901(class_2487 tag, Predicate<class_5321<class_1860<?>>> isRecognized);

        /** toNbt()'s body with no call a mod could mix (the book's own fields, read directly) */
        class_2487 cb$tagCopy();

        /** a fresh 1.21.6 book's state: both recipe sets empty, every setting false (the fields, set directly) */
        void cb$reset();
    }

    /** the restored RecipeBookSettings.write(CompoundTag), and the layer's own reads of the settings */
    public interface LegacySettings {
        void method_30189(class_2487 tag);

        /** write(tag)'s body from the fields, with no call a mod could mix */
        void cb$writeCopy(class_2487 tag);

        /** every setting false, a fresh book's (the fields, set directly) */
        void cb$clear();

        /** one type's settings field, read directly (the states-map view) */
        class_5411.class_5412 cb$type(class_5421 type);

        /** one type's settings field, written directly (the states-map view) */
        void cb$setType(class_5421 type, class_5411.class_5412 settings);
    }

    static final class_5421[] TYPES = {class_5421.field_25763, class_5421.field_25764, class_5421.field_25765, class_5421.field_25766};
    public static final String[][] KEYS = {
        {"isGuiOpen", "isFilteringCraftable"},
        {"isFurnaceGuiOpen", "isFurnaceFilteringCraftable"},
        {"isBlastingFurnaceGuiOpen", "isBlastingFurnaceFilteringCraftable"},
        {"isSmokerGuiOpen", "isSmokerFilteringCraftable"}};

    /** RecipeBookSettings.read(CompoundTag), 1.21.5's static */
    public static class_5411 method_30183(class_2487 tag) {
        class_5411 settings = new class_5411();
        for (int i = 0; i < TYPES.length; i++) {
            boolean open = tag.method_68566(KEYS[i][0], false);
            boolean filtering = tag.method_68566(KEYS[i][1], false);
            if (open || filtering) {
                settings.method_30181(TYPES[i], open);
                settings.method_30188(TYPES[i], filtering);
            }
        }
        return settings;
    }

    /** RecipeBookSettings.write(CompoundTag)'s keys: 1.21.5's TAG_FIELDS (field_25735), restored on the class */
    public static java.util.Map<class_5421, com.mojang.datafixers.util.Pair<String, String>> tagFields() {
        java.util.Map<class_5421, com.mojang.datafixers.util.Pair<String, String>> m = new java.util.LinkedHashMap<>();
        for (int i = 0; i < TYPES.length; i++) {
            m.put(TYPES[i], com.mojang.datafixers.util.Pair.of(KEYS[i][0], KEYS[i][1]));
        }
        return com.google.common.collect.ImmutableMap.copyOf(m);
    }

    /** the restored read / write(FriendlyByteBuf)'s keys in the mixed-member signal */
    public static final String NET_READ = "net/minecraft/class_5411.method_30186(Lnet/minecraft/class_2540;)Lnet/minecraft/class_5411;";
    public static final String NET_WRITE = "net/minecraft/class_5411.method_30190(Lnet/minecraft/class_2540;)V";

    /** whether STREAM_CODEC decodes through the restored read(FriendlyByteBuf): a converted mod mixed it */
    public static boolean routeNetRead() {
        return CbMixedMembers.mixed(NET_READ);
    }

    /** whether STREAM_CODEC encodes through the restored write(FriendlyByteBuf): a converted mod mixed it */
    public static boolean routeNetWrite() {
        return CbMixedMembers.mixed(NET_WRITE);
    }

    /**
     * 1.21.5's RecipeBookSettings states map (field_25736, an EnumMap holding the types whose settings are not
     * DEFAULT), as a live view of 1.21.6's four per-type fields: reads answer the fields (DEFAULT reads as absent),
     * put / remove / clear and the entries' setValue write them (absent = DEFAULT).
     */
    public static final class SettingsView extends java.util.AbstractMap<class_5421, class_5411.class_5412> {
        private final LegacySettings settings;

        public SettingsView(LegacySettings settings) {
            this.settings = settings;
        }

        @Override
        public class_5411.class_5412 get(Object key) {
            if (!(key instanceof class_5421 type)) {
                return null;
            }
            class_5411.class_5412 v = settings.cb$type(type);
            return v == null || v.equals(class_5411.class_5412.field_54549) ? null : v;
        }

        @Override
        public boolean containsKey(Object key) {
            return get(key) != null;
        }

        @Override
        public class_5411.class_5412 put(class_5421 key, class_5411.class_5412 value) {
            class_5411.class_5412 old = get(key);
            settings.cb$setType(key, value == null ? class_5411.class_5412.field_54549 : value);
            return old;
        }

        @Override
        public class_5411.class_5412 remove(Object key) {
            class_5411.class_5412 old = get(key);
            if (old != null) {
                settings.cb$setType((class_5421) key, class_5411.class_5412.field_54549);
            }
            return old;
        }

        @Override
        public void clear() {
            for (class_5421 type : TYPES) {
                settings.cb$setType(type, class_5411.class_5412.field_54549);
            }
        }

        @Override
        public java.util.Set<Entry<class_5421, class_5411.class_5412>> entrySet() {
            return new java.util.AbstractSet<>() {
                @Override
                public java.util.Iterator<Entry<class_5421, class_5411.class_5412>> iterator() {
                    java.util.List<class_5421> present = new java.util.ArrayList<>();
                    for (class_5421 type : TYPES) {
                        if (get(type) != null) {
                            present.add(type);
                        }
                    }
                    java.util.Iterator<class_5421> keys = present.iterator();
                    return new java.util.Iterator<>() {
                        private class_5421 last;

                        @Override
                        public boolean hasNext() {
                            return keys.hasNext();
                        }

                        @Override
                        public Entry<class_5421, class_5411.class_5412> next() {
                            class_5421 type = last = keys.next();
                            return new java.util.AbstractMap.SimpleEntry<>(type, settings.cb$type(type)) {
                                @Override
                                public class_5411.class_5412 setValue(class_5411.class_5412 value) {
                                    super.setValue(value);
                                    return put(type, value);
                                }
                            };
                        }

                        @Override
                        public void remove() {
                            if (last == null) {
                                throw new IllegalStateException();
                            }
                            SettingsView.this.remove(last);
                            last = null;
                        }
                    };
                }

                @Override
                public int size() {
                    int n = 0;
                    for (class_5421 type : TYPES) {
                        if (get(type) != null) {
                            n++;
                        }
                    }
                    return n;
                }
            };
        }
    }

    /** whether the player's save asks the restored toNbt: a converted mod mixed it (or the settings' write), or a subclass */
    public static boolean routeSave(class_3441 book) {
        return book.getClass() != class_3441.class || CbMixedMembers.mixed(TO_NBT) || CbMixedMembers.mixed(SETTINGS_WRITE);
    }

    /** whether the player's load asks the restored fromNbt: a converted mod mixed it, or a subclass */
    public static boolean routeLoad(class_3441 book) {
        return book.getClass() != class_3441.class || CbMixedMembers.mixed(FROM_NBT);
    }
}
