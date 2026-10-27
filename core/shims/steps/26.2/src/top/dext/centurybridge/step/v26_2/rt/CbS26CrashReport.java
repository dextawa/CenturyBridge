package top.dext.centurybridge.step.v26_2.rt;

import java.util.Locale;
import java.util.function.Supplier;
import net.minecraft.CrashReportDetail;

/** 26.1.2 CrashReportCategory / SystemReport members 26.2 dropped or reshaped (javap of both). */
public final class CbS26CrashReport {
    private CbS26CrashReport() {}

    /**
     * 26.1.2 CrashReportCategory.formatLocation(x, y, z): String.format(Locale.ROOT, "%.2f,%.2f,%.2f", x, y, z). 26.2
     * keeps only the forms that take a LevelHeightAccessor, which append the block's section and region.
     */
    public static String formatLocation(double x, double y, double z) {
        return String.format(Locale.ROOT, "%.2f,%.2f,%.2f", x, y, z);
    }

    /**
     * 26.1.2 SystemReport.setDetail(key, Supplier) -> 26.2 setDetail(key, CrashReportDetail): the same value, asked of
     * the supplier when 26.2 asks the detail.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static CrashReportDetail<Object> toDetail(Supplier<String> supplier) {
        return (CrashReportDetail) supplier::get;
    }

    /**
     * The exit code 26.2's Minecraft.saveReport(File, CrashReport, int) / crash(Minecraft, File, CrashReport, int) take
     * for a 26.1.2 call: 26.1.2's saveReport answered -1 once the report was printed or saved (-2 when it could not be
     * saved, which 26.2 still answers itself), and crash exited with that answer; 26.2 answers the given code there.
     * 26.2's own two-argument saveReport and emergencySaveAndCrash pass -1 too (javap of both).
     */
    public static int legacyExitCode() {
        return -1;
    }
}
