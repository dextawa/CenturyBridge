package top.dext.centurybridge.step.v1_21_5.rt;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_5.facade.ModelResourceLocation;

/**
 * 1.21.4's ModelResourceLocation API on 1.21.5, which keys block models by BlockState (javap -c -p both):
 * <ul>
 * <li>1.21.4 ModelManager.getModel(location) (class_1092.method_4742) looked the location up in the baked block state
 *     models (BakingResult.blockStateModels: every block state as BlockModelShaper.stateToModelLocation, plus the item
 *     frame fake states) and answered the missing model otherwise (getOrDefault). 1.21.5 holds the same models per state
 *     (BlockModelShaper.getBlockModel(state), class_773.method_3335) and the item frame states as
 *     BlockStateDefinitions.getItemFrameFakeState(glow, map) (class_10811.method_68014): the location is turned back
 *     into the state it named (the state of the block of that id whose variant string is the location's), then asked.</li>
 * <li>stateToModelLocation / statePropertiesToString (class_773.method_3340/3336/3338): 1.21.4's bodies.</li>
 * <li>ItemFrame / MissingBlockModel location constants: 1.21.4's values.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbModelLocations {

    private static final net.minecraft.class_2960 GLOW_ITEM_FRAME = net.minecraft.class_2960.method_60656("glow_item_frame");
    private static final net.minecraft.class_2960 ITEM_FRAME = net.minecraft.class_2960.method_60656("item_frame");
    private static final net.minecraft.class_2960 MISSING = net.minecraft.class_2960.method_60656("builtin/missing");

    private CbModelLocations() {
    }

    // ---- BlockModelShaper statics (1.21.4 bodies) ----

    /** stateToModelLocation(state): the block's registry id and the state's properties */
    public static ModelResourceLocation method_3340(net.minecraft.class_2680 state) {
        return method_3336(net.minecraft.class_7923.field_41175.method_10221(state.method_26204()), state);
    }

    /** stateToModelLocation(id, state) */
    public static ModelResourceLocation method_3336(net.minecraft.class_2960 id, net.minecraft.class_2680 state) {
        return new ModelResourceLocation(id, method_3338(state.method_11656()));
    }

    /** statePropertiesToString(values): "name=value" joined by ',' in the map's order */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static String method_3338(Map<net.minecraft.class_2769<?>, Comparable<?>> values) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<net.minecraft.class_2769<?>, Comparable<?>> e : values.entrySet()) {
            if (sb.length() != 0) {
                sb.append(',');
            }
            net.minecraft.class_2769 property = e.getKey();
            sb.append(property.method_11899());
            sb.append('=');
            sb.append(property.method_11901((Comparable) e.getValue()));
        }
        return sb.toString();
    }

    // ---- location constants (1.21.4 values) ----

    /** ItemFrameRenderer GLOW_MAP_FRAME_LOCATION (BlockStateModelLoader, class_9824.field_53649) */
    public static ModelResourceLocation field_53649() {
        return new ModelResourceLocation(GLOW_ITEM_FRAME, "map=true");
    }

    /** GLOW_FRAME_LOCATION */
    public static ModelResourceLocation field_53650() {
        return new ModelResourceLocation(GLOW_ITEM_FRAME, "map=false");
    }

    /** MAP_FRAME_LOCATION */
    public static ModelResourceLocation field_53651() {
        return new ModelResourceLocation(ITEM_FRAME, "map=true");
    }

    /** FRAME_LOCATION */
    public static ModelResourceLocation field_53652() {
        return new ModelResourceLocation(ITEM_FRAME, "map=false");
    }

    /** MissingBlockModel.VARIANT (class_10096.field_53661) */
    public static ModelResourceLocation field_53661() {
        return new ModelResourceLocation(MISSING, "missing");
    }

    // ---- ModelManager.getModel(location) ----

    /** the 1.21.5 state a 1.21.4 block state model location named, null when none */
    static net.minecraft.class_2680 state(ModelResourceLocation location) {
        net.minecraft.class_2960 id = location.comp_2875();
        String variant = location.comp_2876();
        boolean glow = id.equals(GLOW_ITEM_FRAME);
        if (glow || id.equals(ITEM_FRAME)) {
            if (variant.equals("map=true")) {
                return net.minecraft.class_10811.method_68014(glow, true);
            }
            if (variant.equals("map=false")) {
                return net.minecraft.class_10811.method_68014(glow, false);
            }
            return null;
        }
        net.minecraft.class_2248 block = net.minecraft.class_7923.field_41175.method_17966(id).orElse(null);
        if (block == null) {
            return null;
        }
        for (net.minecraft.class_2680 s : block.method_9595().method_11662()) {
            if (method_3338(s.method_11656()).equals(variant)) {
                return s;
            }
        }
        return null;
    }

    /** getModel(location): the model of the state it named, the missing model otherwise (1.21.4's getOrDefault) */
    public static net.minecraft.class_1087 method_4742(net.minecraft.class_1092 manager, ModelResourceLocation location) {
        net.minecraft.class_2680 s = location == null ? null : state(location);
        return s == null ? manager.method_68046() : manager.method_4743().method_3335(s);
    }
}
