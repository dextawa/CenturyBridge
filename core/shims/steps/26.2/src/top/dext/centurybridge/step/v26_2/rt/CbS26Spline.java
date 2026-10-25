package top.dext.centurybridge.step.v26_2.rt;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.util.BoundedFloatFunction;
import net.minecraft.util.CubicSpline;
import top.dext.centurybridge.step.v26_2.facade.CubicSplineVisitor;

/**
 * 26.1.2's CubicSpline API on 26.2: the spline is no longer a BoundedFloatFunction (26.2 samples it with the static
 * CubicSpline.sample), mapAll(CoordinateVisitor) became mapCoordinates(UnaryOperator), and the value transformers
 * that were BoundedFloatFunction&lt;Float&gt; are Float2FloatFunction.
 */
public final class CbS26Spline {
    private CbS26Spline() {}

    /** a 26.1.2 BoundedFloatFunction&lt;Float&gt; value transformer as 26.2's Float2FloatFunction (26.1.2 only called apply on it) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Float2FloatFunction toFloat2Float(BoundedFloatFunction transformer) {
        BoundedFloatFunction<Float> t = transformer;
        return f -> t.apply(f);
    }

    // ---- spline.apply(context) (26.1.2: BoundedFloatFunction.apply) ----

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static float apply(CubicSpline spline, Object context) {
        return CubicSpline.sample(spline, context);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static float apply(CubicSpline.Constant spline, Object context) {
        return CubicSpline.sample((CubicSpline) spline, context);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static float apply(CubicSpline.Multipoint spline, Object context) {
        return CubicSpline.sample((CubicSpline) spline, context);
    }

    // ---- spline.mapAll(visitor) ----

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static CubicSpline mapAll(CubicSpline spline, CubicSplineVisitor visitor) {
        return spline.mapCoordinates(visitor::visit);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static CubicSpline mapAll(CubicSpline.Constant spline, CubicSplineVisitor visitor) {
        return spline.mapCoordinates(visitor::visit);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static CubicSpline mapAll(CubicSpline.Multipoint spline, CubicSplineVisitor visitor) {
        return spline.mapCoordinates(visitor::visit);
    }

    // ---- statics whose value transformer changed type ----

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static CubicSpline.Builder builder(BoundedFloatFunction coordinate, BoundedFloatFunction valueTransformer) {
        return CubicSpline.builder(coordinate, toFloat2Float(valueTransformer));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static CubicSpline buildErosionOffsetSpline(BoundedFloatFunction erosion, BoundedFloatFunction ridges,
            float f1, float f2, float f3, float f4, float f5, float f6, boolean b1, boolean b2,
            BoundedFloatFunction valueTransformer) {
        return TerrainProvider.buildErosionOffsetSpline(erosion, ridges, f1, f2, f3, f4, f5, f6, b1, b2,
            toFloat2Float(valueTransformer));
    }
}
