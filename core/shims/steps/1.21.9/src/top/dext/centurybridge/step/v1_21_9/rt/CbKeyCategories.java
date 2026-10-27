package top.dext.centurybridge.step.v1_21_9.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BooleanSupplier;
import net.minecraft.class_2960;
import net.minecraft.class_304;
import net.minecraft.class_3675;
import net.minecraft.class_4666;

/**
 * KeyMapping categories as 1.21.8 code names them. 1.21.8 named a category by a translation-key string
 * ("key.categories.movement", a mod's own "key.categories.mymod"); 1.21.9 by a registered KeyMapping.Category
 * (ResourceLocation, display name "key.category.&lt;namespace&gt;.&lt;path&gt;"; javap: Category.register refuses a
 * second registration, the controls screen lists categories in registration order).
 * <ul>
 * <li>1.21.8's vanilla category strings are 1.21.9's vanilla categories of the same name (movement, misc,
 * multiplayer, gameplay, inventory, creative, spectator);</li>
 * <li>any other string is registered once, on first use, as a category of its own under the namespace
 * {@value #NAMESPACE} (path: the string, lowercased, characters a ResourceLocation path refuses as '_'), and it keeps
 * the 1.21.8 string as its display name (CbKeyCategoryNameMixin), the key the mod's language file translates.</li>
 * </ul>
 * getCategory() answers the string back: the 1.21.8 one for these, "key.categories.&lt;path&gt;" for 1.21.9's vanilla
 * categories (1.21.8's names of them) and 1.21.9's translation key for a category a 1.21.9 mod registered.
 */
@Environment(EnvType.CLIENT)
public final class CbKeyCategories {

    public static final String NAMESPACE = "centurybridge_legacy";

    private static final String VANILLA_PREFIX = "key.categories.";

    private static final Map<String, class_304.class_11900> BY_NAME = new HashMap<>();

    private static final Map<class_2960, String> LEGACY_NAMES = new HashMap<>();

    private CbKeyCategories() {
    }

    // ------------------------------------------------------------------ 1.21.8's KeyMapping category constants

    public static String field_32136() { return "key.categories.movement"; }

    public static String field_32137() { return "key.categories.misc"; }

    public static String field_32138() { return "key.categories.multiplayer"; }

    public static String field_32139() { return "key.categories.gameplay"; }

    public static String field_32140() { return "key.categories.inventory"; }

    public static String field_32141() { return "key.categories.ui"; }

    public static String field_32142() { return "key.categories.creative"; }

    // ------------------------------------------------------------------ string <-> category

    /** the 1.21.9 category 1.21.8's category string names */
    public static synchronized class_304.class_11900 of(String name) {
        class_304.class_11900 known = BY_NAME.get(name);
        if (known != null) {
            return known;
        }
        class_304.class_11900 category = vanilla(name);
        if (category == null) {
            class_2960 id = class_2960.method_60655(NAMESPACE, path(name));
            category = LEGACY_NAMES.containsKey(id) ? new class_304.class_11900(id) : class_304.class_11900.method_74698(id);
            LEGACY_NAMES.putIfAbsent(id, name);
        }
        BY_NAME.put(name, category);
        return category;
    }

    /** 1.21.8's string for a 1.21.9 category (KeyMapping.getCategory) */
    public static synchronized String name(class_304.class_11900 category) {
        class_2960 id = category.comp_4879();
        String legacy = LEGACY_NAMES.get(id);
        if (legacy != null) {
            return legacy;
        }
        return "minecraft".equals(id.method_12836()) ? VANILLA_PREFIX + id.method_12832() : id.method_42093("key.category");
    }

    /** the 1.21.8 string a category made here displays, or null for any other category */
    public static synchronized String legacyName(class_304.class_11900 category) {
        return LEGACY_NAMES.get(category.comp_4879());
    }

    private static class_304.class_11900 vanilla(String name) {
        switch (name) {
            case "key.categories.movement": return class_304.class_11900.field_62555;
            case "key.categories.misc": return class_304.class_11900.field_62556;
            case "key.categories.multiplayer": return class_304.class_11900.field_62557;
            case "key.categories.gameplay": return class_304.class_11900.field_62558;
            case "key.categories.inventory": return class_304.class_11900.field_62559;
            case "key.categories.creative": return class_304.class_11900.field_62561;
            case "key.categories.spectator": return class_304.class_11900.field_62562;
            default: return null;
        }
    }

    private static String path(String name) {
        StringBuilder b = new StringBuilder(name.length());
        for (char c : name.toLowerCase(Locale.ROOT).toCharArray()) {
            b.append(c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '_' || c == '-' || c == '.' || c == '/' ? c : '_');
        }
        return b.length() == 0 ? "_" : b.toString();
    }

    // ------------------------------------------------------------------ constructors and getCategory

    /** KeyMapping.getCategory() */
    public static String method_1423(class_304 key) {
        return name(key.method_1423());
    }

    /** new KeyMapping(name, key, category) */
    public static class_304 keyMapping(String name, int key, String category) {
        return new class_304(name, key, of(category));
    }

    /** new KeyMapping(name, type, key, category) */
    public static class_304 keyMapping(String name, class_3675.class_307 type, int key, String category) {
        return new class_304(name, type, key, of(category));
    }

    /**
     * new ToggleKeyMapping(name, key, category, needsToggle): 1.21.9's restore-on-screen-close flag is false, as
     * 1.21.8's toggle never restored its state when a screen closed
     */
    public static class_4666 toggleKeyMapping(String name, int key, String category, BooleanSupplier needsToggle) {
        return new class_4666(name, key, of(category), needsToggle, false);
    }

    /** a 1.21.8 ToggleKeyMapping subclass's restore-on-screen-close flag (its super constructor): false, as above */
    public static boolean noRestore() {
        return false;
    }
}
