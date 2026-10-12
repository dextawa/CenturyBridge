package top.dext.centurybridge.step.v1_20_5.rt;

import net.minecraft.class_2540;
import net.minecraft.class_9129;

/** FriendlyByteBuf helpers for the 1.20.4 -> 1.20.5 networking bridges (area s4net). */
public final class CbNetBufs {

    /**
     * The RegistryFriendlyByteBuf 1.20.5 reads or writes a value with, for a buffer a 1.20.4 caller
     * handed over. A 1.20.4 FriendlyByteBuf carried no registries -- items went over the wire as a
     * static-registry id plus NBT, components as NBT -- where 1.20.5 writes items, holders and
     * components through a RegistryFriendlyByteBuf whose registries the connection supplies. So: the
     * buffer itself when it is one (everything vanilla passes in play is), else a view of it -- reads
     * and writes go straight through to it, indices included -- over the registries of the running
     * world ({@link CbRegistries#currentAccess()}, the step's one tracker).
     */
    public static class_9129 cbRegistryBuf(class_2540 buf) {
        if (buf instanceof class_9129 registryBuf) {
            return registryBuf;
        }
        return new class_9129(buf, CbRegistries.currentAccess());
    }

    private CbNetBufs() {
    }
}
