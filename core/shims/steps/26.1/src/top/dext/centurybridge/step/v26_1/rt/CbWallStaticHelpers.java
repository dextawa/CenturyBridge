package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.math.MatrixUtil;
import com.mojang.math.Transformation;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import org.joml.Matrix4fc;

/**
 * 1.21.11 static helpers 26.1 removed, each with 1.21.11's body (javap) on what 26.1 keeps:
 * FileUtil.isPathNormalized / createPathToResource (pure path arithmetic), NbtUtils.prettyPrint(tag) (the
 * two-argument prettyPrint without the list breaking), Transformation.identity() (IDENTITY),
 * MatrixUtil.isOrthonormal(matrix) (checkProperty(matrix, PROPERTY_ORTHONORMAL = 16)).
 */
public final class CbWallStaticHelpers {
    private CbWallStaticHelpers() {
    }

    /** FileUtil.isPathNormalized(path) */
    public static boolean isPathNormalized(Path path) {
        return path.normalize().equals(path);
    }

    /** FileUtil.createPathToResource(root, name, extension) */
    public static Path createPathToResource(Path root, String name, String extension) {
        String file = name + extension;
        Path path = Paths.get(file);
        if (path.endsWith(extension)) {
            throw new InvalidPathException(file, "empty resource name");
        }
        return root.resolve(path);
    }

    /** NbtUtils.prettyPrint(tag) */
    public static String prettyPrint(Tag tag) {
        return NbtUtils.prettyPrint(tag, false);
    }

    /** Transformation.identity() */
    public static Transformation identity() {
        return Transformation.IDENTITY;
    }

    /** MatrixUtil.isOrthonormal(matrix) */
    public static boolean isOrthonormal(Matrix4fc matrix) {
        return MatrixUtil.checkProperty(matrix, 16);
    }
}
