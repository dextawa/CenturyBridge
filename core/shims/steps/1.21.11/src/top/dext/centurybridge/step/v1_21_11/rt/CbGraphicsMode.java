package top.dext.centurybridge.step.v1_21_11.rt;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;
import net.minecraft.class_315;
import net.minecraft.class_5365;
import net.minecraft.class_7172;

/**
 * 1.21.10 Options.graphicsMode() (class_315.method_42534, an OptionInstance of GraphicsStatus FAST / FANCY / FABULOUS)
 * is gone from 1.21.11 (javap -c -p both). 1.21.11 split it: the graphics preset (graphicsPreset(), the same enum class
 * plus CUSTOM; setting it rewrites about fifteen options) and improvedTransparency (what useShaderTransparency reads,
 * where 1.21.10 read graphicsMode == FABULOUS). The layer gives graphicsMode() back as a view
 * (mixin/CbOptionsGraphicsModeRestoredMixin), an OptionInstance whose get() is computed from 1.21.11's options on every
 * call (mixin/CbOptionInstanceGraphicsMixin): FABULOUS when improvedTransparency is on, else FAST when the preset is
 * FAST, else FANCY (1.21.10 knew no CUSTOM; fancy was its non-fast mode). set(v) writes improvedTransparency's value
 * (FABULOUS on, FAST / FANCY off) the way 1.21.10's set stored graphicsMode's: without a callback (1.21.10's
 * graphicsMode callback was empty; the options screen's button reloaded the renderer itself), the preset untouched,
 * no other option changed. Lost: FAST vs FANCY no longer switches leaves / weather / clouds (1.21.11 options of their
 * own); see release_notes_known.
 */
@Environment(EnvType.CLIENT)
public final class CbGraphicsMode {
    private CbGraphicsMode() {
    }

    /** implemented by CbOptionInstanceGraphicsMixin on every OptionInstance */
    public interface View {
        /** marks this instance as the graphicsMode view of the options */
        void cb$v1_21_11$graphicsView(class_315 options);

        /** the stored value, written as 1.21.10's set stored it: no callback */
        void cb$v1_21_11$store(Object value);
    }

    /** the view for one Options (built once by the restorer) */
    public static class_7172<class_5365> create(class_315 options) {
        class_7172<class_5365> view = new class_7172<>("options.graphics", class_7172.method_42399(),
            (caption, v) -> class_2561.method_43471(v.method_75305()),
            new class_7172.class_7173<>(List.of(class_5365.field_25427, class_5365.field_25428, class_5365.field_25429),
                class_5365.field_63462),
            class_5365.field_25428, v -> {
            });
        ((View) (Object) view).cb$v1_21_11$graphicsView(options);
        return view;
    }

    /** the view's value now */
    public static class_5365 get(class_315 options) {
        if (options.method_75337().method_41753()) {
            return class_5365.field_25429;
        }
        return options.method_75329().method_41753() == class_5365.field_25427 ? class_5365.field_25427 : class_5365.field_25428;
    }

    /** after the view's set(v): improvedTransparency follows v, stored without its callback */
    public static void set(class_315 options, Object value) {
        ((View) (Object) options.method_75337()).cb$v1_21_11$store(value == class_5365.field_25429);
    }

    /** GraphicsStatus.getId() (1.21.10 method_7362: FAST 0, FANCY 1, FABULOUS 2, the enum order 1.21.11 keeps) */
    public static int method_7362(class_5365 status) {
        return status.ordinal();
    }

    /** GraphicsStatus.getKey() (1.21.10 method_7359: the translation key, 1.21.11's method_75305) */
    public static String method_7359(class_5365 status) {
        return status.method_75305();
    }

    /** GraphicsStatus.byId(int) (1.21.10 method_29592: ByIdMap WRAP over FAST, FANCY, FABULOUS) */
    public static class_5365 method_29592(int id) {
        class_5365[] v = {class_5365.field_25427, class_5365.field_25428, class_5365.field_25429};
        return v[Math.floorMod(id, v.length)];
    }
}
