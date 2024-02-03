package org.aidie8.minecraftechelonforge.Configs;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber
public class EchelonConfigs {


    private static final ForgeConfigSpec spec;


    private static final EchelonBasics basics;
    public EchelonConfigs() {}


    public static EchelonBasics getBasics()
    {
        return basics;
    }

    public static ForgeConfigSpec getSpec() {
        return spec;
    }




    public static class EchelonBasics{

        public enum LeaderBoardTypes
        {

            TOP ("Top X"),
            RELATIVE ("Relative X"),
            PERSONAL ("Personal Score");

            public final String displayString;

            LeaderBoardTypes(String displayString)
            {
                this.displayString = displayString;
            }
        }


        public final ForgeConfigSpec.EnumValue<LeaderBoardTypes> leaderBoardDisplay;
        public final ForgeConfigSpec.IntValue leaderboardRange;

        public EchelonBasics(ForgeConfigSpec.Builder builder)
        {
            builder.push("Echelon");
            builder.comment("Basic Settings for Echelon").push("Basics").push("LeaderBoard");
            leaderBoardDisplay = builder.comment("This is to decide how the Leaderboard Should be displayed on the screen," +
                    " Top shows top x based on the next settings," +
                    "Relative with show the people around your score," +
                    "Personal will only show your score ").defineEnum("Leaderboard",LeaderBoardTypes.TOP);

            builder.pop();
            builder.push("Leaderboard Range");
            leaderboardRange = builder.comment("This is the range used from the leaderboard Type").defineInRange("Range",5,3,10);
            builder.pop(2);
        }

        public void setLeaderboardDisplay(EchelonBasics.LeaderBoardTypes type)
        {
            leaderBoardDisplay.set(type);
        }
    }




    static //constructor
    {
        Pair<EchelonBasics, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(EchelonBasics::new);
        basics = commonSpecPair.getLeft();
        spec = commonSpecPair.getRight();
    }




}
