package org.aidie8.minecraftechelonforge.GUI;


import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import com.EchelonSDK.Echelon;
import org.aidie8.minecraftechelonforge.Configs.EchelonConfigs;
import org.aidie8.minecraftechelonforge.Configs.EchelonConfigs.EchelonBasics.LeaderBoardTypes;
import org.aidie8.minecraftechelonforge.Echelon.EchelonUser;
import org.aidie8.minecraftechelonforge.MinecraftEchelonForge;

import java.awt.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber
@OnlyIn(Dist.CLIENT)
public class InGameScoringUI {


    private static int tickCounter = 0;

    private static final int checkSeconds = 2;

    private static String playerScore;

    private static final HashMap<String,String> playerScores = new HashMap<>();
    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Text event)
    {

        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT)
        {


            if (EchelonConfigs.getBasics().leaderBoardDisplay.get() == LeaderBoardTypes.PERSONAL) {
                Minecraft.getInstance().font.draw(new MatrixStack(), "Score: " + playerScore, (event.getWindow().getGuiScaledWidth() - 75), 10, Color.red.getRGB());
            }

            else
            {
                int offset = 0;
                for (String key: playerScores.keySet())
                {
                    Minecraft.getInstance().font.draw(new MatrixStack(), "Pos: "+ key+ "- Score: " + playerScores.get(key), (event.getWindow().getGuiScaledWidth() - 200), 10+offset, Color.red.getRGB());
                    offset += 10;
                }
            }
        }
    }


    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event)
    {
        if (Echelon.initialised){
            if (event.side == LogicalSide.CLIENT){
                if (event.phase == TickEvent.Phase.START)
                {
                    tickCounter++;
                    if (tickCounter % (checkSeconds*20) == 0)
                    {
                        tickCounter = 0;
                        switch (EchelonConfigs.getBasics().leaderBoardDisplay.get()) {
                            case TOP:
                                checkIdleTime();
                                getTopXScoreAndRender();
                                TestScoreBoard();
                                break;
                            case RELATIVE:
                                break;
                            case PERSONAL:
                                getPlayerScoreAndRender();
                                break;
                        }
                    }
                }
            }
        }
    }

    private static void TestScoreBoard() {
        Random random = new Random();
        int bottom = 1;
        int top = 1;
        for (int i = 0; i < EchelonConfigs.getBasics().leaderboardRange.get()/2;i++)
        {

            if (playerScores.get(String.valueOf(i)) != null)
            {
                if (Integer.parseInt(playerScores.get(String.valueOf(i))) > top)
                {
                    top = Integer.parseInt(playerScores.get(String.valueOf(i)));
                }else if (Integer.parseInt(playerScores.get(String.valueOf(i))) < bottom)
                {
                    bottom = Integer.parseInt(playerScores.get(String.valueOf(i)));
                }

            }else{
            playerScores.put(String.valueOf(i),String.valueOf(random.nextInt(top) + bottom));
            }
        }

    }


    public static void getPlayerScoreAndRender()
    {
        EchelonUser user =  MinecraftEchelonForge.getClientProxy().getUser();
        Echelon echelon = user.getEchelon();

        Util.backgroundExecutor().execute(()->{

            echelon.getPlayerStat(user.getClientToken().uid,"points",playerStat ->
            {
                if (playerStat.success){
                    long dif = playerStat.date - Instant.now().toEpochMilli();
                    if (Math.abs(dif) < 600){ // if there is no score for more than 10 min, reset the score
                        playerScore = playerStat.value;
                    }else
                    {
                        echelon.ResetLocalPlayerStats(user.getClientToken().uid,"points",playerScoreReset ->{
                            System.out.println(playerScoreReset.success);
                            if (playerScoreReset.success)
                            {
                                playerScore = String.valueOf(0);
                            }
                        });
                    }
                }
            });

        });
    }

    public static void getTopXScoreAndRender()
    {
        EchelonUser user =  MinecraftEchelonForge.getClientProxy().getUser();
        Echelon echelon = user.getEchelon();

        Util.backgroundExecutor().execute(()->{

            echelon.getStatLeaderboard("points",EchelonConfigs.getBasics().leaderboardRange.get()/2,playerStat ->
            {
                if (playerStat.success) {

                    playerScores.clear();
                    for (int i = 0; i < playerStat.players.length; i++)
                    {
                        playerScores.put(String.valueOf(playerStat.players[i].pos),playerStat.players[i].value);
                    }
                }
            });

        });
    }



    public static void checkIdleTime()
    {

        EchelonUser user =  MinecraftEchelonForge.getClientProxy().getUser();
        Echelon echelon = user.getEchelon();
        echelon.getPlayerStat(user.getClientToken().uid,"points",playerStat ->
        {
            if (playerStat.success){
                long dif = playerStat.date - Instant.now().toEpochMilli();
                if (!(Math.abs(dif) < 600)){ // if there is no score for more than 10 min, reset the score
                    echelon.ResetLocalPlayerStats(user.getClientToken().uid,"points",playerScoreReset ->{
                        System.out.println(playerScoreReset.success);
                        if (playerScoreReset.success)
                        {
                            playerScore = String.valueOf(0);
                        }
                    });
                }
            }
        });


    }
}
