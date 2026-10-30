package top.dext.centurybridge.step.v1_21_11.rt;

import java.util.List;
import net.minecraft.class_12195;
import net.minecraft.class_12206;
import net.minecraft.class_1937;
import net.minecraft.class_3218;
import net.minecraft.server.MinecraftServer;

/** 1.21.10 members 1.21.11 dropped whose answer 1.21.11 computes elsewhere (javap -c -p of both code jars). */
public final class CbLegacyRules {
    private CbLegacyRules() {
    }

    /** Util.lastOf(List) (1.21.10 class_156.method_20793): list.get(list.size() - 1); 1.21.11 has no such helper */
    public static <T> T method_20793(List<T> list) {
        return list.get(list.size() - 1);
    }

    /**
     * MinecraftServer.isLevelEnabled(Level) (1.21.10 method_74057): a nether level answers the server's
     * allowEnteringNetherUsingPortals rule, any other true. 1.21.11 asks ServerLevel.method_75002(Level) of the
     * portal's source level with the same body over that level's game rules; the server's rules of 1.21.10 were the
     * overworld's (CbGameRules.method_74056 reads them the same way). Before any level exists: true unless nether.
     */
    public static boolean method_74057(MinecraftServer server, class_1937 level) {
        class_3218 overworld = server.method_30002();
        if (overworld != null) {
            return overworld.method_75002(level);
        }
        return level.method_27983() != class_1937.field_25180;
    }

    /**
     * BedBlock.canSetSpawn(Level) (1.21.10 class_2244.method_27352: the dimension type's bedWorks flag). 1.21.11 reads
     * the bed_rule environment attribute (class_12206.field_63756, a BedRule) and asks its canSetSpawn rule
     * (class_12195.method_75644, what ServerPlayer's respawn asks); the overworld default rule sets spawn always, the
     * nether's never, as bedWorks answered. Position-free value of the level, as 1.21.10's flag had no position.
     */
    public static boolean method_27352(class_1937 level) {
        class_12195 rule = level.method_75728().method_75694(class_12206.field_63756);
        return rule.method_75644(level);
    }
}
