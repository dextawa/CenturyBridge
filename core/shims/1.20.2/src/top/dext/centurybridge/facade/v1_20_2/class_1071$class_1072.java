package top.dext.centurybridge.facade.v1_20_2;

/**
 * 1.20.1 SkinManager.SkinTextureCallback (net/minecraft/class_1071$class_1072), removed with 1.20.2's
 * future-based skin loading: called once per texture SkinManager.registerSkins loaded (skin, cape) with
 * its type, its texture id and the profile texture it came from. The 1.20.1 interface; the 1.20.1
 * registerSkins rebuilt by CbGuiSkinManagerBridge calls it as 1.20.1 did. Reached through classRenames.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@FunctionalInterface
public interface class_1071$class_1072 {

    void onSkinTextureAvailable(com.mojang.authlib.minecraft.MinecraftProfileTexture.Type type, net.minecraft.class_2960 location,
                                com.mojang.authlib.minecraft.MinecraftProfileTexture texture);
}
