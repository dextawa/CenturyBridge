package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderModelBase;

/**
 * 1.21.1's ListModel (net/minecraft/class_4595, classRenames), gone in 1.21.2: a model drawing the parts its subclass
 * lists. 1.21.1's bodies, on 1.21.2's EntityModel with an empty root (CbRenderModelBase: this model draws itself).
 */
@Environment(EnvType.CLIENT)
public abstract class ListModel<E extends net.minecraft.class_1297> extends net.minecraft.class_583<net.minecraft.class_10017> {

    public ListModel() {
        this(net.minecraft.class_1921::method_23578);
    }

    public ListModel(Function<net.minecraft.class_2960, net.minecraft.class_1921> renderType) {
        super(CbRenderModelBase.emptyRoot(), renderType);
    }

    /** renderToBuffer: every part of parts() */
    public void method_2828(net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer, int light, int overlay, int color) {
        this.method_22960().forEach(part -> part.method_22699(pose, consumer, light, overlay, color));
    }

    /** parts() */
    public abstract Iterable<net.minecraft.class_630> method_22960();
}
