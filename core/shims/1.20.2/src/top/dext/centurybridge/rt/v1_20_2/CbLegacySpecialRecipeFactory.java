package top.dext.centurybridge.rt.v1_20_2;

/**
 * The 1.20.1 SpecialRecipeSerializer.Factory shape, create(id, category); 1.20.2's factory is
 * create(category). A 1.20.1 lambda or method reference is built as this interface instead
 * (lambdaAdapters) and wrapped by CbRecipeSerializerSupport.specialFactory; a class implementing
 * the old factory gets it added and create(category) synthesized (overrideHooks bridge).
 */
public interface CbLegacySpecialRecipeFactory {

    /** create(id, category) */
    net.minecraft.class_3955 create(net.minecraft.class_2960 id, net.minecraft.class_7710 category);
}
