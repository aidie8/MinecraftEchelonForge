package org.aidie8.minecraftechelonforge.GUI;


import com.EchelonSDK.EchelonTwitchController;
import com.EchelonSDK.Responses.Responses;
import com.EchelonSDK.Responses.TwitchResponses;
import com.google.gson.reflect.TypeToken;
import com.mojang.blaze3d.matrix.MatrixStack;
import jdk.nashorn.internal.parser.TokenType;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.FMLWorldPersistenceHook;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import com.EchelonSDK.Echelon;
import org.aidie8.minecraftechelonforge.Configs.EchelonConfigs;
import org.aidie8.minecraftechelonforge.Configs.EchelonConfigs.EchelonBasics.LeaderBoardTypes;
import org.aidie8.minecraftechelonforge.Core.LeaderboardEntry;
import org.aidie8.minecraftechelonforge.Echelon.EchelonUser;
import org.aidie8.minecraftechelonforge.MinecraftEchelonForge;
import org.aidie8.minecraftechelonforge.Proxy.ClientProxy;
import org.aidie8.minecraftechelonforge.Reference;

import java.awt.*;
import java.lang.reflect.Proxy;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
@OnlyIn(Dist.CLIENT)
public class InGameScoringUI {

    private static int entryIndex = -1;
    private static ArrayList<LeaderboardEntry> getNameLeaderboardCache = new ArrayList<>();
    private static int tickCounter = 0;

    private static final int checkSeconds = 2;

    private static String playerScore;

    private static ArrayList<LeaderboardEntry> leaderboards = new ArrayList<>();

    private static final float resetTimer = 6000000; // 10 minutes
    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Text event)
    {


        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT)
        {


            if (EchelonConfigs.getBasics().leaderBoardDisplay.get() == LeaderBoardTypes.PERSONAL) {
                Minecraft.getInstance().font.draw(new MatrixStack(), "Score: " + playerScore, (event.getWindow().getGuiScaledWidth() - 75), 40, Color.red.getRGB());
            }

            else
            {
                int offset = 0;
                int amount =  EchelonConfigs.getBasics().leaderboardRange.get();
                for (int i = 0; i < amount;i++)
                {
                    if (leaderboards.size() > i){
                        Minecraft.getInstance().font.draw(new MatrixStack(), "Pos "+ leaderboards.get(i).getPosition()+ ": "+ leaderboards.get(i).getName()+ " - Score: " + leaderboards.get(i).getScore(), (event.getWindow().getGuiScaledWidth() - 180), 10+offset, Color.red.getRGB());
                    }
                    else
                    {
                        Minecraft.getInstance().font.draw(new MatrixStack(), "Pos "+ (i+1)+ ": _________", (event.getWindow().getGuiScaledWidth() - 180), 40+offset, Color.red.getRGB());
                    }
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
                                //TestScoreBoard();
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
                    if (dif < resetTimer){ // if there is no score for more than reset timer, reset the score
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
            echelon.getStatLeaderboard("points",EchelonConfigs.getBasics().leaderboardRange.get(), playerStat ->
            {
                System.out.println("LeaderBoard Returned");
                if (playerStat.success) {
                    ArrayList<LeaderboardEntry> newLeaderboard = new ArrayList<>();
                    for (int i = 0; i < playerStat.players.length; i++)
                    {
                        String uid = MinecraftEchelonForge.getClientProxy().getUser().getClientToken().id;
                        int score = (int) Float.parseFloat(playerStat.players[i].value);
                        int pos = playerStat.players[i].pos;
                        if (uid.equals(MinecraftEchelonForge.getClientProxy().getUser().getClientToken().uid))
                        {
                            newLeaderboard.add(new LeaderboardEntry(uid,score,pos,"Local Score"));
                        }else newLeaderboard.add(new LeaderboardEntry(uid,score,pos,"Test Name "+ i));
                    }
                    entryIndex = -1;
                    Comparator<LeaderboardEntry> comparator = Comparator
                            .comparingInt(LeaderboardEntry::getScore);
                    getNameLeaderboardCache = (ArrayList<LeaderboardEntry>) newLeaderboard.stream().sorted(comparator).collect(Collectors.toList());
                    recursiveGetEntryName();
                    leaderboards = getNameLeaderboardCache;
                }
            });
        });
    }


    private static void recursiveGetEntryName()
    {
        entryIndex++;
        if(entryIndex >=getNameLeaderboardCache.size())
        {
            return;
        }
        HashMap<String, Object> formData = new HashMap<>();
        formData.put("id",getNameLeaderboardCache.get(entryIndex).getUid());
        MinecraftEchelonForge.LOGGER().info("Leaderboard ID" + getNameLeaderboardCache.get(entryIndex));
        EchelonTwitchController.PassThrough(EchelonTwitchController.APIEndPoint.USERS,formData,(TwitchResponses.Users users)->
        {
            if (!users.success)
            {
                MinecraftEchelonForge.LOGGER().warn("WaitforLeaderboardEntries: couldn't get entry " + getNameLeaderboardCache.get(entryIndex).getUid());
                getNameLeaderboardCache.get(entryIndex).setName("Error");
            }else
            {
                getNameLeaderboardCache.get(entryIndex).setName(users.displayName);
                MinecraftEchelonForge.LOGGER().info(users.displayName);
            }
            recursiveGetEntryName();
        },new TypeToken<TwitchResponses.Users>() {
        }.getType());

    }



    public static void checkIdleTime()
    {


        Util.backgroundExecutor().execute(()->{
            EchelonUser user =  MinecraftEchelonForge.getClientProxy().getUser();
            Echelon echelon = user.getEchelon();
            echelon.getPlayerStat(user.getClientToken().uid,"points",playerStat ->
            {

                Minecraft.getInstance().submitAsync(()->{
                if (playerStat.success){
                    long dif = Instant.now().toEpochMilli() -playerStat.date;
                    if (dif > resetTimer){ // if there is no score for more than 10 min, reset the score
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

        });
    }







}
