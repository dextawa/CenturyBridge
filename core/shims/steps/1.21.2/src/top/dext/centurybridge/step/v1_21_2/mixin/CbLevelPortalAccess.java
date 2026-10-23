package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelMisc;

/** PortalShape's fields, for a 1.21.1 subclass's super(level, pos, axis) (CbLevelMisc) */
@Mixin(net.minecraft.class_2424.class)
public interface CbLevelPortalAccess extends CbLevelMisc.PortalAccess {

    @Override
    @Accessor("field_11317")
    net.minecraft.class_2350.class_2351 cb$axis();

    @Override
    @Accessor("field_11313")
    int cb$portalBlocks();

    @Override
    @Accessor("field_11314")
    net.minecraft.class_2350 cb$rightDir();

    @Override
    @Accessor("field_11316")
    net.minecraft.class_2338 cb$bottomLeft();

    @Override
    @Accessor("field_11312")
    int cb$height();

    @Override
    @Accessor("field_11311")
    int cb$width();
}
