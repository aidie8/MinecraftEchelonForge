package org.aidie8.minecraftechelonforge.Echelon;

public class EchelonUser {


    //ADDING POINTS \/ \/
    public final float placingBlocksPoints = 1;
    public final float breakingBlockPoints = 2;
    public final float  healPoints = 1;

    public final float getEXPPoints = 3; // 3 x the amount of exp;

    public final float movementPoints = 100; //100 points every 100 blocks

    public final float killMobsPoints = 10; //10 points for every aggressive mob killed

    ///REMOVING POINTS \/\/


    public final float getDeBuffPoints = 20; //flat per deBuff gained per level of deBuff
    public final float  takeDamagePoints = 20; //20 X damage taken

    public final float diePoints = 0.8f; // 80% of points


    public EchelonUser user;

    public void OpenTwitchLogin()
    {
        //TODO use Echelon API to open TwitchLogin
    }

    public void addPoints(float points)
    {
        //add points to echelon API

    }

    public void removePoints(float points)
    {
        //remove points from echelon API
    }


    public void playerDied()
    {
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
        addPoints(health);
    }

    public void gainedEXP(float exp)
    {
        addPoints(exp * getEXPPoints);
    }
    public void killAggressiveMob()
    {
        addPoints(10);
    }
}
