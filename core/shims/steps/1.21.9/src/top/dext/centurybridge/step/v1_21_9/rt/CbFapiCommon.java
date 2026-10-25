package top.dext.centurybridge.step.v1_21_9.rt;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_3218;
import net.minecraft.class_3300;
import net.minecraft.class_3302;

/**
 * Fabric API common members 1.21.9's Fabric API (0.134) reshaped or dropped, for 1.21.8 code (javap of both releases'
 * modules: fabric-api-base's NbtType, fabric-entity-events-v1, fabric-resource-loader-v0).
 */
public final class CbFapiCommon {
    private CbFapiCommon() {
    }

    /** NbtType.NUMBER: 0.136's constant (99, the "any numeric tag" type id vanilla's getTagType checks accept) */
    public static int NUMBER() {
        return 99;
    }

    /** the state of a resource manager, for a 0.136 call that passed the manager (1.21.9's reload gives one per reload) */
    public static class_3302.class_11558 state(class_3300 manager) {
        return new class_3302.class_11558(manager);
    }

    // ------------------------------------------------------------------ ServerEntityCombatEvents.AfterKilledOtherEntity

    /** a 0.136 listener: afterKilledOtherEntity(world, entity, killed) (0.134 adds the damage source) */
    public interface LegacyAfterKilledOtherEntity {
        void afterKilledOtherEntity(class_3218 world, class_1297 entity, class_1309 killed);
    }

    public static ServerEntityCombatEvents.AfterKilledOtherEntity adaptAfterKilledOtherEntity(LegacyAfterKilledOtherEntity fn) {
        return (world, entity, killed, source) -> fn.afterKilledOtherEntity(world, entity, killed);
    }

    /** a 0.136 call of the listener: the killed entity's last damage source (what killed it), else a generic one */
    public static class_1282 killingSource(class_1309 killed) {
        class_1282 source = killed.method_6081();
        return source != null ? source : killed.method_48923().method_48830();
    }
}
