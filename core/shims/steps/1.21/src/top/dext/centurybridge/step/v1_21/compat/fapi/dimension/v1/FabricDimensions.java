package top.dext.centurybridge.step.v1_21.compat.fapi.dimension.v1;

import com.google.common.base.Preconditions;
import net.minecraft.class_1297;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_5454;
import top.dext.centurybridge.step.v1_21.rt.CbEntityPortals;

/**
 * Fabric API 1.20.6's FabricDimensions (fabric-dimensions-v1; 1.21's module dropped the class, the game moves an
 * entity by a DimensionTransition itself). Renamed here (classRenames, s6fin-fapi.json); 1.20.6's body: a target is
 * required, server side only, on the main server thread; within the entity's own level the entity is moved there
 * (a player through its connection's teleport), its motion and head rotation set from the target; to another level
 * it changes dimension with the target as its entry point (1.20.6's changeDimension, CbEntityPortals).
 */
public final class FabricDimensions {
    private FabricDimensions() {
        throw new AssertionError();
    }

    @SuppressWarnings("unchecked")
    public static <E extends class_1297> E teleport(E entity, class_3218 destination, class_5454 target) {
        Preconditions.checkNotNull(target, "A target must be provided");
        Preconditions.checkState(!entity.method_37908().field_9236, "Entities can only be teleported on the server side");
        Preconditions.checkArgument(!entity.method_37908().field_9236, "Entities can only be teleported on the server side");
        Preconditions.checkArgument(Thread.currentThread() == ((class_3218) entity.method_37908()).method_8503().method_3777(),
            "Entities must be teleported from the main server thread");
        net.minecraft.class_243 pos = CbEntityPortals.portalPos(target);
        float yRot = CbEntityPortals.portalYRot(target);
        float xRot = CbEntityPortals.portalXRot(target);
        if (entity.method_37908() == destination) {
            if (entity instanceof class_3222 player) {
                player.field_13987.method_14363(pos.field_1352, pos.field_1351, pos.field_1350, yRot, xRot);
            } else {
                entity.method_5808(pos.field_1352, pos.field_1351, pos.field_1350, yRot, xRot);
            }
            entity.method_18799(CbEntityPortals.portalSpeed(target));
            entity.method_5847(yRot);
            return entity;
        }
        return (E) CbEntityPortals.legacyChangeDimensionTo(entity, destination, target);
    }
}
