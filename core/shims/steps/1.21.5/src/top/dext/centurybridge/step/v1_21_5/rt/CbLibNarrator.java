package top.dext.centurybridge.step.v1_21_5.rt;

/**
 * text2speech 1.17.9 (1.21.4) -> 1.18.11 (1.21.5): the library front of this step
 * (core/segments/damage-lib/1.21.4__1.21.5.tsv). Narrator.say(text, interrupt) took a volume argument; 1.17.9 spoke at the
 * engine's own level, which 1.18.11 keeps for volume 1 (NarratorLinux scales the wave only when volume != 1; NarratorWindows
 * sets the SAPI volume to it, whose default is full) -- so a 1.17.9 call, or a 1.17.9 narrator's own super call entered
 * directly, speaks at volume 1.
 */
public final class CbLibNarrator {
    private CbLibNarrator() {
    }

    /** the volume a 1.17.9 say() spoke at */
    public static float fullVolume() {
        return 1.0F;
    }
}
