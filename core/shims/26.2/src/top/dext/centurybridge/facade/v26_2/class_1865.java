package top.dext.centurybridge.facade.v26_2;

/**
 * Facade for 1.20.1 net/minecraft/class_1865 (RecipeSerializer), which in 1.20.1
 * is an INTERFACE mods implement to serialize custom recipes. On 26.2 it became
 * a concrete class (recipe serialization is codec-based), so a legacy
 * `implements class_1865` fails to load (VerifyError: implementing a class).
 *
 * This interface facade keeps those serializer classes loadable: the mod's
 * serializer implements this instead of the now-concrete vanilla type, and its
 * three serialization method bodies resolve as before. Whether 26.2's codec
 * recipe system actually consumes the serializer is a separate (ledgered)
 * concern -- a mod serializer built on the old fromJson/fromNetwork/toNetwork
 * shape cannot register into the codec system; this only removes the
 * class-load failure (the "load, degrade lazily, never crash boot" contract).
 *
 * Method names are intermediary tokens; parameter/return types use their 26.2
 * real names (the mod-side references are rewritten by the wall rename pass).
 */
public interface class_1865<T extends net.minecraft.world.item.crafting.Recipe<?>> {

    /** fromJson(Identifier, JsonObject) */
    T method_8121(net.minecraft.resources.Identifier id, com.google.gson.JsonObject json);

    /** fromNetwork(Identifier, FriendlyByteBuf) */
    T method_8122(net.minecraft.resources.Identifier id, net.minecraft.network.FriendlyByteBuf buf);

    /** toNetwork(FriendlyByteBuf, T) */
    void method_8124(net.minecraft.network.FriendlyByteBuf buf, T recipe);

    /** register(String id, S serializer): the 1.20.1 vanilla registration
     *  helper. On the facade it is a no-op pass-through -- a legacy serializer
     *  cannot register into 26.2's codec recipe system anyway; this only keeps
     *  the call site resolvable. */
    @SuppressWarnings({"rawtypes", "unchecked"})
    static <S extends class_1865> S method_17724(String id, S serializer) {
        return serializer;
    }
}
