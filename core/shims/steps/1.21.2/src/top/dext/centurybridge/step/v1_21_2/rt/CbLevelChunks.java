package top.dext.centurybridge.step.v1_21_2.rt;

import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortLists;
import java.util.List;
import net.minecraft.class_1922;
import net.minecraft.class_2791;
import net.minecraft.class_2818;
import net.minecraft.class_2821;
import net.minecraft.class_3218;
import net.minecraft.class_6749;
import net.minecraft.class_6759;
import net.minecraft.class_6761;

/**
 * 1.21.1's ChunkAccess state API on 1.21.2.
 * <ul>
 * <li>setUnsaved(b) / the protected unsaved flag: 1.21.2 has markUnsaved() and tryMarkSaved() -- markUnsaved also
 *     tells a LevelChunk's unsaved listener, without which 1.21.2's ChunkMap never saves the chunk; a 1.21.1 write of
 *     true is markUnsaved, of false tryMarkSaved (1.21.1's setter set the flag and nothing else; the read is
 *     isUnsaved). ImposterProtoChunk hands both to its wrapped chunk in either release.</li>
 * <li>setBlendingData(data): 1.21.1's body sets the field (the imposter's, its wrapped chunk's); 1.21.2 sets it only
 *     at construction, and nothing in either release's vanilla code called the setter.</li>
 * <li>addPackedPostProcess(packed, section): 1.21.2 takes a list of them; one is the list of one.</li>
 * <li>getMaxLightLevel(): 15 in 1.21.1's BlockGetter, which ImposterProtoChunk asked its wrapped chunk; 1.21.2's
 *     lighting is fixed at 15 and no longer asks (an override returning another value informs only its mod's own
 *     calls, which still reach it).</li>
 * <li>postProcessGeneration(): 1.21.2's runPostProcessing(ServerLevel) with the chunk's level -- a server level,
 *     the only one 1.21.1 ran it for (ChunkMap).</li>
 * <li>getTicksForSerialization(): 1.21.1 handed out the tick containers, packed when the chunk data was written at
 *     the level's game time; 1.21.2 packs them at a game time it is given -- the level's game time now, what 1.21.2's
 *     chunk serialization passes (a proto chunk's containers ignore it). PackedTicks' 1.21.1 components are those
 *     lists as containers again (each packs to its list).</li>
 * </ul>
 */
public final class CbLevelChunks {

    /** ChunkAccess.blendingData, protected and set only at construction since 1.21.2 (CbLevelChunkAccess) */
    public interface Fields {
        void cb$blendingData(class_6749 data);
    }

    // ---- unsaved ----

    /** chunk.setUnsaved(unsaved) */
    public static void method_12008(class_2791 self, boolean unsaved) {
        if (self instanceof LegacySetUnsaved l) {
            l.method_12008(unsaved);
        } else {
            setUnsaved(self, unsaved);
        }
    }

    private static void setUnsaved(class_2791 self, boolean unsaved) {
        if (unsaved) {
            self.method_65063();
        } else {
            self.method_65064();
        }
    }

    /** a read of the protected unsaved field */
    public static boolean field_34537(class_2791 self) {
        return self.method_12044();
    }

    /** a write of the protected unsaved field */
    public static void field_34537(class_2791 self, boolean unsaved) {
        setUnsaved(self, unsaved);
    }

    public interface LegacySetUnsaved {
        void method_12008(boolean unsaved);
    }

    public interface SuperSetUnsaved {
        void cb$super$method_65063();

        boolean cb$super$method_65064();
    }

    /** vanilla's markUnsaved() of a 1.21.1 chunk overriding setUnsaved */
    public static void markUnsaved(LegacySetUnsaved self) {
        self.method_12008(true);
    }

    /** vanilla's tryMarkSaved() of a 1.21.1 chunk overriding setUnsaved: whether it was unsaved, then saved */
    public static boolean tryMarkSaved(LegacySetUnsaved self) {
        boolean was = ((class_2791) self).method_12044();
        self.method_12008(false);
        return was;
    }

