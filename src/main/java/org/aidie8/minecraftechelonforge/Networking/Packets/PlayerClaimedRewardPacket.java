package org.aidie8.minecraftechelonforge.Networking.Packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import org.aidie8.minecraftechelonforge.Echelon.RewardsEnum;
import org.aidie8.minecraftechelonforge.Networking.IMessage;

import javax.annotation.Nullable;

public class PlayerClaimedRewardPacket implements IMessage {



    RewardsEnum reward;


    public PlayerClaimedRewardPacket()
    {

    }

    public PlayerClaimedRewardPacket(RewardsEnum rewardsEnum)
    {
        this.reward = rewardsEnum;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeEnum(reward);
    }

    @Override
    public void fromBytes(PacketBuffer buf) {
       reward =  buf.readEnum(RewardsEnum.class);
    }

    @Nullable
    @Override
    public LogicalSide getExecutionSide() {
        return null;
    }

    @Override
    public void onExecute(NetworkEvent.Context ctxIn, boolean isLogicalServer) {
            ctxIn.enqueueWork(()-> reward.runnable.apply(ctxIn.getSender()));
    }
}
