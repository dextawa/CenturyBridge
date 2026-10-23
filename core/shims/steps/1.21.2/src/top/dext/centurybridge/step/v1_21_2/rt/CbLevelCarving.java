package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1923;
import net.minecraft.class_2378;
import net.minecraft.class_2791;
import net.minecraft.class_2794;
import net.minecraft.class_2821;
import net.minecraft.class_2839;
import net.minecraft.class_2922;
import net.minecraft.class_2960;
import net.minecraft.class_3233;
import net.minecraft.class_4543;
import net.minecraft.class_5138;
import net.minecraft.class_5321;
import net.minecraft.class_5444;
import net.minecraft.class_5485;
import net.minecraft.class_6643;
import net.minecraft.class_6798;
import net.minecraft.class_6880;
import net.minecraft.class_7138;
import net.minecraft.class_7923;
import top.dext.centurybridge.step.v1_21_2.facade.CarvingMaskPlacement;
import top.dext.centurybridge.step.v1_21_2.facade.GenerationStepCarving;

/**
 * 1.21.1's two carving steps on 1.21.2's one.
 *
 * <p>1.21.1 kept a carving mask and a carver list per GenerationStep.Carving (AIR, LIQUID): ProtoChunk's masks,
 * BiomeGenerationSettings' carvers, ChunkGenerator.applyCarvers(.., step), PlacementContext.getCarvingMask(chunk,
 * step), CarvingMaskPlacement(step). Its own chunk generation only ever ran AIR (ChunkStatusTasks.generateCarvers);
 * LIQUID was a step mods could fill and run themselves. 1.21.2 keeps AIR alone, without the parameter.
 * <ul>
 * <li>AIR is 1.21.2's mask, carvers and applyCarvers, called as they are;</li>
 * <li>LIQUID is a second mask on each proto chunk ({@link ProtoState}, CbLevelCarvingProtoMixin) and a second
 *     carver list on each biome's generation settings, filled by the builders' LIQUID addCarver calls and carried
 *     by build() ({@link SettingsState}, {@link BuilderState}, CbLevelCarvingBiomeMixin). applyCarvers(.., LIQUID)
 *     is 1.21.2's NoiseBasedChunkGenerator.applyCarvers -- 1.21.1's body for the AIR step, step aside -- with the
 *     LIQUID mask and carvers in place of the AIR ones (CbLevelCarvingNoiseMixin); FlatLevelSource and
 *     DebugLevelSource carve nothing in either release.</li>
 * </ul>
 * ImposterProtoChunk answers a mask only when it allows writes, else throws as 1.21.1's did ("Meaningless in this
 * context"). 1.21.2's chunk data saves the AIR mask only: a LIQUID mask lives as long as the proto chunk in memory --
 * 1.21.1 also saved it, and a chunk reloaded between its carving and features stages has an empty LIQUID mask here.
 */
public final class CbLevelCarving {

    /** a proto chunk's LIQUID carving mask (CbLevelCarvingProtoMixin) */
    public interface ProtoState {
        class_6643 cb$liquidMask();

        void cb$liquidMask(class_6643 mask);
    }

    /** ImposterProtoChunk.allowWrites (CbLevelCarvingImposterAccess) */
    public interface ImposterState {
        boolean cb$allowWrites();
    }

    /** a biome's LIQUID carvers (CbLevelCarvingBiomeMixin) */
    public interface SettingsState {
        List<class_6880<class_2922<?>>> cb$liquidCarvers();

        void cb$liquidCarvers(List<class_6880<class_2922<?>>> carvers);
    }

    /** a generation settings builder's LIQUID carvers, handed to the settings it builds (CbLevelCarvingBuilderMixin) */
    public interface BuilderState {
        List<class_6880<class_2922<?>>> cb$liquidCarvers();
    }

