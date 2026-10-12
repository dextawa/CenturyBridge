package top.dext.centurybridge.step.v1_20_3.rt;

/**
 * Items: the 1.20.2 item API on 1.20.3. Wired from s2world.json.
 */
public final class CbWorldItems {

    /**
     * MobEffectUtil.formatDuration and PotionUtils.addPotionTooltip gained a tick-rate argument in 1.20.3
     * (durations print as ticks / rate). 1.20.2 counted 20 ticks to the second, always: a 1.20.2 caller's
     * tooltip reads as it did.
     */
    public static float legacyTickRate() {
        return 20.0F;
    }

    private CbWorldItems() {
    }
}
