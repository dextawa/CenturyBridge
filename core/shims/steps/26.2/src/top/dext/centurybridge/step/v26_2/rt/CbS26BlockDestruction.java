package top.dext.centurybridge.step.v26_2.rt;

import net.minecraft.server.level.BlockDestructionProgress;

/**
 * 26.1.2's int tick of BlockDestructionProgress on 26.2's long one (javap of both). 26.1.2 stamped a progress with
 * LevelRenderer.ticks (int, the renderer's tick count) in LevelRenderer.destroyBlockProgress and dropped it in
 * removeBlockBreakingProgress once {@code ticks - getUpdatedRenderTick() > 400}. 26.2 moved both to ClientLevel and
 * the clock to the level's game time: updateTick(getGameTime()) and {@code getGameTime() - getUpdatedRenderTick() >
 * 400}, the field widened to long. The tick a 26.1.2 caller hands in or reads is therefore taken as the low 32 bits of
 * that clock:
 * <ul>
 *   <li>updateTick(int): the long tick nearest the one the progress holds whose low 32 bits are the int (26.1.2's int
 *       clock differences survive the int wrap; a progress never stamped holds 0, so the int is sign-extended);</li>
 *   <li>getUpdatedRenderTick()I: the low 32 bits of the long tick.</li>
 * </ul>
 * The same answers serve a 26.1.2 subclass's overrides (overrideHooks in s26d-misc.json): 26.2's vanilla hands its
 * updateTick(long) the low 32 bits and reads its getUpdatedRenderTick() answer sign-extended (exact while the game time
 * stays below 2^31 ticks, about 3.4 years of play; the override cannot see the tick it replaces).
 */
public final class CbS26BlockDestruction {
    private CbS26BlockDestruction() {}

    /** instance redirect: 26.1.2 BlockDestructionProgress.updateTick(int) */
    public static void updateTick(BlockDestructionProgress progress, int tick) {
        long held = progress.getUpdatedRenderTick();
        progress.updateTick(held + (tick - (int) held));
    }

    /** instance redirect: 26.1.2 BlockDestructionProgress.getUpdatedRenderTick()I */
    public static int getUpdatedRenderTick(BlockDestructionProgress progress) {
        return (int) progress.getUpdatedRenderTick();
    }

    /** override hook wrap: 26.2's long tick handed to a 26.1.2 updateTick(int) override */
    public static int toOldTick(long tick) {
        return (int) tick;
    }

    /** override hook unwrap: a 26.1.2 getUpdatedRenderTick()I override's answer as 26.2's long tick */
    public static long toNewTick(int tick) {
        return tick;
    }
}
