package top.dext.centurybridge.step.v1_21_4.facade;

import com.google.gson.JsonObject;

/**
 * 1.21.3's MetadataSectionSerializer (net/minecraft/class_3270, classRenames), gone in 1.21.4: a pack
 * metadata section read from its JSON object. 1.21.4 reads sections through MetadataSectionType records
 * (name, codec); a serializer converts to one in CbRestMetadata.type and back in CbRestMetadata.serializer.
 */
public interface MetadataSectionSerializer<T> {

    /** getMetadataSectionName() */
    String method_14420();

    /** fromJson(json) */
    T method_14421(JsonObject json);
}
