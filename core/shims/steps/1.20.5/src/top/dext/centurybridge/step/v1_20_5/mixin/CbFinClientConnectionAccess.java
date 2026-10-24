package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/** ClientPacketListener: its scoreboard, replaceable for 1.20.4's ClientLevel.setScoreboard (CbFinClientLevelMixin) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_634.class)
public interface CbFinClientConnectionAccess {

    @Mutable
    @Accessor("field_47878")
    void cbS4$setScoreboard(net.minecraft.class_269 scoreboard);
}
