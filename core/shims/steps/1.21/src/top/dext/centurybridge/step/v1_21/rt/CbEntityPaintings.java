package top.dext.centurybridge.step.v1_21.rt;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.class_1535;
import net.minecraft.class_2378;
import net.minecraft.class_2487;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import net.minecraft.class_6903;
import net.minecraft.class_7408;
import net.minecraft.class_7871;
import net.minecraft.class_7891;
import net.minecraft.class_7922;
import net.minecraft.class_7924;

/**
 * Painting variants. 1.20.6: PaintingVariant was a class of a width and a height in pixels, registered in the
 * builtin registry BuiltInRegistries.PAINTING_VARIANT, its texture painting/&lt;registry id&gt;. 1.21: a final record
 * (width, height in blocks, assetId), each world's data in the registry minecraft:painting_variant. A 1.20.6
 * object is the 1.21 record of its size (the asset unknown until it is registered); the 1.20.6 registry is a
 * stand-in (CbLegacyRegistries) holding the vanilla variants and what mods register, and a mod's variant becomes
 * a world entry with its registry id as the asset -- the texture 1.20.6 drew it with. Wired from s6world.json.
 */
public final class CbEntityPaintings {

    /** the asset of a variant before it is registered (1.20.6's had none: its texture came from its registry id) */
    private static final class_2960 UNREGISTERED = class_2960.method_60655("centurybridge", "unregistered_painting");

    /** 1.21's block sizes as 1.20.6's pixels */
    public static int pixels(int blocks) {
        return blocks * 16;
    }

    /** 1.20.6's pixel sizes as 1.21's blocks: 16 to a block (every vanilla size is a multiple), the nearest, at least one */
    public static int blocks(int pixels) {
        return Math.max(1, (pixels + 8) / 16);
    }

    /** 1.20.6 new PaintingVariant(width, height), in pixels */
    public static class_1535 newVariant(int width, int height) {
        return new class_1535(blocks(width), blocks(height), UNREGISTERED);
    }

    /** 1.20.6 variant.getWidth(): pixels */
    public static int getWidth(class_1535 variant) {
        return pixels(variant.comp_2670());
    }

    /** 1.20.6 variant.getHeight(): pixels */
    public static int getHeight(class_1535 variant) {
        return pixels(variant.comp_2671());
    }

    /**
     * 1.20.6 Painting.storeVariant(tag, variant): the variant's registry name under "variant" (1.20.6's codec wrote
     * nothing for a variant without one); 1.21 reads the same key.
     */
    public static void storeVariant(class_2487 tag, class_6880<class_1535> variant) {
        variant.method_40230().ifPresent(key -> tag.method_10582("variant", key.method_29177().toString()));
    }

    /** 1.20.6 PaintingVariants.bootstrap(registry): the vanilla variants registered into it; answers the default, kebab */
    public static class_1535 bootstrap(class_2378<class_1535> registry) {
        vanillaInto((key, value) -> class_2378.method_39197(registry, key, value));
        return registry.method_29107(class_7408.field_38947);
    }

    /** the vanilla variants, as 1.21's own bootstrap makes them (the asset is the variant's name) */
    private static void vanillaInto(BiConsumer<class_5321<class_1535>, class_1535> sink) {
        class_7408.method_43406(new class_7891<class_1535>() {
            @Override
            public class_6880.class_6883<class_1535> method_46800(class_5321<class_1535> key, class_1535 value, com.mojang.serialization.Lifecycle lifecycle) {
                sink.accept(key, value);
                return null;
            }

            @Override
            public <S> class_7871<S> method_46799(class_5321<? extends class_2378<? extends S>> registry) {
                throw new UnsupportedOperationException("painting variants look nothing up");
            }
        });
    }

    // ------------------------------------------------------------------------------------------------ the registry
    private static volatile Standin standin;

    /** 1.20.6 BuiltInRegistries.PAINTING_VARIANT */
    public static class_7922<class_1535> field_41182() {
        Standin s = standin;
        if (s == null) {
            synchronized (CbEntityPaintings.class) {
                s = standin;
                if (s == null) {
                    standin = s = new Standin();
                }
            }
        }
        return s;
    }

    /**
     * A holder a 1.20.6 caller hands to a painting (CbEntityPaintingMixin): one of the stand-in's is the world's
     * holder of its name, which is what 1.21 can sync and save; anything else stays.
     */
    public static class_6880<class_1535> worldHolder(class_6880<class_1535> holder) {
        if (holder == null || standin == null) {
            return holder;
        }
        java.util.Optional<class_5321<class_1535>> key = holder.method_40230();
        class_2378<class_1535> world = CbLegacyRegistries.current(class_7924.field_41209);
        if (key.isEmpty() || world == null) {
            return holder;
        }
        java.util.Optional<? extends class_6880<class_1535>> own = world.method_40264(key.get());
        return own.isPresent() ? own.get() : holder;
    }

    /** the stand-in of 1.20.6's BuiltInRegistries.PAINTING_VARIANT: a defaulted registry (kebab) */
    static final class Standin extends CbLegacyRegistries.Legacy<class_1535, class_1535> implements class_7922<class_1535> {
        private final Set<class_5321<class_1535>> vanilla = new HashSet<>();
        private final class_6880.class_6883<class_1535> defaultValue;

        Standin() {
            super(class_7924.field_41209, false);
            vanillaInto((key, value) -> {
                vanilla.add(key);
                cbRegister(key, value);
            });
            defaultValue = this.method_40264(class_7408.field_38947).orElseThrow();
        }

        /** a mod's variant becomes a world entry of its size, its asset its registry id; vanilla's are the world's own data */
        @Override
        protected class_1535 recordFor(class_1535 value, class_5321<class_1535> key, class_6903.class_7863 lookup) {
            return vanilla.contains(key) ? null : new class_1535(value.comp_2670(), value.comp_2671(), key.method_29177());
        }

        /** a world entry no object was registered for (a datapack's): the record itself is its 1.20.6 object */
        @Override
        protected class_1535 newFacade(class_5321<class_1535> key) {
            class_2378<class_1535> world = CbLegacyRegistries.current(class_7924.field_41209);
            return world == null ? null : world.method_29107(key);
        }

        @Override
        public class_2960 method_10137() {
            return class_7408.field_38947.method_29177();
        }

        @Override
        public class_2960 method_10221(class_1535 value) {
            class_2960 id = super.method_10221(value);
            return id != null ? id : method_10137();
        }

        @Override
        public class_1535 method_10223(class_2960 id) {
            class_1535 value = super.method_10223(id);
            return value != null ? value : defaultValue.comp_349();
        }

        @Override
        public class_1535 method_10200(int id) {
            class_1535 value = super.method_10200(id);
            return value != null ? value : defaultValue.comp_349();
        }
    }

    private CbEntityPaintings() {
    }
}
