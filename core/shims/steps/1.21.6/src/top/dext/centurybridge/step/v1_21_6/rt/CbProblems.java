package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * 1.21.5's ProblemReporter in strings on 1.21.6's ProblemReporter in path elements and problems. 1.21.5 built a
 * child's path by appending the string a caller passed (Collector.forChild: the parent's path + name, the caller
 * writing its own separator: ".pools", "[0]", "-&gt;{id}") and filed each reported string under that path; 1.21.6
 * appends PathElement.get() and files Problem.description(). A 1.21.5 string is therefore a path element whose
 * get() is that string (1.21.6's own elements carry their separator the same way: FieldPathElement.get() is
 * "." + name) and a problem whose description is that string, so the paths and reports read as 1.21.5's did.
 */
public final class CbProblems {

    private CbProblems() {
    }

    /** a 1.21.5 path string as a 1.21.6 path element */
    public record Path(String text) implements net.minecraft.class_8942.class_11336 {
        @Override
        public String get() {
            return text;
        }
    }

    /** a 1.21.5 problem string as a 1.21.6 problem */
    public record Problem(String text) implements net.minecraft.class_8942.class_11337 {
        @Override
        public String method_71358() {
            return text;
        }
    }

    public static net.minecraft.class_8942.class_11336 path(String text) {
        return new Path(text);
    }

    public static net.minecraft.class_8942.class_11337 problem(String text) {
        return new Problem(text);
    }

    /** override hook wraps: what a 1.21.5 override is handed for vanilla's element / problem */
    public static String text(net.minecraft.class_8942.class_11336 path) {
        return path.get();
    }

    public static String text(net.minecraft.class_8942.class_11337 problem) {
        return problem.method_71358();
    }

    // ---- calls ----

    /** ProblemReporter.forChild(String) */
    public static net.minecraft.class_8942 forChild(net.minecraft.class_8942 self, String name) {
        return self.method_54946(path(name));
    }

    /** ProblemReporter.report(String) */
    public static void report(net.minecraft.class_8942 self, String message) {
        self.method_54947(problem(message));
    }

    /** Collector.get(): every problem by the path it was reported under */
    public static com.google.common.collect.Multimap<String, String> get(net.minecraft.class_8942.class_8943 self) {
        com.google.common.collect.Multimap<String, String> all = com.google.common.collect.HashMultimap.create();
        self.method_71350((path, problem) -> all.put(path, problem.method_71358()));
        return com.google.common.collect.ImmutableMultimap.copyOf(all);
    }

    /** Collector.getReport(): empty when nothing was reported */
    public static java.util.Optional<String> getReport(net.minecraft.class_8942.class_8943 self) {
        return self.method_71349() ? java.util.Optional.empty() : java.util.Optional.of(self.method_59906());
    }

    /** ValidationContext.forChild(String) */
    public static net.minecraft.class_58 forChild(net.minecraft.class_58 self, String name) {
        return self.method_364(path(name));
    }

    /** ValidationContext.enterElement(String, ResourceKey) */
    public static net.minecraft.class_58 enterElement(net.minecraft.class_58 self, String name,
            net.minecraft.class_5321<?> key) {
        return self.method_51219(path(name), key);
    }

    /** ValidationContext.reportProblem(String) */
    public static void reportProblem(net.minecraft.class_58 self, String message) {
        self.method_360(problem(message));
    }

    // ---- a mod's super calls ----

    public interface SuperCollectorChild {
        net.minecraft.class_8942 cb$super$method_54946(net.minecraft.class_8942.class_11336 path);
    }

    public interface SuperCollectorReport {
        void cb$super$method_54947(net.minecraft.class_8942.class_11337 problem);
    }

    public interface SuperContextChild {
        net.minecraft.class_58 cb$super$method_364(net.minecraft.class_8942.class_11336 path);
    }

    public interface SuperContextElement {
        net.minecraft.class_58 cb$super$method_51219(net.minecraft.class_8942.class_11336 path, net.minecraft.class_5321<?> key);
    }

    public interface SuperContextReport {
        void cb$super$method_360(net.minecraft.class_8942.class_11337 problem);
    }

    public static net.minecraft.class_8942 superForChild(SuperCollectorChild self, String name) {
        return self.cb$super$method_54946(path(name));
    }

    public static void superReport(SuperCollectorReport self, String message) {
        self.cb$super$method_54947(problem(message));
    }

    public static net.minecraft.class_58 superContextForChild(SuperContextChild self, String name) {
        return self.cb$super$method_364(path(name));
    }

    public static net.minecraft.class_58 superEnterElement(SuperContextElement self, String name,
            net.minecraft.class_5321<?> key) {
        return self.cb$super$method_51219(path(name), key);
    }

    public static void superReportProblem(SuperContextReport self, String message) {
        self.cb$super$method_360(problem(message));
    }
}
