package top.dext.centurybridge.step.v1_21_2.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestRealms;

/**
 * CreateSnapshotRealmTask (class_8844) of 1.21.1: created a snapshot realm from a parent realm and
 * regenerated its world from a seed -- requests 1.21.2 removed. Its run reports the task's error (the
 * message CbRestRealms gives) as a failed creation did, and sends nothing.
 */
@Environment(EnvType.CLIENT)
public class CreateSnapshotRealmTask extends net.minecraft.class_4358 {

    private static final net.minecraft.class_2561 TITLE = net.minecraft.class_2561.method_43471("mco.snapshot.creating");

    public final long field_46712;
    public final WorldGenerationInfo field_46713;
    public final String field_46714;
    public final String field_46715;
    public final net.minecraft.class_4325 field_46716;

    public CreateSnapshotRealmTask(net.minecraft.class_4325 realmsMainScreen, long parentId, WorldGenerationInfo info,
            String name, String description) {
        this.field_46712 = parentId;
        this.field_46713 = info;
        this.field_46714 = name;
        this.field_46715 = description;
        this.field_46716 = realmsMainScreen;
    }

    @Override
    public void run() {
        this.method_53809(CbRestRealms.seedResetRemoved());
    }

    @Override
    public net.minecraft.class_2561 method_53808() {
        return TITLE;
    }

    @Override
    public void method_21071() {
        super.method_21071();
    }
}
