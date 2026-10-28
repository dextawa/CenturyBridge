package cb.s215;

/**
 * The shape sample's reference side: run on plain 1.21.4 (no loader, no CenturyBridge; the 1.21.4 intermediary code jar
 * and its libraries on the class path), after SharedConstants.tryDetectVersion (method_36208) and Bootstrap.bootStrap, it prints
 * ShapeSample's lines as 1.21.4 itself builds them.
 */
public final class ShapeDump {
    private ShapeDump() {
    }

    public static void main(String[] args) {
        net.minecraft.class_155.method_36208();
        net.minecraft.class_2966.method_12851();
        for (String line : ShapeSample.lines()) {
            System.out.println("CB-S215 shape " + line);
        }
    }
}
