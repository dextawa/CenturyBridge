package top.dext.centurybridge.step.v1_21.compat.fapi.client.model;

import java.util.function.Consumer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Fabric API 1.20.6's ModelAppender (fabric-models-v0, gone from 1.21's Fabric API, deprecated before): hands
 * model locations to load and bake. Renamed here (classRenames, s6fin-fapi.json); CbFapiModels.
 */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ModelAppender {
    void appendAll(net.minecraft.class_3300 manager, Consumer<net.minecraft.class_1091> out);
}
