package top.dext.centurybridge.step.v1_20_5.compat.fapi.client.screenhandler.v1;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.screenhandler.CbScreenHandlers;

/**
 * Fabric API 0.97.3's (1.20.4) client ScreenRegistry (deprecated there, removed in 1.20.5): a screen factory
 * for a screen handler type, registered with vanilla's HandledScreens as 1.20.4 did. HandledScreens.register
 * is private and its Provider interface package-private in the compile stub; Fabric API's access widener
 * opens both at run time, where they are reached by shape (the static method of a screen handler type and
 * a provider) and the provider is implemented over that interface, its default method (open) running as
 * vanilla wrote it.
 */
@Deprecated
@Environment(EnvType.CLIENT)
public final class ScreenRegistry {

    private static volatile Method register;

    private ScreenRegistry() {
    }

    public static <H extends net.minecraft.class_1703, S extends net.minecraft.class_437 & net.minecraft.class_3936<H>> void register(
            net.minecraft.class_3917<? extends H> type, Factory<H, S> screenFactory) {
        try {
            if (register == null) {
                Method found = null;
                for (Method m : net.minecraft.class_3929.class.getDeclaredMethods()) {
                    Class<?>[] p = m.getParameterTypes();
                    if (java.lang.reflect.Modifier.isStatic(m.getModifiers()) && p.length == 2 && p[0] == net.minecraft.class_3917.class
                            && p[1].isInterface() && p[1].getEnclosingClass() == net.minecraft.class_3929.class && m.getReturnType() == void.class) {
                        found = m;
                    }
                }
                if (found == null) {
                    throw new IllegalStateException("HandledScreens has no register(type, provider)");
                }
                found.setAccessible(true);
                register = found;
            }
            Class<?> provider = register.getParameterTypes()[1];
            @SuppressWarnings("unchecked")
            Object p = Proxy.newProxyInstance(provider.getClassLoader(), new Class<?>[] {provider}, CbScreenHandlers.handler(
                m -> m.getParameterCount() == 3,
                args -> screenFactory.create((H) args[0], (net.minecraft.class_1661) args[1], (net.minecraft.class_2561) args[2])));
            register.invoke(null, type, p);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("cannot register a 1.20.4 screen factory for " + type, e);
        }
    }

    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    public interface Factory<H extends net.minecraft.class_1703, S extends net.minecraft.class_437 & net.minecraft.class_3936<H>> {
        S create(H handler, net.minecraft.class_1661 inventory, net.minecraft.class_2561 title);
    }
}
