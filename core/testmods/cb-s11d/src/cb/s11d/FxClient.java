package cb.s11d;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_5294;
import net.minecraft.class_638;
import cb.s11d.mixin.EffectsAccess;

/**
 * 1.21.10-era client part of cb-s11d: puts FxEffects in DimensionSpecialEffects' effects table under cb_s11d:fx, the
 * effects id this mod's data pack gives the overworld's dimension type (data/minecraft/dimension_type/overworld.json,
 * 1.21.10's "effects" field). 1.21.10's Fabric API no longer has DimensionRenderingRegistry, so a 1.21.10 mod wrote the
 * table through an accessor, as here.
 */
public class FxClient implements ClientModInitializer {
    public static final class_2960 FX = class_2960.method_60655("cb_s11d", "fx");

    @Override
    public void onInitializeClient() {
        Object2ObjectMap<class_2960, class_5294> table = EffectsAccess.cb$effects();
        table.put(FX, new FxEffects());
    }

    /** read by the native fixture: what 1.21.10's API answers in the current level, and the call counts */
    public static String report() {
        class_638 level = class_310.method_1551().field_1687;
        class_5294 e = level == null ? null : level.method_28103();
        return "effects=" + (e == null ? "null" : e.getClass().getName()) + " fogColor=" + FxEffects.fogColor
            + " foggyAt=" + FxEffects.foggyAt + " sunrise=" + FxEffects.sunrise + " sunriseColor=" + FxEffects.sunriseColor
            + " skyType=" + FxEffects.skyType + " constantAmbient=" + FxEffects.constantAmbient
            + " forceBright=" + FxEffects.forceBright;
    }
}
