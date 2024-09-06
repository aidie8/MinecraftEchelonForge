package org.aidie8.minecraftechelonforge.Echelon;
import com.EchelonSDK.Responses.*;
import com.EchelonSDK.*;

import net.minecraft.util.Util;



import java.net.URI;
import java.net.URISyntaxException;

public class EchelonUser {

    //region Adding Points
    // \/ \/
    public final float placingBlocksPoints = 1;
    public final float breakingBlockPoints = 2;
    public final float  healPoints = 1;

    public final float getEXPPoints = 3; // 3 x the amount of exp;

    public final float movementPoints = 100; //100 points every 100 blocks

    public final float killMobsPoints = 10; //10 points for every aggressive mob killed

    public final float brokenToolPoints = 10;

    public final float craftFood = 10;
    public final float smeltFood = 5;
    public final float craftItem = 6;
    public final float smeltItem = 2;

    //endregion
    //regionREMOVING POINTS
        // \/\/


    public final float getDeBuffPoints = 20; //flat per deBuff gained per level of deBuff
    public final float  takeDamagePoints = 20; //20 X damage taken

    public final float diePoints = 0.8f; // 80% of points
//endregion



    private Echelon echelon;
    private TwitchResponses.ClientToken playerData;
    public void OpenTwitchLogin()
    {
        if (!EchelonWebSocketClient.connected)
        {
            Echelon.getClient().init(this::AuthTwitch);
        }else
        {
            AuthTwitch();
        }

    }


    public void AuthTwitch()
    {
        EchelonTwitchController.AuthWithTwitch(required->{
            try {
                Echelon.INSTANCE.logger.info(required.webUrl);
                Util.getPlatform().openUri(new URI(required.webUrl));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public void addPoints(float points)
    {
        echelon.addPlayerStats(playerData.uid,"points",Math.round(points),(onComplete) ->{
            Echelon.INSTANCE.logger.info("Added points " + points);
        });

    }

    public void removePoints(float points)
    {
        echelon.addPlayerStats(playerData.uid,"points",Math.round(-points),(onComplete) ->{
            if (onComplete.success){
                Echelon.INSTANCE.logger.info("Removed points " + points);
            }
        });
        //remove points from echelon API
    }


    public void playerDied()
    {
        Util.backgroundExecutor().execute(()->{
            echelon.getPlayerStat(playerData.uid,"points",onComplete ->{
                if (onComplete.success){
                    removePoints(Float.parseFloat(onComplete.value) * diePoints);
                }
            });

        });

        //remove 80% of points
    }

    public void playerDamaged(float damage)
    {
        Echelon.INSTANCE.logger.info("Player Damaged");
        removePoints(damage * takeDamagePoints);
    }

    public void playerGotDebuff()
    {
        Echelon.INSTANCE.logger.info("Gained De-buff");
        removePoints(getDeBuffPoints);
    }
    public void playerBrokenTool()
    {
        Echelon.INSTANCE.logger.info("broke tool");
        addPoints(brokenToolPoints);
    }
    public void placedBlock()
    {
        Echelon.INSTANCE.logger.info("Placed Block");
        addPoints(placingBlocksPoints);
    }
    public void breakBlock()
    {
        Echelon.INSTANCE.logger.info("Break Block");
        addPoints(breakingBlockPoints);
    }

    public void healHealth(float health)
    {
        Echelon.INSTANCE.logger.info("Heal Health");
        addPoints(health *healPoints);
    }

    public void gainedEXP(float exp)
    {
        Echelon.INSTANCE.logger.info("Exp Points");
        addPoints(exp * getEXPPoints);
    }
    public void killAggressiveMob()
    {
        Echelon.INSTANCE.logger.info("Kill Mob");
        addPoints(killMobsPoints);
    }

    public void smeltFoodItem()
    {
        Echelon.INSTANCE.logger.info("Smelted food item");
        addPoints(smeltFood);
    }

    public void smeltItem()
    {
        Echelon.INSTANCE.logger.info("Smelted non-food item");
        addPoints(smeltItem);
    }

    public void craftFoodItem()
    {
        Echelon.INSTANCE.logger.info("Crafted food item");
        addPoints(craftFood);
    }
    public void craftItem()
    {
        Echelon.INSTANCE.logger.info("Crafted non-food item");
        addPoints(craftItem);
    }


    public void initialiseEchelonSystem(Echelon.Environment environment, String devToken, boolean devMode, Echelon.onSDKInitialisation onSDKInitialisation)
    {
        echelon = new Echelon(environment,devToken,devMode,onSDKInitialisation);
    }


    public Echelon getEchelon()
    {
        return echelon;
    }

    public void setPlayerData(TwitchResponses.ClientToken token)
    {
        this.playerData = token;
    }

    public TwitchResponses.ClientToken getClientToken()
    {
        return playerData;
    }
}
