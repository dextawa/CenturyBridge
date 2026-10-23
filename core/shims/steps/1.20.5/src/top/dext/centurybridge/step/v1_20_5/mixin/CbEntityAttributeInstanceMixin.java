package top.dext.centurybridge.step.v1_20_5.mixin;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * AttributeInstance: 1.20.4's public getModifiers(operation), the live set of the instance's modifiers of one
 * operation. 1.20.5 keeps them as a live map by id (package-private, of the same name); this is that map's
 * values as 1.20.4's set: a removal removes the modifier from the operation's map, an addition puts it there
 * -- as adding to 1.20.4's set did, without registering it by id or marking the value dirty.
 */
@Mixin(net.minecraft.class_1324.class)
public abstract class CbEntityAttributeInstanceMixin {

    @Shadow
    @Final
    private Map<net.minecraft.class_1322.class_1323, Map<UUID, net.minecraft.class_1322>> field_23701;

    /** was getModifiers(operation) */
    public Set<net.minecraft.class_1322> method_6193(net.minecraft.class_1322.class_1323 operation) {
        Map<UUID, net.minecraft.class_1322> byId =
            this.field_23701.computeIfAbsent(operation, k -> new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>());
        return top.dext.centurybridge.step.v1_20_5.rt.CbEntityMisc.modifierSet(byId);
    }
}
