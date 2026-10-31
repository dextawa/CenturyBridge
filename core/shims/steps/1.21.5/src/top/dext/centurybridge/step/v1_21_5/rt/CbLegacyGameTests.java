package top.dext.centurybridge.step.v1_21_5.rt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.fabricmc.fabric.api.event.registry.DynamicRegistryView;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.class_10657;
import net.minecraft.class_10660;
import net.minecraft.class_10664;
import net.minecraft.class_10665;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_4516;
import net.minecraft.class_4525;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.dext.centurybridge.step.v1_21_5.compat.fapi.gametest.v1.FabricGameTest;
import top.dext.centurybridge.step.v1_21_5.facade.GameTest;
import top.dext.centurybridge.step.v1_21_5.facade.GameTestRegistry;
import top.dext.centurybridge.step.v1_21_5.facade.TestFunction;

/**
 * 1.21.4's game tests on 1.21.5's own registry and runner. 1.21.4 kept tests in GameTestRegistry's static lists
 * (facade/GameTestRegistry) and ran them by batch name with one @BeforeBatch / @AfterBatch consumer each; 1.21.5 runs
 * test_instance entries (a dynamic registry, javap 1.21.5 RegistryDataLoader: class_7924.field_56161), each naming a
 * test_function (BuiltInRegistries.TEST_FUNCTION, class_7923.field_56156) and a test_environment whose setup /
 * teardown GameTestRunner calls around each batch of tests sharing it (class_4520.method_56220 / method_66982).
 *
 * <p>Each TestFunction that enters 1.21.4's registry is registered here:
 * <ul>
 * <li>test function &lt;modid&gt;:&lt;test name&gt; = the TestFunction's own function, at once (the built-in registry
 * is open while mods initialize, as fabric-gametest-api-v1 3.1.3 relies on: FabricGameTestModInitializer.onInitialize).
 * The mod id is the provider of the "fabric-gametest" entrypoint that listed the class; a class registered any other
 * way (1.21.4's own GameTestRegistry.register path) is "minecraft".</li>
 * <li>test environment centurybridge_step_1_21_5:&lt;batch&gt; = a {@link Batch} whose setup / teardown are the
 * batch's @BeforeBatch / @AfterBatch consumers (looked up when the batch runs, as 1.21.4's runner did), and test
 * instance &lt;modid&gt;:&lt;test name&gt; = 1.21.5's FunctionGameTestInstance with the TestFunction's values, on each
 * dynamic registry load (Fabric API's DynamicRegistrySetupCallback, before the registries load data entries). One
 * environment per batch name, for every mod, as 1.21.4 grouped batches by name alone.</li>
 * </ul>
 * Both only where 1.21.5's Fabric API registers its own tests: -Dfabric-api.gametest set, or a development
 * environment (FabricGameTestModInitializer.onInitialize's gate, javap 3.1.3).
 *
 * <p>Where fabric-gametest-api-v1 is loaded, its 1.21.4 version's registration is given back as 2.0.26 did it
 * (javap -c -p 2.0.26 FabricGameTestModInitializer / FabricGameTestHelper / TestFunctionsMixin): each "fabric-gametest"
 * entrypoint's 1.21.4 class ({@link #legacyClass}) is registered with GameTestRegistry, its tests made with 2.0.26's makeTestFunction
 * ({@link #fabricTestFunction}) and run through FabricGameTest's invoker. 3.1.3 scans the same entrypoint for its own
 * @GameTest; a 1.21.4 class has none of those, so 3.1.3 logs "No methods with the GameTest annotation were found".
 *
 * <p>Ids: a 1.21.4 test name is class.method lowercased (a generated one any string; 1.21.4 compared test names
 * ignoring case); a character an Identifier path does not allow (a batch name's capitals) is written _x&lt;hex&gt;_.
 */
public final class CbLegacyGameTests {
    private static final Logger LOGGER = LoggerFactory.getLogger("CenturyBridge/GameTests");
    private static final String STEP_ID = "centurybridge_step_1_21_5";
    /** fabric-gametest-api-v1 2.0.26's GAME_TEST_IDS: entrypoint class -> providing mod */
    private static final Map<Class<?>, String> GAME_TEST_IDS = new HashMap<>();
    private static final List<Registered> REGISTERED = new ArrayList<>();
    private static final boolean ENABLED = System.getProperty("fabric-api.gametest") != null
        || FabricLoader.getInstance().isDevelopmentEnvironment();

    private record Registered(TestFunction function, class_2960 id) {
    }

    private CbLegacyGameTests() {
    }

