package top.dext.centurybridge.rt.v1_20_2;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;
import net.minecraft.class_4398;

/**
 * What the Realms bridge mixins of this layer put on vanilla Realms classes, as interfaces those
 * classes then implement (each mixin implements its own), so the bridge statics and the other mixins
 * reach it without reflection. 1.20.1 member names where the member is the 1.20.1 one.
 */
@Environment(EnvType.CLIENT)
public final class CbNetRealmsAccess {

    /** LongRunningTask (CbNetRealmsTaskBridge) */
    public interface Task {

        /** 1.20.1 setScreen(RealmsLongRunningMcoTaskScreen): the screen the task reports to */
        void method_21066(class_4398 screen);

        /** the title a 1.20.1 task gave itself through setTitle, null before it did */
        class_2561 cb$title();

        /** 1.20.1's longRunningMcoTaskScreen field: the screen setScreen handed over, null before */
        class_4398 cb$screen();
    }

    /** RealmsMainScreen (CbNetRealmsPopupBridge): 1.20.1's popup state, for the injectors of CbNetRealmsPopupBridgeHooks */
    public interface Popup {

        /** the server list callback has run (1.20.1 hasFetchedServers = true) */
        void cb$markServersFetched();

        /** 1.20.1 createdTrial */
        boolean cb$createdTrial();
    }

    /** RealmsLongRunningMcoTaskScreen (CbNetRealmsTaskScreenBridge): its 1.20.1 task-facing methods */
    public interface TaskScreen {

        void method_21067(class_2561 message);

        boolean method_21291();

        void method_21292(class_2561 title);
    }

    /** RealmsServiceException (CbNetRealmsExceptionBridgeHooks) */
    public interface Failure {

        /** the failure as 1.20.1 described it, fixed when the exception was made */
        CbNetLegacyRealmsError cb$legacy();
    }

    /**
     * RealmsResetWorldScreen, RealmsSelectFileToUploadScreen, RealmsUploadScreen: the after-upload
     * callback 1.20.1 handed down this chain of screens (null when the screen was made the 1.20.2 way)
     */
    public interface Callback {

        void cb$setLegacyCallback(Runnable callback);

        Runnable cb$legacyCallback();
    }

    /** RealmsResetWorldScreen: the label of its back button that 1.20.1's constructors took */
    public interface ResetScreen {

        void cb$setLegacyButtonTitle(class_2561 title);
    }

    private CbNetRealmsAccess() {
    }
}
