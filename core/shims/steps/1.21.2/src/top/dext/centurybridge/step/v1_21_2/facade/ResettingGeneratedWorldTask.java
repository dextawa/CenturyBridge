package top.dext.centurybridge.step.v1_21_2.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestRealms;

/**
 * ResettingGeneratedWorldTask (class_5673) of 1.21.1: a world reset task whose request was the seed reset
 * 1.21.2 removed. Its request answers with the Realms error CbRestRealms gives (shown by the task's error
 * screen, as any failed reset is) and is never sent.
 */
@Environment(EnvType.CLIENT)
public class ResettingGeneratedWorldTask extends net.minecraft.class_4440 {

    public final WorldGenerationInfo field_27951;

    public ResettingGeneratedWorldTask(WorldGenerationInfo info, long worldId, net.minecraft.class_2561 title, Runnable callback) {
        super(worldId, title, callback);
        this.field_27951 = info;
    }

    @Override
    public void method_32517(net.minecraft.class_4341 client, long worldId) throws net.minecraft.class_4355 {
        throw CbRestRealms.seedResetRemoved();
    }
}