    /** the layer's onInitialize: under the gate the environment type and the dynamic registry hook; 2.0.26's entrypoint registration */
    public static void init() {
        if (ENABLED) {
            class_2378.method_10230(class_7923.field_56154, class_2960.method_60655(STEP_ID, "legacy_batch"), Batch.CODEC);
            DynamicRegistrySetupCallback.EVENT.register(CbLegacyGameTests::registrySetup);
        }
        if (fabricGameTest()) {
            for (EntrypointContainer<Object> c : FabricLoader.getInstance().getEntrypointContainers("fabric-gametest", Object.class)) {
                Class<?> testClass = c.getEntrypoint().getClass();
                if (!legacyClass(testClass)) {
                    continue;
                }
                String modId = c.getProvider().getMetadata().getId();
                if (GAME_TEST_IDS.containsKey(testClass)) {
                    throw new UnsupportedOperationException("Test class (%s) has already been registered with mod (%s)"
                        .formatted(testClass.getCanonicalName(), modId));
                }
                GAME_TEST_IDS.put(testClass, modId);
                GameTestRegistry.method_36068(testClass);
                LOGGER.debug("Registered test class {} for mod {}", testClass.getCanonicalName(), modId);
            }
        }
    }

    /** the 1.21.4 game test API a converted class names (classRenames of s215g-gametest.json) */
    private static final String[] LEGACY_TYPES = {
        "top/dext/centurybridge/step/v1_21_5/facade/GameTest", "top/dext/centurybridge/step/v1_21_5/facade/GameTestGenerator",
        "top/dext/centurybridge/step/v1_21_5/facade/GameTestRegistry", "top/dext/centurybridge/step/v1_21_5/facade/BeforeBatch",
        "top/dext/centurybridge/step/v1_21_5/facade/AfterBatch", "top/dext/centurybridge/step/v1_21_5/compat/fapi/gametest/v1/FabricGameTest"};

