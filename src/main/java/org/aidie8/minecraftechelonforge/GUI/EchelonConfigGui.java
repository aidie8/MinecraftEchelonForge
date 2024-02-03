package org.aidie8.minecraftechelonforge.GUI;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.OptionSlider;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.aidie8.minecraftechelonforge.Configs.EchelonConfigs;

import java.util.ArrayList;
import java.util.List;

public class EchelonConfigGui extends Screen {



    private Button leaderBoardSelection;


    private static final int OPTIONS_LIST_TOP_HEIGHT = 24;
    /** Distance from bottom of the screen to the options row list's bottom */
    private static final int OPTIONS_LIST_BOTTOM_OFFSET = 32;
    /** Height of each item in the options row list */
    private static final int OPTIONS_LIST_ITEM_HEIGHT = 25;

    /** Width of a button */
    private static final int BUTTON_WIDTH = 200;
    /** Height of a button */
    private static final int BUTTON_HEIGHT = 20;
    /** Distance from bottom of the screen to the "Done" button's top */
    private static final int DONE_BUTTON_TOP_OFFSET = 26;
    private static final int TITLE_HEIGHT = 8;


    private OptionsRowList optionsRowList;
    private Screen parentScreen;
    public EchelonConfigGui(Screen screen) {
        super(new StringTextComponent("Test"));
        parentScreen = screen;

    }


    @Override
    public void onClose() {

        this.minecraft.setScreen(parentScreen);

    }

    @Override
    public void init(Minecraft pMinecraft, int pWidth, int pHeight) {
        super.init(pMinecraft, pWidth, pHeight);

        this.optionsRowList = new OptionsRowList(
                pMinecraft, this.width, this.height,
                OPTIONS_LIST_TOP_HEIGHT,
                this.height - OPTIONS_LIST_BOTTOM_OFFSET,
                OPTIONS_LIST_ITEM_HEIGHT
        );


        IteratableOption leaderboardDisplay = new IteratableOption("Leaderboard Display",
                (unused,newValue) ->
                {
                    EchelonConfigs.getBasics().setLeaderboardDisplay(EchelonConfigs.EchelonBasics.LeaderBoardTypes.values()
                            [(EchelonConfigs.getBasics().leaderBoardDisplay.get().ordinal()+newValue)
                            % EchelonConfigs.EchelonBasics.LeaderBoardTypes.values().length]);
                }, (unused, option) -> new StringTextComponent("LeaderBoard Format: "
                + ": "
                + EchelonConfigs.getBasics().leaderBoardDisplay.get().displayString
        ));

        leaderboardDisplay.setTooltip(Minecraft.getInstance().font.split(new StringTextComponent("This is used to determine the format of the leaderboard"),200));
        this.optionsRowList.addBig(leaderboardDisplay);

        SliderPercentageOption leaderboardSlider = new SliderPercentageOption("Leaderboard Display",
                3,10,
                1,(unused) -> Double.valueOf(EchelonConfigs.getBasics().leaderboardRange.get()),
            (gamesettings, newVal)->{
                EchelonConfigs.getBasics().leaderboardRange.set(MathHelper.clamp((int)newVal.doubleValue(), 3, 10));
        },(gs,option)->{
            return new StringTextComponent("LeaderBoard Range " +EchelonConfigs.getBasics().leaderboardRange.get());
        });
        this.optionsRowList.addBig(leaderboardSlider);


        // Add the options row list as this screen's child
        // If this is not done, users cannot click on items in the list
        this.children.add(this.optionsRowList);

        // Add the "Done" button
        this.addButton(new Button(
                (this.width - BUTTON_WIDTH) / 2,
                this.height - DONE_BUTTON_TOP_OFFSET,
                BUTTON_WIDTH, BUTTON_HEIGHT,
                // Text shown on the button
                new TranslationTextComponent("gui.done"),
                // Action performed when the button is pressed
                button -> this.onClose()));
        this.children.add(this.optionsRowList);

    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        // First draw the background of the screen
        this.renderBackground(stack);
        // Draw the title
        this.optionsRowList.render(stack,mouseX, mouseY, partialTicks);
        drawCenteredString(stack,this.font, this.title.getString(),
                this.width / 2, TITLE_HEIGHT, 0xFFFFFF);
        // Call the super class' method to complete rendering
        super.render(stack,mouseX, mouseY, partialTicks);
    }
}
