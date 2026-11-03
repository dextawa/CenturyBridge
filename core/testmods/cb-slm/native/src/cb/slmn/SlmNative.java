package cb.slmn;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Mob;

/** spawns a slime and a magma cube, ticks each 5 times, prints the counters: "CB-SLM <class> native=.. convField=.. convHead=.." */
public class SlmNative implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ServerLevel level = server.overworld();
            try {
                level.getChunk(0, 0);
                for (EntityType<? extends Mob> t : java.util.List.<EntityType<? extends Mob>>of(EntityTypes.SLIME, EntityTypes.MAGMA_CUBE)) {
                    Mob e = t.create(level, EntitySpawnReason.COMMAND);
                    e.setPos(8.5, 200, 8.5);
                    level.addFreshEntity(e);
                    for (int i = 0; i < 5; i++) {
                        e.tick();
                    }
                    String c = e.getClass().getSimpleName();
                    System.out.println("CB-SLM " + c + " native=" + get("native", c) + " convField=" + get("convField", c) + " convHead=" + get("convHead", c) + " convInvoke=" + get("convInvoke", c));
                    e.discard();
                }
            } catch (RuntimeException ex) {
                System.out.println("CB-SLM FAIL " + ex);
                ex.printStackTrace(System.out);
            }
            System.out.println("CB-SLM END");
        });
    }

    static Object get(String what, String cls) {
        return System.getProperties().getOrDefault("cb.slm." + what + "." + cls, 0);
    }
}
