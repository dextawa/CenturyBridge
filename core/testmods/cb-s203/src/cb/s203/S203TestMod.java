package cb.s203;

import cb.s203.mixin.SkullAccessor;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A 1.20.2-era server mod (intermediary names) for s203's bridges of the 1.20.2 -> 1.20.3 step: SkullBlockEntity's
 * profile cache / session service statics (read, and a stored cache asked by the skull lookup), and a 1.20.2 mixin on
 * Blocks.leaves (private, its return type narrowed to Block by 1.20.3). Runs once the server has started; prints
 * "CB-S203 FAIL ..." per failure and "CB-S203 DONE passed/checks".
 */
public class S203TestMod implements ModInitializer {
    public static final List<Object> LEAVES = new ArrayList<>();
    private static final Logger LOG = LoggerFactory.getLogger("cb_s203");
    private int checks;
    private int passed;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(this::run);
    }

    private void check(boolean ok, String what) {
        checks++;
        if (ok) {
            passed++;
            LOG.info("CB-S203 ok " + what);
        } else {
            LOG.info("CB-S203 FAIL " + what);
        }
    }

    /** a profile cache that records the names it is asked for */
    static final class Recording extends net.minecraft.class_3312 {
        final List<String> asked = new ArrayList<>();

        Recording(File file) {
            super(null, file);
        }

        @Override
        public CompletableFuture<Optional<GameProfile>> method_37156(String name) {
            asked.add(name);
            return CompletableFuture.completedFuture(Optional.empty());
        }
    }

    private void run(MinecraftServer server) {
        try {
            check(LEAVES.size() == 8, "Blocks.leaves mixin fired for the 8 leaves blocks (saw " + LEAVES.size() + ")");
            check(LEAVES.stream().allMatch(b -> b instanceof net.minecraft.class_2397), "every leaves answer is a LeavesBlock");

            net.minecraft.class_3312 vanilla = SkullAccessor.cbs203$profileCache();
            check(vanilla != null && vanilla == server.method_3793(), "SkullBlockEntity.profileCache is the server's");
            check(SkullAccessor.cbs203$sessionService() != null && SkullAccessor.cbs203$sessionService() == server.method_3844(),
                "SkullBlockEntity.sessionService is the server's");

            File f = new File(server.method_27050(net.minecraft.class_5218.field_24188).toFile(), "cbs203-usercache.json");
            Recording rec = new Recording(f);
            SkullAccessor.cbs203$setProfileCache(rec);
            check(SkullAccessor.cbs203$profileCache() == rec, "a stored profile cache reads back");
            net.minecraft.class_2487 tag = new net.minecraft.class_2487();
            tag.method_10582("SkullOwner", "cbs203probe");
            net.minecraft.class_2631.method_52590(tag);
            check(rec.asked.contains("cbs203probe"), "the skull lookup asked the stored profile cache (asked " + rec.asked + ")");
            SkullAccessor.cbs203$setProfileCache(vanilla);
            check(SkullAccessor.cbs203$profileCache() == vanilla, "the server's profile cache restored");
        } catch (Throwable t) {
            LOG.error("CB-S203 FAIL exception", t);
            checks++;
        }
        LOG.info("CB-S203 DONE " + passed + "/" + checks);
    }
}
