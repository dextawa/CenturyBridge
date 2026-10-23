package top.dext.centurybridge.step.v1_20_5.compat.fapi.datagen.v1.provider;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

/**
 * Fabric API 0.97.3's (1.20.4) FabricTagProvider.GameEventTagProvider, removed in 1.20.5: a tag provider for
 * game events (classRenames of FabricTagProvider$GameEventTagProvider). 1.20.4 reversed a game event to its key
 * through the event's own registry entry, which 1.20.5's GameEvent no longer carries; the game event registry
 * gives the same key.
 */
public abstract class GameEventTagProvider extends FabricTagProvider<net.minecraft.class_5712> {

    public GameEventTagProvider(FabricDataOutput output, CompletableFuture<net.minecraft.class_7225.class_7874> completableFuture) {
        super(output, net.minecraft.class_7924.field_41273, completableFuture);
    }

    @Override
    protected net.minecraft.class_5321<net.minecraft.class_5712> reverseLookup(net.minecraft.class_5712 element) {
        return net.minecraft.class_7923.field_41171.method_29113(element)
            .orElseThrow(() -> new IllegalArgumentException("Game event is not registered: " + element));
    }
}