    /**
     * whether an entrypoint class is 1.21.4 code: its class file names the 1.21.4 game test API (GameTest /
     * GameTestGenerator / GameTestRegistry, BeforeBatch, AfterBatch, FabricGameTest), which only a converted class
     * can. 1.21.5 mods list their own classes in the same entrypoint (3.1.3's TestAnnotationLocator reads them);
     * 1.21.4's registration never saw those, and reading their methods' annotations here would parse 3.1.3's
     * GameTest. A 1.21.4 class naming none of these registered nothing in 1.21.4 either.
     */
    private static boolean legacyClass(Class<?> c) {
        ClassLoader l = c.getClassLoader();
        try (java.io.InputStream in = l == null ? null : l.getResourceAsStream(c.getName().replace('.', '/') + ".class")) {
            if (in == null) {
                return false;
            }
            String bytes = new String(in.readAllBytes(), java.nio.charset.StandardCharsets.ISO_8859_1);
            for (String t : LEGACY_TYPES) {
                if (bytes.contains(t)) {
                    return true;
                }
            }
            return false;
        } catch (java.io.IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
    }

    /** whether fabric-gametest-api-v1 is loaded (its 1.21.4 version replaced makeTestFunction) */
    public static boolean fabricGameTest() {
        return FabricLoader.getInstance().isModLoaded("fabric-gametest-api-v1");
    }

    /** a TestFunction entering 1.21.4's registry: its 1.21.5 test function now, its instance on each registry load */
    public static void registered(TestFunction f, Class<?> testClass) {
        if (!ENABLED) {
            return;
        }
        class_2960 id = class_2960.method_60655(GAME_TEST_IDS.getOrDefault(testClass, "minecraft"), path(f.comp_2219().toLowerCase(Locale.ROOT)));
        class_2378.method_10230(class_7923.field_56156, id, f.comp_2227());
        REGISTERED.add(new Registered(f, id));
    }

    private static void registrySetup(DynamicRegistryView view) {
        class_2378<class_10665> environments = view.<class_10665>getOptional(class_7924.field_56160).orElse(null);
        class_2378<class_10660> instances = view.<class_10660>getOptional(class_7924.field_56161).orElse(null);
        if (environments == null || instances == null || REGISTERED.isEmpty()) {
            return;
        }
        Map<String, class_6880<class_10665>> batches = new HashMap<>();
        for (Registered r : REGISTERED) {
            TestFunction f = r.function();
            class_6880<class_10665> env = batches.computeIfAbsent(f.comp_2218(),
                b -> class_2378.method_47985(environments, class_2960.method_60655(STEP_ID, path(b)), new Batch(b)));
            class_10664<class_6880<class_10665>> data = new class_10664<>(env, class_2960.method_60654(f.comp_2220()), f.comp_2222(),
                (int) f.comp_2223(), f.comp_2224(), f.comp_2221(), f.comp_2445(), f.comp_2225(), f.comp_2226(), f.comp_2362());
            class_2378.method_10230(instances, r.id(), new class_10657(class_5321.method_29179(class_7924.field_56157, r.id()), data));
        }
    }

    /** an Identifier path for a 1.21.4 name: allowed characters kept, others _x&lt;hex&gt;_ */
    private static String path(String name) {
        StringBuilder b = new StringBuilder(name.length());
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == '_' && name.startsWith("_x", i)) {
                b.append("_x5f_");
            } else if (class_2960.method_29184(c)) {
                b.append(c);
            } else {
                b.append("_x").append(Integer.toHexString(c)).append('_');
            }
        }
        return b.toString();
    }

    /** fabric-gametest-api-v1 2.0.26's TestFunctionsMixin.getTestFunction (it replaced makeTestFunction at HEAD) */
    public static TestFunction fabricTestFunction(Method method) {
        GameTest test = method.getAnnotation(GameTest.class);
        String classLower = method.getDeclaringClass().getSimpleName().toLowerCase(Locale.ROOT);
        String testName = classLower + "." + method.getName().toLowerCase(Locale.ROOT);
        String structure = "%s:%s".formatted(modIdForTestClass(method.getDeclaringClass()), testName);
        if (!test.method_35936().isEmpty()) {
            structure = test.method_35936();
        }
        return new TestFunction(test.method_35933(), testName, structure, class_4525.method_29408(test.method_35934()), test.method_35932(),
            test.method_35937(), test.method_35935(), test.method_57962(), test.method_35938(), test.method_35939(), test.method_57098(),
            testMethodInvoker(method));
    }

    /** 2.0.26's FabricGameTestModInitializer.getModIdForTestClass */
    private static String modIdForTestClass(Class<?> testClass) {
        if (!GAME_TEST_IDS.containsKey(testClass)) {
            throw new UnsupportedOperationException("The test class (%s) was not registered using the '%s' entrypoint"
                .formatted(testClass.getCanonicalName(), "fabric-gametest"));
        }
        return GAME_TEST_IDS.get(testClass);
    }

    /** 2.0.26's FabricGameTestHelper.getTestMethodInvoker */
    private static Consumer<class_4516> testMethodInvoker(Method method) {
        return helper -> {
            Class<?> testClass = method.getDeclaringClass();
            Constructor<?> ctor;
            try {
                ctor = testClass.getConstructor();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Test class (%s) provided by (%s) must have a public default or no args constructor"
                    .formatted(testClass.getSimpleName(), modIdForTestClass(testClass)));
            }
            Object instance;
            try {
                instance = ctor.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to create instance of test class (%s)".formatted(testClass.getCanonicalName()), e);
            }
            if (instance instanceof FabricGameTest t) {
                t.invokeTestMethod(helper, method);
            } else {
                invokeTestMethod(helper, method, instance);
            }
        };
    }

    /** 2.0.26's FabricGameTestHelper.invokeTestMethod (FabricGameTest's default) */
    public static void invokeTestMethod(class_4516 helper, Method method, Object instance) {
        try {
            method.invoke(instance, helper);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to invoke test method (%s) in (%s) because %s"
                .formatted(method.getName(), method.getDeclaringClass().getCanonicalName(), e.getMessage()), e);
        } catch (InvocationTargetException e) {
            LOGGER.error("Exception occurred when invoking test method {} in ({})", method.getName(), method.getDeclaringClass().getCanonicalName(), e);
            if (e.getCause() instanceof RuntimeException r) {
                throw r;
            }
            throw new RuntimeException(e.getCause());
        }
    }

    /** a 1.21.4 batch as a 1.21.5 test environment: setup / teardown are its @BeforeBatch / @AfterBatch */
    public record Batch(String batch) implements class_10665 {
        public static final MapCodec<Batch> CODEC = Codec.STRING.fieldOf("batch").xmap(Batch::new, Batch::batch);

        @Override
        public void method_67051(class_3218 level) {
            GameTestRegistry.method_22198(batch).accept(level);
        }

        @Override
        public void method_67054(class_3218 level) {
            GameTestRegistry.method_32244(batch).accept(level);
        }

        @Override
        public MapCodec<? extends class_10665> method_67050() {
            return CODEC;
        }
    }
}
