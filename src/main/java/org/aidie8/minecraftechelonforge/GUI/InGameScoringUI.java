package org.aidie8.minecraftechelonforge.GUI;

import com.EchelonSDK.Echelon;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.aidie8.minecraftechelonforge.Echelon.EchelonUser;
import org.aidie8.minecraftechelonforge.MinecraftEchelonForge;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber
@OnlyIn(Dist.CLIENT)
public class InGameScoringUI {


    private static int tickCounter = 0;

    private static final int checkSeconds = 4;

    private static String playerScore;
    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Text event)
    {

        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT)
        {
            Minecraft.getInstance().font.draw(event.getMatrixStack(), "Score: " + playerScore , 5, 5, Color.red.getRGB());
        }
    }


    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event)
    {
        if (event.side == LogicalSide.CLIENT){
            if (event.phase == TickEvent.Phase.START)
            {
                tickCounter++;
                if (tickCounter % (checkSeconds*20) == 0)
                {
                    tickCounter = 0;
                    EchelonUser user =  MinecraftEchelonForge.getClientProxy().getUser();
                    Echelon echelon = user.getEchelon();




                    Util.backgroundExecutor().execute(()->{

                        echelon.getPlayerStat(user.getClientToken().uid,"points",playerStat ->
                        {
                            if (playerStat.success){
                                playerScore = playerStat.value;
                            }
                        });

                    });


                }
            }
        }
    }
}
