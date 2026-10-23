package top.dext.centurybridge.step.v1_20_5.compat.fapi.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Fabric API 0.97.3's (1.20.4) fabric-containers-v0 ContainerScreenFactory: the screen of a container.
 */
@Deprecated
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ContainerScreenFactory<C extends net.minecraft.class_1703> {

    net.minecraft.class_465 create(C container);
}
