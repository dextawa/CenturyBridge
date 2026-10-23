package top.dext.centurybridge.step.v1_21_4.facade;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_4.rt.CbTextureMetadata;

/** 1.21.3 VillagerMetadataSectionSerializer (class_3890, gone in 1.21.4): the "villager" section's reader, body for body */
@Environment(EnvType.CLIENT)
public class VillagerMetadataSectionSerializer implements MetadataSectionSerializer<net.minecraft.class_3888> {

    public VillagerMetadataSectionSerializer() {
    }

    /** fromJson(json) */
    public net.minecraft.class_3888 method_17171(JsonObject json) {
        return new net.minecraft.class_3888(CbTextureMetadata.method_17170(net.minecraft.class_3518.method_15253(json, "hat", "none")));
    }

    /** getMetadataSectionName() */
    @Override
    public String method_14420() {
        return "villager";
    }

    @Override
    public net.minecraft.class_3888 method_14421(JsonObject json) {
        return this.method_17171(json);
    }
}
