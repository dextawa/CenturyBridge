package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProtectedBlockProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import top.dext.centurybridge.step.v26_2.facade.StructureProcessorType;

/**
 * 26.1.2's structure processor API on 26.2's (facade/StructureProcessor, facade/StructureProcessorType).
 *
 * <p>Types: 26.1.2's structure_processor registry held StructureProcessorType values, each a holder of a map codec;
 * 26.2's holds the map codecs. One type stands for each codec ({@link #of}, identity both ways: a type's codec comes
 * back as that type). 26.1.2 code reaches the registry through BuiltInRegistries.STRUCTURE_PROCESSOR, answered with
 * a registry of types ({@link Mirror}) mirroring 26.2's id for id: a 26.1.2 registration into it registers the
 * codec in 26.2's registry, and every registration into 26.2's (vanilla's, a 26.2 mod's, the mirror's own) is
 * mirrored at once (mixin/CbS26ProcessorRegistryMixin), as is freezing and tag binding.
 *
 * <p>Original block info: 26.1.2's processBlock received the template's original info; 26.2's receives its
 * position. Both 26.2 callers hold the info and the layer's mixins record it for the call ({@link #recordOriginal});
 * {@link #original} answers it for a 26.1.2 processor.
 */
public final class CbS26Processors {
    private CbS26Processors() {}

    // ---------------------------------------------------------------- types

    private static final Map<MapCodec<?>, StructureProcessorType<?>> TYPES = new IdentityHashMap<>();

    private record Type<P extends StructureProcessor>(MapCodec<P> codec) implements StructureProcessorType<P> {
    }

    /** the type standing for a map codec (a 26.1.2 type keeps its own) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static synchronized <P extends StructureProcessor> StructureProcessorType<P> of(MapCodec<P> codec) {
        return (StructureProcessorType<P>) TYPES.computeIfAbsent(codec, c -> new Type(c));
    }

    /** a type a 26.1.2 mod made (its own StructureProcessorType implementation): its codec now stands for it */
    private static synchronized void adopt(StructureProcessorType<?> type) {
        TYPES.putIfAbsent(type.codec(), type);
    }

    /** facade/StructureProcessorType's constants: the type of 26.2's codec registered under minecraft:<path> */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <P extends StructureProcessor> StructureProcessorType<P> vanilla(String path) {
        MapCodec codec = BuiltInRegistries.STRUCTURE_PROCESSOR.getValue(Identifier.withDefaultNamespace(path));
        if (codec == null) {
            throw new IllegalStateException("26.2 registers no structure processor minecraft:" + path);
        }
        return of(codec);
    }

    /** 26.1.2 StructureProcessorType.register(name, codec): Registry.register(STRUCTURE_PROCESSOR, name, type) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <P extends StructureProcessor> StructureProcessorType<P> register(String name, MapCodec<P> codec) {
        StructureProcessorType<P> type = of(codec);
        Registry.register((Registry) structureProcessor(), name, type);
        return type;
    }

    /** 26.1.2 processor.getType() on a processor of 26.2's (or a 26.1.2 one) */
    public static StructureProcessorType<?> getType(StructureProcessor p) {
        if (p instanceof top.dext.centurybridge.step.v26_2.facade.StructureProcessor f) {
            return f.cbType();
        }
        return of(p.codec());
    }

    // ---------------------------------------------------------------- the original block info

    private static final ThreadLocal<StructureTemplate.StructureBlockInfo> ORIGINAL = new ThreadLocal<>();

    /** mixins: the original info a 26.2 caller is about to hand to processBlock as its position */
    public static BlockPos recordOriginal(StructureTemplate.StructureBlockInfo info) {
        ORIGINAL.set(info);
        return info.pos();
    }

    /** mixins: the 26.2 caller is done with its processors */
    public static void clearOriginal() {
        ORIGINAL.remove();
    }

    /**
     * The original block info for a 26.1.2 processor called with 26.2's original position: the one the caller recorded
     * for this position. A caller the layer does not know (no record for the position) passes only the position; the
     * info is then that position with the current info's state and block entity data -- the original state is not
     * there to give.
     */
    public static StructureTemplate.StructureBlockInfo original(BlockPos originalPos, StructureTemplate.StructureBlockInfo current) {
        StructureTemplate.StructureBlockInfo o = ORIGINAL.get();
        if (o != null && o.pos().equals(originalPos)) {
            return o;
        }
        return new StructureTemplate.StructureBlockInfo(originalPos, current.state(), current.nbt());
    }

    /**
     * 26.1.2 processor.processBlock(level, pos, pivot, original, current, settings) on any processor: a 26.1.2 one
     * takes it as it is; a 26.2 one gets the original's position, the original recorded for processors it calls.
     */
    public static StructureTemplate.StructureBlockInfo processBlock(StructureProcessor p, LevelReader level, BlockPos pos, BlockPos pivot,
            StructureTemplate.StructureBlockInfo original, StructureTemplate.StructureBlockInfo current, StructurePlaceSettings settings) {
        if (p instanceof top.dext.centurybridge.step.v26_2.facade.StructureProcessor f) {
            return f.processBlock(level, pos, pivot, original, current, settings);
        }
        StructureTemplate.StructureBlockInfo outer = ORIGINAL.get();
        ORIGINAL.set(original);
        try {
            return p.processBlock(level, pos, pivot, original.pos(), current, settings);
        } finally {
            ORIGINAL.set(outer);
        }
    }

