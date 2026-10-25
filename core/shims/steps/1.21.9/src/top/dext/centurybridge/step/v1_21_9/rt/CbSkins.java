package top.dext.centurybridge.step.v1_21_9.rt;

import net.minecraft.class_12079;
import net.minecraft.class_2960;
import net.minecraft.class_7920;
import net.minecraft.class_8685;

/**
 * 1.21.9's PlayerSkin for the 1.21.8 one.
 *
 * 1.21.8's PlayerSkin held the texture ids (ResourceLocations registered with the TextureManager), the skin's
 * download URL and a PlayerSkin.Model; 1.21.9's holds ClientAsset.Textures (a DownloadedTexture for a skin
 * fetched from a URL, a ResourceTexture for a built-in one) and the top-level PlayerModelType, which has the
 * same two constants (the step renames PlayerSkin$Model to it). A texture id is the texture's texturePath()
 * (what 1.21.9 binds to render it); a 1.21.8 skin built from ids keeps each id as the texture path, and its URL
 * as a DownloadedTexture's.
 */
public final class CbSkins {

    private CbSkins() {
    }

    /** texture() / capeTexture() / elytraTexture(): the id 1.21.8 bound (null stays null) */
    public static class_2960 texturePath(class_12079.class_12081 t) {
        return t == null ? null : t.comp_3627();
    }

    /** textureUrl(): the downloaded skin's URL, null for a built-in skin (as 1.21.8's default skins had) */
    public static String comp_1911(class_8685 skin) {
        return skin.comp_1626() instanceof class_12079.class_12080 d ? d.comp_4939() : null;
    }

    private static class_12079.class_12081 texture(class_2960 id) {
        return id == null ? null : new class_12079.class_10726(id, id);
    }

    /** new PlayerSkin(texture, textureUrl, capeTexture, elytraTexture, model, secure) */
    public static class_8685 create(class_2960 texture, String url, class_2960 cape, class_2960 elytra, class_7920 model,
                                    boolean secure) {
        class_12079.class_12081 body = url != null && texture != null ? new class_12079.class_12080(texture, url) : texture(texture);
        return new class_8685(body, texture(cape), texture(elytra), model, secure);
    }

    /** PlayerSkin.Model.id(): "slim" / "default", 1.21.9's legacy services id of the same constant */
    public static String method_52856(class_7920 model) {
        return model.field_62914;
    }

    /** PlayerSkin.Model.byName(name): null or an unknown name is WIDE, as 1.21.9's byName answers */
    public static class_7920 method_52857(String name) {
        return name == null ? class_7920.field_41123 : class_7920.method_74671(name);
    }
}
