package top.dext.centurybridge.step.v1_21_4.facade;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** 1.21.3 TextureMetadataSectionSerializer (class_1085, gone in 1.21.4): the "texture" section's reader, body for body */
@Environment(EnvType.CLIENT)
public class TextureMetadataSectionSerializer implements MetadataSectionSerializer<net.minecraft.class_1084> {

    public TextureMetadataSectionSerializer() {
    }

    /** fromJson(json) */
    public net.minecraft.class_1084 method_4698(JsonObject json) {
        boolean blur = net.minecraft.class_3518.method_15258(json, "blur", false);
        boolean clamp = net.minecraft.class_3518.method_15258(json, "clamp", false);
        return new net.minecraft.class_1084(blur, clamp);
    }

    /** getMetadataSectionName() */
    @Override
    public String method_14420() {
        return "texture";
    }

    @Override
    public net.minecraft.class_1084 method_14421(JsonObject json) {
        return this.method_4698(json);
    }
}
