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

    /** RecipeBookSettings.write(CompoundTag)'s body */
    public static void writeSettings(class_5411 settings, class_2487 tag) {
        for (int i = 0; i < TYPES.length; i++) {
            tag.method_10556(KEYS[i][0], settings.method_30180(TYPES[i]));
            tag.method_10556(KEYS[i][1], settings.method_30187(TYPES[i]));
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
