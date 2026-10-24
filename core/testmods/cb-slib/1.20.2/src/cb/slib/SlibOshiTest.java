package cb.slib;

import java.util.List;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.software.os.linux.LinuxOSProcess;
import oshi.util.platform.windows.WmiQueryHandler;
import oshi.util.tuples.Pair;

/** OSHI 6.2.2-era uses (step 1's library bridges); run from SlibAuthlib2Test. Linux-only checks skip elsewhere. */
final class SlibOshiTest {
    private SlibOshiTest() {
    }

    /** a 6.2.2 subclass of WmiQueryHandler reading and writing its protected state */
    static class Handler extends WmiQueryHandler {
        int timeout() {
            return wmiTimeout;
        }

        void timeout(int t) {
            wmiTimeout = t;
        }

        int failedCount() {
            return failedWmiClassNames.size();
        }
    }

    /** a 6.2.2 subclass of a platform process */
    static class MyLinuxProcess extends LinuxOSProcess {
        MyLinuxProcess(int pid) {
            super(pid);
        }
    }

    static void run() {
        SlibAuthlib2Test.run("oshi WmiQueryHandler", () -> {
            Handler h = new Handler();
            h.timeout(1234);
            SlibAuthlib2Test.check(h.timeout() == 1234 && h.getWmiTimeout() == 1234, "WmiQueryHandler.wmiTimeout read/write from a subclass");
            SlibAuthlib2Test.check(h.failedCount() == 0, "WmiQueryHandler.failedWmiClassNames from a subclass");
            WmiQueryHandler plain = new WmiQueryHandler();
            SlibAuthlib2Test.check(plain.getWmiTimeout() == new Handler().getWmiTimeout(), "new WmiQueryHandler()");
        });
        OperatingSystem os = new SystemInfo().getOperatingSystem();
        int pid = os.getProcessId();
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            SlibAuthlib2Test.run("oshi LinuxOSProcess", () -> {
                OSProcess p = new LinuxOSProcess(pid);
                SlibAuthlib2Test.check(p.getProcessID() == pid && p.getName().equals(os.getProcess(pid).getName()), "new LinuxOSProcess(pid)");
                OSProcess q = new MyLinuxProcess(pid);
                SlibAuthlib2Test.check(q.getProcessID() == pid && q.getUserID().equals(p.getUserID()), "a LinuxOSProcess subclass");
            });
        }
        SlibAuthlib2Test.run("oshi processor", () -> {
            CentralProcessor cpu = new SystemInfo().getHardware().getProcessor();
            List<CentralProcessor.LogicalProcessor> lp = cpu.getLogicalProcessors();
            SlibAuthlib2Test.check(!lp.isEmpty(), "CentralProcessor.getLogicalProcessors");
            Pair<String, String> pair = new Pair<>("a", "b");
            SlibAuthlib2Test.check("b".equals(pair.getB()), "Pair");
        });
    }
}
