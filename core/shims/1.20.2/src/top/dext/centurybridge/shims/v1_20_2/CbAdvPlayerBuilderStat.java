package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;

/**
 * PlayerPredicate.Builder.addStat(Stat, Ints) of 1.20.1: 1.20.1 put the range under the stat; 1.20.2
 * names the stat by its type and the registered value's holder, the same stat.
 */
@Mixin(value = net.minecraft.class_4553.class_4557.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class CbAdvPlayerBuilderStat {

    /** addStat(stat, range) */
    public net.minecraft.class_4553.class_4557 method_35311(net.minecraft.class_3445<?> stat, net.minecraft.class_2096.class_2100 range) {
        return cb$add(stat, range);
    }

    private <T> net.minecraft.class_4553.class_4557 cb$add(net.minecraft.class_3445<T> stat, net.minecraft.class_2096.class_2100 range) {
        net.minecraft.class_3448<T> type = stat.method_14949();
        net.minecraft.class_2378<T> registry = type.method_14959();
        net.minecraft.class_5321<T> key = registry.method_29113(stat.method_14951()).orElseThrow(() ->
            new IllegalArgumentException("CenturyBridge: stat value not registered: " + stat));
        return ((net.minecraft.class_4553.class_4557) (Object) this).method_35311(type, registry.method_40290(key), range);
    }
}
