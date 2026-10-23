package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's RecipeSerializer network contract on 1.20.5's (area s4fin, world/item/crafting).
 *
 * <p>1.20.4: a serializer read a recipe with fromNetwork(buf) and wrote it with toNetwork(buf, recipe), and
 * the game called those on whatever serializer a recipe had -- vanilla's own (ShapedRecipe.Serializer,
 * SimpleCookingSerializer, ...) or a mod's (implementing RecipeSerializer, or extending a vanilla one and
 * overriding them). 1.20.5: a serializer answers streamCodec(), and the game encodes and decodes through
 * it (on a RegistryFriendlyByteBuf). So:
 * <ul>
 * <li>a mod class implementing RecipeSerializer with 1.20.4's fromNetwork / toNetwork gets streamCodec()
 *     built from them ({@link #streamCodec(LegacyNetwork)}, an adapter hook);</li>
 * <li>a mod class extending a vanilla serializer and overriding the typed fromNetwork and/or toNetwork gets
 *     streamCodec() built from its overrides, the other half being the vanilla serializer's own codec
 *     (guarded adapter hooks: calling streamCodec() again from the bridge reaches super's);</li>
 * <li>a 1.20.4 call of fromNetwork / toNetwork on any serializer (instance redirects) goes through that
 *     serializer's streamCodec() -- the mod's own read / write when it has them, as 1.20.4's virtual call
 *     did; a mod's super.fromNetwork / super.toNetwork (super redirects) reaches the vanilla class's own
 *     codec, as 1.20.4's super call reached its body.</li>
 * </ul>
 * A 1.20.4 buffer that carries no registries is viewed over the running world's (CbNetBufs.cbRegistryBuf).
 */
public final class CbFinItemRecipes {

    private CbFinItemRecipes() {
    }

    // ---------------------------------------------------------------- adapters

    /** a mod RecipeSerializer with 1.20.4's (erased) fromNetwork and toNetwork */
    public interface LegacyNetwork {
        net.minecraft.class_1860<?> method_8122(net.minecraft.class_2540 buf);

        void method_8124(net.minecraft.class_2540 buf, net.minecraft.class_1860<?> recipe);
    }

    /** ShapedRecipe.Serializer subclass overriding fromNetwork / toNetwork */
    public interface ShapedRead {
        net.minecraft.class_1869 method_8163(net.minecraft.class_2540 buf);
    }

    public interface ShapedWrite {
        void method_8165(net.minecraft.class_2540 buf, net.minecraft.class_1869 recipe);
    }

    /** ShapelessRecipe.Serializer subclass */
    public interface ShapelessRead {
        net.minecraft.class_1867 method_8141(net.minecraft.class_2540 buf);
    }

    public interface ShapelessWrite {
        void method_8143(net.minecraft.class_2540 buf, net.minecraft.class_1867 recipe);
    }

    /** SimpleCookingSerializer subclass (erased: AbstractCookingRecipe) */
    public interface CookingRead {
        net.minecraft.class_1874 method_17737(net.minecraft.class_2540 buf);
    }

    public interface CookingWrite {
        void method_17735(net.minecraft.class_2540 buf, net.minecraft.class_1874 recipe);
    }

    /** SimpleCraftingRecipeSerializer subclass (erased: CraftingRecipe) */
    public interface SimpleRead {
        net.minecraft.class_3955 method_45444(net.minecraft.class_2540 buf);
    }

    public interface SimpleWrite {
        void method_45442(net.minecraft.class_2540 buf, net.minecraft.class_3955 recipe);
    }

    /** SingleItemRecipe.Serializer subclass (erased: SingleItemRecipe) */
    public interface SingleRead {
        net.minecraft.class_3972 method_17882(net.minecraft.class_2540 buf);
    }

    public interface SingleWrite {
        void method_17880(net.minecraft.class_2540 buf, net.minecraft.class_3972 recipe);
    }

    /** SmithingTransformRecipe.Serializer subclass */
    public interface TransformRead {
        net.minecraft.class_8060 method_48456(net.minecraft.class_2540 buf);
    }

    public interface TransformWrite {
        void method_48457(net.minecraft.class_2540 buf, net.minecraft.class_8060 recipe);
    }

    /** SmithingTrimRecipe.Serializer subclass */
    public interface TrimRead {
        net.minecraft.class_8062 method_48460(net.minecraft.class_2540 buf);
    }

    public interface TrimWrite {
        void method_48461(net.minecraft.class_2540 buf, net.minecraft.class_8062 recipe);
    }

    /** a shaped subclass overriding both */
    public interface ShapedBoth extends ShapedRead, ShapedWrite {
    @Override
    net.minecraft.class_1869 method_8163(net.minecraft.class_2540 buf);

    @Override
    void method_8165(net.minecraft.class_2540 buf, net.minecraft.class_1869 recipe);
    }

    /** a shapeless subclass overriding both */
    public interface ShapelessBoth extends ShapelessRead, ShapelessWrite {
    @Override
    net.minecraft.class_1867 method_8141(net.minecraft.class_2540 buf);

    @Override
    void method_8143(net.minecraft.class_2540 buf, net.minecraft.class_1867 recipe);
    }

    /** a cooking subclass overriding both */
    public interface CookingBoth extends CookingRead, CookingWrite {
    @Override
    net.minecraft.class_1874 method_17737(net.minecraft.class_2540 buf);

    @Override
    void method_17735(net.minecraft.class_2540 buf, net.minecraft.class_1874 recipe);
    }

    /** a simple subclass overriding both */
    public interface SimpleBoth extends SimpleRead, SimpleWrite {
    @Override
    net.minecraft.class_3955 method_45444(net.minecraft.class_2540 buf);

    @Override
    void method_45442(net.minecraft.class_2540 buf, net.minecraft.class_3955 recipe);
    }

    /** a single subclass overriding both */
    public interface SingleBoth extends SingleRead, SingleWrite {
    @Override
    net.minecraft.class_3972 method_17882(net.minecraft.class_2540 buf);

    @Override
    void method_17880(net.minecraft.class_2540 buf, net.minecraft.class_3972 recipe);
    }

    /** a transform subclass overriding both */
    public interface TransformBoth extends TransformRead, TransformWrite {
    @Override
    net.minecraft.class_8060 method_48456(net.minecraft.class_2540 buf);

    @Override
    void method_48457(net.minecraft.class_2540 buf, net.minecraft.class_8060 recipe);
    }

    /** a trim subclass overriding both */
    public interface TrimBoth extends TrimRead, TrimWrite {
    @Override
    net.minecraft.class_8062 method_48460(net.minecraft.class_2540 buf);

    @Override
    void method_48461(net.minecraft.class_2540 buf, net.minecraft.class_8062 recipe);
    }

    // ---------------------------------------------------------------- codec()

    /** codec() of a 1.20.4 serializer (override hook unwrap): the map codec 1.20.4's dispatch read its codec as */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static com.mojang.serialization.MapCodec codec(com.mojang.serialization.Codec codec) {
        return CbCodecs.toMapCodec(codec);
    }

    // ---------------------------------------------------------------- streamCodec() of a mod serializer

    /** a mod RecipeSerializer's 1.20.4 network form as its streamCodec() */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_9139 streamCodec(LegacyNetwork s) {
        return net.minecraft.class_9139.method_56437(
            (buf, recipe) -> s.method_8124((net.minecraft.class_2540) buf, (net.minecraft.class_1860<?>) recipe),
            buf -> s.method_8122((net.minecraft.class_2540) buf));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <T> net.minecraft.class_9139 halves(net.minecraft.class_9139 superCodec,
            java.util.function.Function<net.minecraft.class_2540, T> read, java.util.function.BiConsumer<net.minecraft.class_2540, T> write) {
        return net.minecraft.class_9139.method_56437(
            (buf, recipe) -> {
                if (write != null) {
                    write.accept((net.minecraft.class_2540) buf, (T) recipe);
                } else {
                    superCodec.encode(buf, recipe);
                }
            },
            buf -> read != null ? read.apply((net.minecraft.class_2540) buf) : superCodec.decode(buf));
    }

    /** the vanilla serializer's own streamCodec(), from inside a guarded synthesized one (re-entry reaches super) */
    @SuppressWarnings("rawtypes")
    private static net.minecraft.class_9139 superCodec(Object serializer) {
        return ((net.minecraft.class_1865<?>) serializer).method_56104();
    }

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_9139 shapedBoth(ShapedBoth s) {
        return halves(null, s::method_8163, s::method_8165);
    }

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_9139 shapedRead(ShapedRead s) {
        return halves(superCodec(s), s::method_8163, null);
    }

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_9139 shapedWrite(ShapedWrite s) {
        return halves(superCodec(s), null, s::method_8165);
    }

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_9139 shapelessBoth(ShapelessBoth s) {
        return halves(null, s::method_8141, s::method_8143);
    }

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_9139 shapelessRead(ShapelessRead s) {
        return halves(superCodec(s), s::method_8141, null);
    }

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_9139 shapelessWrite(ShapelessWrite s) {
        return halves(superCodec(s), null, s::method_8143);
    }

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_9139 cookingBoth(CookingBoth s) {
        return halves(null, s::method_17737, s::method_17735);
    }

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_9139 cookingRead(CookingRead s) {
        return halves(superCodec(s), s::method_17737, null);
    }

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_9139 cookingWrite(CookingWrite s) {
        return halves(superCodec(s), null, s::method_17735);
    }

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_9139 simpleBoth(SimpleBoth s) {
        return halves(null, s::method_45444, s::method_45442);
    }

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_9139 simpleRead(SimpleRead s) {
        return halves(superCodec(s), s::method_45444, null);
    }

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_9139 simpleWrite(SimpleWrite s) {
        return halves(superCodec(s), null, s::method_45442);
    }

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_9139 singleBoth(SingleBoth s) {
        return halves(null, s::method_17882, s::method_17880);
    }

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_9139 singleRead(SingleRead s) {
        return halves(superCodec(s), s::method_17882, null);
    }

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_9139 singleWrite(SingleWrite s) {
        return halves(superCodec(s), null, s::method_17880);
    }

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_9139 transformBoth(TransformBoth s) {
        return halves(null, s::method_48456, s::method_48457);
    }

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_9139 transformRead(TransformRead s) {
        return halves(superCodec(s), s::method_48456, null);
    }

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_9139 transformWrite(TransformWrite s) {
        return halves(superCodec(s), null, s::method_48457);
    }

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_9139 trimBoth(TrimBoth s) {
        return halves(null, s::method_48460, s::method_48461);
    }

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_9139 trimRead(TrimRead s) {
        return halves(superCodec(s), s::method_48460, null);
    }

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_9139 trimWrite(TrimWrite s) {
        return halves(superCodec(s), null, s::method_48461);
    }

    // ---------------------------------------------------------------- 1.20.4 calls

    /** RecipeSerializer.fromNetwork(buf) on any serializer: its streamCodec()'s decode */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_1860 method_8122(net.minecraft.class_1865 serializer, net.minecraft.class_2540 buf) {
        return (net.minecraft.class_1860) serializer.method_56104().decode(CbNetBufs.cbRegistryBuf(buf));
    }

    /** RecipeSerializer.toNetwork(buf, recipe) on any serializer: its streamCodec()'s encode */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void method_8124(net.minecraft.class_1865 serializer, net.minecraft.class_2540 buf, net.minecraft.class_1860 recipe) {
        serializer.method_56104().encode(CbNetBufs.cbRegistryBuf(buf), recipe);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <T> T read(net.minecraft.class_9139 codec, net.minecraft.class_2540 buf) {
        return (T) codec.decode(CbNetBufs.cbRegistryBuf(buf));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void write(net.minecraft.class_9139 codec, net.minecraft.class_2540 buf, Object recipe) {
        codec.encode(CbNetBufs.cbRegistryBuf(buf), recipe);
    }

    // the typed calls: through the serializer's streamCodec() (a subclass's override answers, as 1.20.4's
    // virtual call reached it); the super forms: the vanilla class's own codec

    public static net.minecraft.class_1869 method_8163(net.minecraft.class_1869.class_1870 s, net.minecraft.class_2540 buf) {
        return read(s.method_56104(), buf);
    }

    public static void method_8165(net.minecraft.class_1869.class_1870 s, net.minecraft.class_2540 buf, net.minecraft.class_1869 r) {
        write(s.method_56104(), buf, r);
    }

    public static net.minecraft.class_1869 superShapedRead(net.minecraft.class_1869.class_1870 s, net.minecraft.class_2540 buf) {
        return read(net.minecraft.class_1869.class_1870.field_48358, buf);
    }

    public static void superShapedWrite(net.minecraft.class_1869.class_1870 s, net.minecraft.class_2540 buf, net.minecraft.class_1869 r) {
        write(net.minecraft.class_1869.class_1870.field_48358, buf, r);
    }

    public static net.minecraft.class_1867 method_8141(net.minecraft.class_1867.class_1868 s, net.minecraft.class_2540 buf) {
        return read(s.method_56104(), buf);
    }

    public static void method_8143(net.minecraft.class_1867.class_1868 s, net.minecraft.class_2540 buf, net.minecraft.class_1867 r) {
        write(s.method_56104(), buf, r);
    }

    public static net.minecraft.class_1867 superShapelessRead(net.minecraft.class_1867.class_1868 s, net.minecraft.class_2540 buf) {
        return read(net.minecraft.class_1867.class_1868.field_48360, buf);
    }

    public static void superShapelessWrite(net.minecraft.class_1867.class_1868 s, net.minecraft.class_2540 buf, net.minecraft.class_1867 r) {
        write(net.minecraft.class_1867.class_1868.field_48360, buf, r);
    }

    public static net.minecraft.class_1874 method_17737(net.minecraft.class_3957<?> s, net.minecraft.class_2540 buf) {
        return read(s.method_56104(), buf);
    }

    public static void method_17735(net.minecraft.class_3957<?> s, net.minecraft.class_2540 buf, net.minecraft.class_1874 r) {
        write(s.method_56104(), buf, r);
    }

    public static net.minecraft.class_1874 superCookingRead(net.minecraft.class_3957<?> s, net.minecraft.class_2540 buf) {
        return read(((top.dext.centurybridge.step.v1_20_5.mixin.CbFinItemRecipeCookingAccess) (Object) s).cbFin$streamCodec(), buf);
    }

    public static void superCookingWrite(net.minecraft.class_3957<?> s, net.minecraft.class_2540 buf, net.minecraft.class_1874 r) {
        write(((top.dext.centurybridge.step.v1_20_5.mixin.CbFinItemRecipeCookingAccess) (Object) s).cbFin$streamCodec(), buf, r);
    }

    public static net.minecraft.class_3955 method_45444(net.minecraft.class_1866<?> s, net.minecraft.class_2540 buf) {
        return read(s.method_56104(), buf);
    }

    public static void method_45442(net.minecraft.class_1866<?> s, net.minecraft.class_2540 buf, net.minecraft.class_3955 r) {
        write(s.method_56104(), buf, r);
    }

    public static net.minecraft.class_3955 superSimpleRead(net.minecraft.class_1866<?> s, net.minecraft.class_2540 buf) {
        return read(((top.dext.centurybridge.step.v1_20_5.mixin.CbFinItemRecipeSimpleAccess) (Object) s).cbFin$streamCodec(), buf);
    }

    public static void superSimpleWrite(net.minecraft.class_1866<?> s, net.minecraft.class_2540 buf, net.minecraft.class_3955 r) {
        write(((top.dext.centurybridge.step.v1_20_5.mixin.CbFinItemRecipeSimpleAccess) (Object) s).cbFin$streamCodec(), buf, r);
    }

    public static net.minecraft.class_3972 method_17882(net.minecraft.class_3972.class_3973<?> s, net.minecraft.class_2540 buf) {
        return read(s.method_56104(), buf);
    }

    public static void method_17880(net.minecraft.class_3972.class_3973<?> s, net.minecraft.class_2540 buf, net.minecraft.class_3972 r) {
        write(s.method_56104(), buf, r);
    }

    public static net.minecraft.class_3972 superSingleRead(net.minecraft.class_3972.class_3973<?> s, net.minecraft.class_2540 buf) {
        return read(((top.dext.centurybridge.step.v1_20_5.mixin.CbFinItemRecipeSingleAccess) (Object) s).cbFin$streamCodec(), buf);
    }

    public static void superSingleWrite(net.minecraft.class_3972.class_3973<?> s, net.minecraft.class_2540 buf, net.minecraft.class_3972 r) {
        write(((top.dext.centurybridge.step.v1_20_5.mixin.CbFinItemRecipeSingleAccess) (Object) s).cbFin$streamCodec(), buf, r);
    }

    public static net.minecraft.class_8060 method_48456(net.minecraft.class_8060.class_8061 s, net.minecraft.class_2540 buf) {
        return read(s.method_56104(), buf);
    }

    public static void method_48457(net.minecraft.class_8060.class_8061 s, net.minecraft.class_2540 buf, net.minecraft.class_8060 r) {
        write(s.method_56104(), buf, r);
    }

    public static net.minecraft.class_8060 superTransformRead(net.minecraft.class_8060.class_8061 s, net.minecraft.class_2540 buf) {
        return read(net.minecraft.class_8060.class_8061.field_48364, buf);
    }

    public static void superTransformWrite(net.minecraft.class_8060.class_8061 s, net.minecraft.class_2540 buf, net.minecraft.class_8060 r) {
        write(net.minecraft.class_8060.class_8061.field_48364, buf, r);
    }

    public static net.minecraft.class_8062 method_48460(net.minecraft.class_8062.class_8063 s, net.minecraft.class_2540 buf) {
        return read(s.method_56104(), buf);
    }

    public static void method_48461(net.minecraft.class_8062.class_8063 s, net.minecraft.class_2540 buf, net.minecraft.class_8062 r) {
        write(s.method_56104(), buf, r);
    }

    public static net.minecraft.class_8062 superTrimRead(net.minecraft.class_8062.class_8063 s, net.minecraft.class_2540 buf) {
        return read(net.minecraft.class_8062.class_8063.field_48365, buf);
    }

    public static void superTrimWrite(net.minecraft.class_8062.class_8063 s, net.minecraft.class_2540 buf, net.minecraft.class_8062 r) {
        write(net.minecraft.class_8062.class_8063.field_48365, buf, r);
    }
}
