package top.dext.centurybridge.rt.v1_20_2;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;

/**
 * The 1.20.1 skin getters of PlayerListEntry, given to a mod subclass overriding any of them so that
 * 1.20.2's getSkinTextures() is answered from them (CbNetClient.cbSkin). The ones the subclass does
 * not override are implemented by the 1.20.1 bodies CbNetPlayerListEntryBridge puts back.
 */
@Environment(EnvType.CLIENT)
public interface CbNetLegacySkin {

    class_2960 method_2968();

    class_2960 method_2979();

    class_2960 method_2957();

    String method_2977();
}
