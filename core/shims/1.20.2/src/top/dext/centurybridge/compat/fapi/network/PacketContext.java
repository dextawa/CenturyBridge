package top.dext.centurybridge.compat.fapi.network;

/**
 * Fabric API networking v0's PacketContext: the side a payload arrived on, the player it concerns and
 * the thread executor to hand work to (the payload itself is received off the game thread).
 */
public interface PacketContext {

    net.fabricmc.api.EnvType getPacketEnvironment();

    net.minecraft.class_1657 getPlayer();

    net.minecraft.class_1255<?> getTaskQueue();
}
