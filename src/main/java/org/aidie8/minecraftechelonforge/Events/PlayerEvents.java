package org.aidie8.minecraftechelonforge.Events;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.aidie8.minecraftechelonforge.MinecraftEchelonForge;

@Mod.EventBusSubscriber
public class PlayerEvents {



    @SubscribeEvent
    public static void PlayerBreakBlock(BlockEvent.BreakEvent event)
    {

        if (!event.getPlayer().level.isClientSide)
        {
            if (!(event.getPlayer() instanceof FakePlayer))
            {
                MinecraftEchelonForge.getClientProxy().getUser().breakBlock();
            }
        }
    }
    @SubscribeEvent
    public static void PlayerPlaceBlock(BlockEvent.EntityPlaceEvent event)
    {
        if ((event.getEntity() instanceof PlayerEntity))
        {
            if (!event.getEntity().level.isClientSide)
            {
                MinecraftEchelonForge.getClientProxy().getUser().placedBlock();
            }
        }
    }

    @SubscribeEvent
    public static void PlayerGainExp(PlayerXpEvent.XpChange event)
    {
        if (event.getPlayer().level.isClientSide)
        {
            if (!(event.getPlayer() instanceof FakePlayer))
            {
                MinecraftEchelonForge.getClientProxy().getUser().gainedEXP(event.getAmount());
            }
        }
    }
}
