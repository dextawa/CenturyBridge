package top.dext.centurybridge.step.v1_21_6.mixin;

/**
 * 1.21.6's MultiTextureStateShard binds its texture list in its setup runnable and keeps only the first id
 * (javap: the constructor captures the list in a lambda, field_29453 = the first); CbGuiTextures needs the whole list to
 * draw a 1.21.5 GUI fill of such a render type (the end portal's two textures), so it is kept as it was passed.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@org.spongepowered.asm.mixin.Mixin(net.minecraft.class_4668.class_5940.class)
public abstract class CbTextureMultiMixin implements top.dext.centurybridge.step.v1_21_6.rt.CbGuiTextures.Multi {

    @org.spongepowered.asm.mixin.Unique
    private java.util.List<net.minecraft.class_2960> cb$textures = java.util.List.of();

    @org.spongepowered.asm.mixin.injection.Inject(method = "<init>(Ljava/util/List;)V",
        at = @org.spongepowered.asm.mixin.injection.At("RETURN"))
    private void cb$keepTextures(java.util.List<net.minecraft.class_4668.class_5940.class_10542> entries,
            org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        java.util.List<net.minecraft.class_2960> ids = new java.util.ArrayList<>(entries.size());
        for (net.minecraft.class_4668.class_5940.class_10542 entry : entries) {
            ids.add(entry.comp_3462);
        }
        this.cb$textures = java.util.List.copyOf(ids);
    }

    @Override
    public java.util.List<net.minecraft.class_2960> cb$textures() {
        return this.cb$textures;
    }
}
