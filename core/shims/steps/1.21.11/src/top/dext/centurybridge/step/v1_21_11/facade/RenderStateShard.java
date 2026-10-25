package top.dext.centurybridge.step.v1_21_11.facade;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1059;
import net.minecraft.class_12245;
import net.minecraft.class_12246;
import net.minecraft.class_12250;
import net.minecraft.class_276;
import net.minecraft.class_2960;

/**
 * 1.21.10's RenderStateShard (class_4668), which 1.21.11 removed: a render type's state is a RenderSetup
 * (rendertype/RenderSetup: textures, lightmap / overlay use, LayeringTransform, OutputTarget, TextureTransform) that
 * each draw applies, where 1.21.10 ran each shard's setup / clear runnables around the draw. A shard here is what
 * 1.21.10 built it from (name, runnables, the shard's own data) and, for the shards 1.21.11 still has a piece for, that
 * piece (cb$layering / cb$output / cb$transform): CompositeState hands them to RenderSetup when a render type is created
 * (rt/CbRenderTypeCreate). setupRenderState / clearRenderState run the shard's runnables as 1.21.10 did; 1.21.11's
 * draws no longer read the global render state those runnables set (RenderSystem's texture matrix, line width, shader
 * textures: gone), so for vanilla's shards they are the 1.21.11 pieces' effect on what 1.21.11 still has (the model
 * view stack for layering) and nothing for the rest, as 1.21.11 itself does outside a draw.
 */
@Environment(EnvType.CLIENT)
public abstract class RenderStateShard {
    public static final double field_42230 = class_12250.field_64067;
    public final String field_21363;
    public final Runnable field_21361;
    public final Runnable field_21362;

    public static final TextureStateShard field_21376 = new TextureStateShard(class_1059.field_5275, true);
    public static final TextureStateShard field_21377 = new TextureStateShard(class_1059.field_5275, false);
    public static final EmptyTextureStateShard field_21378 = new EmptyTextureStateShard();
    public static final TexturingStateShard field_21379 = new TexturingStateShard(class_12250.field_64068);
    public static final TexturingStateShard field_21381 = new TexturingStateShard(class_12250.field_64069);
    public static final TexturingStateShard field_21382 = new TexturingStateShard(class_12250.field_64070);
    public static final TexturingStateShard field_58053 = new TexturingStateShard(class_12250.field_64071);
    public static final LightmapStateShard field_21383 = new LightmapStateShard(true);
    public static final LightmapStateShard field_21384 = new LightmapStateShard(false);
    public static final OverlayStateShard field_21385 = new OverlayStateShard(true);
    public static final OverlayStateShard field_21386 = new OverlayStateShard(false);
    public static final LayeringStateShard field_21352 = new LayeringStateShard(class_12245.field_63975);
    public static final LayeringStateShard field_22241 = new LayeringStateShard(class_12245.field_63976);
    public static final LayeringStateShard field_53123 = new LayeringStateShard(class_12245.field_63977);
    public static final OutputStateShard field_21358 = new OutputStateShard(class_12246.field_63980);
    public static final OutputStateShard field_21359 = new OutputStateShard(class_12246.field_63981);
    public static final OutputStateShard field_25282 = new OutputStateShard(class_12246.field_63982);
    public static final OutputStateShard field_25643 = new OutputStateShard(class_12246.field_63983);
    public static final LineStateShard field_21360 = new LineStateShard(OptionalDouble.of(1.0));

    public RenderStateShard(String name, Runnable setupState, Runnable clearState) {
        this.field_21363 = name;
        this.field_21361 = setupState;
        this.field_21362 = clearState;
    }

    /** setupRenderState */
    public void method_23516() {
        this.field_21361.run();
    }

    /** clearRenderState */
    public void method_23518() {
        this.field_21362.run();
    }

    @Override
    public String toString() {
        return this.field_21363;
    }

    /** getName */
    public String method_68484() {
        return this.field_21363;
    }

    private static void none() {
    }

    /** 1.21.10 RenderStateShard.BooleanStateShard */
    @Environment(EnvType.CLIENT)
    public static class BooleanStateShard extends RenderStateShard {
        public final boolean field_21390;

