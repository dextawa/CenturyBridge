package top.dext.centurybridge.facade.v26_2;

/**
 * Facade for 1.20.1 net/minecraft/class_4573 (a simple runnable-style render
 * callback, removed in the render-pipeline rework). Single execute() method;
 * legacy classes implement it to be invoked by code paths that no longer exist
 * on 26.2 -- keeping the interface loadable prevents the class-load failure.
 */
public interface class_4573 {
    void execute();
}
