package org.aidie8.minecraftechelonforge.Events;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ToolItem;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.aidie8.minecraftechelonforge.MinecraftEchelonForge;
import org.aidie8.minecraftechelonforge.Networking.Network;
import org.aidie8.minecraftechelonforge.Networking.Packets.PlayerBreakBlockPacket;
import org.aidie8.minecraftechelonforge.Networking.Packets.PlayerBreaksToolsPacket;
import org.aidie8.minecraftechelonforge.Networking.Packets.PlayerGainExpPacket;
import org.aidie8.minecraftechelonforge.Networking.Packets.PlayerPlaceBlockPacket;

@Mod.EventBusSubscriber
public class PlayerEvents {



    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void PlayerBreakBlock(BlockEvent.BreakEvent event)
    {
        if(event.isCanceled())return;
        if (!event.getPlayer().level.isClientSide)
        {
            if (!(event.getPlayer() instanceof FakePlayer))
            {
                Network.getNetwork().sendToPlayer(new PlayerBreakBlockPacket(), (ServerPlayerEntity) event.getPlayer());
            }
        }
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void PlayerPlaceBlock(BlockEvent.EntityPlaceEvent event)
    {
        if(event.isCanceled())return;
        if ((event.getEntity() instanceof PlayerEntity))
        {
            if (!event.getEntity().level.isClientSide)
            {
                Network.getNetwork().sendToPlayer(new PlayerPlaceBlockPacket(), (ServerPlayerEntity) event.getEntity());
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void PlayerGainExp(PlayerXpEvent.XpChange event)
    {
        if(event.isCanceled())return;
        if (!event.getPlayer().level.isClientSide)
        {
            if (!(event.getPlayer() instanceof FakePlayer))
            {
                Network.getNetwork().sendToPlayer(new PlayerGainExpPacket(event.getAmount()), (ServerPlayerEntity) event.getEntity());
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerBreaksTool(PlayerDestroyItemEvent event)
    {
        if(event.getOriginal().getItem() instanceof ToolItem)
        {
            if (!event.getPlayer().level.isClientSide)
            {
                Network.getNetwork().sendToPlayer(new PlayerBreaksToolsPacket(), (ServerPlayerEntity) event.getEntity());
            }
        }
    }
}