        public BooleanStateShard(String name, Runnable setupState, Runnable clearState, boolean enabled) {
            super(name, setupState, clearState);
            this.field_21390 = enabled;
        }

        @Override
        public String toString() {
            return this.field_21363 + "[" + this.field_21390 + "]";
        }
    }

    /** 1.21.10 RenderStateShard.EmptyTextureStateShard: no texture */
    @Environment(EnvType.CLIENT)
    public static class EmptyTextureStateShard extends RenderStateShard {
        public EmptyTextureStateShard(Runnable setupState, Runnable clearState) {
            super("texture", setupState, clearState);
        }

        public EmptyTextureStateShard() {
            super("texture", RenderStateShard::none, RenderStateShard::none);
        }

        /** cutoutTexture */
        public Optional<class_2960> method_23564() {
            return Optional.empty();
        }

        /** the textures 1.21.11's RenderSetup binds, in sampler order (Sampler0, Sampler1, ...) */
        public List<class_2960> cb$textures() {
            return List.of();
        }
    }

    /** 1.21.10 RenderStateShard.TextureStateShard: one texture (1.21.11 binds it as Sampler0) */
    @Environment(EnvType.CLIENT)
    public static class TextureStateShard extends EmptyTextureStateShard {
        public final Optional<class_2960> field_21397;
        public final boolean field_21399;

        public TextureStateShard(class_2960 texture, boolean mipmap) {
            super(RenderStateShard::none, RenderStateShard::none);
            this.field_21397 = Optional.of(texture);
            this.field_21399 = mipmap;
        }

        @Override
        public String toString() {
            return this.field_21363 + "[" + this.field_21397 + "(mipmap=" + this.field_21399 + ")]";
        }

        @Override
        public Optional<class_2960> method_23564() {
            return this.field_21397;
        }

        @Override
        public List<class_2960> cb$textures() {
            return List.of(this.field_21397.get());
        }
    }

    /** 1.21.10 RenderStateShard.MultiTextureStateShard: textures bound to successive samplers */
    @Environment(EnvType.CLIENT)
    public static class MultiTextureStateShard extends EmptyTextureStateShard {
        public final Optional<class_2960> field_29453;
        public final List<Entry> cb$entries;

        public MultiTextureStateShard(List<Entry> entries) {
            super(RenderStateShard::none, RenderStateShard::none);
            this.cb$entries = List.copyOf(entries);
            this.field_29453 = entries.isEmpty() ? Optional.empty() : Optional.of(entries.getFirst().comp_3462);
        }

        @Override
        public Optional<class_2960> method_23564() {
            return this.field_29453;
        }

        @Override
        public List<class_2960> cb$textures() {
            return this.cb$entries.stream().map(e -> e.comp_3462).toList();
        }

        /** builder */
        public static Builder method_34560() {
            return new Builder();
        }

        /** 1.21.10 MultiTextureStateShard.Builder */
        @Environment(EnvType.CLIENT)
        public static final class Builder {
            public final ImmutableList.Builder<Entry> field_29454 = new ImmutableList.Builder<>();

            /** add(texture, mipmap) */
            public Builder method_34563(class_2960 texture, boolean mipmap) {
                this.field_29454.add(new Entry(texture, mipmap));
                return this;
            }

            /** build */
            public MultiTextureStateShard method_34562() {
                return new MultiTextureStateShard(this.field_29454.build());
            }
        }

        /** 1.21.10 MultiTextureStateShard.Entry (a record) */
        @Environment(EnvType.CLIENT)
        public static final class Entry {
            public final class_2960 comp_3462;
            public final boolean comp_3464;

            public Entry(class_2960 id, boolean mipmap) {
                this.comp_3462 = id;
                this.comp_3464 = mipmap;
            }

            public class_2960 comp_3462() {
                return this.comp_3462;
            }

            public boolean comp_3464() {
                return this.comp_3464;
            }

            @Override
            public boolean equals(Object o) {
                return o instanceof Entry e && this.comp_3462.equals(e.comp_3462) && this.comp_3464 == e.comp_3464;
            }

            @Override
            public int hashCode() {
                return 31 * this.comp_3462.hashCode() + Boolean.hashCode(this.comp_3464);
            }