    /** Builder's carver lookup (CbLevelCarvingBuilderAccess) */
    public interface BuilderLookup {
        net.minecraft.class_7871<class_2922<?>> cb$carvers();
    }

    /** the step NoiseBasedChunkGenerator.applyCarvers carves on this thread: null is AIR, 1.21.2's own */
    private static final ThreadLocal<GenerationStepCarving> CARVING = new ThreadLocal<>();

    public static boolean liquid() {
        return CARVING.get() == GenerationStepCarving.LIQUID;
    }

    private static void checkImposter(class_2839 chunk) {
        if (chunk instanceof class_2821 && !((ImposterState) chunk).cb$allowWrites()) {
            throw new UnsupportedOperationException("Meaningless in this context");
        }
    }

    // ---- ProtoChunk masks ----

    /** protoChunk.getCarvingMask(step): null when none was made */
    public static class_6643 method_12025(class_2839 self, GenerationStepCarving step) {
        if (step == GenerationStepCarving.AIR) {
            return self.method_12025();
        }
        checkImposter(self);
        return ((ProtoState) self).cb$liquidMask();
    }

    /** protoChunk.getOrCreateCarvingMask(step) */
    public static class_6643 method_28510(class_2839 self, GenerationStepCarving step) {
        if (step == GenerationStepCarving.AIR) {
            return self.method_28510();
        }
        checkImposter(self);
        ProtoState s = (ProtoState) self;
        class_6643 mask = s.cb$liquidMask();
        if (mask == null) {
            mask = new class_6643(self.method_31605(), self.method_31607());
            s.cb$liquidMask(mask);
        }
        return mask;
    }

    /** protoChunk.setCarvingMask(step, mask) */
    public static void method_12307(class_2839 self, GenerationStepCarving step, class_6643 mask) {
        if (step == GenerationStepCarving.AIR) {
            self.method_12307(mask);
        } else {
            ((ProtoState) self).cb$liquidMask(mask);
        }
    }

    /** placementContext.getCarvingMask(chunkPos, step): 1.21.1's body, the chunk at that position as a proto chunk */
    public static class_6643 method_30459(class_5444 self, class_1923 chunk, GenerationStepCarving step) {
        if (step == GenerationStepCarving.AIR) {
            return self.method_30459(chunk);
        }
        return method_28510((class_2839) self.method_34383().method_8392(chunk.field_9181, chunk.field_9180), step);
    }

    // ---- biome carvers ----

    /** settings.getCarvers(step) */
    public static Iterable<class_6880<class_2922<?>>> method_30976(class_5485 self, GenerationStepCarving step) {
        if (step == GenerationStepCarving.AIR) {
            return self.method_30976();
        }
        List<class_6880<class_2922<?>>> carvers = ((SettingsState) self).cb$liquidCarvers();
        return carvers != null ? carvers : List.of();
    }

    /** plainBuilder.addCarver(step, carver) */
    public static class_5485.class_7868 method_46675(class_5485.class_7868 self, GenerationStepCarving step,
                                                     class_6880<class_2922<?>> carver) {
        if (step == GenerationStepCarving.AIR) {
            return self.method_46675(carver);
        }
        ((BuilderState) self).cb$liquidCarvers().add(carver);
        return self;
    }

    /** builder.addCarver(step, key): the key looked up as 1.21.1's did, then addCarver(step, holder) as the class has it */
    public static class_5485.class_5495 method_30991(class_5485.class_5495 self, GenerationStepCarving step,
                                                     class_5321<class_2922<?>> key) {
        if (step == GenerationStepCarving.AIR) {
            return self.method_30991(key);
        }
        class_6880<class_2922<?>> carver = ((BuilderLookup) self).cb$carvers().method_46747(key);
        if (self instanceof LegacyPlainAddCarver l) {
            l.method_46675(step, carver);
        } else {
            method_46675(self, step, carver);
        }
        return self;
    }

