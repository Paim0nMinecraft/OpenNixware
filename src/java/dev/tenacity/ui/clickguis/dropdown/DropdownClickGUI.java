package dev.tenacity.ui.clickguis.dropdown;

import dev.tenacity.NIXWARE;
import dev.tenacity.utils.tuples.Pair;
import dev.tenacity.module.Category;
import dev.tenacity.module.impl.movement.Inventory;
import dev.tenacity.module.impl.render.ClickGUIMod;
import dev.tenacity.ui.searchbar.SearchBar;
import dev.tenacity.ui.sidegui.SideGUI;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.EaseBackIn;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.Theme;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class DropdownClickGUI extends GuiScreen {

    private final Pair<Animation, Animation> openingAnimations = Pair.of(
            new EaseBackIn(400, 1, 2f),
            new EaseBackIn(400, .4f, 2f));


    private List<CategoryPanel> categoryPanels;

    public boolean binding;


    public static boolean gradient;

    @Override
    public void onDrag(int mouseX, int mouseY) {
        for (CategoryPanel catPanels : categoryPanels) {
            catPanels.onDrag(mouseX, mouseY);
        }
        NIXWARE.INSTANCE.getSideGui().onDrag(mouseX, mouseY);
    }

    @Override
    public void initGui() {
        openingAnimations.use((fade, opening) -> {
            fade.setDirection(Direction.FORWARDS);
            opening.setDirection(Direction.FORWARDS);
        });


        if (categoryPanels == null) {
            categoryPanels = new ArrayList<>();
            for (Category category : Category.values()) {
                categoryPanels.add(new CategoryPanel(category, openingAnimations));
            }
        }

        NIXWARE.INSTANCE.getSideGui().initGui();
        NIXWARE.INSTANCE.getSearchBar().initGui();


        for (CategoryPanel catPanels : categoryPanels) {
            catPanels.initGui();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE && !binding) {
            if (NIXWARE.INSTANCE.getSearchBar().isFocused()) {
                NIXWARE.INSTANCE.getSearchBar().getSearchField().setText("");
                NIXWARE.INSTANCE.getSearchBar().getSearchField().setFocused(false);
                return;
            }

            if (NIXWARE.INSTANCE.getSideGui().isFocused()) {
                NIXWARE.INSTANCE.getSideGui().setFocused(false);
                return;
            }

            NIXWARE.INSTANCE.getSearchBar().getOpenAnimation().setDirection(Direction.BACKWARDS);
            openingAnimations.use((fade, opening) -> {
                fade.setDirection(Direction.BACKWARDS);
                opening.setDirection(Direction.BACKWARDS);
            });
        }
        NIXWARE.INSTANCE.getSideGui().keyTyped(typedChar, keyCode);
        NIXWARE.INSTANCE.getSearchBar().keyTyped(typedChar, keyCode);
        categoryPanels.forEach(categoryPanel -> categoryPanel.keyTyped(typedChar, keyCode));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        binding = categoryPanels.stream().anyMatch(CategoryPanel::isTyping) ||
                (NIXWARE.INSTANCE.getSideGui().isFocused() && NIXWARE.INSTANCE.getSideGui().typing) || NIXWARE.INSTANCE.getSearchBar().isTyping();


     //  Gui.drawRect2(0,0, width, height, ColorUtil.applyOpacity(0, nixware.INSTANCE.getSearchBar().getFocusAnimation().getOutput().floatValue() * .25f));
        if (ClickGUIMod.walk.isEnabled() && !binding) {
            Inventory.updateStates();
        }

        // If the closing animation finished then change the gui screen to null
        if (openingAnimations.getSecond().finished(Direction.BACKWARDS)) {
            mc.displayGuiScreen(null);
            return;
        }

        gradient = Theme.getCurrentTheme().isGradient() || ClickGUIMod.gradient.isEnabled();


        boolean focusedConfigGui = NIXWARE.INSTANCE.getSideGui().isFocused() || NIXWARE.INSTANCE.getSearchBar().isTyping();
        int fakeMouseX = focusedConfigGui ? 0 : mouseX, fakeMouseY = focusedConfigGui ? 0 : mouseY;
        ScaledResolution sr = new ScaledResolution(mc);


        RenderUtil.scaleStart(sr.getScaledWidth() / 2f, sr.getScaledHeight() / 2f, openingAnimations.getSecond().getOutput().floatValue() + .6f);

        for (CategoryPanel catPanels : categoryPanels) {
            catPanels.drawScreen(fakeMouseX, fakeMouseY);
        }

        RenderUtil.scaleEnd();
        categoryPanels.forEach(categoryPanel -> categoryPanel.drawToolTips(fakeMouseX, fakeMouseY));

        //Draw Side GUI

        SideGUI sideGUI = NIXWARE.INSTANCE.getSideGui();
        sideGUI.getOpenAnimation().setDirection(openingAnimations.getFirst().getDirection());
        sideGUI.drawScreen(mouseX, mouseY);

        SearchBar searchBar = NIXWARE.INSTANCE.getSearchBar();
        searchBar.setAlpha(openingAnimations.getFirst().getOutput().floatValue() * (1 - sideGUI.getClickAnimation().getOutput().floatValue()));
        searchBar.drawScreen(fakeMouseX, fakeMouseY);
    }

    public void renderEffects() {
        ScaledResolution sr = new ScaledResolution(mc);
        RenderUtil.scaleStart(sr.getScaledWidth() / 2f, sr.getScaledHeight() / 2f, openingAnimations.getSecond().getOutput().floatValue() + .6f);
        for (CategoryPanel catPanels : categoryPanels) {
            catPanels.renderEffects();
        }
        RenderUtil.scaleEnd();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean focused = NIXWARE.INSTANCE.getSideGui().isFocused();
        NIXWARE.INSTANCE.getSideGui().mouseClicked(mouseX, mouseY, mouseButton);
        NIXWARE.INSTANCE.getSearchBar().mouseClicked(mouseX, mouseY, mouseButton);
        if (!focused) {
            categoryPanels.forEach(cat -> cat.mouseClicked(mouseX, mouseY, mouseButton));
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        boolean focused = NIXWARE.INSTANCE.getSideGui().isFocused();
        NIXWARE.INSTANCE.getSideGui().mouseReleased(mouseX, mouseY, state);
        NIXWARE.INSTANCE.getSearchBar().mouseReleased(mouseX, mouseY, state);
        if (!focused) {
            categoryPanels.forEach(cat -> cat.mouseReleased(mouseX, mouseY, state));
        }
    }

    @Override
    public void onGuiClosed() {
        if (ClickGUIMod.rescale.isEnabled()) {
            mc.gameSettings.guiScale = ClickGUIMod.prevGuiScale;
        }
    }


}
