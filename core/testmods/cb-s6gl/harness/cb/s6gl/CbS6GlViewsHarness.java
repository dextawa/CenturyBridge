package cb.s6gl;

import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import top.dext.centurybridge.step.v1_21_6.rt.CbS6GlViews;

/** No-GL behavior checks for the 1.21.5 sampler map view; no RenderSystem device is needed. */
public final class CbS6GlViewsHarness {
    private static int checks;

    private static void check(boolean condition, String label) {
        checks++;
        if (!condition) {
            throw new AssertionError(label);
        }
    }

    private static final class Texture extends GpuTexture {
        private boolean closed;

        Texture(String label) {
            super(0, label, TextureFormat.RGBA8, 16, 16, 1, 4);
        }

        @Override
        public void close() {
            closed = true;
        }

        @Override
        public boolean isClosed() {
            return closed;
        }
    }

    private static final class View extends GpuTextureView {
        private boolean closed;

        View(GpuTexture texture, int baseMip, int mips) {
            super(texture, baseMip, mips);
        }

        @Override
        public void close() {
            closed = true;
        }

        @Override
        public boolean isClosed() {
            return closed;
        }
    }

    public static void main(String[] args) {
        Texture texture = new Texture("legacy");
        Texture other = new Texture("other");
        View subview = new View(texture, 1, 2);
        HashMap<String, GpuTextureView> nativeMap = new HashMap<>();
        nativeMap.put("Sampler0", subview);
        HashMap<String, GpuTexture> legacy = CbS6GlViews.textureMap(nativeMap);

        check(legacy.size() == 1 && !legacy.isEmpty(), "native size");
        check(legacy.get("Sampler0") == texture, "view unwraps its texture");
        check(legacy.get("missing") == null, "absent read");
        check(legacy.getOrDefault("missing", other) == other, "absent default");
        check(legacy.containsKey("Sampler0"), "contains key");
        check(legacy.containsValue(texture), "contains legacy value");
        check(!legacy.containsValue(other), "rejects other value");
        check(legacy.put("Sampler0", texture) == texture, "same-value put answer");
        check(nativeMap.get("Sampler0") == subview, "same-value put preserves a native subview");
        check(legacy.putIfAbsent("Sampler0", other) == texture, "putIfAbsent answer");
        check(nativeMap.get("Sampler0") == subview, "putIfAbsent does not replace");
        check(legacy.replace("Sampler0", other, texture) == false, "replace expected mismatch");
        check(legacy.replace("Sampler0", texture, texture), "replace expected match");
        check(nativeMap.get("Sampler0") == subview, "replace keeps native identity");
        check(legacy.replace("Sampler0", texture) == texture, "replace present answer");
        check(nativeMap.get("Sampler0") == subview, "replace present keeps native identity");
        check(legacy.merge("Sampler0", texture, (oldValue, ignored) -> oldValue) == texture, "merge answer");
        check(nativeMap.get("Sampler0") == subview, "merge keeps native identity");
        check(legacy.computeIfAbsent("Sampler0", key -> other) == texture, "computeIfAbsent present");
        check(legacy.computeIfPresent("Sampler0", (key, oldValue) -> oldValue) == texture, "computeIfPresent");
        check(legacy.compute("Sampler0", (key, oldValue) -> oldValue) == texture, "compute");
        legacy.replaceAll((key, oldValue) -> oldValue);
        check(nativeMap.get("Sampler0") == subview, "replaceAll keeps native identity");

        AtomicInteger visits = new AtomicInteger();
        legacy.forEach((key, value) -> {
            check(key.equals("Sampler0") && value == texture, "forEach converts values");
            visits.incrementAndGet();
        });
        check(visits.get() == 1, "forEach visits native map");

        Map.Entry<String, GpuTexture> entry = legacy.entrySet().iterator().next();
        check(entry.getValue() == texture, "entry unwraps value");
        check(entry.setValue(texture) == texture, "entry setValue answer");
        check(nativeMap.get("Sampler0") == subview, "entry setValue keeps native identity");
        check(legacy.entrySet().contains(new SimpleEntry<>("Sampler0", texture)), "entrySet contains legacy entry");
        check(!legacy.entrySet().contains(new SimpleEntry<>("Sampler0", other)), "entrySet rejects other entry");
        check(legacy.values().contains(texture), "values contains texture");
        check(legacy.values().iterator().next() == texture, "values iterator unwraps");

        View replacement = new View(texture, 0, 1);
        nativeMap.put("Sampler0", replacement);
        check(legacy.get("Sampler0") == texture, "native replacement is visible");
        legacy.put("Sampler0", texture);
        check(nativeMap.get("Sampler0") == replacement, "round trip keeps the replacement view");

        @SuppressWarnings("unchecked")
        HashMap<String, GpuTexture> clone = (HashMap<String, GpuTexture>) legacy.clone();
        check(clone != legacy && clone.equals(legacy), "clone has legacy values");
        check(clone.remove("Sampler0") == texture, "clone removal");
        check(nativeMap.get("Sampler0") == replacement, "clone is independent of native map");

        check(!legacy.remove("Sampler0", other), "two-arg remove mismatch");
        check(nativeMap.containsKey("Sampler0"), "failed remove preserves native map");
        check(legacy.remove("Sampler0", other) == false, "two-arg remove still mismatched");
        check(legacy.remove("Sampler0", texture), "two-arg remove match");
        check(!nativeMap.containsKey("Sampler0"), "two-arg remove writes through");

        nativeMap.put("Sampler0", subview);
        nativeMap.put("Sampler1", replacement);
        Iterator<Map.Entry<String, GpuTexture>> entries = legacy.entrySet().iterator();
        String removed = entries.next().getKey();
        entries.remove();
        check(!nativeMap.containsKey(removed), "entry iterator removal writes through");
        check(legacy.size() == 1, "entry iterator size follows native map");
        Iterator<GpuTexture> values = legacy.values().iterator();
        check(values.next() == texture, "remaining value");
        values.remove();
        check(nativeMap.isEmpty(), "values iterator removal writes through");

        nativeMap.put("Sampler0", subview);
        check(legacy.entrySet().remove(new SimpleEntry<>("Sampler0", texture)), "entrySet remove");
        check(nativeMap.isEmpty(), "entrySet remove writes through");
        legacy.put("null", null);
        check(legacy.containsKey("null") && legacy.get("null") == null, "null remains a map value");
        check(nativeMap.containsKey("null") && nativeMap.get("null") == null, "null writes through natively");
        check(legacy.remove("null", null), "null two-arg remove");
        check(legacy.computeIfAbsent("absent", key -> null) == null, "computeIfAbsent null");
        check(!legacy.containsKey("absent"), "computeIfAbsent null does not insert");

        nativeMap.put("Sampler0", subview);
        legacy.clear();
        check(nativeMap.isEmpty() && legacy.isEmpty(), "clear writes through");
        System.out.println("CB_S6GL_HARNESS OK " + checks + " checks");
    }
}
