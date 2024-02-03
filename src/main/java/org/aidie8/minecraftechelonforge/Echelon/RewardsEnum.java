package org.aidie8.minecraftechelonforge.Echelon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Objects;
import java.util.function.Function;

public enum RewardsEnum {


    FOOD(1,"food", (PlayerEntity player) ->{
        player.spawnAtLocation(new ItemStack(Items.BREAD));
        return null;
    }),
    EXP(2,"exp", player -> {
        player.giveExperiencePoints(200);
        return null;
    });

    public final int index;
    public final String reward;
    public final Function<PlayerEntity, Void> runnable;
    RewardsEnum(int index, String reward, Function<PlayerEntity, Void> run) {
        this.index = index;
        this.reward = reward;
        this.runnable = run;
    }


    public static RewardsEnum getRewardsByIndex(int id)
    {
        for (RewardsEnum reward: RewardsEnum.values())
        {
            if (reward.index == id)
            {
                return reward;
            }
        }
        return null;
    }

    public static RewardsEnum getByReward(int rewardId,String reward)
    {

        for (RewardsEnum rewardsEnum: RewardsEnum.values())
        {
            if (rewardsEnum.index == rewardId && reward.equals(rewardsEnum.reward))
            {
                return rewardsEnum;
            }
        }
        return null;

    }
}
