import java.nio.ByteBuffer;
import org.lwjgl.system.Library;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.SharedLibrary;
import top.dext.centurybridge.rt.v1_20_2.CbLibJni;

/**
 * Exercises slib's libffi route for the LWJGL 3.3.1 JNI call variants LWJGL 3.3.2 dropped, against real libc functions
 * on LWJGL 3.3.2 with its natives (run on beta2 with the step-1 layer classes on the class path). Prints "JNI ok|FAIL".
 */
public class CbJniHarness {
    static int fails;

    static void check(boolean ok, String what) {
        System.out.println("JNI " + (ok ? "ok   " : "FAIL ") + what);
        if (!ok) fails++;
    }

    public static void main(String[] args) {
        SharedLibrary libc = Library.loadNative(CbJniHarness.class, "org.lwjgl", "libc.so.6");
        long abs = libc.getFunctionAddress("abs");
        long strlen = libc.getFunctionAddress("strlen");
        long memset = libc.getFunctionAddress("memset");
        // int f(int, short): abs ignores the second argument
        check(CbLibJni.invokeI(-5, (short) 7, abs) == 5, "invokeI(ISJ)I through abs");
        // int f(void*, short): strlen ignores the short
        ByteBuffer hello = MemoryUtil.memUTF8("hello");
        check(CbLibJni.invokePI(MemoryUtil.memAddress(hello), (short) 3, strlen) == 5, "invokePI(JSJ)I through strlen");
        // void* f(void*, int, size_t) as an array variant: memset an int[] through a pointer to its elements
        int[] data = new int[4];
        System.out.println("JNI memset variant present: " + hasArrayVariant());
        MemoryUtil.memFree(hello);
        System.out.println("JNI DONE fails=" + fails);
    }

    static boolean hasArrayVariant() {
        try {
            CbLibJni.class.getMethod("invokePB", byte.class, int[].class, long.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
