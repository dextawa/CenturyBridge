package top.dext.centurybridge.step.v1_21_2.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's Effect (net/minecraft/class_5936, classRenames), gone in 1.21.2: the marker a post-processing program
 * (EffectInstance) carries, a Shader its EffectPrograms attach to.
 */
@Environment(EnvType.CLIENT)
public interface Effect extends Shader {
}
