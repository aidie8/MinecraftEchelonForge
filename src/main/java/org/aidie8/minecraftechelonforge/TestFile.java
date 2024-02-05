package org.aidie8.minecraftechelonforge;

import com.google.gson.Gson;
import com.EchelonSDK.Echelon;
import com.EchelonSDK.EchelonTwitchController;
import com.EchelonSDK.Responses.TwitchResponses;
import org.aidie8.minecraftechelonforge.Echelon.RewardsEnum;
import org.aidie8.minecraftechelonforge.Networking.Network;
import org.aidie8.minecraftechelonforge.Networking.Packets.PlayerClaimedRewardPacket;
import org.aidie8.minecraftechelonforge.Proxy.ClientProxy;

import java.util.ArrayList;

import static org.aidie8.minecraftechelonforge.MinecraftEchelonForge.getClientProxy;

public class TestFile {


    public void InitEchelon()
    {

        getClientProxy().getUser().initialiseEchelonSystem(Echelon.Environment.DEVELOPMENT,
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6NzcsImdhbWUiOjQ3LCJ0" +
                        "eXBlIjoiZGV2LXByaXYifQ.cEzrUM-GoZRYWJsx7o_XU7cE-yoQmGo6UlakwSpghvM",true,(success,error) ->{
                    if(!success){
                        Echelon.logger.warn("Echelon System could not be Initialised " + error);
                    }
                    else
                    {
                        Echelon.logger.warn("Echelon System Initialised successfully");

                    }

                });




        //TODO update these to the correct event hooks
        getClientProxy().getUser().getEchelon().onPlayerRewardClaimed = this::onPlayerRewardClaimed;
        getClientProxy().getUser().getEchelon().onAuthCompletedEvents.add(this::onAuthCompleted);
        EchelonTwitchController.onTwitchAuthCompleted.add(this::onAuthCompletedTriggered);
    }
    private void onAuthCompleted(TwitchResponses.ClientToken tokenData, boolean success) {
        Echelon.logger.info("Twitch user Authorized: " + tokenData.name + " id " + tokenData.id + " data " + new Gson().toJson(tokenData));
        getClientProxy().getUser().setPlayerData(tokenData);


        //region startRewardsListener
        ArrayList<String> testPlayers = new ArrayList<>();
        testPlayers.add(tokenData.uid);

        boolean rewardsListenerSuccess = getClientProxy().getUser().getEchelon().startPlayerRewardsListener(testPlayers);
        MinecraftEchelonForge.LOGGER().info("Checking for unlocked rewards (" + rewardsListenerSuccess + ")");
        //endregion

    }

    private void onAuthCompletedTriggered(TwitchResponses.ClientToken tokenData, boolean fromStoredCredentials)
    {
        for(EchelonTwitchController.onAuthComplete complete: getClientProxy().getUser().getEchelon().onAuthCompletedEvents)
        {
            complete.run(tokenData,fromStoredCredentials);
        }
    }

    private void onPlayerRewardClaimed(String playerUID, String rewardId,String rewardToken,String value) {
        Network.getNetwork().sendToServer(new PlayerClaimedRewardPacket(RewardsEnum.getByReward(Integer.parseInt(rewardId),value)));
    }
}
