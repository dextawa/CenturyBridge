package top.dext.centurybridge.facade.v1_20_2;

/**
 * 1.20.1 BeeDebugRenderer.HiveInfo (net/minecraft/class_4703$class_4705): a hive's debug state and the game
 * time it was last seen. 1.20.2 carries the state in HiveDebugPayload.HiveInfo, a record, and
 * BeeDebugRenderer.addOrUpdateHiveInfo takes the time beside it; CbGuiDebug converts between the two.
 * Reached through classRenames.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class class_4703$class_4705 {

    /** pos */
    public final net.minecraft.class_2338 field_21543;
    /** hiveType */
    public final String field_21544;
    /** occupantCount */
    public final int field_21545;
    /** honeyLevel */
    public final int field_21625;
    /** sedated */
    public final boolean field_21626;
    /** lastSeen */
    public final long field_21546;

    public class_4703$class_4705(net.minecraft.class_2338 pos, String hiveType, int occupantCount, int honeyLevel,
                                 boolean sedated, long lastSeen) {
        this.field_21543 = pos;
        this.field_21544 = hiveType;
        this.field_21545 = occupantCount;
        this.field_21625 = honeyLevel;
        this.field_21626 = sedated;
        this.field_21546 = lastSeen;
    }
}
