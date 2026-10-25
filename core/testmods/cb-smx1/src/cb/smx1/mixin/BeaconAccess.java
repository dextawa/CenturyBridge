package cb.smx1.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** BeaconBlockEntity.primaryPower (field_11795; the same field on 1.20.2) */
@Mixin(net.minecraft.class_2580.class)
public interface BeaconAccess {
    @Accessor(value = "field_11795", remap = false)
    net.minecraft.class_1291 cbsmx1$primary();

    @Accessor(value = "field_11795", remap = false)
    void cbsmx1$setPrimary(net.minecraft.class_1291 effect);
}
