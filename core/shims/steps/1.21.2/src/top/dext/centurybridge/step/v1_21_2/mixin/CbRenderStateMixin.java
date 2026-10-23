package top.dext.centurybridge.step.v1_21_2.mixin;

import java.lang.ref.WeakReference;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderStateEntity;

/** EntityRenderState remembers, weakly, the entity and partial tick of its latest extraction (CbRenderStateEntity). */
@Mixin(net.minecraft.class_10017.class)
public abstract class CbRenderStateMixin implements CbRenderStateEntity {

    @Unique
    private WeakReference<net.minecraft.class_1297> cb$entity;

    @Unique
    private float cb$partialTick = 1.0F;

    @Unique
    private int cb$tick;

    @Override
    public net.minecraft.class_1297 cb$entity() {
        WeakReference<net.minecraft.class_1297> r = this.cb$entity;
        return r == null ? null : r.get();
    }

    @Override
    public float cb$partialTick() {
        return this.cb$partialTick;
    }

    @Override
    public void cb$capture(net.minecraft.class_1297 entity, float partialTick) {
        net.minecraft.class_1297 had = cb$entity();
        if (had != entity) {
            this.cb$entity = entity == null ? null : new WeakReference<>(entity);
        }
        this.cb$partialTick = partialTick;
        this.cb$tick = entity == null ? 0 : entity.field_6012;
    }

    @Override
    public int cb$tick() {
        return this.cb$tick;
    }
}
