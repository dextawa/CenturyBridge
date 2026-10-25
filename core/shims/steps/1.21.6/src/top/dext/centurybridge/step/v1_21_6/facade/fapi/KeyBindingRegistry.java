package top.dext.centurybridge.step.v1_21_6.facade.fapi;

/**
 * Fabric API 1.21.5's fabric-keybindings-v0 KeyBindingRegistry, a module 1.21.6's Fabric API removed: 1.21.5's
 * INSTANCE, on fabric-key-binding-api-v1's registry implementation, which 1.21.6 keeps (javap: addCategory as is,
 * register answering whether the binding was registered).
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface KeyBindingRegistry {

    KeyBindingRegistry INSTANCE = new KeyBindingRegistry() {
        @Override
        public boolean addCategory(String category) {
            return net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl.addCategory(category);
        }

        @Override
        public boolean register(FabricKeyBinding binding) {
            return net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl.registerKeyBinding(binding) != null;
        }
    };

    boolean addCategory(String category);

    boolean register(FabricKeyBinding binding);
}
