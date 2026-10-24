import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Dumps what the public static constants of some game classes ARE, by registry identity: for each public static
 * field of each named class (and, inside a record value, each component, two levels down) one line
 *
 *   owner  field  path  kind  registry  id  class
 *
 * kind "value" (the object is registered under id in registry), "key" (a ResourceKey of that registry), "holder"
 * (a Holder bound to that id), "tag" (a TagKey); values that are none of these are not printed. class is the
 * value's runtime class (a field W declares as Holder may still hold a Holder$Reference). Reads the game by
 * reflection only (no compile-time dependency): boots the built-in registries with Bootstrap.bootStrap(), as a
 * dedicated server does before any mod code runs.
 *
 * Feeds gen_registry_constants.py: a V constant W no longer declares is the W constant (or record component)
 * holding the object registered under the same id -- both sides measured on the real game.
 *
 *   java -cp <client jar>:<its libraries> RegistryDump net.minecraft.world.level.block.Blocks ... > dump.tsv
 */
public class RegistryDump {
    static final Map<Object, String> REGISTERED = new IdentityHashMap<>();
    static Class<?> resourceKey, holder, tagKey;
    /** the real stdout: Bootstrap routes System.out through the game's logger */
    static final java.io.PrintStream OUT = System.out;

    public static void main(String[] args) throws Exception {
        Class.forName("net.minecraft.SharedConstants").getMethod("tryDetectVersion").invoke(null);
        Class.forName("net.minecraft.server.Bootstrap").getMethod("bootStrap").invoke(null);
        resourceKey = Class.forName("net.minecraft.resources.ResourceKey");
        holder = Class.forName("net.minecraft.core.Holder");
        tagKey = Class.forName("net.minecraft.tags.TagKey");
        Class<?> bir = Class.forName("net.minecraft.core.registries.BuiltInRegistries");
        Object root = bir.getField("REGISTRY").get(null);
        Method getKey = Class.forName("net.minecraft.core.Registry").getMethod("getKey", Object.class);
        for (Object reg : (Iterable<?>) root) {
            String rid = String.valueOf(getKey.invoke(root, reg));
            for (Object v : (Iterable<?>) reg) {
                Object id = getKey.invoke(reg, v);
                REGISTERED.putIfAbsent(v, rid + "\t" + id);
            }
        }
        for (String cn : args) {
            Class<?> c;
            try {
                c = Class.forName(cn);
            } catch (Throwable t) {
                System.err.println("skip " + cn + ": " + t);
                continue;
            }
            for (Field f : c.getDeclaredFields()) {
                int m = f.getModifiers();
                if (!Modifier.isStatic(m) || !Modifier.isPublic(m)) {
                    continue;
                }
                Object v;
                try {
                    v = f.get(null);
                } catch (Throwable t) {
                    System.err.println("skip " + cn + "." + f.getName() + ": " + t);
                    continue;
                }
                describe(cn.replace('.', '/'), f.getName(), "", v, 0);
            }
        }
    }

    static String cls(Object v) {
        return v.getClass().getName().replace('.', '/');
    }

    static void describe(String owner, String field, String path, Object v, int depth) throws Exception {
        if (v == null) {
            return;
        }
        String reg = REGISTERED.get(v);
        if (reg != null) {
            OUT.println(owner + "\t" + field + "\t" + path + "\tvalue\t" + reg + "\t" + cls(v));
            return;
        }
        if (resourceKey.isInstance(v)) {
            Object r = resourceKey.getMethod("registry").invoke(v);
            Object id = resourceKey.getMethod("identifier").invoke(v);
            OUT.println(owner + "\t" + field + "\t" + path + "\tkey\t" + r + "\t" + id + "\t" + cls(v));
            return;
        }
        if (tagKey.isInstance(v)) {
            Object rk = tagKey.getMethod("registry").invoke(v);
            Object r = resourceKey.getMethod("identifier").invoke(rk);
            Object id = tagKey.getMethod("location").invoke(v);
            OUT.println(owner + "\t" + field + "\t" + path + "\ttag\t" + r + "\t" + id + "\t" + cls(v));
            return;
        }
        if (holder.isInstance(v)) {
            Object key = holder.getMethod("unwrapKey").invoke(v);
            if (key instanceof java.util.Optional<?> o && o.isPresent()) {
                Object k = o.get();
                Object r = resourceKey.getMethod("registry").invoke(k);
                Object id = resourceKey.getMethod("identifier").invoke(k);
                OUT.println(owner + "\t" + field + "\t" + path + "\tholder\t" + r + "\t" + id + "\t" + cls(v));
            }
            return;
        }
        if (depth < 2 && v.getClass().isRecord()) {
            for (RecordComponent rc : v.getClass().getRecordComponents()) {
                Method acc = rc.getAccessor();
                acc.setAccessible(true);
                describe(owner, field, path + "." + rc.getName() + "()", acc.invoke(v), depth + 1);
            }
        }
    }
}
