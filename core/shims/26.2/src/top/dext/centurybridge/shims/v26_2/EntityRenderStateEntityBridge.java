package top.dext.centurybridge.shims.v26_2;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.rt.v26_2.CbRenderStateEntity;

/** Every entity render state remembers the entity and partial tick it was extracted from. */
@Mixin(EntityRenderState.class)
public abstract class EntityRenderStateEntityBridge implements CbRenderStateEntity {

    @Unique
    private Entity centurybridge$entity;
    @Unique
    private float centurybridge$partialTick;

    @Override
    public Entity centurybridge$entity() {
        return centurybridge$entity;
    }

    @Override
    public float centurybridge$partialTick() {
        return centurybridge$partialTick;
    }

    @Override
    public void centurybridge$capture(Entity entity, float partialTick) {
        this.centurybridge$entity = entity;
        this.centurybridge$partialTick = partialTick;
    }
}
