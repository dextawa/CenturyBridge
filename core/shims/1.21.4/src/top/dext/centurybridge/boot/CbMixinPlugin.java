package top.dext.centurybridge.boot;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinErrorHandler;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * Makes residual mixin failures of converted mods KNOWN instead of silent.
 *
 * The converter repairs or disables injectors it can judge statically and
 * marks converted mods' mixin configs "required": false, so whatever it could
 * not foresee makes Mixin skip that one mixin rather than end the game. Mixin
 * would log that as a warning among thousands of lines; this handler writes
 * every such failure to logs/centurybridge-mixin.log and keeps a count, so the
 * residue is measured, not hidden. It never changes Mixin's decision (returns
 * null = default action): a native mod's required config still fails hard.
 *
 * Lives outside the mixin package: classes there get transformed as mixins.
 */
public final class CbMixinPlugin implements IMixinConfigPlugin {

    private static final AtomicInteger FAILURES = new AtomicInteger();
    private static final Path LOG = Path.of("logs", "centurybridge-mixin.log");
    private static volatile boolean registered;

    @Override
    public void onLoad(String mixinPackage) {
        if (registered) {
            return;
        }
        registered = true;
        try {
            Files.createDirectories(LOG.getParent());
            Files.writeString(LOG, "# CenturyBridge mixin failure log -- converted mods' mixins Mixin skipped at runtime\n",
                StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            // logging is best effort; the console line below still reports
        }
        Mixins.registerErrorHandlerClass(Handler.class.getName());
    }

    static void record(String phase, String mixin, String target, Throwable t) {
        int n = FAILURES.incrementAndGet();
        Throwable root = t;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        String line = LocalTime.now().withNano(0) + " " + phase + " " + mixin
            + (target != null ? " -> " + target : "") + ": " + root.getClass().getSimpleName() + ": " + root.getMessage();
        System.out.println("[CenturyBridge] mixin failure #" + n + " (" + phase + "): " + mixin
            + " -- see " + LOG);
        try (Writer w = Files.newBufferedWriter(LOG, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            w.write(line.replace('\n', ' ') + "\n");
        } catch (IOException ex) {
            // best effort
        }
    }

    public static final class Handler implements IMixinErrorHandler {
        @Override
        public ErrorAction onPrepareError(IMixinConfig config, Throwable th, IMixinInfo mixin, ErrorAction action) {
            record("PREPARE[" + action + "]", config.getName() + ":" + (mixin != null ? mixin.getClassName() : "?"), null, th);
            return null;
        }

        @Override
        public ErrorAction onApplyError(String targetClassName, Throwable th, IMixinInfo mixin, ErrorAction action) {
            record("APPLY[" + action + "]", mixin.getConfig().getName() + ":" + mixin.getClassName(), targetClassName, th);
            return null;
        }
    }

    // ---- plugin contract: no filtering, CenturyBridge's own mixins all apply

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
