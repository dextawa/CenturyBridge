package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** PlayerDataStorage's player data directory, for 1.20.4's getSeenPlayers (CbLevelStorage) */
@Mixin(net.minecraft.class_29.class)
public interface CbLevelStoragePlayerDataAccessor {

    /** playerDir */
    @Accessor("field_144")
    java.io.File cbS4$playerDir();
}
