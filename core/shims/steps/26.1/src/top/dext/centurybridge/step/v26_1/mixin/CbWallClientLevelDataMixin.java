package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v26_1.rt.CbWallLevelDataClient;

/** 1.21.11 ClientLevelData's raining flag (field_24440), which 26.1's ClientLevelData no longer has */
@Mixin(ClientLevel.ClientLevelData.class)
public abstract class CbWallClientLevelDataMixin implements CbWallLevelDataClient.RainFlag {

    @Unique
    private boolean cb$raining;

    @Override
    public boolean cb$raining() {
        return this.cb$raining;
    }

    @Override
    public void cb$setRaining(boolean raining) {
        this.cb$raining = raining;
    }
}
