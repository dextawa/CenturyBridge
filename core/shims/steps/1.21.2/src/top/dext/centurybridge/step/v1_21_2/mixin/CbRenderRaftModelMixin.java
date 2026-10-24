package top.dext.centurybridge.step.v1_21_2.mixin;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * 1.21.1 RaftModel's parts (see CbRenderBoats): createPartsBuilder(root) -- bottom and the two paddles, the chest raft's
 * also its chest parts (1.21.2's RaftModel baked from the chest raft layer has them) -- and parts(), built once.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_7754.class)
public abstract class CbRenderRaftModelMixin {

    @Unique
    private ImmutableList<net.minecraft.class_630> cb$parts;

    /** 1.21.1 createPartsBuilder(root) */
    protected ImmutableList.Builder<net.minecraft.class_630> method_45710(net.minecraft.class_630 root) {
        return top.dext.centurybridge.step.v1_21_2.rt.CbRenderBoats.raftParts(root);
    }

    /** 1.21.1 parts() */
    public ImmutableList<net.minecraft.class_630> method_45715() {
        if (this.cb$parts == null) {
            this.cb$parts = this.method_45710(((net.minecraft.class_3879) (Object) this).method_63512()).build();
        }
        return this.cb$parts;
    }
}
