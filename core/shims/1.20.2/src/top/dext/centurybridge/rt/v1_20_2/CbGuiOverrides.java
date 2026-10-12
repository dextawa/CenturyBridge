package top.dext.centurybridge.rt.v1_20_2;

/**
 * Whether a class overrides one of the 1.20.1 hooks 1.20.2 stopped calling: a 1.20.1 override is only
 * reached when vanilla goes back through the 1.20.1 path, so a bridge takes that path for exactly the
 * classes that declare the override below the vanilla class (cached per class; runtime names are the
 * intermediary names the converted mod declares).
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbGuiOverrides {

    /** a per-hook cache: does the class declare name(params) anywhere below declarer? */
    public static final class Hook extends ClassValue<Boolean> {
        private final Class<?> declarer;
        private final String name;
        private final Class<?>[] params;

        public Hook(Class<?> declarer, String name, Class<?>... params) {
            this.declarer = declarer;
            this.name = name;
            this.params = params;
        }

        @Override
        protected Boolean computeValue(Class<?> type) {
            for (Class<?> c = type; c != null && c != declarer; c = c.getSuperclass()) {
                try {
                    c.getDeclaredMethod(name, params);
                    return Boolean.TRUE;
                } catch (NoSuchMethodException e) {
                    // not declared here: look further up
                }
            }
            return Boolean.FALSE;
        }

        public boolean in(Object o) {
            return get(o.getClass());
        }
    }

    private CbGuiOverrides() {
    }
}
