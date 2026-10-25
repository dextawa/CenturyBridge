package top.dext.centurybridge.step.v26_2.rt;

import java.util.Locale;

/** 26.1.2 CrashReportCategory members 26.2 dropped (javap of both). */
public final class CbS26CrashReport {
    private CbS26CrashReport() {}

    /**
     * 26.1.2 CrashReportCategory.formatLocation(x, y, z): String.format(Locale.ROOT, "%.2f,%.2f,%.2f", x, y, z). 26.2
     * keeps only the forms that take a LevelHeightAccessor, which append the block's section and region.
     */
    public static String formatLocation(double x, double y, double z) {
        return String.format(Locale.ROOT, "%.2f,%.2f,%.2f", x, y, z);
    }
}
