package top.dext.centurybridge.step.v1_21.rt;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.class_1297;
import net.minecraft.class_2596;
import net.minecraft.class_3218;
import net.minecraft.class_3231;

/**
 * An entity's spawn packet: 1.20.6's getAddEntityPacket() built it from the entity itself; 1.21's
 * getAddEntityPacket(serverEntity) reads the position, rotation and motion its tracker last sent. A 1.20.6 call
 * hands it the entity's own tracker when the server tracks it, and otherwise a fresh one -- which takes the
 * entity's current position, rotations and motion, exactly what 1.20.6's packet carried.
 */
public final class CbEntityPackets {

    /** what the layer's ChunkMap and TrackedEntity accessors expose */
    public interface TrackedEntities {
        Int2ObjectMap<?> cb$entityMap();
    }

    public interface TrackedServerEntity {
        class_3231 cb$serverEntity();
    }

    /** the entity's tracker as 1.21 hands it to getAddEntityPacket, for a 1.20.6 call that has none */
    public static class_3231 serverEntityFor(class_1297 entity) {
        if (entity.method_37908() instanceof class_3218 level) {
            Object tracked = ((TrackedEntities) level.method_14178().field_17254).cb$entityMap().get(entity.method_5628());
            if (tracked instanceof TrackedServerEntity t && t.cb$serverEntity() != null) {
                return t.cb$serverEntity();
            }
            return new class_3231(level, entity, entity.method_5864().method_18388(), entity.method_5864().method_18389(), packet -> { });
        }
        // a client-side entity: the tracker only stores the level, and the spawn packet never reads it
        return new class_3231(null, entity, entity.method_5864().method_18388(), entity.method_5864().method_18389(), packet -> { });
    }

    /** 1.20.6 entity.getAddEntityPacket(): 1.21's, asked virtually, with the entity's tracker */
    public static class_2596<?> getAddEntityPacket(class_1297 entity) {
        return entity.method_18002(serverEntityFor(entity));
    }

    private CbEntityPackets() {
    }
}
