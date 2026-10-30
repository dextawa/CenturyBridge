package top.dext.centurybridge.step.v1_21_11.rt;

import net.minecraft.class_12206;
import net.minecraft.class_1937;

/**
 * RespawnAnchorBlock.canSetSpawn(Level) (1.21.10 class_4969.method_27353(class_1937)): 1.21.10 answered the level's
 * dimension type flag respawnAnchorWorks (class_2874.comp_649). 1.21.11 dropped the flag and reads the environment
 * attribute gameplay/respawn_anchor_works (class_12206.field_63757, default false) per position
 * (method_27353(ServerLevel, BlockPos): environmentAttributes().getValue(attr, pos)); a dimension type carries its
 * value in its attribute map (comp_5146, the dimension layer of EnvironmentAttributeSystem). Without a position the
 * level's answer is the position-free value (EnvironmentAttributeSystem.getValue(attr), method_75694): the dimension's,
 * which is what 1.21.10's flag was (javap -c -p of both code jars).
 */
public final class CbRespawnAnchor {
    private CbRespawnAnchor() {
    }

    public static boolean method_27353(class_1937 level) {
        return level.method_75728().method_75694(class_12206.field_63757);
    }
}
