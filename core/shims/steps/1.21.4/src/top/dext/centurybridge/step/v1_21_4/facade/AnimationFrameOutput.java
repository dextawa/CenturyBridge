package top.dext.centurybridge.step.v1_21_4.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** 1.21.3 AnimationMetadataSection.FrameOutput (class_1079$class_5792, gone in 1.21.4): forEachFrame's callback */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface AnimationFrameOutput {
    void accept(int index, int time);
}
