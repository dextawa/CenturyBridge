package top.dext.centurybridge.step.v1_20_5.rt;

import net.minecraft.class_2540;
import net.minecraft.class_9129;

/** FriendlyByteBuf helpers for the 1.20.4 -> 1.20.5 networking bridges (area s4net). */
public final class CbNetBufs {

    /**
     * The RegistryFriendlyByteBuf 1.20.5 reads or writes a value with, for a buffer a 1.20.4 caller
     * handed over: the buffer itself when it is one (everything vanilla passes in play is), else a
     * view of it -- reads and writes go straight through to it, indices included -- with the
     * registries of the game it is used in ({@link CbNetRegistries}).
     */
    public static class_9129 cbRegistryBuf(class_2540 buf) {
        if (buf instanceof class_9129 registryBuf) {
            return registryBuf;
        }
        return new class_9129(buf, CbNetRegistries.cbCurrent());
    }

    private CbNetBufs() {
    }
}
