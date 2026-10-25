package top.dext.centurybridge.step.v26_1.rt;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * 1.21.11's Entity.updateFluidHeightAndDoFluidPushing(tag, speed) (class_1297.method_5692) for a fluid tag 26.1's
 * EntityFluidInteraction does not track. 26.1 tracks water and lava only (Entity's constructor builds the
 * interaction with Set.of(WATER, LAVA)) and answers getFluidHeight 0 for any other tag; 1.21.11 measured any tag a
 * caller named -- a mod's own fluid tag included -- and kept its height in Entity.fluidHeight. This is 1.21.11's
 * measurement, unchanged: the fluid height over the entity's box (deflated by 0.001), the flow of the matching
 * fluid blocks averaged and scaled by speed when the entity is pushed by fluids, the small-motion floor of 0.0045,
 * and the height recorded per tag (the mixin keeps those heights and answers getFluidHeight for them).
 */
public final class CbWallEntityFluids {

    private CbWallEntityFluids() {
    }

    /** whether 26.1's EntityFluidInteraction tracks the tag (Entity's constructor: water and lava) */
    public static boolean tracked(TagKey<Fluid> tag) {
        return FluidTags.WATER.equals(tag) || FluidTags.LAVA.equals(tag);
    }

    /** 1.21.11's updateFluidHeightAndDoFluidPushing body after the unloaded-chunk test; the height goes to heights */
    public static boolean measureAndPush(Entity entity, TagKey<Fluid> tag, double speed, Object2DoubleMap<TagKey<Fluid>> heights) {
        AABB box = entity.getBoundingBox().deflate(0.001);
        int x0 = Mth.floor(box.minX);
        int x1 = Mth.ceil(box.maxX);
        int y0 = Mth.floor(box.minY);
        int y1 = Mth.ceil(box.maxY);
        int z0 = Mth.floor(box.minZ);
        int z1 = Mth.ceil(box.maxZ);
        double height = 0.0;
        boolean pushed = entity.isPushedByFluid();
        boolean inside = false;
        Vec3 flow = Vec3.ZERO;
        int count = 0;
        Level level = entity.level();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                for (int z = z0; z < z1; z++) {
                    pos.set(x, y, z);
                    FluidState fluid = level.getFluidState(pos);
                    if (fluid.is(tag)) {
                        double top = y + fluid.getHeight(level, pos);
                        if (top >= box.minY) {
                            inside = true;
                            height = Math.max(top - box.minY, height);
                            if (pushed) {
                                Vec3 f = fluid.getFlow(level, pos);
                                if (height < 0.4) {
                                    f = f.scale(height);
                                }
                                flow = flow.add(f);
                                count++;
                            }
                        }
                    }
                }
            }
        }
        if (flow.length() > 0.0) {
            if (count > 0) {
                flow = flow.scale(1.0 / count);
            }
            if (!(entity instanceof Player)) {
                flow = flow.normalize();
            }
            Vec3 motion = entity.getDeltaMovement();
            flow = flow.scale(speed);
            if (Math.abs(motion.x) < 0.003 && Math.abs(motion.z) < 0.003 && flow.length() < 0.0045000000000000005) {
                flow = flow.normalize().scale(0.0045000000000000005);
            }
            entity.setDeltaMovement(entity.getDeltaMovement().add(flow));
        }
        heights.put(tag, height);
        return inside;
    }
}
