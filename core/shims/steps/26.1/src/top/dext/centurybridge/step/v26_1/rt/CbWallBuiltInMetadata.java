package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.ResourceMetadata;

/**
 * 1.21.11's BuiltInMetadata (class_7662: a map of metadata section type to value, made by of() / of(type, value) /
 * of(type1, value1, type2, value2), read by get(type), nullable) is 26.1's ResourceMetadata: 26.1 dropped the class and
 * VanillaPackResourcesBuilder.setMetadata / VanillaPackResources take a ResourceMetadata, whose of(..) factories build
 * the same Map.of-backed metadata (ResourceMetadata.MapBased) and whose EMPTY answers no section (javap of both). The
 * type is renamed to ResourceMetadata (classRenames); its members are these.
 */
public final class CbWallBuiltInMetadata {
    private CbWallBuiltInMetadata() {
    }

    /** BuiltInMetadata.get(type): the section's value, null when absent */
    public static Object get(ResourceMetadata metadata, MetadataSectionType<?> type) {
        return metadata.getSection(type).orElse(null);
    }

    /** BuiltInMetadata.of() */
    public static ResourceMetadata of() {
        return ResourceMetadata.EMPTY;
    }

    /** BuiltInMetadata.of(type, value) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static ResourceMetadata of(MetadataSectionType type, Object value) {
        return ResourceMetadata.of(type, value);
    }

    /** BuiltInMetadata.of(type1, value1, type2, value2) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static ResourceMetadata of(MetadataSectionType type1, Object value1, MetadataSectionType type2, Object value2) {
        return ResourceMetadata.of(type1, value1, type2, value2);
    }
}
