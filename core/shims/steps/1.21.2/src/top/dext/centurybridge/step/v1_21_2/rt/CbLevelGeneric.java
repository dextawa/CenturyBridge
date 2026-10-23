package top.dext.centurybridge.step.v1_21_2.rt;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_3499;
import net.minecraft.class_8965;

/**
 * Generic drift in the level area at 1.21.1 -> 1.21.2: members whose erased shape stayed while their type arguments
 * changed.
 * <ul>
 * <li>StructurePoolElement.getShuffledJigsawBlocks(..): 1.21.1 answered the jigsaw blocks as StructureBlockInfos,
 *     1.21.2 as JigsawBlockInfos (the block info with its jigsaw fields parsed from the tag). A 1.21.1 call gets the
 *     block infos (callReshapes); a 1.21.1 override's answer reaches 1.21.2 parsed (JigsawBlockInfo.of, convertHooks).
 *     Both converters pass an element already of the wanted kind, so each undoes the other.</li>
 * <li>TrialSpawnerConfig.CODEC: 1.21.1's codec of the config itself, which 1.21.2 keeps as DIRECT_CODEC (the same
 *     fields) while CODEC became the codec of a holder.</li>
 * </ul>
 */
public final class CbLevelGeneric {
    private CbLevelGeneric() {
    }

    /** 1.21.2's jigsaw block list as 1.21.1's: each JigsawBlockInfo's block info */
    public static List<?> blockInfos(List<?> jigsaws) {
        List<Object> out = new ArrayList<>(jigsaws.size());
        for (Object o : jigsaws) {
            out.add(o instanceof class_3499.class_10326 j ? j.comp_3278() : o);
        }
        return out;
    }

    /** a 1.21.1 override's block info list as 1.21.2's: each parsed into a JigsawBlockInfo */
    public static List<?> jigsawInfos(List<?> infos) {
        List<Object> out = new ArrayList<>(infos.size());
        for (Object o : infos) {
            out.add(o instanceof class_3499.class_3501 s ? class_3499.class_10326.method_64824(s) : o);
        }
        return out;
    }

    /** TrialSpawnerConfig.CODEC read by a 1.21.1 mod: the config's own codec */
    public static Codec<class_8965> field_47368() {
        return class_8965.field_53980;
    }
}
