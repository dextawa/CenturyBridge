package top.dext.centurybridge.rt.v1_21_4;

/**
 * 1.20.5 holderization (core/tools/gen_holder_bridges.py -- generated, do not edit): a 1.20.1
 * mod reads MobEffect/Potion/GameEvent/SoundEvent/Attribute/MapDecorationType constants as
 * values and passes values; the API now holds and takes Holder<T>. Constants answer their
 * holder's value(); arguments are wrapped by their registry, returned holders unwrapped.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class ClientCbHolders {

    /** net/minecraft/class_1291 -> its built-in registry's holder (Registry.wrapAsHolder) */
    public static net.minecraft.class_6880<net.minecraft.class_1291> wrapMobEffect(net.minecraft.class_1291 value) {
        return (value == null ? null : net.minecraft.class_7923.field_41174.method_47983(value));
    }

    /** net/minecraft/class_1320 -> its built-in registry's holder (Registry.wrapAsHolder) */
    public static net.minecraft.class_6880<net.minecraft.class_1320> wrapAttribute(net.minecraft.class_1320 value) {
        return (value == null ? null : net.minecraft.class_7923.field_41190.method_47983(value));
    }

    /** net/minecraft/class_1842 -> its built-in registry's holder (Registry.wrapAsHolder) */
    public static net.minecraft.class_6880<net.minecraft.class_1842> wrapPotion(net.minecraft.class_1842 value) {
        return (value == null ? null : net.minecraft.class_7923.field_41179.method_47983(value));
    }

    /** net/minecraft/class_5712 -> its built-in registry's holder (Registry.wrapAsHolder) */
    public static net.minecraft.class_6880<net.minecraft.class_5712> wrapGameEvent(net.minecraft.class_5712 value) {
        return (value == null ? null : net.minecraft.class_7923.field_41171.method_47983(value));
    }

    /** net/minecraft/class_638.method_32888(Lnet/minecraft/class_5712;Lnet/minecraft/class_243;Lnet/minecraft/class_5712$class_7397;)V */
    public static void method_32888(net.minecraft.class_638 self, net.minecraft.class_5712 a0, net.minecraft.class_243 a1, net.minecraft.class_5712.class_7397 a2) {
        self.method_32888((a0 == null ? null : net.minecraft.class_7923.field_41171.method_47983(a0)), a1, a2);
    }

    /** net/minecraft/class_746.method_6111(Lnet/minecraft/class_1291;)Lnet/minecraft/class_1293; */
    public static net.minecraft.class_1293 method_6111(net.minecraft.class_746 self, net.minecraft.class_1291 a0) {
        return self.method_6111((a0 == null ? null : net.minecraft.class_7923.field_41174.method_47983(a0)));
    }

    private ClientCbHolders() {
    }
}
