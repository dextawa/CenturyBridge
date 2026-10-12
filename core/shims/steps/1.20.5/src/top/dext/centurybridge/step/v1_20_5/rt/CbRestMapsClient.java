package top.dext.centurybridge.step.v1_20_5.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Client side of 1.20.4's map API on 1.20.5: a MapRenderer 1.20.4 built from a texture manager alone
 * (it drew decorations from the fixed map icon texture) takes 1.20.5's decoration sprite manager as
 * well -- the game's own, the one 1.20.5's renderer draws with.
 */
@Environment(EnvType.CLIENT)
public final class CbRestMapsClient {

    /** the game's map decoration sprites (Minecraft.getMapDecorationTextures()) */
    public static net.minecraft.class_9443 decorationTextures() {
        return net.minecraft.class_310.method_1551().method_58476();
    }

    private CbRestMapsClient() {
    }
}
