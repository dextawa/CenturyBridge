package top.dext.centurybridge.step.v26_2.mixin;

import java.util.Queue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleGroup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v26_2.rt.CbVanillaReads;

/**
 * 26.1.2 ParticleGroup.getAll(): the group's particle queue. 26.2 keeps the same protected final {@code particles} queue
 * (javap of both) and dropped only the getter; restored with 26.1.2's body. Priority below every mod's.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = ParticleGroup.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbS26ParticleGroupRestoredMixin<P extends Particle> {

    @Shadow
    @Final
    protected Queue<P> particles;

    /** getAll() */
    public Queue<P> getAll() {
        return this.particles;
    }
}