    /** the settings a builder builds take its LIQUID carvers (CbLevelCarvingBuilderMixin) */
    public static void built(class_5485.class_7868 builder, class_5485 settings) {
        List<class_6880<class_2922<?>>> carvers = ((BuilderState) builder).cb$liquidCarvers();
        ((SettingsState) settings).cb$liquidCarvers(carvers.isEmpty() ? List.of() : List.copyOf(carvers));
    }

    // ---- chunk generators ----

    /** generator.applyCarvers(region, seed, random, biomes, structures, chunk, step) */
    public static void method_12108(class_2794 self, class_3233 region, long seed, class_7138 random, class_4543 biomes,
                                    class_5138 structures, class_2791 chunk, GenerationStepCarving step) {
        if (self instanceof LegacyApplyCarvers l) {
            l.method_12108(region, seed, random, biomes, structures, chunk, step);
        } else {
            carve(step, () -> self.method_12108(region, seed, random, biomes, structures, chunk));
        }
    }

    /** 1.21.2's applyCarvers, carving the step */
    private static void carve(GenerationStepCarving step, Runnable call) {
        if (step == GenerationStepCarving.AIR) {
            call.run();
            return;
        }
        GenerationStepCarving previous = CARVING.get();
        CARVING.set(step);
        try {
            call.run();
        } finally {
            if (previous == null) {
                CARVING.remove();
            } else {
                CARVING.set(previous);
            }
        }
    }

    // ---- 1.21.1 overrides (override hooks: bridge, superBridge) ----

    public interface LegacyApplyCarvers {
        void method_12108(class_3233 region, long seed, class_7138 random, class_4543 biomes, class_5138 structures,
                          class_2791 chunk, GenerationStepCarving step);
    }

    public interface SuperApplyCarvers {
        void cb$super$method_12108(class_3233 region, long seed, class_7138 random, class_4543 biomes,
                                   class_5138 structures, class_2791 chunk);
    }

    /** vanilla's applyCarvers of a 1.21.1 generator: the AIR step, the one 1.21.1's chunk generation ran */
    public static void applyCarvers(LegacyApplyCarvers self, class_3233 region, long seed, class_7138 random,
                                    class_4543 biomes, class_5138 structures, class_2791 chunk) {
        self.method_12108(region, seed, random, biomes, structures, chunk, GenerationStepCarving.AIR);
    }

    public static void superApplyCarvers(SuperApplyCarvers self, class_3233 region, long seed, class_7138 random,
                                         class_4543 biomes, class_5138 structures, class_2791 chunk,
                                         GenerationStepCarving step) {
        carve(step, () -> self.cb$super$method_12108(region, seed, random, biomes, structures, chunk));
    }

    public interface LegacyCarvingMask {
        class_6643 method_12025(GenerationStepCarving step);
    }

    public interface SuperCarvingMask {
        class_6643 cb$super$method_12025();
    }

    public static class_6643 carvingMask(LegacyCarvingMask self) {
        return self.method_12025(GenerationStepCarving.AIR);
    }

    public static class_6643 superCarvingMask(SuperCarvingMask self, GenerationStepCarving step) {
        if (step == GenerationStepCarving.AIR) {
            return self.cb$super$method_12025();
        }
        checkImposter((class_2839) self);
        return ((ProtoState) self).cb$liquidMask();
    }

    public interface LegacyOrCreateCarvingMask {
        class_6643 method_28510(GenerationStepCarving step);
    }

    public interface SuperOrCreateCarvingMask {
        class_6643 cb$super$method_28510();
    }

    public static class_6643 orCreateCarvingMask(LegacyOrCreateCarvingMask self) {
        return self.method_28510(GenerationStepCarving.AIR);
    }

    public static class_6643 superOrCreateCarvingMask(SuperOrCreateCarvingMask self, GenerationStepCarving step) {
        if (step == GenerationStepCarving.AIR) {
            return self.cb$super$method_28510();
        }
        return method_28510((class_2839) self, step);
    }

