package top.dext.centurybridge.step.v26_2.rt;

import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;

/**
 * 26.1.2 AtlasIds.SIGNS (minecraft:signs, the atlas atlases/signs.json built from entity/signs): 26.2 has no signs
 * atlas; the layer lists entity/signs in the blocks atlas (resources/assets/minecraft/atlases/blocks.json), so the
 * sign sprites' atlas is AtlasIds.BLOCKS (rt/CbS26SignsClient.SIGN_SHEET is its texture).
 */
public final class CbS26Atlases {
    private CbS26Atlases() {}

    public static Identifier AtlasIds_SIGNS() {
        return AtlasIds.BLOCKS;
    }
}
