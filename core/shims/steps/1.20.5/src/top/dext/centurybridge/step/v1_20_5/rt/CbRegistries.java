package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * The registries of the running world, for every bridge of step 1.20.4 -> 1.20.5: 1.20.5 threads a
 * registry lookup through calls 1.20.4 made without one (an item stack's NBT save / load, saved data,
 * block entity and chunk NBT, a byte buffer that encodes registry entries, a recipe handed a lookup
 * provider where 1.20.4 handed a RegistryAccess). One tracker for the whole step; the step's lookupRt
 * (cbLookup) answers from it too.
 *
 * Which registries {@link #current()} answers, in this order:
 * <ol>
 * <li>on the client (render) thread: the client world's registries -- registered by the client-only
 *     CbRegistriesClientMixin through the {@link Client} slot, so no client type is reachable from a
 *     class a dedicated server loads;</li>
 * <li>otherwise, while a server runs in this process (a dedicated server, or the integrated server of
 *     a singleplayer world, captured by CbRegistriesServerMixin): that server's registryAccess() --
 *     its own thread and its workers;</li>
 * <li>otherwise, on a client in a world with no server in this process (multiplayer): the client
 *     world's registries on every thread;</li>
 * <li>otherwise -- before a world loads, or on a client between worlds -- the built-in registries
 *     (RegistryAccess over BuiltInRegistries.REGISTRY). That is what 1.20.4 code had at that moment:
 *     1.20.4 kept items, blocks, enchantments, effects, potions and every other registry its item and
 *     data code touched without a world in the static built-in registries. What the built-in registries
 *     cannot encode are the data-driven ones, which exist only inside a loaded world -- trim materials
 *     and patterns, banner patterns, wolf variants, damage types, biomes and the other datapack
 *     registries: a value naming one of those fails to encode or decode with the codec's own error.
 *     1.20.4 code could not have reached those registries without a world either.</li>
 * </ol>
 * Never null.
 */
public final class CbRegistries {

    /** the client's side of the tracker (filled by the client-only mixin; empty on a dedicated server) */
    public interface Client {
        /** whether the calling thread is the client (render) thread */
        boolean onThread();

        /** the client world's registries, or null outside a world */
        net.minecraft.class_5455 access();
    }

    private static volatile net.minecraft.server.MinecraftServer server;
    private static volatile Client client;
    private static volatile net.minecraft.class_5455 builtin;

    /** CbRegistriesServerMixin: a server was constructed */
    public static void serverStarted(net.minecraft.server.MinecraftServer s) {
        server = s;
    }

    /** CbRegistriesServerMixin: a server stopped (an integrated one ends with its world) */
    public static void serverStopped(net.minecraft.server.MinecraftServer s) {
        if (server == s) {
            server = null;
        }
    }

    /** CbRegistriesClientMixin: how the client answers (see {@link Client}) */
    public static void client(Client c) {
        client = c;
    }

    /**
     * The server running in this process (a dedicated server, or a singleplayer world's integrated
     * server), or null when there is none -- for a bridge that needs the server itself (its recipe
     * manager, its levels), not only its registries.
     */
    public static net.minecraft.server.MinecraftServer server() {
        return server;
    }

    /** the running world's registries as a lookup provider (see the class comment); never null */
    public static net.minecraft.class_7225.class_7874 current() {
        return currentAccess();
    }

    /** lookupRt: the lookup a lookup-hooked call site appends -- the same as {@link #current()} */
    public static net.minecraft.class_7225.class_7874 cbLookup() {
        return currentAccess();
    }

    /** {@link #current()} as the RegistryAccess it is; never null */
    public static net.minecraft.class_5455 currentAccess() {
        Client c = client;
        if (c != null && c.onThread()) {
            net.minecraft.class_5455 a = c.access();
            if (a != null) {
                return a;
            }
        }
        net.minecraft.server.MinecraftServer s = server;
        if (s != null) {
            return s.method_30611();
        }
        if (c != null) {
            net.minecraft.class_5455 a = c.access();
            if (a != null) {
                return a;
            }
        }
        return builtin();
    }

    /** the built-in (static) registries; see the class comment for what they cannot encode */
    public static net.minecraft.class_5455 builtin() {
        net.minecraft.class_5455 b = builtin;
        if (b == null) {
            builtin = b = net.minecraft.class_5455.method_40302(net.minecraft.class_7923.field_41167);
        }
        return b;
    }

    private CbRegistries() {
    }
}