    // ---------------------------------------------------------------- ProtectedBlockProcessor (a record of a HolderSet)

    /** 26.1.2 ProtectedBlockProcessor.processBlock(.., original, current, ..): as on any 26.2 processor */
    public static StructureTemplate.StructureBlockInfo processBlock(ProtectedBlockProcessor p, LevelReader level, BlockPos pos, BlockPos pivot,
            StructureTemplate.StructureBlockInfo original, StructureTemplate.StructureBlockInfo current, StructurePlaceSettings settings) {
        return processBlock((StructureProcessor) p, level, pos, pivot, original, current, settings);
    }

    /** 26.1.2 new ProtectedBlockProcessor(tag): the tag read at use, as 26.1.2's Feature.isReplaceable(tag) did */
    public static ProtectedBlockProcessor cbNewProtected(TagKey<net.minecraft.world.level.block.Block> cannotReplace) {
        return new ProtectedBlockProcessor(new CbS26LazyTagSet<>(BuiltInRegistries.BLOCK, cannotReplace));
    }

    /**
     * 26.1.2's ProtectedBlockProcessor.CODEC: {"value": "#tag"} through TagKey.hashedCodec (javap), whatever the ops;
     * 26.2's MAP_CODEC writes a HolderSet, as its member list unless the ops are registry ops.
     */
    private static final MapCodec<ProtectedBlockProcessor> PROTECTED_CODEC = TagKey.hashedCodec(Registries.BLOCK)
        .xmap(CbS26Processors::cbNewProtected, CbS26Processors::cannotReplace).fieldOf("value");

    public static MapCodec<ProtectedBlockProcessor> PROTECTED_CODEC() {
        return PROTECTED_CODEC;
    }

    /** 26.1.2 ProtectedBlockProcessor.cannotReplace: the tag (null when 26.2 data gave a list of blocks, not a tag) */
    public static TagKey<net.minecraft.world.level.block.Block> cannotReplace(ProtectedBlockProcessor p) {
        return p.cannotReplace().unwrapKey().orElse(null);
    }

    // ---------------------------------------------------------------- the registry

    /** a registry of types standing for 26.2's registry of map codecs */
    public static final class Mirror extends MappedRegistry<StructureProcessorType<?>> {
        final Registry<MapCodec<? extends StructureProcessor>> real;

        @SuppressWarnings({"unchecked", "rawtypes"})
        Mirror(Registry<MapCodec<? extends StructureProcessor>> real) {
            super((ResourceKey) real.key(), real.registryLifecycle());
            this.real = real;
            for (MapCodec<? extends StructureProcessor> value : real) {
                ResourceKey key = real.getResourceKey(value).orElseThrow();
                mirror(key, value, (RegistrationInfo) real.registrationInfo(key).orElse(RegistrationInfo.BUILT_IN));
            }
            if (real instanceof Frozen f && f.cbS26$frozen()) {
                freeze();
            }
        }

        /** a 26.1.2 registration: the type's codec goes into 26.2's registry (which refuses as 26.1.2's did once frozen) */
        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public Holder.Reference<StructureProcessorType<?>> register(ResourceKey<StructureProcessorType<?>> key, StructureProcessorType<?> value,
                RegistrationInfo info) {
            adopt(value);
            ((WritableRegistry) real).register((ResourceKey) key, value.codec(), info);
            return getOrThrow(key);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        void mirror(ResourceKey key, Object codec, RegistrationInfo info) {
            super.register(key, of((MapCodec) codec), info);
        }
    }

    /** mixin/CbS26ProcessorRegistryMixin: whether a MappedRegistry is frozen */
    public interface Frozen {
        boolean cbS26$frozen();
    }

    private static Mirror mirror;

    /** BuiltInRegistries.STRUCTURE_PROCESSOR, as 26.1.2 code holds it: a registry of types */
    public static synchronized Registry<?> structureProcessor() {
        if (mirror == null) {
            mirror = new Mirror(BuiltInRegistries.STRUCTURE_PROCESSOR);
        }
        return mirror;
    }

    private static synchronized Mirror mirrorOf(Registry<?> real) {
        return mirror != null && mirror.real == real ? mirror : null;
    }

    /** mixin: something was registered in a registry; mirrored when it is 26.2's structure_processor */
    public static void registered(Registry<?> registry, ResourceKey<?> key, Object value, RegistrationInfo info) {
        Mirror m = mirrorOf(registry);
        if (m != null) {
            m.mirror(key, value, info);
        }
    }

    /** mixin: a registry froze; its mirror freezes too (binding its holders' values) */
    public static void frozen(Registry<?> registry) {
        Mirror m = mirrorOf(registry);
        if (m != null) {
            m.freeze();
        }
    }

    /** mixin: a registry's tags were bound; the mirror's holders get the same tags */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void tagsBound(Registry<?> registry, Map<TagKey, List<Holder>> tags) {
        Mirror m = mirrorOf(registry);
        if (m == null) {
            return;
        }
        Map<TagKey, List<Holder>> mirrored = new IdentityHashMap<>();
        for (Map.Entry<TagKey, List<Holder>> e : tags.entrySet()) {
            List<Holder> holders = new ArrayList<>();
            for (Holder h : e.getValue()) {
                h.unwrapKey().ifPresent(k -> holders.add(m.getOrThrow((ResourceKey) k)));
            }
            mirrored.put(e.getKey(), holders);
        }
        ((MappedRegistry) m).bindTags(mirrored);
    }
}
