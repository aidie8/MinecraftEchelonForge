package org.aidie8.minecraftechelonforge.Events;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ToolItem;
import net.minecraft.util.Hand;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.aidie8.minecraftechelonforge.MinecraftEchelonForge;
import org.aidie8.minecraftechelonforge.Networking.Network;
import org.aidie8.minecraftechelonforge.Networking.Packets.*;

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


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerCraftItemEvent(PlayerEvent.ItemCraftedEvent event)
    {
        if(!event.getPlayer().level.isClientSide){
            if(event.getCrafting().isEdible())
            {
                Network.getNetwork().sendToPlayer(new PlayerCraftPacket(PlayerCraftPacket.CraftType.FOOD), (ServerPlayerEntity) event.getEntity());
            } else if (event.getCrafting().isDamageableItem()) {
                Network.getNetwork().sendToPlayer(new PlayerCraftPacket(PlayerCraftPacket.CraftType.NON_FOOD), (ServerPlayerEntity) event.getEntity());
            }
    }
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerSmeltedItemEvent(PlayerEvent.ItemSmeltedEvent event)
    {
        if(!event.getPlayer().level.isClientSide){
            if(event.getSmelting().isEdible())
            {
                Network.getNetwork().sendToPlayer(new PlayerSmeltPacket(PlayerSmeltPacket.SmeltType.FOOD), (ServerPlayerEntity) event.getEntity());
            }

            if(event.getSmelting().isDamageableItem())
            {
                Network.getNetwork().sendToPlayer(new PlayerSmeltPacket(PlayerSmeltPacket.SmeltType.NON_FOOD), (ServerPlayerEntity) event.getEntity());
            }
        }
    }
}
