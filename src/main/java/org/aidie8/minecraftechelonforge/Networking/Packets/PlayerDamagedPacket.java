package org.aidie8.minecraftechelonforge.Networking.Packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import org.aidie8.minecraftechelonforge.MinecraftEchelonForge;
import org.aidie8.minecraftechelonforge.Networking.IMessage;

import javax.annotation.Nullable;

public class PlayerDamagedPacket implements IMessage {

    private float amount;


    public PlayerDamagedPacket(){}

    public PlayerDamagedPacket(float amount){
        this.amount = amount;
    }
    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeFloat(amount);
    }

    @Override
    public void fromBytes(PacketBuffer buf) {
        amount = buf.readFloat();
    }

    @Nullable
    @Override
    public LogicalSide getExecutionSide() {
        return null;
    }

    @Override
    public void onExecute(NetworkEvent.Context ctxIn, boolean isLogicalServer) {
        ctxIn.enqueueWork(()-> MinecraftEchelonForge.getClientProxy().getUser().playerDamaged(amount)); // send to sever modifying the points
    }
}
