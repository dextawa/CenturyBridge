package top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.particle;

import top.dext.centurybridge.step.v1_20_5.facade.ParticleOptionsDeserializer;
import top.dext.centurybridge.step.v1_20_5.rt.CbParticles;

/**
 * Fabric API 1.20.4's FabricParticleTypes.complex(factory) and complex(alwaysSpawn, factory): a particle type
 * whose options read and write through a 1.20.4 ParticleEffect.Factory (facade/ParticleOptionsDeserializer).
 * 1.20.5's complex takes codecs; the 1.20.4 type is s4item's CbParticles.legacyType (its codecs read through
 * the deserializer, its options write themselves the 1.20.4 way). complex(factory) is complex(false, factory),
 * as in 1.20.4.
 */
public final class CbFapiParticles {

    private CbFapiParticles() {
    }

    public static <T extends net.minecraft.class_2394> net.minecraft.class_2396<T> complex(ParticleOptionsDeserializer<T> factory) {
        return CbParticles.legacyType(false, factory);
    }
}
