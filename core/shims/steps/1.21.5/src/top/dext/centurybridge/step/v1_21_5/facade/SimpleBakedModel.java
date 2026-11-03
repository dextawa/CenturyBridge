package top.dext.centurybridge.step.v1_21_5.facade;

import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_5.rt.CbBakedModels;

/**
 * 1.21.4 SimpleBakedModel (class_1093, gone in 1.21.5; javap -c -p 1.21.4): unculled quads, quads per culling side, ao,
 * isGui3d, usesBlockLight, particle, transforms; getQuads(state, side, random) = side == null ? unculled :
 * culled.get(side). Members public (access wideners never reach a CenturyBridge class). 1.21.5's collectParts /
 * particleIcon come from the 1.21.4 methods (CbBakedModels.collectParts). bake (method_65762) and its sprite lookup
 * (method_65761) take 1.21.4's SpriteGetter (class_10527), gone in 1.21.5 with no class of that shape: facadeGaps.
 */
@Environment(EnvType.CLIENT)
public class SimpleBakedModel implements CbBakedModels.LegacyBakedModel, CbBakedModels.LegacyItemProps {
    /** the particle texture slot (1.21.4 constant) */
    public static final String field_55481 = "particle";

    public final List<net.minecraft.class_777> field_5411;
    public final Map<net.minecraft.class_2350, List<net.minecraft.class_777>> field_5414;
    /** ambient occlusion */
    public final boolean field_5415;
    /** isGui3d */
    public final boolean field_5413;
    /** usesBlockLight */
    public final boolean field_21864;
    public final net.minecraft.class_1058 field_5416;
    public final net.minecraft.class_809 field_5417;

    /** (unculled, culled, ao, usesBlockLight, isGui3d, particle, transforms), 1.21.4's parameter order */
    public SimpleBakedModel(List<net.minecraft.class_777> unculled, Map<net.minecraft.class_2350, List<net.minecraft.class_777>> culled,
            boolean ao, boolean usesBlockLight, boolean isGui3d, net.minecraft.class_1058 particle, net.minecraft.class_809 transforms) {
        this.field_5411 = unculled;
        this.field_5414 = culled;
        this.field_5415 = ao;
        this.field_5413 = isGui3d;
        this.field_21864 = usesBlockLight;
        this.field_5416 = particle;
        this.field_5417 = transforms;
    }

    /** bakeFace: 1.21.4's FaceBakery.bakeQuad call with the element's from / to / rotation / shade / light emission */
    public static net.minecraft.class_777 method_65760(net.minecraft.class_785 element, net.minecraft.class_783 face,
            net.minecraft.class_1058 sprite, net.minecraft.class_2350 side, net.minecraft.class_3665 state) {
        return net.minecraft.class_796.method_3468(element.comp_3727(), element.comp_3728(), face, sprite, side, state,
            element.comp_3730(), element.comp_3731(), element.comp_3732());
    }

    @Override
    public List<net.minecraft.class_777> method_4707(net.minecraft.class_2680 state, net.minecraft.class_2350 side, net.minecraft.class_5819 random) {
        return side == null ? this.field_5411 : this.field_5414.get(side);
    }

    @Override
    public boolean method_4708() {
        return this.field_5415;
    }

    @Override
    public boolean method_4712() {
        return this.field_5413;
    }

    @Override
    public boolean method_24304() {
        return this.field_21864;
    }

    @Override
    public net.minecraft.class_1058 method_4711() {
        return this.field_5416;
    }

    @Override
    public net.minecraft.class_809 method_4709() {
        return this.field_5417;
    }

    @Override
    public void method_68513(net.minecraft.class_5819 random, List<net.minecraft.class_10889> parts) {
        CbBakedModels.collectParts(this, random, parts);
    }

    @Override
    public net.minecraft.class_1058 method_68511() {
        return CbBakedModels.particleIcon(this);
    }
}
