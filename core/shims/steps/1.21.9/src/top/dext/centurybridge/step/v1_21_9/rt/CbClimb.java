package top.dext.centurybridge.step.v1_21_9.rt;

import net.minecraft.class_11580;
import net.minecraft.class_11897;
import net.minecraft.class_1299;
import net.minecraft.class_1928;
import net.minecraft.class_2487;
import net.minecraft.class_3806;
import net.minecraft.class_5434;
import net.minecraft.class_5455;
import net.minecraft.class_9279;
import net.minecraft.server.MinecraftServer;

/**
 * 1.21.8 calls whose 1.21.9 shape takes the same value in another form, or a value 1.21.8 code had no part in (the
 * climbed layers reach these: riding, chunk serialization, jigsaw structures, game tests, bee occupants, the
 * dedicated server's allow-nether).
 */
public final class CbClimb {

    private CbClimb() {
    }

    /**
     * Entity.startRiding(vehicle, force): 1.21.9's third argument sends the mount game event and the player riding
     * triggers. 1.21.8 always did both (addPassenger sent the event, startRiding ran the triggers); only 1.21.9's
     * own re-mount after a dimension change passes false
     */
    public static boolean sendEvents() {
        return true;
    }

    /** SerializableChunkData.parse(level, registries, tag): 1.21.9 takes the containers' factory built from the same registries */
    public static class_11897 containers(class_5455 registries) {
        return class_11897.method_74159(registries);
    }

    /** JigsawStructure's maxDistanceFromCenter: 1.21.8's one distance bounded both axes; 1.21.9's MaxDistance(d) is (d, d) */
    public static class_5434.class_11600 maxDistance(int distance) {
        return new class_5434.class_11600(distance);
    }

    /** new GameTestRunner(..): 1.21.9's last flag (clear space between batches) is off unless asked for, as its builder has it */
    public static boolean no() {
        return false;
    }

    // ------------------------------------------------------------------ BeehiveBlockEntity.Occupant

    /**
     * Occupant.entityData: 1.21.8's CustomData held the bee's whole saved tag, its "id" included; 1.21.9's
     * TypedEntityData keeps the type beside the tag. A tag without a known id is a bee's (the hive's only
     * occupant, as 1.21.8's Occupant.create(ticks) wrote)
     */
    public static class_11580<class_1299<?>> toTyped(class_9279 data) {
        class_2487 tag = data.method_57461();
        class_1299<?> type = tag.method_10558("id").flatMap(class_1299::method_5898).orElse(class_1299.field_20346);
        return class_11580.method_72535(type, tag);
    }

    /** Occupant.entityData() for 1.21.8 code: the saved tag with its "id", as 1.21.8 stored it */
    public static class_9279 toCustom(class_11580<class_1299<?>> data) {
        class_2487 tag = data.method_72540();
        tag.method_10582("id", class_1299.method_5890(data.method_72530()).toString());
        return class_9279.method_57456(tag);
    }

    // ------------------------------------------------------------------ DedicatedServerProperties.allowNether

    /**
     * 1.21.8's allow-nether property is 1.21.9's allowNether game rule (the server reads only the rule); before the
     * server is up, 1.21.8's default
     */
    public static boolean field_16811(class_3806 properties) {
        MinecraftServer s = CbProfiles.cbServer();
        return s == null || s.method_3767().method_8355(class_1928.field_62435);
    }
}