    /** its super.setUnsaved(b) */
    public static void superSetUnsaved(SuperSetUnsaved self, boolean unsaved) {
        if (unsaved) {
            self.cb$super$method_65063();
        } else {
            self.cb$super$method_65064();
        }
    }

    // ---- blending data ----

    /** chunk.setBlendingData(data): a 1.21.1 class's own setter when it has one */
    public static void method_39294(class_2791 self, class_6749 data) {
        if (self instanceof LegacySetBlendingData l) {
            l.method_39294(data);
        } else {
            setBlendingData(self, data);
        }
    }

    /** super.setBlendingData(data): ChunkAccess's body, or ImposterProtoChunk's (its wrapped chunk's) */
    public static void setBlendingData(class_2791 self, class_6749 data) {
        if (self instanceof class_2821 imposter) {
            method_39294(imposter.method_12240(), data);
        } else {
            ((Fields) self).cb$blendingData(data);
        }
    }

    public interface LegacySetBlendingData {
        void method_39294(class_6749 data);
    }

    // ---- post-processing ----

    /** chunk.addPackedPostProcess(packed, section) */
    public static void method_12029(class_2791 self, short packed, int section) {
        if (self instanceof LegacyAddPackedPostProcess l) {
            l.method_12029(packed, section);
        } else {
            self.method_12029(ShortLists.singleton(packed), section);
        }
    }

    public interface LegacyAddPackedPostProcess {
        void method_12029(short packed, int section);
    }

    public interface SuperAddPackedPostProcess {
        void cb$super$method_12029(ShortList packed, int section);
    }

    /** vanilla's addPackedPostProcess(list, section) of a 1.21.1 chunk: its override, one position at a time */
    public static void addPackedPostProcess(LegacyAddPackedPostProcess self, ShortList packed, int section) {
        for (int i = 0; i < packed.size(); i++) {
            self.method_12029(packed.getShort(i), section);
        }
    }

    public static void superAddPackedPostProcess(SuperAddPackedPostProcess self, short packed, int section) {
        self.cb$super$method_12029(ShortLists.singleton(packed), section);
    }

    /** levelChunk.postProcessGeneration() */
    public static void method_12221(class_2818 self) {
        if (!(self.method_12200() instanceof class_3218 level)) {
            throw new UnsupportedOperationException("1.21.1's LevelChunk.postProcessGeneration on a client chunk: "
                + "1.21.1 ran it on server chunks only, as 1.21.2's runPostProcessing(ServerLevel)");
        }
        self.method_12221(level);
    }

    // ---- light ----

    /** getter.getMaxLightLevel() */
    public static int method_8315(class_1922 self) {
        if (self instanceof LegacyMaxLightLevel l) {
            return l.method_8315();
        }
        return maxLightLevel(self);
    }

    /** super.getMaxLightLevel(): BlockGetter's 15, ImposterProtoChunk's wrapped chunk's */
    public static int maxLightLevel(class_1922 self) {
        if (self instanceof class_2821 imposter) {
            return method_8315(imposter.method_12240());
        }
        return 15;
    }

    public interface LegacyMaxLightLevel {
        int method_8315();
    }

    // ---- ticks ----

    /** chunk.getTicksForSerialization(), packed at the level's game time */
    public static class_2791.class_6745 method_39296(class_2791 self) {
        return self.method_39296(gameTime(self));
    }

    private static long gameTime(class_2791 chunk) {
        class_2791 c = chunk instanceof class_2821 imposter ? imposter.method_12240() : chunk;
        return c instanceof class_2818 level ? level.method_12200().method_8510() : 0L;
    }

    /** packedTicks.blocks(): the packed list as a container */
    public static class_6761<?> comp_239(class_2791.class_6745 self) {
        return container(self.comp_239());
    }

    /** packedTicks.fluids() */
    public static class_6761<?> comp_240(class_2791.class_6745 self) {
        return container(self.comp_240());
    }

    private static <T> class_6761<T> container(List<class_6759<T>> ticks) {
        return gameTime -> ticks;
    }

    private CbLevelChunks() {
    }
}
