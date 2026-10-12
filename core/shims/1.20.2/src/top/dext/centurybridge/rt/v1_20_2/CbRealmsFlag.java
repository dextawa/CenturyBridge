package top.dext.centurybridge.rt.v1_20_2;

/** Minecraft's 1.20.1 "connected to Realms" flag (CbGuiMinecraftBridge), forgotten where 1.20.1 cleared it. */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbRealmsFlag {

    void cb$forgetRealmsFlag();
}
