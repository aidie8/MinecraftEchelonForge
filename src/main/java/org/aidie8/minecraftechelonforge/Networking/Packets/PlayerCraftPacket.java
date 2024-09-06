package org.aidie8.minecraftechelonforge.Networking.Packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import org.aidie8.minecraftechelonforge.MinecraftEchelonForge;
import org.aidie8.minecraftechelonforge.Networking.IMessage;

import javax.annotation.Nullable;

public class PlayerCraftPacket implements IMessage {
    private PlayerCraftPacket.CraftType type;
    public enum CraftType
    {
        FOOD,
        NON_FOOD
    }

    public PlayerCraftPacket(){}

    public PlayerCraftPacket(PlayerCraftPacket.CraftType type)
    {
        this.type = type;
    }
    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeEnum(type);
    }

    @Override
    public void fromBytes(PacketBuffer buf) {
        type = buf.readEnum(PlayerCraftPacket.CraftType.class);
    }

    @Nullable
    @Override
    public LogicalSide getExecutionSide() {
        return LogicalSide.CLIENT;
    }

    @Override
    public void onExecute(NetworkEvent.Context ctxIn, boolean isLogicalServer) {
        ctxIn.enqueueWork(()->{
            switch(type)
            {
                case FOOD:
                    MinecraftEchelonForge.getClientProxy().getUser().craftFoodItem();
                    break;

                case NON_FOOD:
                    MinecraftEchelonForge.getClientProxy().getUser().craftItem();
                    break;
            }

        });
    }
}
