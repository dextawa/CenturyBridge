package top.dext.centurybridge.step.v26_1.rt;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

/**
 * 1.21.11's AbstractPackResources.getMetadataFromStream(type, stream, location) (class_3255.method_14392), which 26.1
 * folded into ResourceMetadata.fromJsonStream / getSection: 1.21.11's body -- the stream read as a JSON object, the
 * type's section (null when absent) parsed with its codec, a parse error or an unreadable stream logged as 1.21.11
 * logged it ("Couldn't load {pack id} {section} metadata: {message}") and answered null.
 */
public final class CbWallPackResources {

    private static final Logger LOGGER = LogUtils.getLogger();

    private CbWallPackResources() {
    }

    public static <T> T getMetadataFromStream(MetadataSectionType<T> type, InputStream stream, PackLocationInfo location) {
        JsonObject json;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            json = GsonHelper.parse(reader);
        } catch (Exception e) {
            LOGGER.error("Couldn't load {} {} metadata: {}", location.id(), type.name(), e.getMessage());
            return null;
        }
        if (!json.has(type.name())) {
            return null;
        }
        return type.codec().parse(JsonOps.INSTANCE, json.get(type.name()))
            .ifError(error -> LOGGER.error("Couldn't load {} {} metadata: {}", location.id(), type.name(), error.message()))
            .result()
            .orElse(null);
    }
}
