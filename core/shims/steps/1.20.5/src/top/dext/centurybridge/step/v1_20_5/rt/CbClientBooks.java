package top.dext.centurybridge.step.v1_20_5.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_20_5.facade.BookAccess;

/**
 * 1.20.4's book screen API on 1.20.5 (s4client): BookViewScreen took a BookAccess interface mods implement
 * (facade/BookAccess); 1.20.5's screen takes a record over a page list. A 1.20.4 access reaches the screen as
 * such a record over a live view of its pages -- the count and each page asked of the access when the screen
 * asks, as 1.20.4's screen asked it.
 */
@Environment(EnvType.CLIENT)
public final class CbClientBooks {

    private CbClientBooks() {
    }

    /** 1.20.4 BookViewScreen.EMPTY_ACCESS: no pages */
    public static final BookAccess EMPTY_ACCESS = new BookAccess() {
        @Override
        public int method_17560() {
            return 0;
        }

        @Override
        public net.minecraft.class_5348 method_17561(int index) {
            return net.minecraft.class_5348.field_25310;
        }
    };

    /** 1.20.4 BookViewScreen.EMPTY_ACCESS */
    public static BookAccess field_17417() {
        return EMPTY_ACCESS;
    }

    /** 1.20.5's record over a live view of a 1.20.4 access's pages */
    public static net.minecraft.class_3872.class_3931 record(BookAccess access) {
        return new net.minecraft.class_3872.class_3931(new java.util.AbstractList<net.minecraft.class_2561>() {
            @Override
            public int size() {
                return access.method_17560();
            }

            @Override
            public net.minecraft.class_2561 get(int index) {
                return component(access.method_17563(index));
            }
        });
    }

    /** a page as the component 1.20.5's record holds: the component itself, else its styled pieces in order */
    private static net.minecraft.class_2561 component(net.minecraft.class_5348 page) {
        if (page instanceof net.minecraft.class_2561 c) {
            return c;
        }
        net.minecraft.class_5250 out = net.minecraft.class_2561.method_43473();
        page.method_27658((style, text) -> {
            out.method_10852(net.minecraft.class_2561.method_43470(text).method_10862(style));
            return java.util.Optional.empty();
        }, net.minecraft.class_2583.field_24360);
        return out;
    }

    /**
     * after a 1.20.4 BookViewScreen(access), built as 1.20.5's BookViewScreen(): the access and the page-turn
     * sound, as 1.20.4's constructor (this(access, true)) set them -- before init, so nothing else to update
     */
    public static void cbBuilt(net.minecraft.class_3872 screen, BookAccess access) {
        top.dext.centurybridge.step.v1_20_5.mixin.CbClientBookViewScreenAccessor a =
            (top.dext.centurybridge.step.v1_20_5.mixin.CbClientBookViewScreenAccessor) screen;
        a.cb$setBookAccess(record(access));
        a.cb$setPlayTurnSound(true);
    }

    /** 1.20.4 BookViewScreen.setBookAccess(access) */
    public static void method_17554(net.minecraft.class_3872 screen, BookAccess access) {
        screen.method_17554(record(access));
    }

    /** 1.20.4 BookViewScreen.loadPages(tag): the pages loadPages(tag, consumer) hands over, in order */
    public static java.util.List<String> method_17555(net.minecraft.class_2487 tag) {
        com.google.common.collect.ImmutableList.Builder<String> pages = com.google.common.collect.ImmutableList.builder();
        method_33888(tag, pages::add);
        return pages.build();
    }

    /**
     * 1.20.4 BookViewScreen.loadPages(tag, consumer): each string of the tag's "pages" list, replaced by its
     * "filtered_pages" entry (keyed by the page index) when the client filters text and the tag has them
     */
    public static void method_33888(net.minecraft.class_2487 tag, java.util.function.Consumer<String> consumer) {
        net.minecraft.class_2499 pages = tag.method_10554("pages", 8).method_10612();
        java.util.function.IntFunction<String> page;
        if (net.minecraft.class_310.method_1551().method_33883() && tag.method_10573("filtered_pages", 10)) {
            net.minecraft.class_2487 filtered = tag.method_10562("filtered_pages");
            page = i -> {
                String key = String.valueOf(i);
                return filtered.method_10545(key) ? filtered.method_10558(key) : pages.method_10608(i);
            };
        } else {
            page = pages::method_10608;
        }
        for (int i = 0; i < pages.size(); i++) {
            consumer.accept(page.apply(i));
        }
    }
}
