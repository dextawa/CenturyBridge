package cb.skspawn.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Kilt's SpawnPlacementsDataAccessor.getPlacement: an @Accessor on 1.20.4's SpawnPlacements.Data.field_6315 (the Type enum) */
@Mixin(net.minecraft.class_1317.class_1318.class)
public interface DataAccessor {
    @Accessor(value = "field_6315", remap = false)
    net.minecraft.class_1317.class_1319 cbskspawn$placement();
}
