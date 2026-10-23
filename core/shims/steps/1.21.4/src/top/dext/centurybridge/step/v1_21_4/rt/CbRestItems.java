package top.dext.centurybridge.step.v1_21_4.rt;

import java.util.List;
import net.minecraft.class_10162;
import net.minecraft.class_1293;
import net.minecraft.class_1792;
import net.minecraft.class_1826;
import net.minecraft.class_1842;
import net.minecraft.class_1844;
import net.minecraft.class_1937;
import net.minecraft.class_2960;
import net.minecraft.class_4208;
import net.minecraft.class_5455;
import net.minecraft.class_5537;
import net.minecraft.class_6880;
import net.minecraft.class_7225;
import net.minecraft.class_7923;
import net.minecraft.class_9280;
import top.dext.centurybridge.step.v1_21_4.mixin.CbRestItemPropertiesAccess;

/**
 * 1.21.3 item API gone in 1.21.4, with 1.21.3's bodies over 1.21.4's members.
 * <ul>
 * <li>SpawnEggItem(type, backgroundColor, highlightColor, props) and getColor(tint): 1.21.4 moved the colors
 *     to the client item model; the constructor's colors are kept on the item (CbRestItemStateMixin) and
 *     getColor answers them as 1.21.3 did (tint 0 the background, else the highlight).</li>
 * <li>SpawnEggItem.getType(stack) / spawnsEntity(stack, type): 1.21.4 takes registries to parse the entity
 *     id; 1.21.3 parsed it against the built-in entity type registry, which {@link #builtInRegistries} is.</li>
 * <li>BundleItem(openFront, openBack, props) and its two model getters: kept on the item likewise.</li>
 * <li>Item.Properties.overrideModel(id): 1.21.3's body, the properties' model set to that id.</li>
 * <li>CustomModelData(int) / value() / DEFAULT: 1.21.4's record holds lists; 1.21.3's single value is its
 *     first float (what 1.21.4's custom_model_data model property reads), DEFAULT is 1.21.4's EMPTY (both
 *     read as 0).</li>
 * <li>PotionContents.getColor(potion / effects), CompassItem.getSpawnPosition(level): 1.21.3's bodies.</li>
 * </ul>
 * Overridable members keep a 1.21.3 subclass's override reachable through the interfaces below
 * (adapterHooks); a mod's super call runs 1.21.3's body.
 */
public final class CbRestItems {

    private CbRestItems() {
    }

    /** the per-item state CbRestItemStateMixin adds */
    public interface LegacyState {
        void cb$setEggColors(int background, int highlight);

        int cb$eggColor(int tintIndex);

        void cb$setBundleModels(class_2960 front, class_2960 back);

        class_2960 cb$openFrontModel();

        class_2960 cb$openBackModel();
    }

    public interface EggColor { int method_8016(int tintIndex); }
    public interface BundleFront { class_2960 method_63677(); }
    public interface BundleBack { class_2960 method_63678(); }
    public interface OverrideModel { class_1792.class_1793 method_63681(class_2960 id); }

    // ---- SpawnEggItem ----

    /** the colors of 1.21.3's constructor (superCtorReshapes "post") */
    public static void eggColors(class_1826 egg, int background, int highlight) {
        ((LegacyState) egg).cb$setEggColors(background, highlight);
    }

    public static int method_8016(class_1826 egg, int tintIndex) {
        return (Object) egg instanceof EggColor a ? a.method_8016(tintIndex) : superGetColor(egg, tintIndex);
    }

    public static int superGetColor(class_1826 egg, int tintIndex) {
        return ((LegacyState) egg).cb$eggColor(tintIndex);
    }

    private static volatile class_7225.class_7874 builtIns;

    /** the built-in registries: the lookup 1.21.3's spawn egg parsed its entity id against */
    public static class_7225.class_7874 builtInRegistries() {
        class_7225.class_7874 r = builtIns;
        if (r == null) {
            r = class_5455.method_40302(class_7923.field_41167);
            builtIns = r;
        }
        return r;
    }

    // ---- BundleItem ----

    /** the models of 1.21.3's constructor (superCtorReshapes "post") */
    public static void bundleModels(class_5537 bundle, class_2960 front, class_2960 back) {
        ((LegacyState) bundle).cb$setBundleModels(front, back);
    }

    public static class_2960 method_63677(class_5537 bundle) {
        return (Object) bundle instanceof BundleFront a ? a.method_63677() : superOpenFrontModel(bundle);
    }

    public static class_2960 superOpenFrontModel(class_5537 bundle) {
        return ((LegacyState) bundle).cb$openFrontModel();
    }

    public static class_2960 method_63678(class_5537 bundle) {
        return (Object) bundle instanceof BundleBack a ? a.method_63678() : superOpenBackModel(bundle);
    }

    public static class_2960 superOpenBackModel(class_5537 bundle) {
        return ((LegacyState) bundle).cb$openBackModel();
    }

    // ---- Item.Properties.overrideModel ----

    public static class_1792.class_1793 method_63681(class_1792.class_1793 props, class_2960 id) {
        return props instanceof OverrideModel a ? a.method_63681(id) : superOverrideModel(props, id);
    }

    public static class_1792.class_1793 superOverrideModel(class_1792.class_1793 props, class_2960 id) {
        ((CbRestItemPropertiesAccess) props).cb$setModel(class_10162.fixed(id));
        return props;
    }

    // ---- CustomModelData ----

    public static class_9280 customModelData(int value) {
        return new class_9280(List.of((float) value), List.of(), List.of(), List.of());
    }

    public static int comp_2382(class_9280 data) {
        Float f = data.method_65362(0);
        return f == null ? 0 : (int) f.floatValue();
    }

    // ---- PotionContents / CompassItem ----

    public static int method_8062(class_6880<class_1842> potion) {
        return method_8055(potion.comp_349().method_8049());
    }

    public static int method_8055(Iterable<class_1293> effects) {
        return class_1844.method_58111(effects).orElse(-13083194);
    }

    public static class_4208 method_43123(class_1937 level) {
        return level.method_8597().comp_645() ? class_4208.method_19443(level.method_27983(), level.method_43126()) : null;
    }
}