            @Override
            public String toString() {
                return "Entry[id=" + this.comp_3462 + ", mipmap=" + this.comp_3464 + "]";
            }
        }
    }

    /** 1.21.10 RenderStateShard.LayeringStateShard; vanilla's carry 1.21.11's LayeringTransform */
    @Environment(EnvType.CLIENT)
    public static class LayeringStateShard extends RenderStateShard {
        public final class_12245 cb$layering;

        public LayeringStateShard(String name, Runnable setupState, Runnable clearState) {
            super(name, setupState, clearState);
            this.cb$layering = null;
        }

        /** a vanilla layering: 1.21.10's runnables pushed the model view stack, applied it, popped */
        public LayeringStateShard(class_12245 layering) {
            super(layering.toString(), () -> {
                org.joml.Matrix4fStack s = com.mojang.blaze3d.systems.RenderSystem.getModelViewStack();
                s.pushMatrix();
                layering.method_75918().accept(s);
            }, () -> com.mojang.blaze3d.systems.RenderSystem.getModelViewStack().popMatrix());
            this.cb$layering = layering;
        }
    }

    /** 1.21.10 RenderStateShard.LightmapStateShard */
    @Environment(EnvType.CLIENT)
    public static class LightmapStateShard extends BooleanStateShard {
        public LightmapStateShard(boolean enabled) {
            super("lightmap", RenderStateShard::none, RenderStateShard::none, enabled);
        }
    }

    /** 1.21.10 RenderStateShard.OverlayStateShard */
    @Environment(EnvType.CLIENT)
    public static class OverlayStateShard extends BooleanStateShard {
        public OverlayStateShard(boolean enabled) {
            super("overlay", RenderStateShard::none, RenderStateShard::none, enabled);
        }
    }

    /** 1.21.10 RenderStateShard.LineStateShard: 1.21.11 draws lines at the width each vertex carries */
    @Environment(EnvType.CLIENT)
    public static class LineStateShard extends RenderStateShard {
        public final OptionalDouble field_21392;

        public LineStateShard(OptionalDouble width) {
            super("line_width", RenderStateShard::none, RenderStateShard::none);
            this.field_21392 = width;
        }

        @Override
        public String toString() {
            return this.field_21363 + "[" + (this.field_21392.isPresent() ? this.field_21392.getAsDouble() : "window_scale") + "]";
        }
    }

    /** 1.21.10 RenderStateShard.OutputStateShard: 1.21.11's OutputTarget (same name and target supplier) */
    @Environment(EnvType.CLIENT)
    public static class OutputStateShard extends RenderStateShard {
        public final Supplier<class_276> field_57930;
        public final class_12246 cb$output;

        public OutputStateShard(String name, Supplier<class_276> renderTargetSupplier) {
            super(name, RenderStateShard::none, RenderStateShard::none);
            this.field_57930 = renderTargetSupplier;
            this.cb$output = new class_12246(name, renderTargetSupplier);
        }

        public OutputStateShard(class_12246 output) {
            super(output.toString(), RenderStateShard::none, RenderStateShard::none);
            this.field_57930 = output::method_75921;
            this.cb$output = output;
        }

        /** getRenderTarget */
        public class_276 method_68491() {
            return this.field_57930.get();
        }
    }

    /** 1.21.10 RenderStateShard.TexturingStateShard; vanilla's carry 1.21.11's TextureTransform */
    @Environment(EnvType.CLIENT)
    public static class TexturingStateShard extends RenderStateShard {
        public final class_12250 cb$transform;

        public TexturingStateShard(String name, Runnable setupState, Runnable clearState) {
            super(name, setupState, clearState);
            this.cb$transform = null;
        }

        public TexturingStateShard(class_12250 transform) {
            super(transform.toString(), RenderStateShard::none, RenderStateShard::none);
            this.cb$transform = transform;
        }
    }

    /** 1.21.10 RenderStateShard.OffsetTexturingStateShard: 1.21.11's OffsetTextureTransform */
    @Environment(EnvType.CLIENT)
    public static final class OffsetTexturingStateShard extends TexturingStateShard {
        public OffsetTexturingStateShard(float u, float v) {
            super(new class_12250.class_12251(u, v));
        }
    }
}