    public interface LegacySetCarvingMask {
        void method_12307(GenerationStepCarving step, class_6643 mask);
    }

    public interface SuperSetCarvingMask {
        void cb$super$method_12307(class_6643 mask);
    }

    public static void setCarvingMask(LegacySetCarvingMask self, class_6643 mask) {
        self.method_12307(GenerationStepCarving.AIR, mask);
    }

    public static void superSetCarvingMask(SuperSetCarvingMask self, GenerationStepCarving step, class_6643 mask) {
        if (step == GenerationStepCarving.AIR) {
            self.cb$super$method_12307(mask);
        } else {
            ((ProtoState) self).cb$liquidMask(mask);
        }
    }

    public interface LegacyContextCarvingMask {
        class_6643 method_30459(class_1923 chunk, GenerationStepCarving step);
    }

    public interface SuperContextCarvingMask {
        class_6643 cb$super$method_30459(class_1923 chunk);
    }

    public static class_6643 contextCarvingMask(LegacyContextCarvingMask self, class_1923 chunk) {
        return self.method_30459(chunk, GenerationStepCarving.AIR);
    }

    public static class_6643 superContextCarvingMask(SuperContextCarvingMask self, class_1923 chunk,
                                                     GenerationStepCarving step) {
        if (step == GenerationStepCarving.AIR) {
            return self.cb$super$method_30459(chunk);
        }
        return method_30459((class_5444) self, chunk, step);
    }

    public interface LegacyPlainAddCarver {
        class_5485.class_7868 method_46675(GenerationStepCarving step, class_6880<class_2922<?>> carver);
    }

    public interface SuperPlainAddCarver {
        class_5485.class_7868 cb$super$method_46675(class_6880<class_2922<?>> carver);
    }

    public static class_5485.class_7868 plainAddCarver(LegacyPlainAddCarver self, class_6880<class_2922<?>> carver) {
        return self.method_46675(GenerationStepCarving.AIR, carver);
    }

    public static class_5485.class_7868 superPlainAddCarver(SuperPlainAddCarver self, GenerationStepCarving step,
                                                            class_6880<class_2922<?>> carver) {
        if (step == GenerationStepCarving.AIR) {
            return self.cb$super$method_46675(carver);
        }
        ((BuilderState) self).cb$liquidCarvers().add(carver);
        return (class_5485.class_7868) self;
    }

    public interface LegacyAddCarver {
        class_5485.class_5495 method_30991(GenerationStepCarving step, class_5321<class_2922<?>> key);
    }

    public interface SuperAddCarver {
        class_5485.class_5495 cb$super$method_30991(class_5321<class_2922<?>> key);
    }

    public static class_5485.class_5495 addCarver(LegacyAddCarver self, class_5321<class_2922<?>> key) {
        return self.method_30991(GenerationStepCarving.AIR, key);
    }

    public static class_5485.class_5495 superAddCarver(SuperAddCarver self, GenerationStepCarving step,
                                                       class_5321<class_2922<?>> key) {
        if (step == GenerationStepCarving.AIR) {
            return self.cb$super$method_30991(key);
        }
        return method_30991((class_5485.class_5495) self, step, key);
    }

    // ---- CarvingMaskPlacement's type: 1.21.1's "minecraft:carving_mask" ----

    private static class_6798<CarvingMaskPlacement> type;

    /** PlacementModifierType.CARVING_MASK_PLACEMENT, registered on first use (and by CbLevelInit at startup) */
    public static synchronized class_6798<?> field_35751() {
        if (type == null) {
            class_6798<CarvingMaskPlacement> t = () -> CarvingMaskPlacement.field_28934;
            type = class_2378.method_10230(class_7923.field_41148, class_2960.method_60656("carving_mask"), t);
        }
        return type;
    }

    private CbLevelCarving() {
    }
}
