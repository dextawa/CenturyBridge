package top.dext.centurybridge.rt.v1_20_2;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1068;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_640;
import net.minecraft.class_8685;

/**
 * Player skins where 1.20.2 moved from a texture id to PlayerSkin (class_8685: texture, texture URL,
 * cape, elytra, model, secure) under the same erased types (generic drift).
 */
@Environment(EnvType.CLIENT)
@SuppressWarnings({"rawtypes", "unchecked"})
public final class CbGenSkins {

    // ---------------------------------------------------------------- PlayerEntry(.., uuid, name, skin, ..)

    /**
     * superCtorReshapes conv (old arguments 2 and 4): the social screen's PlayerEntry took a
     * Supplier of the player's skin texture in 1.20.1 and takes a Supplier of the PlayerSkin in
     * 1.20.2. The entry draws the face from the skin's texture in both releases; 1.20.2 also hands the
     * supplier to the skin report screen, which renders the model and reports the texture URL.
     * Each get() asks the 1.20.1 supplier, so the skin follows it as it did, and answers:
     * <ul>
     * <li>the player's own PlayerSkin when the texture is the one the client has for that player
     *     (its PlayerInfo on the connection) -- everything about the skin as vanilla knows it;</li>
     * <li>else the player's default skin when the texture is that one (DefaultPlayerSkin.get(uuid));</li>
     * <li>else a skin of that texture alone: the classic model (1.20.1's "default"), no cape, elytra
     *     or URL (1.20.1 had no skin report; a texture the client never downloaded has no URL).</li>
     * </ul>
     * A value that already is a PlayerSkin passes, and a 1.20.2 supplier passes unwrapped.
     */
    public static Supplier cbSkins(UUID id, Supplier legacy) {
        if (legacy == null || legacy instanceof SkinOfTexture) {
            return legacy;
        }
        return new SkinOfTexture(id, legacy);
    }

    private static final class SkinOfTexture implements Supplier<class_8685> {
        private final UUID id;
        private final Supplier<?> legacy;

        SkinOfTexture(UUID id, Supplier<?> legacy) {
            this.id = id;
            this.legacy = legacy;
        }

        @Override
        public class_8685 get() {
            Object value = this.legacy.get();
            if (value instanceof class_8685 skin || value == null) {
                return (class_8685) value;
            }
            return skinOf(this.id, (class_2960) value);
        }
    }

    /** the PlayerSkin 1.20.2 has for a player whose skin texture 1.20.1 code names */
    static class_8685 skinOf(UUID id, class_2960 texture) {
        if (id != null) {
            class_634 connection = class_310.method_1551().method_1562();
            class_640 info = connection == null ? null : connection.method_2871(id);
            if (info != null) {
                class_8685 skin = info.method_52810();
                if (skin != null && texture.equals(skin.comp_1626())) {
                    return skin;
                }
            }
            class_8685 fallback = class_1068.method_4648(id);
            if (texture.equals(fallback.comp_1626())) {
                return fallback;
            }
        }
        return new class_8685(texture, null, null, null, class_8685.class_7920.field_41123, false);
    }

    // ---------------------------------------------------------------- EntityRenderers.createPlayerRenderers

    /**
     * callReshapes ret: createPlayerRenderers(context) answered a map keyed by the 1.20.1 model name
     * ("default", "slim") and answers one keyed by PlayerSkin.Model in 1.20.2, whose id() is that
     * very name (WIDE "default", SLIM "slim"). A fresh immutable map in both releases, so a copy under
     * the 1.20.1 keys, in the same order; a key that already is a name passes.
     */
    public static Map cbPlayerRenderers(Map renderers) {
        if (renderers == null) {
            return null;
        }
        ImmutableMap.Builder<Object, Object> out = ImmutableMap.builder();
        for (Map.Entry<?, ?> e : ((Map<?, ?>) renderers).entrySet()) {
            Object key = e.getKey() instanceof class_8685.class_7920 model ? model.method_52856() : e.getKey();
            out.put(key, e.getValue());
        }
        return out.build();
    }

    private CbGenSkins() {
    }
}
