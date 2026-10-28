package top.dext.centurybridge.step.v1_21_9.rt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.List;
import net.minecraft.class_11895;
import net.minecraft.class_2359;
import net.minecraft.class_2837;
import net.minecraft.class_2841;
import net.minecraft.class_6490;
import net.minecraft.class_6563;
import net.minecraft.class_7522;
import top.dext.centurybridge.step.v1_21_9.facade.PalettedContainerStrategy;

/**
 * PalettedContainer members 1.21.8 code reaches with a registry and a registry-free strategy
 * ({@link PalettedContainerStrategy}); 1.21.9's take the strategy holding the registry (class_6563). Each call hands
 * 1.21.9 the strategy of the same kind for that registry; what 1.21.9 then does is its own. The containers built here
 * get their palettes' registry and resize handler as every container does (CbPalettedContainerTagMixin).
 */
public final class CbPaletteContainers {
    private CbPaletteContainers() {
    }

    /** new PalettedContainer(registry, value, strategy) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class_2841 container(class_2359 registry, Object value, PalettedContainerStrategy strategy) {
        return new class_2841(value, strategy.cb$forRegistry(registry));
    }

    /**
     * new PalettedContainer(registry, strategy, configuration, storage, values): 1.21.8 built the palette with
     * configuration.factory().create(bits, registry, container, values) (javap -c Configuration.createData); 1.21.9's
     * Configuration.Simple builds it with factory.create(bits, values), the container giving it registry and handler
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class_2841 container(class_2359 registry, PalettedContainerStrategy strategy, class_11895.class_6560 configuration,
                                       class_6490 storage, List values) {
        class_6563 s = strategy.cb$forRegistry(registry);
        class_2837 palette = configuration.method_74152(s, values);
        return new class_2841(s, configuration, storage, palette);
    }

    /** PalettedContainer.codecRW(registry, codec, strategy, defaultValue) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Codec method_44343(class_2359 registry, Codec codec, PalettedContainerStrategy strategy, Object defaultValue) {
        return class_2841.method_44343(codec, strategy.cb$forRegistry(registry), defaultValue);
    }

    /** PalettedContainer.codecRO(registry, codec, strategy, defaultValue) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Codec method_44347(class_2359 registry, Codec codec, PalettedContainerStrategy strategy, Object defaultValue) {
        return class_2841.method_44347(codec, strategy.cb$forRegistry(registry), defaultValue);
    }

    /** PalettedContainerRO.pack(registry, strategy) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class_7522.class_6562 method_44345(class_7522 container, class_2359 registry, PalettedContainerStrategy strategy) {
        return container.method_44345(strategy.cb$forRegistry(registry));
    }

    /** PalettedContainerRO.Unpacker.read(registry, strategy, data) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static DataResult read(class_7522.class_7523 unpacker, class_2359 registry, PalettedContainerStrategy strategy,
                                  class_7522.class_6562 data) {
        return unpacker.read(strategy.cb$forRegistry(registry), data);
    }

    /** a 1.21.9 strategy's registry, for a 1.21.8 pack(registry, strategy) override vanilla calls with the strategy */
    @SuppressWarnings("rawtypes")
    public static class_2359 registry(class_6563 strategy) {
        return strategy.method_74163();
    }

    /** a 1.21.9 strategy's 1.21.8 kind, for the same override */
    @SuppressWarnings("rawtypes")
    public static PalettedContainerStrategy legacy(class_6563 strategy) {
        return PalettedContainerStrategy.cb$legacy(strategy);
    }

    /** 1.21.8's pack(registry, strategy) of a container class a converted mod wrote (overrideHooks bridge) */
    public interface LegacyPack {
        @SuppressWarnings("rawtypes")
        class_7522.class_6562 method_44345(class_2359 registry, PalettedContainerStrategy strategy);
    }

    /** the 1.21.9 pack(strategy) of such a class: its 1.21.8 override with the strategy's registry and kind */
    @SuppressWarnings("rawtypes")
    public static class_7522.class_6562 pack(LegacyPack container, class_6563 strategy) {
        return container.method_44345(strategy.method_74163(), PalettedContainerStrategy.cb$legacy(strategy));
    }
}
