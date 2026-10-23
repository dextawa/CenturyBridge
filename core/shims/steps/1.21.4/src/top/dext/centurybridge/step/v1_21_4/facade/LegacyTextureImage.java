package top.dext.centurybridge.step.v1_21_4.facade;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 1.21.3 SimpleTexture.TextureImage (class_1049$class_4006, gone in 1.21.4, whose TextureContents is its successor): a loaded
 * image and its texture metadata, or the IOException loading failed with. Body for body.
 */
@Environment(EnvType.CLIENT)
public class LegacyTextureImage implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger("CenturyBridge/SimpleTexture");

    /** metadata */
    public final net.minecraft.class_1084 field_17895;
    /** image */
    public final net.minecraft.class_1011 field_17896;
    /** exception */
    public final IOException field_17897;

    public LegacyTextureImage(IOException exception) {
        this.field_17897 = exception;
        this.field_17895 = null;
        this.field_17896 = null;
    }

    public LegacyTextureImage(net.minecraft.class_1084 metadata, net.minecraft.class_1011 image) {
        this.field_17897 = null;
        this.field_17895 = metadata;
        this.field_17896 = image;
    }

    /** load(resourceManager, id) */
    public static LegacyTextureImage method_18156(net.minecraft.class_3300 resourceManager, net.minecraft.class_2960 id) {
        try {
            net.minecraft.class_3298 resource = resourceManager.getResourceOrThrow(id);
            net.minecraft.class_1011 image;
            try (InputStream in = resource.method_14482()) {
                image = net.minecraft.class_1011.method_4309(in);
            }
            net.minecraft.class_1084 metadata = null;
            try {
                metadata = resource.method_14481().method_43041(net.minecraft.class_1084.field_55542).orElse(null);
            } catch (RuntimeException e) {
                LOGGER.warn("Failed reading metadata of: {}", id, e);
            }
            return new LegacyTextureImage(metadata, image);
        } catch (IOException e) {
            return new LegacyTextureImage(e);
        }
    }

    /** getTextureMetadata() */
    public net.minecraft.class_1084 method_18155() {
        return this.field_17895;
    }

    /** getImage() */
    public net.minecraft.class_1011 method_18157() throws IOException {
        if (this.field_17897 != null) {
            throw this.field_17897;
        }
        return this.field_17896;
    }

    @Override
    public void close() {
        if (this.field_17896 != null) {
            this.field_17896.close();
        }
    }

    /** throwIfError() */
    public void method_18158() throws IOException {
        if (this.field_17897 != null) {
            throw this.field_17897;
        }
    }
}
