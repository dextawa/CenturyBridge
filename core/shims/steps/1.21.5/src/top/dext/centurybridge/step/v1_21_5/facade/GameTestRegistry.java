package top.dext.centurybridge.step.v1_21_5.facade;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.class_2470;
import net.minecraft.class_3218;
import net.minecraft.class_4516;
import net.minecraft.class_4525;
import top.dext.centurybridge.step.v1_21_5.rt.CbLegacyGameTests;

/**
 * 1.21.4's net.minecraft.gametest.framework.GameTestRegistry (net/minecraft/class_4519), gone in 1.21.5 (tests are
 * test_function / test_instance registry entries there). Every member, with 1.21.4's bodies (javap -c -p 1.21.4
 * class_4519): register(Class) registers the declared methods sorted by name; register(Method) turns a @GameTest
 * method into a TestFunction and a @GameTestGenerator method's collection into TestFunctions (the class's simple name
 * joins the test class names), and keeps one @BeforeBatch / @AfterBatch per batch (a second is 1.21.4's
 * RuntimeException). Every member is public (a facade stands in for the class; access wideners never reach it). Each TestFunction that enters the registry is also handed to rt/CbLegacyGameTests, which
 * registers it where 1.21.5's runner finds tests.
 *
 * makeTestFunction keeps 1.21.4's argument order: it passes requiredSuccesses() into maxAttempts and attempts() into
 * requiredSuccesses (javap -c 1.21.4 class_4519.method_36073 offsets 125/131 against class_4529's constructor). Where
 * Fabric API's game test module is loaded, makeTestFunction is fabric-gametest-api-v1 2.0.26's instead
 * (TestFunctionsMixin replaced 1.21.4's at HEAD; rt/CbLegacyGameTests.fabricTestFunction).
 */
public class GameTestRegistry {
    public static final Collection<TestFunction> field_20570 = Lists.newArrayList();
    public static final Set<String> field_20571 = Sets.newHashSet();
    public static final Map<String, Consumer<class_3218>> field_20572 = Maps.newHashMap();
    public static final Map<String, Consumer<class_3218>> field_27806 = Maps.newHashMap();
    public static final Set<TestFunction> field_25302 = Sets.newHashSet();

    public GameTestRegistry() {
    }

    /** register(Class) */
    public static void method_36068(Class<?> testClass) {
        Arrays.stream(testClass.getDeclaredMethods()).sorted(Comparator.comparing(Method::getName)).forEach(GameTestRegistry::method_36069);
    }

    /** register(Method) */
    public static void method_36069(Method method) {
        String className = method.getDeclaringClass().getSimpleName();
        GameTest test = method.getAnnotation(GameTest.class);
        if (test != null) {
            TestFunction f = method_36073(method);
            field_20570.add(f);
            CbLegacyGameTests.registered(f, method.getDeclaringClass());
            field_20571.add(className);
        }
        GameTestGenerator generator = method.getAnnotation(GameTestGenerator.class);
        if (generator != null) {
            Collection<TestFunction> fs = method_36072(method);
            field_20570.addAll(fs);
            for (TestFunction f : fs) {
                CbLegacyGameTests.registered(f, method.getDeclaringClass());
            }
            field_20571.add(className);
        }
        method_36070(method, BeforeBatch.class, BeforeBatch::method_35931, field_20572);
        method_36070(method, AfterBatch.class, AfterBatch::method_35930, field_27806);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Annotation> void method_36070(Method method, Class<T> annotationType, Function<T, String> batchName,
                                                            Map<String, Consumer<class_3218>> batchMap) {
        T annotation = method.getAnnotation(annotationType);
        if (annotation != null) {
            String batch = batchName.apply(annotation);
            Consumer<class_3218> previous = batchMap.putIfAbsent(batch, (Consumer<class_3218>) method_36074(method));
            if (previous != null) {
                throw new RuntimeException("Hey, there should only be one " + annotationType + " method per batch. Batch '" + batch + "' has more than one!");
            }
        }
    }

    /** getTestFunctionsForClassName(String) */
    public static Stream<TestFunction> method_22193(String className) {
        return field_20570.stream().filter(f -> method_22192(f, className));
    }

    /** getAllTestFunctions() */
    public static Collection<TestFunction> method_22191() {
        return field_20570;
    }

    /** getAllTestClassNames() */
    public static Collection<String> method_22195() {
        return field_20571;
    }

    /** isTestClass(String) */
    public static boolean method_22196(String className) {
        return field_20571.contains(className);
    }

    /** getBeforeBatchFunction(String) */
    public static Consumer<class_3218> method_22198(String batch) {
        return field_20572.getOrDefault(batch, level -> {
        });
    }

    /** getAfterBatchFunction(String) */
    public static Consumer<class_3218> method_32244(String batch) {
        return field_27806.getOrDefault(batch, level -> {
        });
    }

    /** findTestFunction(String) */
    public static Optional<TestFunction> method_22199(String testName) {
        return method_22191().stream().filter(f -> f.comp_2219().equalsIgnoreCase(testName)).findFirst();
    }

    /** getTestFunction(String) */
    public static TestFunction method_22200(String testName) {
        Optional<TestFunction> f = method_22199(testName);
        if (f.isEmpty()) {
            throw new IllegalArgumentException("Can't find the test function for " + testName);
        }
        return f.get();
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    public static Collection<TestFunction> method_36072(Method method) {
        try {
            Object instance = method.getDeclaringClass().newInstance();
            return (Collection<TestFunction>) method.invoke(instance);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static TestFunction method_36073(Method method) {
        if (CbLegacyGameTests.fabricGameTest()) {
            return CbLegacyGameTests.fabricTestFunction(method);
        }
        GameTest test = method.getAnnotation(GameTest.class);
        String classLower = method.getDeclaringClass().getSimpleName().toLowerCase();
        String testName = classLower + "." + method.getName().toLowerCase();
        String structure = test.method_35936().isEmpty() ? testName : classLower + "." + test.method_35936();
        String batch = test.method_35933();
        class_2470 rotation = class_4525.method_29408(test.method_35934());
        return new TestFunction(batch, testName, structure, rotation, test.method_35932(), test.method_35937(), test.method_35935(),
            test.method_57962(), test.method_35939(), test.method_35938(), test.method_57098(), (Consumer<class_4516>) method_36074(method));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Consumer<?> method_36074(Method method) {
        return (Consumer) arg -> method_36071(method, arg);
    }

    public static boolean method_22192(TestFunction f, String className) {
        return f.comp_2219().toLowerCase().startsWith(className.toLowerCase() + ".");
    }

    /** getLastFailedTests() */
    public static Stream<TestFunction> method_29405() {
        return field_25302.stream();
    }

    /** rememberFailedTest(TestFunction) */
    public static void method_29404(TestFunction f) {
        field_25302.add(f);
    }

    /** forgetFailedTests() */
    public static void method_29406() {
        field_25302.clear();
    }

    @SuppressWarnings("deprecation")
    public static void method_36071(Method method, Object arg) {
        try {
            Object instance = method.getDeclaringClass().newInstance();
            method.invoke(instance, arg);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException r) {
                throw r;
            }
            throw new RuntimeException(e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
