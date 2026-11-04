package top.dext.centurybridge.step.v1_21_5.facade;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.util.EnumMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.4 SimpleBakedModel.Builder (class_1093$class_1094, gone in 1.21.5; javap -c -p 1.21.4): constructor (ao,
 * usesBlockLight, isGui3d, transforms), one list builder per side (every side present, empty unless added) plus the
 * unculled one; addCulledFace / addUnculledFace / particle / item (returns itself) / build, which throws "Missing
 * particle!" without a particle and makes a SimpleBakedModel facade. Members public (access wideners never reach a
 * CenturyBridge class).
 */
@Environment(EnvType.CLIENT)
public class SimpleBakedModel$class_1094 {
    public final ImmutableList.Builder<net.minecraft.class_777> field_5419;
    public final EnumMap<net.minecraft.class_2350, ImmutableList.Builder<net.minecraft.class_777>> field_5422;
    /** ao */
    public final boolean field_5421;
    /** particle */
    public net.minecraft.class_1058 field_5424;
    /** usesBlockLight */
    public final boolean field_5420;
    /** isGui3d */
    public final boolean field_21865;
    public final net.minecraft.class_809 field_5418;

    public SimpleBakedModel$class_1094(boolean ao, boolean usesBlockLight, boolean isGui3d, net.minecraft.class_809 transforms) {
        this.field_5419 = ImmutableList.builder();
        this.field_5422 = Maps.newEnumMap(net.minecraft.class_2350.class);
        this.field_5421 = ao;
        this.field_5420 = usesBlockLight;
        this.field_21865 = isGui3d;
        this.field_5418 = transforms;
        for (net.minecraft.class_2350 d : net.minecraft.class_2350.values()) {
            this.field_5422.put(d, ImmutableList.builder());
        }
    }

    /** addCulledFace */
    public SimpleBakedModel$class_1094 method_4745(net.minecraft.class_2350 side, net.minecraft.class_777 quad) {
        this.field_5422.get(side).add(quad);
        return this;
    }

    /** addUnculledFace */
    public SimpleBakedModel$class_1094 method_4748(net.minecraft.class_777 quad) {
        this.field_5419.add(quad);
        return this;
    }

    /** particle */
    public SimpleBakedModel$class_1094 method_4747(net.minecraft.class_1058 sprite) {
        this.field_5424 = sprite;
        return this;
    }

    /** item */
    public SimpleBakedModel$class_1094 method_35809() {
        return this;
    }

    /** build */
    public net.minecraft.class_1087 method_4746() {
        if (this.field_5424 == null) {
            throw new RuntimeException("Missing particle!");
        }
        java.util.Map<net.minecraft.class_2350, java.util.List<net.minecraft.class_777>> culled =
            Maps.transformValues(this.field_5422, b -> b.build());
        return new SimpleBakedModel(this.field_5419.build(), new EnumMap<>(culled), this.field_5421, this.field_5420, this.field_21865,
            this.field_5424, this.field_5418);
    }
}
