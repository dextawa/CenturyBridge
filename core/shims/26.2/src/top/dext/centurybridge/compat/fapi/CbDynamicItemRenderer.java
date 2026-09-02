package top.dext.centurybridge.compat.fapi;

/**
 * was BuiltinItemRendererRegistry.DynamicItemRenderer. Its SAM took a
 * MultiBufferSource, a type 26.2 no longer has, so it cannot be declared here:
 * the interface is a loadable marker (lambdas and the three corpus implementors
 * still construct) and CbBuiltinItemRendererRegistry never invokes it.
 */
public interface CbDynamicItemRenderer {
}
