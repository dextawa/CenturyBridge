package top.dext.centurybridge.step.v1_21_4.facade;

import com.google.gson.JsonObject;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** 1.21.3 ModelTemplate.JsonFactory (class_4942$class_8073, gone in 1.21.4): a model's JSON from its id and textures */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ModelTemplateJsonFactory {
    JsonObject create(net.minecraft.class_2960 id, Map<net.minecraft.class_4945, net.minecraft.class_2960> textures);
}
