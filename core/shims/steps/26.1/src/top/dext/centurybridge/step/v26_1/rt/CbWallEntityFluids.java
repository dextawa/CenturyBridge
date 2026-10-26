package top.dext.centurybridge.step.v26_1.rt;

import it.unimi.dsi.fastutil.objects.AbstractObject2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.List;
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

    /** the heights 1.21.11's measurement recorded for tags 26.1 does not track (mixin/CbWallEntityFluidMixin) */
    public interface Legacy {
        Object2DoubleMap<TagKey<Fluid>> cb$legacyFluidHeights();
    }

    /**
     * 1.21.11 Entity.fluidHeight (class_1297.field_5964, protected, read by subclasses): a view answering what
     * getFluidHeight(tag) answers -- 26.1's EntityFluidInteraction for water and lava, 1.21.11's measurement for any
     * other tag -- and listing the tags with a height. A value a mod puts is kept and read back through the view (and,
     * for a tag 26.1 does not track, through getFluidHeight); for water and lava 26.1's own measurement stays what vanilla
     * reads (known loss).
     */
    public static Object2DoubleMap<TagKey<Fluid>> fluidHeight(Entity entity) {
        return new HeightView(entity);
    }

    /** 1.21.11 `entity.fluidHeight = map` (class_1297.field_5964): its heights replace the recorded ones */
    public static void setFluidHeight(Entity entity, Object2DoubleMap<TagKey<Fluid>> heights) {
        Object2DoubleMap<TagKey<Fluid>> legacy = ((Legacy) entity).cb$legacyFluidHeights();
        if (heights instanceof HeightView view && view.entity == entity) {
            return;
        }
        legacy.clear();
        legacy.putAll(heights);
    }

    private static final class HeightView extends AbstractObject2DoubleMap<TagKey<Fluid>> {
        final Entity entity;

        HeightView(Entity entity) {
            this.entity = entity;
        }

        private Object2DoubleMap<TagKey<Fluid>> legacy() {
            return ((Legacy) this.entity).cb$legacyFluidHeights();
        }

        @Override
        @SuppressWarnings("unchecked")
        public double getDouble(Object key) {
            if (!(key instanceof TagKey<?> tag)) {
                return defRetValue;
            }
            Object2DoubleMap<TagKey<Fluid>> legacy = legacy();
            if (legacy.containsKey(tag)) {
                return legacy.getDouble(tag);
            }
            return this.entity.getFluidHeight((TagKey<Fluid>) tag);
        }

        @Override
        public boolean containsKey(Object key) {
            return key instanceof TagKey<?> && (legacy().containsKey(key) || getDouble(key) != 0.0);
        }

        @Override
        public double put(TagKey<Fluid> key, double value) {
            double old = getDouble(key);
            legacy().put(key, value);
            return old;
        }

        @Override
        public double removeDouble(Object key) {
            double old = getDouble(key);
            legacy().removeDouble(key);
            return old;
        }

        @Override
        public void clear() {
            legacy().clear();
        }

        @Override
        public int size() {
            return entries().size();
        }

        private Object2DoubleMap<TagKey<Fluid>> entries() {
            Object2DoubleMap<TagKey<Fluid>> all = new Object2DoubleArrayMap<>(legacy());
            for (TagKey<Fluid> tag : List.of(FluidTags.WATER, FluidTags.LAVA)) {
                double h = this.entity.getFluidHeight(tag);
                if (h != 0.0 && !all.containsKey(tag)) {
                    all.put(tag, h);
                }
            }
            return all;
        }

        @Override
        public ObjectSet<Object2DoubleMap.Entry<TagKey<Fluid>>> object2DoubleEntrySet() {
            return entries().object2DoubleEntrySet();
        }
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
