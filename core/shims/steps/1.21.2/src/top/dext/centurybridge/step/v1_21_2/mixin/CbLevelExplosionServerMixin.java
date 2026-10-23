package top.dext.centurybridge.step.v1_21_2.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.class_10209;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_2394;
import net.minecraft.class_243;
import net.minecraft.class_3218;
import net.minecraft.class_3414;
import net.minecraft.class_3695;
import net.minecraft.class_5712;
import net.minecraft.class_6880;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelExplosions;

/**
 * ServerExplosion as 1.21.1 saw its Explosion (CbLevelExplosions): the exploded positions are kept as the explosion's
 * live toBlow -- 1.21.2's explode() blows the very list it records, so a clearToBlow made while it runs (from a
 * callback during hurtEntities, as 1.21.1 allowed between its phases) keeps the blocks, as it did in 1.21.1 -- and the
 * two 1.21.1 phases run 1.21.2's own steps.
 */
@Mixin(net.minecraft.class_9892.class)
public abstract class CbLevelExplosionServerMixin implements CbLevelExplosions.ServerState {

    @Shadow
    @Final
    private boolean field_52620;
    @Shadow
    @Final
    private class_3218 field_52622;
    @Shadow
    @Final
    private class_243 field_52623;
    @Shadow
    @Final
    private class_1297 field_52624;

    @Unique
    private final ObjectArrayList<class_2338> cb$toBlow = new ObjectArrayList<>();
    @Unique
    private class_2394 cb$small;
    @Unique
    private class_2394 cb$large;
    @Unique
    private class_6880<class_3414> cb$sound;

    @Shadow
    abstract List<class_2338> method_61740();

    @Shadow
    abstract void method_61741();

    @Shadow
    abstract boolean method_61742();

    @Shadow
    abstract void method_61732(List<class_2338> positions);

    @Shadow
    abstract void method_61735(List<class_2338> positions);

    @Redirect(method = "method_61737", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_9892;method_61740()Ljava/util/List;"))
    private List<class_2338> cb$record(net.minecraft.class_9892 self) {
        this.cb$toBlow.addAll(this.method_61740());
        return this.cb$toBlow;
    }

    @Override
    public List<class_2338> cb$toBlow() {
        return this.cb$toBlow;
    }

    /** 1.21.1's explode(): the game event, the positions to blow (added to toBlow), the entities */
    @Override
    public void cb$explodePhase() {
        this.field_52622.method_43275(this.field_52624, class_5712.field_28178, this.field_52623);
        this.cb$toBlow.addAll(this.method_61740());
        this.method_61741();
    }

    /** 1.21.1's finalizeExplosion() on a server level: the blocks of toBlow, then fire */
    @Override
    public void cb$finalizePhase() {
        if (this.method_61742()) {
            class_3695 profiler = class_10209.method_64146();
            profiler.method_15396("explosion_blocks");
            this.method_61732(this.cb$toBlow);
            profiler.method_15407();
        }
        if (this.field_52620) {
            this.method_61735(this.cb$toBlow);
        }
    }

    @Override
    public void cb$effects(class_2394 small, class_2394 large, class_6880<class_3414> sound) {
        this.cb$small = small;
        this.cb$large = large;
        this.cb$sound = sound;
    }

    @Override
    public class_2394 cb$small() {
        return this.cb$small;
    }

    @Override
    public class_2394 cb$large() {
        return this.cb$large;
    }

    @Override
    public class_6880<class_3414> cb$sound() {
        return this.cb$sound;
    }
}
