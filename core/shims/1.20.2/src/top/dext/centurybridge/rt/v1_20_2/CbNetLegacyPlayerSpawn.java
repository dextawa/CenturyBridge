package top.dext.centurybridge.rt.v1_20_2;

import net.minecraft.class_2604;

/**
 * The 1.20.1 ClientPlayPacketListener.onPlayerSpawn(PlayerSpawnS2CPacket), with the packet type
 * 1.20.2 spawns players with (the converter renames PlayerSpawnS2CPacket to EntitySpawnS2CPacket).
 * A ClientPlayNetworkHandler subclass overriding it is given this interface; the onEntitySpawn
 * vanilla now calls for every spawn reaches it for players (CbNet.cbSpawnEntity).
 */
public interface CbNetLegacyPlayerSpawn {

    void method_11097(class_2604 packet);
}
