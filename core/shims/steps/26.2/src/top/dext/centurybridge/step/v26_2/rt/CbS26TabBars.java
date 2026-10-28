package top.dext.centurybridge.step.v26_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.tabs.MenuTabBar;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;

/**
 * 26.1.2's menu tab bar on 26.2 (javap of both). 26.1.2's TabNavigationBar was the screens' menu bar: builder(manager,
 * width), addTabs(tabs...) making a 24-high TabButton per tab, updateWidth(width) = width then arrangeElements(), whose
 * body sized the buttons (min(400, width) - 28 split over the tabs, rounded to 2) and centred the row at y 0. 26.2 made
 * TabNavigationBar a general positioned widget (builder(manager, x, y, w, h), addTab(button, tab)) and moved the menu
 * bar, with exactly those bodies, to its subclass MenuTabBar: builder(manager, width) (x 0, y 0, height 24), addTabs
 * making a MenuTabButton per tab, arrangeElements(width) = 26.1.2's updateWidth. 26.2's CreateWorldScreen and
 * StatsScreen, 26.1.2's two users, build MenuTabBars.
 */
@Environment(EnvType.CLIENT)
public final class CbS26TabBars {
    private CbS26TabBars() {
    }

    /** 26.1.2 TabNavigationBar.builder(manager, width): 26.2's menu bar builder */
    public static TabNavigationBar.Builder builder(TabManager manager, int width) {
        return MenuTabBar.builder(manager, width);
    }

    /**
     * instance redirect: 26.1.2 TabNavigationBar.Builder.addTabs(tabs...). Every builder 26.1.2 code can hold comes from
     * builder(manager, width) (26.1.2's Builder constructor was private), a MenuTabBar.Builder here; a 26.2 builder of a
     * plain bar handed to it gets a MenuTabButton per tab, at the builder's height, as MenuTabBar.Builder.addTab makes.
     */
    public static TabNavigationBar.Builder addTabs(TabNavigationBar.Builder builder, Tab... tabs) {
        if (builder instanceof MenuTabBar.Builder menu) {
            return menu.addTabs(tabs);
        }
        for (Tab tab : tabs) {
            builder.addTab(new MenuTabBar.MenuTabButton(builder.tabManager, tab, 0, builder.height), tab);
        }
        return builder;
    }

    /** instance redirect: 26.1.2 TabNavigationBar.updateWidth(width), 26.2's arrangeElements(width) */
    public static void updateWidth(TabNavigationBar bar, int width) {
        bar.arrangeElements(width);
    }

    /** instance redirect: 26.1.2 TabNavigationBar.arrangeElements(), at the bar's own width as 26.1.2 read it */
    public static void arrangeElements(TabNavigationBar bar) {
        bar.arrangeElements(bar.getWidth());
    }
}
