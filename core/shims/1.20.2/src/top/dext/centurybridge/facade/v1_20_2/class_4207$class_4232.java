package top.dext.centurybridge.facade.v1_20_2;

/**
 * 1.20.1 BrainDebugRenderer.BrainDump (net/minecraft/class_4207$class_4232): a mob's brain debug state, its
 * lists and point-of-interest sets filled in after construction. 1.20.2 carries it in
 * BrainDebugPayload.BrainDump, a record of the same values (a Vec3 position); CbGuiDebug converts between
 * the two. Reached through classRenames. The bodies are 1.20.1's; hasPoi / hasPotentialPoi were
 * package-private there and are public here, as every member a facade stands in with must be.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class class_4207$class_4232 {

    /** uuid */
    public final java.util.UUID field_18923;
    /** id */
    public final int field_18924;
    /** name */
    public final String field_19328;
    /** profession */
    public final String field_18925;
    /** xp */
    public final int field_19329;
    /** health */
    public final float field_22406;
    /** maxHealth */
    public final float field_22407;
    /** pos */
    public final net.minecraft.class_2374 field_18926;
    /** inventory */
    public final String field_19372;
    /** path */
    public final net.minecraft.class_11 field_19330;
    /** wantsGolem */
    public final boolean field_19373;
    /** angerLevel */
    public final int field_38348;
    /** activities */
    public final java.util.List<String> field_18927 = new java.util.ArrayList<>();
    /** behaviors */
    public final java.util.List<String> field_18928 = new java.util.ArrayList<>();
    /** memories */
    public final java.util.List<String> field_19374 = new java.util.ArrayList<>();
    /** gossips */
    public final java.util.List<String> field_19375 = new java.util.ArrayList<>();
    /** pois */
    public final java.util.Set<net.minecraft.class_2338> field_18930 = new java.util.HashSet<>();
    /** potentialPois */
    public final java.util.Set<net.minecraft.class_2338> field_25287 = new java.util.HashSet<>();

    public class_4207$class_4232(java.util.UUID uuid, int id, String name, String profession, int xp, float health,
                                 float maxHealth, net.minecraft.class_2374 pos, String inventory, net.minecraft.class_11 path,
                                 boolean wantsGolem, int angerLevel) {
        this.field_18923 = uuid;
        this.field_18924 = id;
        this.field_19328 = name;
        this.field_18925 = profession;
        this.field_19329 = xp;
        this.field_22406 = health;
        this.field_22407 = maxHealth;
        this.field_18926 = pos;
        this.field_19372 = inventory;
        this.field_19330 = path;
        this.field_19373 = wantsGolem;
        this.field_38348 = angerLevel;
    }

    /** hasPoi(pos) */
    public boolean method_23151(net.minecraft.class_2338 pos) {
        return this.field_18930.stream().anyMatch(pos::equals);
    }

    /** hasPotentialPoi(pos) */
    public boolean method_29388(net.minecraft.class_2338 pos) {
        return this.field_25287.contains(pos);
    }

    /** getUuid() */
    public java.util.UUID method_23149() {
        return this.field_18923;
    }
}
