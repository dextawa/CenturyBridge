package top.dext.centurybridge.step.v1_21_9.rt;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.class_11555;
import net.minecraft.class_155;
import net.minecraft.class_3264;
import net.minecraft.class_3272;
import net.minecraft.class_3518;
import net.minecraft.class_6497;
import net.minecraft.class_7368;
import net.minecraft.class_7677;
import org.slf4j.Logger;

/**
 * 1.21.9's PackFormat (major.minor) where 1.21.8 counted pack formats in plain ints.
 *
 * Every 1.21.8 format was a major version; 1.21.9's are (major, 0) for 1.21.8's numbers (PackFormat.of(major)) and
 * 1.21.8 code reading a 1.21.9 format sees its major. The converters of ranges are adaptive (an Integer range
 * becomes a PackFormat one, a PackFormat range an Integer one, anything else passes), as erasure keeps the range's
 * element type out of the descriptor (OverlayEntry's formats).
 *
 * A 1.21.8 int handed where 1.21.9 also wants the pack type (Pack.readPackMetadata) came from
 * WorldVersion.packVersion(type): the type is the one whose current format has that major.
 */
public final class CbPackFormats {

    private static final Logger LOGGER = LogUtils.getLogger();

    private CbPackFormats() {
    }

    // ------------------------------------------------------------------ formats

    public static int major(class_11555 format) {
        return format.comp_4416();
    }

    public static class_11555 format(int major) {
        return class_11555.method_72319(major);
    }

    /** the pack type whose current format has this major (WorldVersion.packVersion's answer); resources otherwise */
    public static class_3264 type(int major) {
        return class_155.method_16673().method_70592(class_3264.field_14190).comp_4416() == major
            ? class_3264.field_14190 : class_3264.field_14188;
    }

    /** the current format of that type when the major is its, else (major, 0) */
    public static class_11555 currentFormat(int major) {
        class_11555 current = class_155.method_16673().method_70592(type(major));
        return current.comp_4416() == major ? current : format(major);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class_6497 range(class_6497 range) {
        if (range == null) {
            return null;
        }
        if (range.comp_1() instanceof Integer) {
            return new class_6497(format((Integer) range.comp_1()), format((Integer) range.comp_2()));
        }
        if (range.comp_1() instanceof class_11555) {
            return new class_6497(((class_11555) range.comp_1()).comp_4416(), ((class_11555) range.comp_2()).comp_4416());
        }
        return range;
    }

    // ------------------------------------------------------------------ PackMetadataSection

    /**
     * new PackMetadataSection(description, packFormat, supportedFormats): 1.21.8 accepted the supported range when
     * given, else exactly the declared format; 1.21.9 keeps that range of PackFormats
     */
    public static class_6497<class_11555> supported(int packFormat, Optional<class_6497<Integer>> supported) {
        class_6497<Integer> r = supported.orElse(new class_6497<>(packFormat, packFormat));
        return new class_6497<>(format(r.comp_1()), format(r.comp_2()));
    }

    /** PackMetadataSection.packFormat(): the newest major of the range (1.21.9 keeps no separate declared format) */
    public static int comp_1581(class_3272 section) {
        return section.comp_1582().comp_2().comp_4416();
    }

    /** PackMetadataSection.supportedFormats(): the range's majors */
    public static Optional<class_6497<Integer>> supportedOut(class_6497<class_11555> range) {
        return Optional.of(new class_6497<>(range.comp_1().comp_4416(), range.comp_2().comp_4416()));
    }

    // ------------------------------------------------------------------ AbstractPackResources.getMetadataFromStream

    /**
     * 1.21.8's body: read the stream as JSON and parse the section with its codec when present (1.21.9's takes the
     * pack's location only to name it in the error log)
     */
    public static <T> T method_14392(class_7677<T> section, InputStream stream) {
        JsonObject json;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            json = class_3518.method_15255(reader);
        } catch (Exception e) {
            LOGGER.error("Couldn't load {} metadata", section.comp_3436(), e);
            return null;
        }
        if (!json.has(section.comp_3436())) {
            return null;
        }
        JsonElement element = json.get(section.comp_3436());
        return section.comp_3437().parse(JsonOps.INSTANCE, element)
            .ifError(error -> LOGGER.error("Couldn't load {} metadata: {}", section.comp_3436(), error))
            .result().orElse(null);
    }

    // ------------------------------------------------------------------ ResourceMetadata

    /** ResourceMetadata.copySections(types): the sections of those types this metadata has, nothing else */
    public static class_7368 method_52446(class_7368 metadata, Collection<class_7677<?>> types) {
        Map<class_7677<?>, Object> sections = new HashMap<>();
        for (class_7677<?> type : types) {
            metadata.method_43041(type).ifPresent(v -> sections.put(type, v));
        }
        return of(sections);
    }

    public static class_7368 of(Map<class_7677<?>, Object> sections) {
        if (sections.isEmpty()) {
            return class_7368.field_38688;
        }
        Map<class_7677<?>, Object> copy = Map.copyOf(sections);
        return new class_7368() {
            @Override
            @SuppressWarnings("unchecked")
            public <T> Optional<T> method_43041(class_7677<T> type) {
                return Optional.ofNullable((T) copy.get(type));
            }
        };
    }
}
