package top.dext.centurybridge.step.v1_21_9.facade;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.class_7368;
import net.minecraft.class_7677;
import top.dext.centurybridge.step.v1_21_9.rt.CbPackFormats;

/**
 * 1.21.8's ResourceMetadata.Builder (gone in 1.21.9, which builds metadata only from JSON): put(type, value) collects
 * sections, build() answers a ResourceMetadata whose getSection(type) is the value put for it (the empty metadata when
 * nothing was put), as 1.21.8's body did.
 */
public class ResourceMetadataBuilder {
    private final Map<class_7677<?>, Object> sections = new LinkedHashMap<>();

    public ResourceMetadataBuilder() {
    }

    /** put(type, value) */
    public <T> ResourceMetadataBuilder method_52448(class_7677<T> type, T value) {
        sections.put(type, value);
        return this;
    }

    /** build() */
    public class_7368 method_52447() {
        return CbPackFormats.of(sections);
    }
}
