package top.dext.centurybridge.rt.v1_20_2;

/**
 * The 1.20.1 shape of BeeDebugRenderer.addOrUpdateHiveInfo(hiveInfo) (1.20.2's takes the hive's record and the
 * time it was seen), given to a mod subclass overriding it so that the 1.20.2 shape reaches its override
 * (CbGuiDebug.cbAddHive).
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbGuiLegacyBeeRenderer {

    void method_23807(top.dext.centurybridge.facade.v1_20_2.class_4703$class_4705 hive);
}
