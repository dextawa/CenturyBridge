package top.dext.centurybridge.step.v1_21_4.facade;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.dext.centurybridge.step.v1_21_4.mixin.CbTextureSkinDownloaderAccess;
import top.dext.centurybridge.step.v1_21_4.rt.CbTextures;

/**
 * 1.21.3 HttpTexture (class_1046, gone in 1.21.4, whose skins download through SkinTextureDownloader): a texture loaded from a
 * local cache file, else downloaded from a URL into it (and uploaded on the render thread), optionally processed as a legacy
 * skin. Body for body; the legacy-skin processing is 1.21.4's (the same code), behind 1.21.3's own size check. Like 1.21.3's
 * every texture it loads on register and resets on reload (it is no ReloadableTexture's contents: see {@link CbTextures}).
 */
@Environment(EnvType.CLIENT)
public class HttpTexture extends net.minecraft.class_1049 implements CbTextures.CbLegacyTexture {
    public static final Logger field_5212 = LoggerFactory.getLogger("CenturyBridge/HttpTexture");
    public static final int field_34017 = 64;
    public static final int field_34018 = 64;
    public static final int field_34019 = 32;

    /** file */
    public final File field_5210;
    /** urlString */
    public final String field_5214;
    /** processLegacySkin */
    public final boolean field_20842;
    /** onDownloaded */
    public final Runnable field_20843;
    /** future */
    public CompletableFuture<?> field_20844;
    /** uploaded */
    public boolean field_5215;

    public HttpTexture(File file, String urlString, net.minecraft.class_2960 location, boolean processLegacySkin, Runnable onDownloaded) {
        super(location);
        this.field_5210 = file;
        this.field_5214 = urlString;
        this.field_20842 = processLegacySkin;
        this.field_20843 = onDownloaded;
    }

    /** loadCallback(image) */
    public void method_4534(net.minecraft.class_1011 image) {
        if (this.field_20843 != null) {
            this.field_20843.run();
        }
        net.minecraft.class_310.method_1551().execute(() -> {
            this.field_5215 = true;
            if (!com.mojang.blaze3d.systems.RenderSystem.isOnRenderThread()) {
                com.mojang.blaze3d.systems.RenderSystem.recordRenderCall(() -> this.method_4531(image));
            } else {
                this.method_4531(image);
            }
        });
    }

    /** upload(image) */
    public void method_4531(net.minecraft.class_1011 image) {
        com.mojang.blaze3d.platform.TextureUtil.prepareImage(this.method_4624(), image.method_4307(), image.method_4323());
        image.method_4301(0, 0, 0, true);
    }

    /** load(resourceManager) */
    @Override
    public void method_4625(net.minecraft.class_3300 resourceManager) throws IOException {
        net.minecraft.class_310.method_1551().execute(() -> {
            if (!this.field_5215) {
                try {
                    // SimpleTexture.load (a ReloadableTexture's contents, applied)
                    this.method_65857(this.method_65809(resourceManager));
                } catch (IOException e) {
                    field_5212.warn("Failed to load texture: {}", this.method_65859(), e);
                }
                this.field_5215 = true;
            }
        });
        if (this.field_20844 == null) {
            net.minecraft.class_1011 image;
            if (this.field_5210 != null && this.field_5210.isFile()) {
                field_5212.debug("Loading http texture from local cache ({})", this.field_5210);
                FileInputStream in = new FileInputStream(this.field_5210);
                image = this.method_22795(in);
            } else {
                image = null;
            }
            if (image != null) {
                this.method_4534(image);
            } else {
                this.field_20844 = CompletableFuture.runAsync(this::method_22801, net.minecraft.class_156.method_18349().method_64116("downloadTexture"));
            }
        }
    }

    /** 1.21.3's reset: registered again (loads again; a downloaded texture stays as it is) */
    @Override
    public void method_18169(net.minecraft.class_1060 textureManager, net.minecraft.class_3300 resourceManager, net.minecraft.class_2960 id,
            java.util.concurrent.Executor executor) {
        CbTextures.reset(this, textureManager, id);
    }

    /** load(stream) */
    public net.minecraft.class_1011 method_22795(InputStream in) {
        net.minecraft.class_1011 image = null;
        try {
            image = net.minecraft.class_1011.method_4309(in);
            if (this.field_20842) {
                image = this.method_22798(image);
            }
        } catch (Exception e) {
            field_5212.warn("Error while loading the skin texture", e);
        }
        return image;
    }

    /** processLegacySkin(image): an incorrectly sized image is discarded (released, logged) */
    public net.minecraft.class_1011 method_22798(net.minecraft.class_1011 image) {
        int height = image.method_4323();
        int width = image.method_4307();
        if (width != 64 || height != 32 && height != 64) {
            image.close();
            field_5212.warn("Discarding incorrectly sized ({}x{}) skin texture from {}", width, height, this.field_5214);
            return null;
        }
        return CbTextureSkinDownloaderAccess.cb$processLegacySkin(image, this.field_5214);
    }

    /** the download: HTTP(S) through the game's proxy into the cache file, then loaded on the game thread */
    public void method_22801() {
        HttpURLConnection connection = null;
        field_5212.debug("Downloading http texture from {} to {}", this.field_5214, this.field_5210);
        try {
            connection = (HttpURLConnection) new URL(this.field_5214).openConnection(net.minecraft.class_310.method_1551().method_1487());
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.connect();
            if (connection.getResponseCode() / 100 == 2) {
                InputStream in;
                if (this.field_5210 != null) {
                    org.apache.commons.io.FileUtils.copyInputStreamToFile(connection.getInputStream(), this.field_5210);
                    in = new FileInputStream(this.field_5210);
                } else {
                    in = connection.getInputStream();
                }
                net.minecraft.class_310.method_1551().execute(() -> this.method_22797(in));
            }
        } catch (Exception e) {
            field_5212.error("Couldn't download http texture", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /** doNotchTransparencyHack(image, x0, y0, x1, y1) */
    public static void method_22794(net.minecraft.class_1011 image, int x0, int y0, int x1, int y1) {
        CbTextureSkinDownloaderAccess.cb$doNotchTransparencyHack(image, x0, y0, x1, y1);
    }

    /** setNoAlpha(image, x0, y0, x1, y1) */
    public static void method_22796(net.minecraft.class_1011 image, int x0, int y0, int x1, int y1) {
        CbTextureSkinDownloaderAccess.cb$setNoAlpha(image, x0, y0, x1, y1);
    }

    /** the downloaded stream, loaded */
    public void method_22797(InputStream in) {
        net.minecraft.class_1011 image = this.method_22795(in);
        if (image != null) {
            this.method_4534(image);
        }
    }
}
