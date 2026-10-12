package top.dext.centurybridge.rt.v1_20_2;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Added (adapterHooks) to every mod class extending 1.20.1's LongRunningTask: such a task speaks
 * 1.20.1's protocol with the screen running it -- the screen hands itself over (setScreen), the task
 * sets the screen's title (setTitle) and reports its failures to it (error) -- and declares no
 * getTitle(), which 1.20.2's screen asks every task for; the hook synthesizes it
 * (CbNetRealms.cbTaskTitle).
 */
@Environment(EnvType.CLIENT)
public interface CbNetLegacyTask {
}
