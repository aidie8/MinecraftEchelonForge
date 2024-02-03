package org.aidie8.minecraftechelonforge.Echelon;
import org.aidie8.EchelonJavaSDK.EchelonSDK.Responses.*;
import org.aidie8.EchelonJavaSDK.EchelonSDK.*;
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
        EchelonTwitchController.AuthWithTwitch(required->{
            try {
                Echelon.logger.info(required.webUrl);
                Util.getPlatform().openUri(new URI(required.webUrl));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void addPoints(float points)
    {
        echelon.addPlayerStat(playerData.uid,"points",Math.round(points),(onComplete) ->{
            Echelon.logger.info("Added points " + points);
        });

    }

    public void removePoints(float points)
    {
        echelon.addPlayerStat(playerData.uid,"points",Math.round(-points),(onComplete) ->{
            if (onComplete.success){
                Echelon.logger.info("Removed points " + points);
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
        removePoints(damage * takeDamagePoints);
    }

    public void playerGotDebuff()
    {
        removePoints(getDeBuffPoints);
    }
    public void playerBrokenTool()
    {
        addPoints(brokenToolPoints);
    }
    public void placedBlock()
    {
        addPoints(placingBlocksPoints);
    }
    public void breakBlock()
    {
        addPoints(breakingBlockPoints);
    }

    public void healHealth(float health)
    {
        addPoints(health *healPoints);
    }

    public void gainedEXP(float exp)
    {
        addPoints(exp * getEXPPoints);
    }
    public void killAggressiveMob()
    {
        addPoints(killMobsPoints);
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
