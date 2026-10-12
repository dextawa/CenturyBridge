package top.dext.centurybridge.facade.v1_20_2;

/**
 * 1.20.1 BeeDebugRenderer.BeeInfo (net/minecraft/class_4703$class_5243): a bee's debug state, its goals and
 * blacklisted hives filled in after construction. 1.20.2 carries it in BeeDebugPayload.BeeInfo, a record
 * (a Vec3 position, the goals a Set, the blacklist a List); CbGuiDebug converts between the two. Reached
 * through classRenames. The bodies are 1.20.1's.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class class_4703$class_5243 {

    /** uuid */
    public final java.util.UUID field_24322;
    /** id */
    public final int field_24323;
    /** pos */
    public final net.minecraft.class_2374 field_24324;
    /** path */
    public final net.minecraft.class_11 field_24325;
    /** hivePos */
    public final net.minecraft.class_2338 field_24326;
    /** flowerPos */
    public final net.minecraft.class_2338 field_24327;
    /** travelTicks */
    public final int field_24328;
    /** goals */
    public final java.util.List<String> field_24329 = new java.util.ArrayList<>();
    /** blacklistedHives */
    public final java.util.Set<net.minecraft.class_2338> field_24330 = new java.util.HashSet<>();

    public class_4703$class_5243(java.util.UUID uuid, int id, net.minecraft.class_2374 pos, net.minecraft.class_11 path,
                                 net.minecraft.class_2338 hivePos, net.minecraft.class_2338 flowerPos, int travelTicks) {
        this.field_24322 = uuid;
        this.field_24323 = id;
        this.field_24324 = pos;
        this.field_24325 = path;
        this.field_24326 = hivePos;
        this.field_24327 = flowerPos;
        this.field_24328 = travelTicks;
    }

    /** hasHive(pos) */
    public boolean method_27649(net.minecraft.class_2338 pos) {
        return this.field_24326 != null && this.field_24326.equals(pos);
    }

    /** getUuid() */
    public java.util.UUID method_27648() {
        return this.field_24322;
    }

    /** getName() */
    public String method_27650() {
        return net.minecraft.class_4617.method_23267(this.field_24322);
    }

    @Override
    public String toString() {
        return this.method_27650();
    }

    /** hasFlower() */
    public boolean method_27651() {
        return this.field_24327 != null;
    }
}
