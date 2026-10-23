package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's particle command syntax on 1.20.5. 1.20.4's ParticleArgument.readParticle(reader, lookup) read
 * the type's id, then the options in the text form the type's deserializer reads ("dust 1.0 0.5 0.0
 * 1.0"); 1.20.5 reads the options as SNBT after the id and takes a registry provider. The 1.20.4 call
 * keeps 1.20.4's syntax: the id through the given lookup as 1.20.4 read it, the options through the
 * type's 1.20.4 deserializer (CbParticles: a converted type's own, vanilla's 1.20.4 ones for vanilla's).
 */
public final class CbRestParticles {

    /** ParticleArgument.readParticle(reader, lookup): 1.20.4's body */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_2394 method_9418(com.mojang.brigadier.StringReader reader,
                                                     net.minecraft.class_7225<net.minecraft.class_2396<?>> lookup)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        net.minecraft.class_2396 type = readParticleType(reader, lookup);
        return CbParticles.deserializer(type).method_10296(type, reader);
    }

    /** 1.20.4 ParticleArgument.readParticleType(reader, lookup): an unknown id fails without the reader's context */
    private static net.minecraft.class_2396<?> readParticleType(com.mojang.brigadier.StringReader reader,
                                                               net.minecraft.class_7225<net.minecraft.class_2396<?>> lookup)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        net.minecraft.class_2960 id = net.minecraft.class_2960.method_12835(reader);
        net.minecraft.class_5321<net.minecraft.class_2396<?>> key = net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_41210, id);
        java.util.Optional<net.minecraft.class_6880.class_6883<net.minecraft.class_2396<?>>> holder = lookup.method_46746(key);
        if (holder.isEmpty()) {
            throw net.minecraft.class_2223.field_9936.create(id);
        }
        return ((net.minecraft.class_6880<net.minecraft.class_2396<?>>) holder.get()).comp_349();
    }

    private CbRestParticles() {
    }
}
