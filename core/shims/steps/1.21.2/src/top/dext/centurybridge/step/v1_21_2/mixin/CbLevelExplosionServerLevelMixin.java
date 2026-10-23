package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_2394;
import net.minecraft.class_3414;
import net.minecraft.class_5362;
import net.minecraft.class_6880;
import net.minecraft.class_9892;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelExplosions;

/**
 * ServerLevel.explode: the ServerExplosion it makes remembers the particles and sound it was made with (1.21.1's
 * Explosion getters answered them) and is handed to a 1.21.1 Level.explode call, which answered the explosion
 * (CbLevelExplosions).
 */
@Mixin(net.minecraft.class_3218.class)
public abstract class CbLevelExplosionServerLevelMixin {

    @Redirect(method = "method_8454", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_9892;method_61737()V"))
    private void cb$made(class_9892 explosion, class_1297 source, class_1282 damageSource, class_5362 calculator, double x,
                         double y, double z, float radius, boolean fire, class_1937.class_7867 interaction,
                         class_2394 small, class_2394 large, class_6880<class_3414> sound) {
        ((CbLevelExplosions.ServerState) explosion).cb$effects(small, large, sound);
        CbLevelExplosions.made(explosion);
        explosion.method_61737();
    }
}
