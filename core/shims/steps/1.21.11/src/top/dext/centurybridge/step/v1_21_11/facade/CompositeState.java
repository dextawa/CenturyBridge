package top.dext.centurybridge.step.v1_21_11.facade;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12247;

/**
 * 1.21.10's RenderType.CompositeState (class_1921$class_4688) and its builder: the shards a render type was made of,
 * which 1.21.11 replaced with RenderSetup. rt/CbRenderTypeCreate turns one into 1.21.11's RenderSetup when 1.21.10's
 * RenderType.create is called with it.
 */
@Environment(EnvType.CLIENT)
public final class CompositeState {
    public final RenderStateShard.EmptyTextureStateShard field_21406;
    public final RenderStateShard.LightmapStateShard cb$lightmap;
    public final RenderStateShard.OverlayStateShard cb$overlay;
    public final RenderStateShard.LayeringStateShard cb$layering;
    public final RenderStateShard.OutputStateShard field_57931;
    public final RenderStateShard.TexturingStateShard cb$texturing;
    public final RenderStateShard.LineStateShard cb$line;
    public final class_12247.class_4750 field_21852;
    public final ImmutableList<RenderStateShard> field_21422;

    public CompositeState(RenderStateShard.EmptyTextureStateShard texture, RenderStateShard.LightmapStateShard lightmap,
                          RenderStateShard.OverlayStateShard overlay, RenderStateShard.LayeringStateShard layering,
                          RenderStateShard.OutputStateShard output, RenderStateShard.TexturingStateShard texturing,
                          RenderStateShard.LineStateShard line, class_12247.class_4750 outlineProperty) {
        this.field_21406 = texture;
        this.cb$lightmap = lightmap;
        this.cb$overlay = overlay;
        this.cb$layering = layering;
        this.field_57931 = output;
        this.cb$texturing = texturing;
        this.cb$line = line;
        this.field_21852 = outlineProperty;
        this.field_21422 = ImmutableList.of(texture, lightmap, overlay, layering, output, texturing, line);
    }

    @Override
    public String toString() {
        return "CompositeState[" + this.field_21422 + ", outlineProperty=" + this.field_21852 + "]";
    }

    /** builder */
    public static Builder method_23598() {
        return new Builder();
    }

    /** 1.21.10 RenderType.CompositeState.CompositeStateBuilder (vanilla's defaults) */
    @Environment(EnvType.CLIENT)
    public static class Builder {
        public RenderStateShard.EmptyTextureStateShard field_29462 = RenderStateShard.field_21378;
        public RenderStateShard.LightmapStateShard field_21430 = RenderStateShard.field_21384;
        public RenderStateShard.OverlayStateShard field_21431 = RenderStateShard.field_21386;
        public RenderStateShard.LayeringStateShard field_21433 = RenderStateShard.field_21352;
        public RenderStateShard.OutputStateShard field_21434 = RenderStateShard.field_21358;
        public RenderStateShard.TexturingStateShard field_21435 = RenderStateShard.field_21379;
        public RenderStateShard.LineStateShard field_21437 = RenderStateShard.field_21360;

        public Builder() {
        }

        /** setTextureState */
        public Builder method_34577(RenderStateShard.EmptyTextureStateShard s) {
            this.field_29462 = s;
            return this;
        }

        /** setLightmapState */
        public Builder method_23608(RenderStateShard.LightmapStateShard s) {
            this.field_21430 = s;
            return this;
        }

        /** setOverlayState */
        public Builder method_23611(RenderStateShard.OverlayStateShard s) {
            this.field_21431 = s;
            return this;
        }

        /** setLayeringState */
        public Builder method_23607(RenderStateShard.LayeringStateShard s) {
            this.field_21433 = s;
            return this;
        }

        /** setOutputState */
        public Builder method_23610(RenderStateShard.OutputStateShard s) {
            this.field_21434 = s;
            return this;
        }

        /** setTexturingState */
        public Builder method_23614(RenderStateShard.TexturingStateShard s) {
            this.field_21435 = s;
            return this;
        }

        /** setLineState */
        public Builder method_23609(RenderStateShard.LineStateShard s) {
            this.field_21437 = s;
            return this;
        }

        /** createCompositeState(outline): AFFECTS_OUTLINE or NONE */
        public CompositeState method_23617(boolean outline) {
            return this.method_24297(outline ? class_12247.class_4750.field_21855 : class_12247.class_4750.field_21853);
        }

        /** createCompositeState(outlineProperty) */
        public CompositeState method_24297(class_12247.class_4750 outlineProperty) {
            return new CompositeState(this.field_29462, this.field_21430, this.field_21431, this.field_21433, this.field_21434,
                this.field_21435, this.field_21437, outlineProperty);
        }
    }
}
