package top.dext.centurybridge.step.v1_21_4.rt;

import oshi.hardware.PhysicalMemory;

/**
 * OSHI 6.4.10's PhysicalMemory(bankLabel, capacity, clockSpeed, manufacturer, memoryType): 6.6.5 adds part and serial
 * numbers, which 6.4.10 did not know ("unknown", OSHI's own word for a value it cannot read), and prints them in toString;
 * a 6.4.10 memory prints what 6.4.10 printed.
 */
public class CbLibPhysicalMemory extends PhysicalMemory {
    public CbLibPhysicalMemory(String bankLabel, long capacity, long clockSpeed, String manufacturer, String memoryType) {
        super(bankLabel, capacity, clockSpeed, manufacturer, memoryType, oshi.util.Constants.UNKNOWN, oshi.util.Constants.UNKNOWN);
    }

    @Override
    public String toString() {
        return "Bank label: " + getBankLabel() + ", Capacity: " + oshi.util.FormatUtil.formatBytes(getCapacity())
            + ", Clock speed: " + oshi.util.FormatUtil.formatHertz(getClockSpeed()) + ", Manufacturer: " + getManufacturer()
            + ", Memory type: " + getMemoryType();
    }
}
