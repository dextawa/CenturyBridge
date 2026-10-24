package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's chunk-section rendering shapes over 1.21.2's.
 * <ul>
 * <li>ViewArea.repositionCamera(x, z): 1.21.2 recentres on the camera's SectionPos (its LevelRenderer passes
 *     SectionPos.of(camera position) where 1.21.1's passed the camera's x and z; both centre the grid on the camera's
 *     section and read only its x and z). A mod ViewArea's 1.21.1 override answers 1.21.2's calls (overrideHooks: the
 *     section's centre handed in as x and z, which map back to the same section).</li>
 * <li>SectionOcclusionGraph.addSectionsInFrustum(frustum, visible): 1.21.2 also collects the nearby sections into a
 *     second list, which 1.21.1 had no notion of -- a list of the call's own, discarded.</li>
 * <li>SectionOcclusionGraph.update(..., visible): 1.21.2 also takes the level's loaded empty sections, which its
 *     LevelRenderer passes from the client chunk cache -- the same here.</li>
 * <li>new SectionRenderDispatcher(.., executor, ..): 1.21.2 takes a TracingExecutor; the game's background executor is
 *     one, any other executor is wrapped in one that runs on it.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbRenderSections {

    /** ViewArea's 1.21.1 repositionCamera(x, z) (CbRenderViewAreaMixin declares it) */
    public interface LegacyViewArea {
        void method_3330(double x, double z);
    }

    private CbRenderSections() {
    }

    /** the section 1.21.2 recentres on for 1.21.1's camera x and z */
    public static net.minecraft.class_4076 section(double x, double z) {
        return net.minecraft.class_4076.method_18682(net.minecraft.class_2338.method_49637(x, 0.0, z));
    }

    /** 1.21.2's repositionCamera(section) on a mod ViewArea: its 1.21.1 override, with the section's centre */
    public static void repositionCamera(LegacyViewArea area, net.minecraft.class_4076 section) {
        area.method_3330(section.method_19527() + 8.0, section.method_19529() + 8.0);
    }

    /** SectionOcclusionGraph.addSectionsInFrustum(frustum, visible) */
    public static void method_52828(net.minecraft.class_8679 graph, net.minecraft.class_4604 frustum, List<net.minecraft.class_846.class_851> visible) {
        graph.method_52828(frustum, visible, new ArrayList<>());
    }

    /** SectionOcclusionGraph.update(smartCull, camera, frustum, visible) */
    public static void method_52834(net.minecraft.class_8679 graph, boolean smartCull, net.minecraft.class_4184 camera, net.minecraft.class_4604 frustum,
                                    List<net.minecraft.class_846.class_851> visible) {
        graph.method_52834(smartCull, camera, frustum, visible, net.minecraft.class_310.method_1551().field_1687.method_2935().method_62890());
    }

    /** the TracingExecutor 1.21.2's SectionRenderDispatcher takes for 1.21.1's executor */
    public static net.minecraft.class_10207 tracing(Executor executor) {
        if (executor instanceof net.minecraft.class_10207 tracing) {
            return tracing;
        }
        if (executor instanceof ExecutorService service) {
            return new net.minecraft.class_10207(service);
        }
        return new net.minecraft.class_10207(new AbstractExecutorService() {
            @Override
            public void execute(Runnable command) {
                executor.execute(command);
            }

            @Override
            public void shutdown() {
            }

            @Override
            public List<Runnable> shutdownNow() {
                return List.of();
            }

            @Override
            public boolean isShutdown() {
                return false;
            }

            @Override
            public boolean isTerminated() {
                return false;
            }

            @Override
            public boolean awaitTermination(long timeout, TimeUnit unit) {
                return false;
            }
        });
    }

    /** RenderSection's 1.21.1 cancelTasks() answer (CbRenderSectionMixin) */
    public interface LegacySection {
        boolean cb$cancelTasks();
    }

    /** a mod RenderSection's 1.21.1 cancelTasks() override, as its class declares it (overrideHooks adapter) */
    public interface LegacyCancel {
        boolean method_3663();
    }

    /** RenderSection.cancelTasks() in 1.21.1's shape: whether a task was cancelled (also a mod's super call: 1.21.2's body under the hook's guard) */
    public static boolean cancelTasks(net.minecraft.class_846.class_851 section) {
        return ((LegacySection) section).cb$cancelTasks();
    }

    /** 1.21.2's cancelTasks() on a mod RenderSection: its 1.21.1 override; 1.21.2's callers take no answer */
    public static void cancelTasks(LegacyCancel section) {
        section.method_3663();
    }

    /** addSectionsInFrustum's nearby list for a 1.21.1 override entered directly (frameDefaults): a list of its own */
    public static List<net.minecraft.class_846.class_851> nearbySections(net.minecraft.class_8679 graph, net.minecraft.class_4604 frustum,
                                                                        List<net.minecraft.class_846.class_851> visible) {
        return new ArrayList<>();
    }
}
