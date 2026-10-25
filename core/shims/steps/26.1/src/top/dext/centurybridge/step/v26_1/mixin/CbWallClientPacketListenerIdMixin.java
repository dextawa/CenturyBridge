package top.dext.centurybridge.step.v26_1.mixin;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * 1.21.11 ClientPacketListener.getId() (class_634.method_16690): a random UUID drawn when the listener was built
 * (class_634.field_16771), the same for the listener's life. 1.21.11's own code read it nowhere else; 26.1 dropped it.
 */
@Environment(EnvType.CLIENT)
@Mixin(ClientPacketListener.class)
public abstract class CbWallClientPacketListenerIdMixin {

    @Unique
    private final UUID cb$id = UUID.randomUUID();

    /** 1.21.11 ClientPacketListener.getId() */
    public UUID getId() {
        return this.cb$id;
    }
}
