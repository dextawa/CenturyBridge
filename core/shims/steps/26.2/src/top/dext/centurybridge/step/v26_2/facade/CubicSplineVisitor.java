package top.dext.centurybridge.step.v26_2.facade;

/**
 * 26.1.2's CubicSpline.CoordinateVisitor (gone on 26.2, whose CubicSpline.mapCoordinates takes a UnaryOperator):
 * reached by classRenames; rt/CbS26Spline hands it to mapCoordinates as {@code visitor::visit}.
 */
@FunctionalInterface
public interface CubicSplineVisitor<I> {
    I visit(I coordinate);
}
