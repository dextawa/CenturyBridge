package top.dext.centurybridge.step.v26_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 26.1.2's LevelRenderer.ticks, restored on LevelRenderer (mixin/CbS26LevelTicksMixin) and kept on 26.2's clock
 * (mixin/CbS26LevelTicksClockMixin).
 * <p>26.1.2: LevelRenderer.tick(Camera), once per client tick while the level's tick rate runs normally, did
 * {@code ticks++}, then WeatherEffectRenderer.tickRainParticles(level, camera, ticks, ...) and
 * removeBlockBreakingProgress() (expiry: ticks - progress.getUpdatedRenderTick() > 400); destroyBlockProgress stamped
 * progress.updateTick(ticks); extractLevel gave ticks to WeatherEffectRenderer.extractRenderState. 26.2 (javap): that
 * body is ClientLevel.tick's (runsNormally: tickTime() -- game time + 1 --, tickWeatherEffects(),
 * removeBlockBreakingProgress()), and every one of those readers reads ClientLevel.getGameTime() where 26.1.2 read
 * ticks (tickWeatherEffects @51, removeBlockBreakingProgress @1, destroyBlockProgress @148,
 * WeatherEffectRenderer.extractRenderState @299 / @342). So the restored field holds (int) getGameTime() of the
 * level being drawn, set where 26.2 advances or sets it: after tickTime() in ClientLevel.tick (26.1.2's ticks++) and
 * after setTimeFromServer. It agrees with BlockDestructionProgress.getUpdatedRenderTick() as the layer gives it
 * (rt/CbS26BlockDestruction, the low 32 bits of 26.2's long tick).
 * <p>A mod's write to the field is kept until the next tick but is not read by 26.2's vanilla, which reads the game
 * time (release_notes_known.md).
 */
@Environment(EnvType.CLIENT)
public interface CbS26LevelTicks {
    void cbS26$setTicks(int ticks);
}
