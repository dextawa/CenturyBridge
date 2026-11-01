package top.dext.centurybridge.step.v1_21_5.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.4's com.mojang.blaze3d.pipeline.RenderCall (net/minecraft/class_4573), gone in 1.21.5 with RenderSystem's
 * render-call queue. Converted references (and lambdas implementing it) name this interface (classRenames); 1.21.4's
 * only member, execute().
 */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface RenderCall {
    void execute();
}
