package top.dext.centurybridge.step.v1_21_6.rt;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.class_1860;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2519;
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
 * The 1.21.5 members are restored on the classes (CbRecipeBookRestoredMixin, CbRecipeBookSettingsRestoredMixin);
 * this class holds their bodies once more where no mod mixin reaches them, for the route (CbRecipeBookRouteMixin) to
 * tell whether a converted mod changed them.
 */
public final class CbRecipeBooks {

    private CbRecipeBooks() {
    }

    /** the restored ServerRecipeBook members, as the route and the rt bodies call them */
    public interface LegacyBook {
        class_2487 method_14902();

        void method_14901(class_2487 tag, Predicate<class_5321<class_1860<?>>> isRecognized);
    }

    /** the restored RecipeBookSettings.write(CompoundTag) */
    public interface LegacySettings {
        void method_30189(class_2487 tag);
    }

    static final class_5421[] TYPES = {class_5421.field_25763, class_5421.field_25764, class_5421.field_25765, class_5421.field_25766};
    static final String[][] KEYS = {
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

    /** RecipeBookSettings.write(CompoundTag)'s body */
    public static void writeSettings(class_5411 settings, class_2487 tag) {
        for (int i = 0; i < TYPES.length; i++) {
            tag.method_10556(KEYS[i][0], settings.method_30180(TYPES[i]));
            tag.method_10556(KEYS[i][1], settings.method_30187(TYPES[i]));
        }
    }

    /** ServerRecipeBook.toNbt()'s body; settings is where the book's settings are written (a restored call or this class's) */
    public static class_2487 toTag(class_3441 book, java.util.function.BiConsumer<class_5411, class_2487> settings) {
        class_2487 tag = new class_2487();
        class_3441.class_11328 packed = book.method_71344();
        settings.accept(book.method_30173(), tag);
        tag.method_10566("recipes", ids(packed.comp_4197()));
        tag.method_10566("toBeDisplayed", ids(packed.comp_4198()));
        return tag;
    }

    private static class_2499 ids(List<class_5321<class_1860<?>>> keys) {
        class_2499 list = new class_2499();
        for (class_5321<class_1860<?>> k : keys) {
            list.add(class_2519.method_23256(k.method_29177().toString()));
        }
        return list;
    }

    private static final com.mojang.serialization.Codec<List<class_5321<class_1860<?>>>> KEYS_CODEC = class_1860.field_56667.listOf();

    /** ServerRecipeBook.fromNbt(tag, isRecognized)'s body; settings read as given (a restored call or this class's) */
    public static void fromTag(class_3441 book, class_2487 tag, Predicate<class_5321<class_1860<?>>> isRecognized,
            java.util.function.Function<class_2487, class_5411> settings) {
        List<class_5321<class_1860<?>>> recipes = tag.method_67491("recipes", KEYS_CODEC).orElse(List.of());
        List<class_5321<class_1860<?>>> shown = tag.method_67491("toBeDisplayed", KEYS_CODEC).orElse(List.of());
        book.method_71343(new class_3441.class_11328(settings.apply(tag), new ArrayList<>(recipes), new ArrayList<>(shown)), isRecognized);
    }

    /** the unmixable copy of toNbt */
    public static class_2487 toTagCopy(class_3441 book) {
        return toTag(book, CbRecipeBooks::writeSettings);
    }

    /** the unmixable copy of fromNbt */
    public static void fromTagCopy(class_3441 book, class_2487 tag, Predicate<class_5321<class_1860<?>>> isRecognized) {
        fromTag(book, tag, isRecognized, CbRecipeBooks::method_30183);
    }

    /** a book's whole state, to compare two books */
    public static class_3441.class_11328 state(class_3441 book) {
        return book.method_71344();
    }

    /** whether two books' states are the same (each type's two settings, then both lists in order) */
    public static boolean sameState(class_3441 a, class_3441 b) {
        class_3441.class_11328 x = state(a);
        class_3441.class_11328 y = state(b);
        for (class_5421 t : TYPES) {
            if (x.comp_4196().method_30180(t) != y.comp_4196().method_30180(t)
                    || x.comp_4196().method_30187(t) != y.comp_4196().method_30187(t)) {
                return false;
            }
        }
        return x.comp_4197().equals(y.comp_4197()) && x.comp_4198().equals(y.comp_4198());
    }

    /** the empty state a fresh 1.21.5 ServerPlayer's book had before fromNbt */
    public static class_3441.class_11328 empty() {
        return new class_3441.class_11328(new class_5411(), List.of(), List.of());
    }

    /** back to a fresh book's state: 1.21.6's private copy-in (clears both sets, takes the settings; layer AW) */
    public static void reset(class_3441 book) {
        book.method_71342(empty());
    }
}
