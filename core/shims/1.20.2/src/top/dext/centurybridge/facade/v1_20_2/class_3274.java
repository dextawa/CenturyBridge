package top.dext.centurybridge.facade.v1_20_2;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.class_2561;
import net.minecraft.class_3272;
import net.minecraft.class_3518;
import net.minecraft.class_5250;
import net.minecraft.class_7677;

/**
 * 1.20.1 PackMetadataSectionSerializer (class_3274), gone in 1.20.2 (the pack section is a codec
 * type there): reads and writes the "pack" metadata section as 1.20.1 did -- the description
 * component and the pack format, nothing else (1.20.1 had no supported-formats range; a section it
 * reads has none). Ported as it was: fromJson / toJson under their 1.20.1 names (method_14426 /
 * method_45255), reached through the MetadataSectionType methods 1.20.2 calls.
 */
public class class_3274 implements class_7677<class_3272> {

    public class_3274() {
    }

    /** fromJson(json) */
    public class_3272 method_14426(JsonObject json) {
        class_5250 description = class_2561.class_2562.method_10872(json.get("description"));
        if (description == null) {
            throw new JsonParseException("Invalid/missing description!");
        }
        int format = class_3518.method_15260(json, "pack_format");
        return new class_3272(description, format, java.util.Optional.empty());
    }

    /** toJson(section) */
    public JsonObject method_45255(class_3272 section) {
        JsonObject json = new JsonObject();
        json.add("description", class_2561.class_2562.method_10868(section.comp_1580()));
        json.addProperty("pack_format", section.comp_1581());
        return json;
    }

    /** getMetadataSectionName() */
    @Override
    public String method_14420() {
        return "pack";
    }

    @Override
    public class_3272 method_14421(JsonObject json) {
        return this.method_14426(json);
    }

    @Override
    public JsonObject method_45251(class_3272 section) {
        return this.method_45255(section);
    }
}
