package top.dext.centurybridge.step.v1_21_4.rt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.dext.centurybridge.step.v1_21_4.facade.LegacyTextureImage;

/**
 * 1.21.3's texture lifecycle on 1.21.4's.
 * <p>
 * 1.21.3: every texture loaded itself (AbstractTexture.load(resourceManager)); TextureManager.register(id, texture) loaded it
 * (a missing file: the missing texture, logged; anything else: a crash report), and a resource reload reset every registered
 * texture (reset(..) registered it again, so it loaded again). 1.21.4: only ReloadableTextures load, from contents
 * (loadContents / apply), and register(id, texture) only records it.
 * <ul>
 * <li>Every texture has load / reset again (CbTextureAbstractMixin; a ReloadableTexture's load is apply(loadContents), the same
 *     upload 1.21.3's SimpleTexture did); a mod's register(id, texture) loads it as 1.21.3's did ({@link #register});</li>
 * <li>a texture of a mod's own that loads or resets itself ({@link CbLegacyTexture}, given to the mod classes declaring either) is
 *     reset on reload as 1.21.3 reset it, and is left out of 1.21.4's own reload of reloadable textures
 *     (CbTextureManagerMixin);</li>
 * <li>SimpleTexture.getTextureImage (TextureImage, gone in 1.21.4: {@link LegacyTextureImage}) is what a 1.21.3 subclass's
 *     contents come from (override hook to loadContents).</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbTextures {
    private CbTextures() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger("CenturyBridge/TextureManager");

    /** a texture class of a 1.21.3 mod that loads (load) or resets (reset) itself (adapterHooks) */
    public interface CbLegacyTexture {
        void method_4625(net.minecraft.class_3300 resourceManager) throws IOException;

        void method_18169(net.minecraft.class_1060 textureManager, net.minecraft.class_3300 resourceManager, net.minecraft.class_2960 id,
            java.util.concurrent.Executor executor);
    }

    /** every texture's 1.21.3 load / reset (CbTextureAbstractMixin) */
    public interface CbTexture {
        void method_4625(net.minecraft.class_3300 resourceManager) throws IOException;

        void method_18169(net.minecraft.class_1060 textureManager, net.minecraft.class_3300 resourceManager, net.minecraft.class_2960 id,
            java.util.concurrent.Executor executor);
    }

    /** TextureManager's resource manager (CbTextureManagerMixin) */
    public interface CbTextureManager {
        net.minecraft.class_3300 cb$resourceManager();
    }

    /** was TextureManager.register(id, texture) (method_4616), called by a mod: 1.21.3's loadTexture, then the 1.21.4 register */
    public static void register(net.minecraft.class_1060 manager, net.minecraft.class_2960 id, net.minecraft.class_1044 texture) {
        registerLoaded(manager, id, load(manager, id, texture));
    }

    /** set while a texture already loaded the 1.21.3 way is handed to 1.21.4's register (it is not loaded again there) */
    private static final ThreadLocal<Boolean> LOADED = ThreadLocal.withInitial(() -> false);

    /** whether the texture register is given now was already loaded (register / registerLoaded) */
    public static boolean loaded() {
        return LOADED.get();
    }

    /** 1.21.4's register(id, texture) of a texture loaded the 1.21.3 way */
    public static void registerLoaded(net.minecraft.class_1060 manager, net.minecraft.class_2960 id, net.minecraft.class_1044 texture) {
        boolean was = LOADED.get();
        LOADED.set(true);
        try {
            manager.method_4616(id, texture);
        } finally {
            LOADED.set(was);
        }
    }

    /**
     * 1.21.4's own register(id, texture) of a texture that does not reload from contents (dynamic, atlas, font textures;
     * CbTextureManagerMixin): 1.21.3's register loaded it (loadTexture) first. The texture to register: the texture, or
     * the missing texture when its load fails with an IOException, as 1.21.3's did.
     */
    public static net.minecraft.class_1044 loadOnRegister(net.minecraft.class_1060 manager, net.minecraft.class_2960 id, net.minecraft.class_1044 texture) {
        return load(manager, id, texture);
    }

    /** 1.21.3 TextureManager.loadTexture(id, texture) */
    private static net.minecraft.class_1044 load(net.minecraft.class_1060 manager, net.minecraft.class_2960 id, net.minecraft.class_1044 texture) {
        try {
            ((CbTexture) texture).method_4625(((CbTextureManager) manager).cb$resourceManager());
            return texture;
        } catch (IOException e) {
            if (id != net.minecraft.class_1060.field_5285) {
                LOGGER.warn("Failed to load texture: {}", id, e);
            }
            return missingTexture();
        } catch (Throwable t) {
            net.minecraft.class_128 report = net.minecraft.class_128.method_560(t, "Registering texture");
            net.minecraft.class_129 category = report.method_562("Resource location being registered");
            category.method_578("Resource location", id);
            category.method_577("Texture object class", () -> texture.getClass().getName());
            throw new net.minecraft.class_148(report);
        }
    }

    /** was MissingTextureAtlasSprite.getTexture() (method_4540): 1.21.4's texture manager keeps it under the missing texture's id */
    public static net.minecraft.class_1043 method_4540() {
        net.minecraft.class_1060 manager = net.minecraft.class_310.method_1551().method_1531();
        return (net.minecraft.class_1043) manager.method_4619(net.minecraft.class_1047.method_4539());
    }

    private static net.minecraft.class_1044 missingTexture() {
        return method_4540();
    }

    /** AbstractTexture.reset(manager, resourceManager, id, executor): 1.21.3 registered the texture again */
    public static void reset(net.minecraft.class_1044 texture, net.minecraft.class_1060 manager, net.minecraft.class_2960 id) {
        register(manager, id, texture);
    }

    /** whether 1.21.4's reload leaves the texture to 1.21.3's reset */
    public static boolean legacy(net.minecraft.class_1044 texture) {
        return texture instanceof CbLegacyTexture;
    }

    /** 1.21.3's reload of the textures of mods' own classes: each reset, as every texture was */
    public static void resetLegacy(net.minecraft.class_1060 manager, Map<net.minecraft.class_2960, net.minecraft.class_1044> textures) {
        List<Map.Entry<net.minecraft.class_2960, net.minecraft.class_1044>> legacy = new ArrayList<>();
        for (Map.Entry<net.minecraft.class_2960, net.minecraft.class_1044> e : textures.entrySet()) {
            if (legacy(e.getValue())) {
                legacy.add(Map.entry(e.getKey(), e.getValue()));
            }
        }
        net.minecraft.class_3300 resources = ((CbTextureManager) manager).cb$resourceManager();
        for (Map.Entry<net.minecraft.class_2960, net.minecraft.class_1044> e : legacy) {
            ((CbTexture) e.getValue()).method_18169(manager, resources, e.getKey(), net.minecraft.class_156.method_18349());
        }
    }

    // ---- SimpleTexture ----

    /** was SimpleTexture.location (field_5224) */
    public static net.minecraft.class_2960 field_5224(net.minecraft.class_1049 texture) {
        return texture.method_65859();
    }

    /** was SimpleTexture.getTextureImage(resourceManager) (method_18153), and a subclass's super call: 1.21.3's body */
    public static LegacyTextureImage method_18153(net.minecraft.class_1049 texture, net.minecraft.class_3300 resourceManager) {
        return LegacyTextureImage.method_18156(resourceManager, texture.method_65859());
    }

    /** 1.21.4's loadContents of a 1.21.3 SimpleTexture: the contents of its getTextureImage (its error thrown, as 1.21.3's load threw it) */
    public static net.minecraft.class_10539 contents(LegacyTextureImage image) {
        try {
            image.method_18158();
            return new net.minecraft.class_10539(image.method_18157(), image.method_18155());
        } catch (IOException e) {
            throw sneaky(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> RuntimeException sneaky(Throwable t) throws T {
        throw (T) t;
    }
}
