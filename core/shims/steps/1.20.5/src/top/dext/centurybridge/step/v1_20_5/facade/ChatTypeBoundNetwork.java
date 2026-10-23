package top.dext.centurybridge.step.v1_20_5.facade;

import java.util.Optional;

/**
 * 1.20.4's ChatType.BoundNetwork: a bound chat type as the network carried it -- the chat type's id in the
 * CHAT_TYPE registry, the sender's name and the optional target name. 1.20.5 dropped it (ChatType.Bound has
 * a stream codec of its own and the chat packets carry Bound); 1.20.4's record (as a class: its members public),
 * constructors, write and resolve as they were, over 1.20.5's registries and component stream codec.
 */
public final class ChatTypeBoundNetwork {

    /** chatType (its id in the CHAT_TYPE registry); fields public, as a facade's members must be */
    public final int comp_922;

    /** name */
    public final net.minecraft.class_2561 comp_923;

    /** targetName, or null */
    public final net.minecraft.class_2561 comp_924;

    public ChatTypeBoundNetwork(int chatType, net.minecraft.class_2561 name, net.minecraft.class_2561 targetName) {
        this.comp_922 = chatType;
        this.comp_923 = name;
        this.comp_924 = targetName;
    }

    public int comp_922() {
        return this.comp_922;
    }

    public net.minecraft.class_2561 comp_923() {
        return this.comp_923;
    }

    public net.minecraft.class_2561 comp_924() {
        return this.comp_924;
    }

    /** the record's equality: by its three components */
    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof ChatTypeBoundNetwork b && b.comp_922 == this.comp_922
            && java.util.Objects.equals(b.comp_923, this.comp_923) && java.util.Objects.equals(b.comp_924, this.comp_924);
    }

    @Override
    public int hashCode() {
        return (Integer.hashCode(this.comp_922) * 31 + java.util.Objects.hashCode(this.comp_923)) * 31
            + java.util.Objects.hashCode(this.comp_924);
    }

    @Override
    public String toString() {
        return "BoundNetwork[chatType=" + this.comp_922 + ", name=" + this.comp_923 + ", targetName=" + this.comp_924 + "]";
    }

    /** BoundNetwork(FriendlyByteBuf): var-int id, component, nullable component (1.20.4's wire form, read trusted) */
    public ChatTypeBoundNetwork(net.minecraft.class_2540 buf) {
        this(buf.method_10816(), readComponent(buf), buf.readBoolean() ? readComponent(buf) : null);
    }

    private static net.minecraft.class_2561 readComponent(net.minecraft.class_2540 buf) {
        return net.minecraft.class_8824.field_49666.decode(top.dext.centurybridge.step.v1_20_5.rt.CbNetBufs.cbRegistryBuf(buf));
    }

    private static void writeComponent(net.minecraft.class_2540 buf, net.minecraft.class_2561 c) {
        net.minecraft.class_8824.field_49666.encode(top.dext.centurybridge.step.v1_20_5.rt.CbNetBufs.cbRegistryBuf(buf), c);
    }

    /** write(buf) */
    public void method_44841(net.minecraft.class_2540 buf) {
        buf.method_10804(this.comp_922);
        writeComponent(buf, this.comp_923);
        buf.writeBoolean(this.comp_924 != null);
        if (this.comp_924 != null) {
            writeComponent(buf, this.comp_924);
        }
    }

    /** resolve(registryAccess): the chat type with this id, bound to the names */
    public Optional<net.minecraft.class_2556.class_7602> method_44840(net.minecraft.class_5455 access) {
        net.minecraft.class_2378<net.minecraft.class_2556> r = access.method_30530(net.minecraft.class_7924.field_41237);
        return r.method_40265(this.comp_922).map(h -> new net.minecraft.class_2556.class_7602(
            (net.minecraft.class_6880<net.minecraft.class_2556>) h, this.comp_923, Optional.ofNullable(this.comp_924)));
    }
}
