package top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.screenhandler;

import io.netty.buffer.Unpooled;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.BiFunction;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;

/**
 * Fabric API 1.20.4's extended screen handlers on 1.20.5's. 1.20.4 wrote a screen's opening data straight
 * into the open packet (ExtendedScreenHandlerFactory.writeScreenOpeningData(player, buf)) and handed the
 * client factory that buffer (ExtendedFactory.create(syncId, inventory, buf)); 1.20.5 has the factory answer
 * a value (getScreenOpeningData) that the screen handler type's packet codec writes and reads. A 1.20.4 type
 * is one whose value is that buffer: {@link #RAW} writes its bytes where 1.20.4 wrote them, at the end of the
 * open packet, and reads them back as a buffer for the client factory, keeping a play buffer's registry
 * access. A 1.20.4 factory's opening data is the buffer its writeScreenOpeningData fills
 * ({@link #getScreenOpeningData}, the bridge of an override hook through {@link LegacyFactory}).
 */
public final class CbScreenHandlers {

    private CbScreenHandlers() {
    }

    /** the opening data of a 1.20.4 type: the bytes of the buffer the factory wrote */
    public static final net.minecraft.class_9139<net.minecraft.class_9129, net.minecraft.class_2540> RAW =
        new net.minecraft.class_9139<>() {
            @Override
            public net.minecraft.class_2540 decode(net.minecraft.class_9129 buf) {
                net.minecraft.class_2540 data = new net.minecraft.class_9129(Unpooled.buffer(buf.readableBytes()), buf.method_56349());
                data.writeBytes(buf, buf.readableBytes());
                return data;
            }

            @Override
            public void encode(net.minecraft.class_9129 buf, net.minecraft.class_2540 data) {
                buf.writeBytes(data.copy());
            }
        };

    /** {@link #RAW}, as the reshaped 1.20.4 constructor's second argument */
    public static net.minecraft.class_9139 rawCodec() {
        return RAW;
    }

    // ---------------------------------------------------------------- screen handler factories

    /** a mod's 1.20.4 ExtendedScreenHandlerFactory (added by the override hook to the class declaring it) */
    public interface LegacyFactory {
        void writeScreenOpeningData(net.minecraft.class_3222 player, net.minecraft.class_2540 buf);
    }

    /** 1.20.5's getScreenOpeningData(player) of a 1.20.4 factory: the buffer its writeScreenOpeningData fills */
    public static Object getScreenOpeningData(LegacyFactory factory, net.minecraft.class_3222 player) {
        net.minecraft.class_2540 buf = new net.minecraft.class_2540(Unpooled.buffer());
        factory.writeScreenOpeningData(player, buf);
        return buf;
    }

    /** factory.writeScreenOpeningData(player, buf) (instanceRedirects): the 1.20.4 factory's own, or a 1.20.5 one's buffer data */
    @SuppressWarnings("rawtypes")
    public static void writeScreenOpeningData(ExtendedScreenHandlerFactory factory, net.minecraft.class_3222 player, net.minecraft.class_2540 buf) {
        if (factory instanceof LegacyFactory legacy) {
            legacy.writeScreenOpeningData(player, buf);
            return;
        }
        Object data = factory.getScreenOpeningData(player);
        if (!(data instanceof net.minecraft.class_2540 bytes)) {
            throw new IllegalArgumentException("screen handler factory " + factory.getClass().getName()
                + " answers 1.20.5 opening data (" + (data == null ? "null" : data.getClass().getName()) + "), which has no 1.20.4 buffer form");
        }
        buf.writeBytes(bytes.copy());
    }

    // ---------------------------------------------------------------- client factories

    /** the 1.20.4 shape of ExtendedScreenHandlerType.ExtendedFactory, as a converted lambda implements it */
    @FunctionalInterface
    public interface LegacyExtendedFactory {
        net.minecraft.class_1703 create(int syncId, net.minecraft.class_1661 inventory, net.minecraft.class_2540 buf);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static ExtendedScreenHandlerType.ExtendedFactory adapt(LegacyExtendedFactory legacy) {
        return (syncId, inventory, data) -> legacy.create(syncId, inventory, (net.minecraft.class_2540) data);
    }

    // ---------------------------------------------------------------- vanilla screen handler types

    private static volatile MethodHandle typeConstructor;
    private static volatile Class<?> factoryInterface;

    /**
     * new ScreenHandlerType&lt;&gt;(factory, FeatureFlags.VANILLA_FEATURES), as 1.20.4's ScreenHandlerRegistry.registerSimple
     * made it. The constructor is private and its Factory interface package-private in the compile stub; Fabric
     * API's access widener opens both at run time, which is where this reaches them: by their shape (the one
     * constructor, its first parameter's type), with the factory implemented over that interface.
     */
    @SuppressWarnings("unchecked")
    public static <T extends net.minecraft.class_1703> net.minecraft.class_3917<T> simpleType(
            BiFunction<Integer, net.minecraft.class_1661, T> create) {
        try {
            if (typeConstructor == null) {
                Constructor<?> c = null;
                for (Constructor<?> k : net.minecraft.class_3917.class.getDeclaredConstructors()) {
                    Class<?>[] p = k.getParameterTypes();
                    if (p.length == 2 && p[0].isInterface() && p[1] == net.minecraft.class_7699.class) {
                        c = k;
                    }
                }
                if (c == null) {
                    throw new IllegalStateException("ScreenHandlerType has no (factory, feature set) constructor");
                }
                factoryInterface = c.getParameterTypes()[0];
                c.setAccessible(true);
                typeConstructor = MethodHandles.lookup().unreflectConstructor(c);
            }
            Object factory = Proxy.newProxyInstance(factoryInterface.getClassLoader(), new Class<?>[] {factoryInterface},
                handler(m -> m.getParameterCount() == 2, args -> create.apply((Integer) args[0], (net.minecraft.class_1661) args[1])));
            return (net.minecraft.class_3917<T>) typeConstructor.invoke(factory, net.minecraft.class_7701.field_40182);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable t) {
            throw new IllegalStateException("cannot build a 1.20.4 simple screen handler type", t);
        }
    }

    /**
     * An invocation handler implementing one abstract method over a function; default methods run as declared,
     * Object's methods answer as an identity object.
     */
    public static InvocationHandler handler(java.util.function.Predicate<Method> isTheMethod,
                                            java.util.function.Function<Object[], Object> body) {
        return (proxy, method, args) -> {
            if (method.getDeclaringClass() == Object.class) {
                return switch (method.getName()) {
                    case "equals" -> proxy == args[0];
                    case "hashCode" -> System.identityHashCode(proxy);
                    default -> "CenturyBridge 1.20.4 " + method.getDeclaringClass().getName() + "@" + Integer.toHexString(System.identityHashCode(proxy));
                };
            }
            if (method.isDefault()) {
                return InvocationHandler.invokeDefault(proxy, method, args);
            }
            if (isTheMethod.test(method)) {
                return body.apply(args);
            }
            throw new AbstractMethodError(method.toString());
        };
    }
}
