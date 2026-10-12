package top.dext.centurybridge.step.v1_21.rt;

import net.minecraft.class_2960;

/**
 * ResourceLocation: 1.21 made the class final and its constructors private, and builds ids through
 * fromNamespaceAndPath / parse / withDefaultNamespace. Those are 1.20.6's constructors exactly -- parse(s) is
 * new ResourceLocation(s) (split at the first ':', an empty or missing namespace is minecraft), and both
 * validate namespace and path with the same ResourceLocationException messages, namespace first. A 1.20.6
 * subclass keeps working: the layer's access widener makes the class extendable and its (namespace, path)
 * constructor accessible, and the subclass's super(..) is reshaped onto it with the validation the 1.20.6
 * constructor did. Wired from s6world.json.
 */
public final class CbWorldResources {

    /** new ResourceLocation(s) of 1.20.6 */
    public static class_2960 newResourceLocation(String location) {
        return class_2960.method_60654(location);
    }

    /** new ResourceLocation(namespace, path) of 1.20.6 */
    public static class_2960 newResourceLocation(String namespace, String path) {
        return class_2960.method_60655(namespace, path);
    }

    /** super(namespace, path) of a 1.20.6 subclass: the namespace, validated with the path as 1.20.6 did */
    public static String checkedNamespace(String namespace, String path) {
        return class_2960.method_60655(namespace, path).method_12836();
    }

    /** super(namespace, path) of a 1.20.6 subclass: the path, validated with the namespace as 1.20.6 did */
    public static String checkedPath(String namespace, String path) {
        return class_2960.method_60655(namespace, path).method_12832();
    }

    /** super(location) of a 1.20.6 subclass: the namespace 1.20.6's decompose(location, ':') and validation gave */
    public static String parsedNamespace(String location) {
        return class_2960.method_60654(location).method_12836();
    }

    /** super(location) of a 1.20.6 subclass: the path 1.20.6's decompose(location, ':') and validation gave */
    public static String parsedPath(String location) {
        return class_2960.method_60654(location).method_12832();
    }

    /**
     * 1.20.6 ResourceLocation.decompose(location, separator): {namespace, path} without validation -- the
     * namespace minecraft unless the separator stands after the first character.
     */
    public static String[] decompose(String location, char separator) {
        String[] parts = {"minecraft", location};
        int i = location.indexOf(separator);
        if (i >= 0) {
            parts[1] = location.substring(i + 1);
            if (i >= 1) {
                parts[0] = location.substring(0, i);
            }
        }
        return parts;
    }

    /** 1.20.6 ResourceLocation.isValidResourceLocation(location): an empty namespace counts as minecraft */
    public static boolean isValidResourceLocation(String location) {
        String[] parts = decompose(location, ':');
        return class_2960.method_20209(parts[0].isEmpty() ? "minecraft" : parts[0]) && class_2960.method_20208(parts[1]);
    }

    private CbWorldResources() {
    }
}
