package org.aidie8.minecraftechelonforge.GUI;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.aidie8.minecraftechelonforge.MinecraftEchelonForge;
import org.aidie8.minecraftechelonforge.Reference;

@Mod.EventBusSubscriber
public class TwitchLoginMainMenu {


    private static final ResourceLocation TWITCHLOGO = new ResourceLocation("minecraftechelonforge","textures/gui/twitchlogo.png");

    @SubscribeEvent
    public static  void onMainMenuOpen(GuiScreenEvent.InitGuiEvent.Post event)
    {
        if (event.getGui() instanceof MainMenuScreen)
        {

            int j = event.getGui().height / 4 + 48;
            Button newButton = new ImageButton(event.getGui().width / 2 + 126, j + 72 + 12, 20, 20, 0, 0, 2, TWITCHLOGO, 64, 64, button -> {
                System.out.println("Button pressed");
                System.out.println("Button pressed");

                MinecraftEchelonForge.getClientProxy().getUser().OpenTwitchLogin();
            })
            {
                @Override
                public void renderButton(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks)
                {
                    Minecraft minecraft = Minecraft.getInstance();
                    minecraft.getTextureManager().bind(TWITCHLOGO);
                    int i = 50;
                    if (this.isHovered()) {
                        i += 8;
                    }

                    RenderSystem.enableDepthTest();
                    blit(pMatrixStack, this.x, this.y, this.width, this.height,50, i,128,128 ,225,225);
                    if (this.isHovered()) {
                        this.renderToolTip(pMatrixStack, pMouseX, pMouseY);
                    }

                }
            };
            event.addWidget(newButton);
        }
    }
}
