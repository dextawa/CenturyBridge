package cb.smx6.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** a 1.20.6 mod's accessors on Mob's private leash fields (leashHolder field_6202, delayedLeashInfo field_6192) */
@Mixin(net.minecraft.class_1308.class)
public interface LeashFieldAccessor {
    @Accessor("field_6202")
    net.minecraft.class_1297 cbsmx6$holder();

    @Accessor("field_6192")
    com.mojang.datafixers.util.Either<java.util.UUID, net.minecraft.class_2338> cbsmx6$delayed();

    @Accessor("field_6192")
    void cbsmx6$setDelayed(com.mojang.datafixers.util.Either<java.util.UUID, net.minecraft.class_2338> info);
}
