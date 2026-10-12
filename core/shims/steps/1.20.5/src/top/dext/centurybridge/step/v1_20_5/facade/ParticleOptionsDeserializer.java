package top.dext.centurybridge.step.v1_20_5.facade;

/**
 * 1.20.4's ParticleOptions.Deserializer&lt;T&gt;: how a particle type's options are read from a command (after
 * the type's id) and from the network. 1.20.5 replaced it with the type's map codec and stream codec;
 * rt/CbParticles keeps 1.20.4's deserializers -- a converted type's own, vanilla's -- and reads with
 * them where 1.20.4 did.
 */
public interface ParticleOptionsDeserializer<T extends net.minecraft.class_2394> {

    /** fromCommand(type, reader) */
    T method_10296(net.minecraft.class_2396<T> type, com.mojang.brigadier.StringReader reader)
        throws com.mojang.brigadier.exceptions.CommandSyntaxException;

    /** fromNetwork(type, buf) */
    T method_10297(net.minecraft.class_2396<T> type, net.minecraft.class_2540 buf);
}
