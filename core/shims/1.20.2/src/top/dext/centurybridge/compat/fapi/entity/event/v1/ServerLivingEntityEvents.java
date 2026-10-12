package top.dext.centurybridge.compat.fapi.entity.event.v1;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Fabric API entity events v1's MOB_CONVERSION (with its MobConversion listener type), which 0.92
 * backported to 1.20.1 and 1.20.2's Fabric API does not have (its ServerLivingEntityEvents keeps the
 * other events; Fabric API ships this one again from 1.20.4). Reads of the Fabric API field are
 * redirected here (fieldRedirects) and the listener type renamed here (classRenames). Fired by this
 * layer's mixins wherever 1.20.1's Fabric API fired it: MobEntity.convertTo with its keepEquipment,
 * and the pig, tadpole and villager conversions with false.
 */
public final class ServerLivingEntityEvents {

    public static final Event<MobConversion> MOB_CONVERSION = EventFactory.createArrayBacked(MobConversion.class,
        callbacks -> (previous, converted, keepEquipment) -> {
            for (MobConversion callback : callbacks) {
                callback.onConversion(previous, converted, keepEquipment);
            }
        });

    private ServerLivingEntityEvents() {
    }

    @FunctionalInterface
    public interface MobConversion {
        void onConversion(net.minecraft.class_1308 previous, net.minecraft.class_1308 converted, boolean keepEquipment);
    }
}
