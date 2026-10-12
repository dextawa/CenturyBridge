package top.dext.centurybridge.step.v1_20_3.rt;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.2 Realms API on 1.20.3 (RealmsScreen constants; the realms main screen, world options and reset
 * requests, world slot buttons, long-running tasks, world reset and creation).
 */
@Environment(EnvType.CLIENT)
public final class CbRealms {

    // ------------------------------------------------------------------ RealmsScreen (class_4905)

    /** RealmsScreen.COLOR_RED_FADE (field_33036), 1.20.2's value; javac inlines it, a direct read lands here */
    public static final int field_33036 = 15553363;

    /** RealmsScreen.COLOR_YELLOW (field_33038), 1.20.2's value */
    public static final int field_33038 = 13413468;

    /** RealmsScreen.COLOR_BRIGHT_YELLOW (field_33039), 1.20.2's value */
    public static final int field_33039 = -256;

    // ------------------------------------------------------------------ RealmsMainScreen (class_4325)

    /**
     * refreshServerList() (method_53793) was an instance method in 1.20.2 and is static in 1.20.3, with the
     * same body (the realms data fetcher's server list task is reset): a 1.20.2 call on a screen.
     */
    public static void method_53793(net.minecraft.class_4325 screen) {
        net.minecraft.class_4325.method_53793();
    }

    /**
     * A 1.20.2 RealmsMainScreen subclass overriding refreshServerList() (adapterHooks marks it): 1.20.2's
     * RealmsConfigureWorldScreen asked the main screen it came from to refresh, and so reached the override
     * (see the RealmsConfigureWorldScreen mixin).
     */
    public interface CbLegacyRefresh {
        void method_53793();
    }

    // ------------------------------------------------------------------ world options / reset requests

    /**
     * RealmsWorldOptions(pvp, spawnAnimals, spawnMonsters, spawnNPCs, spawnProtection, commandBlocks,
     * difficulty, gameMode, forceGameMode, slotName) of 1.20.2 had no version and no compatibility: 1.20.3's
     * createDefaults() leaves them at "" and the default compatibility, which its JSON leaves out -- the
     * 1.20.2 document again.
     */
    public static String cbNoVersion() {
        return "";
    }

    public static net.minecraft.class_4877.class_8842 cbDefaultCompatibility() {
        return net.minecraft.class_4877.class_8842.field_46697;
    }

    /** RealmsWorldResetDto(seed, worldTemplateId, levelType, generateStructures): 1.20.2 reset no experiments */
    public static Set<String> cbNoExperiments() {
        return Set.of();
    }

    // ------------------------------------------------------------------ RealmsWorldSlotButton (class_4367)

    /** what a slot button made with the 1.20.2 constructor keeps: where its server comes from, whom it tells its tooltip */
    public interface CbLegacySlotButton {
        void cb$legacy(Supplier<net.minecraft.class_4877> server, Consumer<net.minecraft.class_2561> tooltipSetter);
    }

    /**
     * RealmsWorldSlotButton(x, y, width, height, serverDataProvider, toolTipSetter, slotIndex, onPress) of
     * 1.20.2 pulled its server from the supplier at each tick() and handed its action prompt to the
     * setter while hovered; 1.20.3's is told the server (setServerData) and shows its own tooltip.
     */
    public static void cbSlotButtonBuilt(net.minecraft.class_4367 button, int x, int y, int width, int height,
                                         Supplier<net.minecraft.class_4877> server, Consumer<net.minecraft.class_2561> tooltipSetter,
                                         int slotIndex, net.minecraft.class_4185.class_4241 onPress) {
        ((CbLegacySlotButton) button).cb$legacy(server, tooltipSetter);
    }

    // ------------------------------------------------------------------ long-running tasks, reset, creation

    /** RealmsLongRunningMcoTaskScreen(lastScreen, task): 1.20.3 runs a sequence of tasks, here the one */
    public static net.minecraft.class_4358[] cbTasks(net.minecraft.class_4358 task) {
        return new net.minecraft.class_4358[] {task};
    }

    /**
     * RealmsResetWorldScreen.forNewRealm(lastScreen, server, callback) of 1.20.2 reset a realm already
     * created; 1.20.3's takes the creation task to run first, which it skips when there is none.
     */
    public static net.minecraft.class_4445 cbNoCreationTask() {
        return null;
    }

    /** a creation task made with the 1.20.2 constructor: the screen it showed once the realm was initialized */
    public interface CbLegacyCreationTask {
        void cb$returnTo(net.minecraft.class_437 screen);
    }

    /** RealmCreationTask(worldId, name, motd, lastScreen): 1.20.2's run() went to lastScreen after initializing */
    public static void cbCreationTaskBuilt(net.minecraft.class_4445 task, long worldId, String name, String motd,
                                           net.minecraft.class_437 lastScreen) {
        ((CbLegacyCreationTask) task).cb$returnTo(lastScreen);
    }

    // ------------------------------------------------------------------ DataFetcher.Subscription (class_7581$class_7584)

    /**
     * A 1.20.2 consumer subscribed to a data fetcher task (Subscription.subscribe(task, consumer), converted in
     * place): RealmsDataFetcher's serverListUpdateTask (field_39683) answered the realms servers in 1.20.2 and
     * answers a ServerListData since 1.20.3, under the same field -- the servers, and the realms a snapshot
     * client may join. Its servers are the list 1.20.2's task answered (the servers of the client's server list
     * request, method_21015); ServerListData is 1.20.3's own, so no consumer written for 1.20.2 takes one. Every
     * other task's value is handed on as it is.
     */
    public static Consumer<?> cbLegacyConsumer(Consumer<?> consumer) {
        return consumer == null || consumer instanceof CbLegacyConsumer ? consumer : new CbLegacyConsumer(consumer);
    }

    private static final class CbLegacyConsumer implements Consumer<Object> {
        private final Consumer<Object> to;

        @SuppressWarnings("unchecked")
        CbLegacyConsumer(Consumer<?> to) {
            this.to = (Consumer<Object>) to;
        }

        @Override
        public void accept(Object value) {
            to.accept(value instanceof net.minecraft.class_7578.class_8843 data ? data.comp_1989() : value);
        }
    }

    private CbRealms() {
    }
}
