package top.dext.centurybridge.step.v1_21_6.facade.fapi;

/**
 * Fabric API 1.21.5's fabric-keybindings-v0 FabricKeyBinding (net.fabricmc.fabric.api.client.keybinding), a module
 * 1.21.6's Fabric API removed: 1.21.5's bodies on fabric-key-binding-api-v1, which 1.21.6 keeps -- the key mapping
 * named "key.&lt;namespace&gt;.&lt;path&gt;" of the id, its bound key from KeyBindingHelper (javap).
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class FabricKeyBinding extends net.minecraft.class_304 {

    protected FabricKeyBinding(net.minecraft.class_2960 id, net.minecraft.class_3675.class_307 type, int code, String category) {
        super(String.format("key.%s.%s", id.method_12836(), id.method_12832()), type, code, category);
    }

    public net.minecraft.class_3675.class_306 getBoundKey() {
        return net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.getBoundKeyOf(this);
    }

    /** FabricKeyBinding.Builder of 1.21.5 */
    public static class Builder {
        protected final FabricKeyBinding binding;

        protected Builder(FabricKeyBinding binding) {
            this.binding = binding;
        }

        public FabricKeyBinding build() {
            return binding;
        }

        public static Builder create(net.minecraft.class_2960 id, net.minecraft.class_3675.class_307 type, int code, String category) {
            return new Builder(new FabricKeyBinding(id, type, code, category));
        }
    }
}
