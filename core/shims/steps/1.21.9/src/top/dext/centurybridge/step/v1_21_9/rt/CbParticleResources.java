package top.dext.centurybridge.step.v1_21_9.rt;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1059;
import net.minecraft.class_11939;
import net.minecraft.class_2394;
import net.minecraft.class_2396;
import net.minecraft.class_310;
import net.minecraft.class_702;
import net.minecraft.class_707;

/**
 * ParticleEngine's 1.21.8 sprite-set and provider members, reached by 1.21.8 code through its own access widener.
 * 1.21.9 moved them to ParticleResources (the engine's field_62618, the game's one): javap -c, the register bodies
 * (1.21.8 method_3043 / method_18834, 1.21.9 method_74300 / method_74301) are the same instructions over the moved
 * maps, and MutableSpriteSet / SpriteParticleRegistration moved with them (class renames, s9k-particles.json). The
 * particle atlas the 1.21.8 engine built over the texture manager is the AtlasManager's particle atlas on 1.21.9.
 * The register calls go to 1.21.9's own private methods, so a native 1.21.9 mixin on them still runs.
 */
@Environment(EnvType.CLIENT)
public final class CbParticleResources {
    private CbParticleResources() {
    }

    /** ParticleEngine.register(type, provider) */
    public static <T extends class_2394> void method_3043(class_702 engine, class_2396<T> type, class_707<T> provider) {
        engine.field_62618.method_74300(type, provider);
    }

    /** ParticleEngine.register(type, registration) */
    public static <T extends class_2394> void method_18834(class_702 engine, class_2396<T> type,
            class_11939.class_4091<T> registration) {
        engine.field_62618.method_74301(type, registration);
    }

    /** ParticleEngine.spriteSets */
    public static Map<?, ?> field_18300(class_702 engine) {
        return engine.field_62618.field_62627;
    }

    /** ParticleEngine.textureAtlas: the particle atlas, the AtlasManager's on 1.21.9 */
    public static class_1059 field_18301(class_702 engine) {
        return class_310.method_1551().method_72703().method_73025(class_1059.field_17898);
    }
}
