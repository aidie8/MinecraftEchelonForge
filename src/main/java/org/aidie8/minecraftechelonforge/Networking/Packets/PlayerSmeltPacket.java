package org.aidie8.minecraftechelonforge.Networking.Packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import org.aidie8.minecraftechelonforge.MinecraftEchelonForge;
import org.aidie8.minecraftechelonforge.Networking.IMessage;

import javax.annotation.Nullable;

public class PlayerSmeltPacket implements IMessage {

    private SmeltType type;
    public enum SmeltType
    {
        FOOD,
        NON_FOOD
    }

    public PlayerSmeltPacket(){}

    public PlayerSmeltPacket(SmeltType type)
    {
       this.type = type;
    }
    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeEnum(type);
    }

    @Override
    public void fromBytes(PacketBuffer buf) {
        type = buf.readEnum(SmeltType.class);
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
                    MinecraftEchelonForge.getClientProxy().getUser().smeltFoodItem();
                    break;

                case NON_FOOD:
                    MinecraftEchelonForge.getClientProxy().getUser().smeltItem();
                    break;
            }

        });
    }
}
