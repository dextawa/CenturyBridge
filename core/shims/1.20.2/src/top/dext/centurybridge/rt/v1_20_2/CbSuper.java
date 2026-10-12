package top.dext.centurybridge.rt.v1_20_2;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A vanilla method's own implementation, invoked on a given receiver the way a subclass's
 * {@code super.m(..)} does: no virtual dispatch past the named class. A 1.20.1 subclass's super
 * call into a reshaped vanilla method lands in a static bridge, and the bridge must run
 * vanilla's body on the subclass instance -- calling the method virtually would come back into
 * the subclass's own (converted) override. Built with MethodHandles.privateLookupIn on the
 * vanilla class (CenturyBridge and the game share the loader's unnamed module), cached per
 * method.
 */
public final class CbSuper {

    private static final ConcurrentHashMap<String, MethodHandle> HANDLES = new ConcurrentHashMap<>();

    /** owner's implementation of name+type; the handle's first argument is the receiver */
    public static MethodHandle special(Class<?> owner, String name, MethodType type) {
        return HANDLES.computeIfAbsent(owner.getName() + "." + name + type.toMethodDescriptorString(), k -> {
            try {
                return MethodHandles.privateLookupIn(owner, MethodHandles.lookup()).findSpecial(owner, name, type, owner);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("CenturyBridge: cannot reach " + owner.getName() + "." + name
                    + type.toMethodDescriptorString() + " as a super call", e);
            }
        });
    }

    /** rethrows what a super-invoked handle threw, unchecked as the call it stands for */
    public static RuntimeException rethrow(Throwable t) {
        if (t instanceof RuntimeException r) {
            return r;
        }
        if (t instanceof Error e) {
            throw e;
        }
        return new IllegalStateException(t);
    }

    private CbSuper() {
    }
}
