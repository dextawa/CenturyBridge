package top.dext.centurybridge.step.v1_21_4.facade;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.Validate;
import top.dext.centurybridge.step.v1_21_4.rt.CbTextureMetadata;

/**
 * 1.21.3 AnimationMetadataSectionSerializer (class_1081, gone in 1.21.4, which reads the section with a codec): the
 * "animation" section's reader, body for body, making 1.21.4's AnimationMetadataSection record (see {@link CbTextureMetadata}).
 */
@Environment(EnvType.CLIENT)
public class AnimationMetadataSectionSerializer implements MetadataSectionSerializer<net.minecraft.class_1079> {

    public AnimationMetadataSectionSerializer() {
    }

    /** fromJson(json) */
    public net.minecraft.class_1079 method_4692(JsonObject json) {
        ImmutableList.Builder<net.minecraft.class_1080> frames = ImmutableList.builder();
        int frameTime = net.minecraft.class_3518.method_15282(json, "frametime", 1);
        if (frameTime != 1) {
            Validate.inclusiveBetween(1L, Integer.MAX_VALUE, frameTime, "Invalid default frame time");
        }
        if (json.has("frames")) {
            try {
                JsonArray array = net.minecraft.class_3518.method_15261(json, "frames");
                for (int i = 0; i < array.size(); i++) {
                    net.minecraft.class_1080 frame = this.method_4693(i, array.get(i));
                    if (frame != null) {
                        frames.add(frame);
                    }
                }
            } catch (ClassCastException e) {
                throw new JsonParseException("Invalid animation->frames: expected array, was " + json.get("frames"), e);
            }
        }
        int width = net.minecraft.class_3518.method_15282(json, "width", -1);
        int height = net.minecraft.class_3518.method_15282(json, "height", -1);
        if (width != -1) {
            Validate.inclusiveBetween(1L, Integer.MAX_VALUE, width, "Invalid width");
        }
        if (height != -1) {
            Validate.inclusiveBetween(1L, Integer.MAX_VALUE, height, "Invalid height");
        }
        boolean interpolate = net.minecraft.class_3518.method_15258(json, "interpolate", false);
        return CbTextureMetadata.animation(frames.build(), width, height, frameTime, interpolate);
    }

    public net.minecraft.class_1080 method_4693(int i, JsonElement element) {
        if (element.isJsonPrimitive()) {
            return new net.minecraft.class_1080(net.minecraft.class_3518.method_15257(element, "frames[" + i + "]"));
        }
        if (element.isJsonObject()) {
            JsonObject o = net.minecraft.class_3518.method_15295(element, "frames[" + i + "]");
            int time = net.minecraft.class_3518.method_15282(o, "time", -1);
            if (o.has("time")) {
                Validate.inclusiveBetween(1L, Integer.MAX_VALUE, time, "Invalid frame time");
            }
            int index = net.minecraft.class_3518.method_15260(o, "index");
            Validate.inclusiveBetween(0L, Integer.MAX_VALUE, index, "Invalid frame index");
            return CbTextureMetadata.frame(index, time);
        }
        return null;
    }

    /** getMetadataSectionName() */
    @Override
    public String method_14420() {
        return "animation";
    }

    @Override
    public net.minecraft.class_1079 method_14421(JsonObject json) {
        return this.method_4692(json);
    }
}
