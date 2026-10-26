package top.dext.centurybridge.step.v26_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.rt.CbS26GlowingEntities;

/**
 * 26.1.2's LevelRenderer.extractVisibleEntities set haveGlowingEntities (javap): it read shouldShowEntityOutlines()
 * once, then for each entity it extracted into entityRenderStates set the flag when the state appearsGlowing() and
 * outlines were shown; it never cleared it. 26.2 moved the extraction to LevelExtractor.extractVisibleEntities (the
 * same loop, the same list) and reads its own shouldShowEntityOutlines(camera) (panoramic camera / no player) later
 * in extract(); after the loop this sets the restored field (mixin/CbS26GlowingStateMixin) the way 26.1.2's loop did,
 * from the states the loop added and the same outline test.
 */
@Environment(EnvType.CLIENT)
@Mixin(LevelExtractor.class)
public abstract class CbS26GlowingExtractMixin {
    @Shadow
    private boolean shouldShowEntityOutlines(Camera camera) {
        throw new AssertionError();
    }

    @Inject(method = "extractVisibleEntities", at = @At("TAIL"))
    private void cbS26$setGlowing(Camera camera, Frustum frustum, DeltaTracker deltaTracker, LevelRenderState state,
                                  CallbackInfo ci) {
        if (!this.shouldShowEntityOutlines(camera)) {
            return;
        }
        for (EntityRenderState entity : state.entityRenderStates) {
            if (entity.appearsGlowing()) {
                ((CbS26GlowingEntities) state).cbS26$setHaveGlowingEntities(true);
                return;
            }
        }
    }
}
