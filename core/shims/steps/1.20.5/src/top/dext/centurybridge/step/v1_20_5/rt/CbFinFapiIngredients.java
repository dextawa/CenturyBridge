package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * Fabric API's CustomIngredientSerializer from 1.20.4 on 1.20.5's (area s4fin, Fabric API side). 1.20.4's
 * serializer answered a Codec for getCodec(allowEmpty) and read / wrote the network form itself; 1.20.5's answers a
 * MapCodec and a packet codec. A 1.20.4 serializer's three methods become 1.20.5's two (adapter hook); calls of
 * 1.20.4's three on any serializer go through 1.20.5's.
 */
public final class CbFinFapiIngredients {

    /** a 1.20.4 serializer: getCodec(allowEmpty) a Codec, read(buf), write(buf, ingredient) */
    public interface Legacy {
        com.mojang.serialization.Codec<?> getCodec(boolean allowEmpty);

        net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient read(net.minecraft.class_2540 buf);

        void write(net.minecraft.class_2540 buf, net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient ingredient);
    }

    /** getCodec(allowEmpty) of a 1.20.4 serializer: its codec as a map codec */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static com.mojang.serialization.MapCodec mapCodec(Legacy legacy, boolean allowEmpty) {
        return CbCodecs.toMapCodec((com.mojang.serialization.Codec) legacy.getCodec(allowEmpty));
    }

    /** getPacketCodec() of a 1.20.4 serializer: its read / write */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_9139 packetCodec(Legacy legacy) {
        return net.minecraft.class_9139.method_56437(
            (buf, value) -> legacy.write((net.minecraft.class_2540) buf, (net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient) value),
            buf -> legacy.read((net.minecraft.class_2540) buf));
    }

    /** serializer.getCodec(allowEmpty) as 1.20.4's Codec: the class's own (a 1.20.4 one), else 1.20.5's map codec as a codec */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static com.mojang.serialization.Codec getCodec(net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer serializer, boolean allowEmpty) {
        if (serializer instanceof Legacy legacy) {
            return legacy.getCodec(allowEmpty);
        }
        return CbCodecs.fromMapCodec(serializer.getCodec(allowEmpty));
    }

    /** serializer.read(buf): 1.20.5's packet codec */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient read(
            net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer serializer, net.minecraft.class_2540 buf) {
        if (serializer instanceof Legacy legacy) {
            return legacy.read(buf);
        }
        return (net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient) serializer.getPacketCodec().decode(CbNetBufs.cbRegistryBuf(buf));
    }

    /** serializer.write(buf, ingredient): 1.20.5's packet codec */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void write(net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer serializer, net.minecraft.class_2540 buf,
                             net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient ingredient) {
        if (serializer instanceof Legacy legacy) {
            legacy.write(buf, ingredient);
            return;
        }
        serializer.getPacketCodec().encode(CbNetBufs.cbRegistryBuf(buf), ingredient);
    }

    private CbFinFapiIngredients() {
    }
}
