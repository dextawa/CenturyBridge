package cb.smx1n;

import java.lang.reflect.Field;
import java.util.Map;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.class_1291;
import net.minecraft.class_1294;

/** Test-only SERVER_STARTED template dump, independent of any injection. */
public final class CbAttrDebugMod implements ModInitializer {
    @Override
    @SuppressWarnings("unchecked")
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            try {
                Field field = class_1291.class.getDeclaredField("field_5885");
                field.setAccessible(true);
                Map<?, ?> map = (Map<?, ?>) field.get(class_1294.field_5910.comp_349());
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    System.out.println("CB-ATTR-DEBUG key=" + entry.getKey().getClass().getName()
                        + " value=" + entry.getValue().getClass().getName());
                }
                Object attribute = top.dext.centurybridge.step.v1_20_5.rt.CbRestFields.field_23721();
                Object holder = top.dext.centurybridge.step.v1_20_5.rt.CbRestHolders.cbAttributeHolder(
                    (net.minecraft.class_1320) attribute);
                System.out.println("CB-ATTR-DEBUG attribute=" + attribute.getClass().getName()
                    + " holder=" + holder.getClass().getName());
                net.minecraft.class_1642 zombie = new net.minecraft.class_1642(server.method_30002());
                Object instance = zombie.method_5996((net.minecraft.class_6880<net.minecraft.class_1320>) holder);
                System.out.println("CB-ATTR-DEBUG instance=" + instance.getClass().getName());
                zombie.method_6092(new net.minecraft.class_1293(class_1294.field_5910, 600, 1));
                System.out.println("CB-ATTR-DEBUG add-effect returned");
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
