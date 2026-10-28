package cb.smx6;

/** what LeashSaveMixin gives the driver: Mob's private 1.20.6 leash fields through its @Shadow fields */
public interface LeashSaveAccess {
    net.minecraft.class_1297 cbsmx6$shadowHolder();

    com.mojang.datafixers.util.Either<java.util.UUID, net.minecraft.class_2338> cbsmx6$shadowDelayed();

    void cbsmx6$shadowSetDelayed(com.mojang.datafixers.util.Either<java.util.UUID, net.minecraft.class_2338> info);
}
