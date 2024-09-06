package org.aidie8.minecraftechelonforge.Events;

import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectType;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.aidie8.minecraftechelonforge.MinecraftEchelonForge;
import org.aidie8.minecraftechelonforge.Networking.Network;
import org.aidie8.minecraftechelonforge.Networking.Packets.*;

@Mod.EventBusSubscriber
public class PlayerDamageEvents {



    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerDamageEvent(LivingHurtEvent event) // this happens after things like armor is taken into account
    {
        if (event.getEntity().level.isClientSide){
            if(event.getEntity() instanceof PlayerEntity)
            {
                Network.getNetwork().sendToPlayer(new PlayerDamagedPacket(event.getAmount()), (ServerPlayerEntity) event.getEntity());
            }
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerDeath(LivingDeathEvent event)
    {
        if (event.getEntity() instanceof PlayerEntity){
            if (((PlayerEntity) event.getEntity()).isDeadOrDying())
            {
                Network.getNetwork().sendToPlayer(new PlayerDeathPacket(), (ServerPlayerEntity) event.getEntity());
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerPotionApplied(PotionEvent.PotionAddedEvent event)
    {
        if (event.getEntity() instanceof PlayerEntity){
            if(!event.getEntity().level.isClientSide){
                if (event.getPotionEffect().getEffect().getCategory() == EffectType.HARMFUL)
                {
                    Network.getNetwork().sendToPlayer(new PlayerDebuffPacket(), (ServerPlayerEntity) event.getEntity());
                }
            }
        }

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerAttackHostileMobEvent(LivingDeathEvent event)
    {

        if (event.getSource().getEntity() instanceof PlayerEntity)
        {
            if(!event.getSource().getEntity().level.isClientSide)
            {
                if (!(event.getEntity() instanceof PlayerEntity) && (event.getSource().getEntity() instanceof PlayerEntity)){
                    Network.getNetwork().sendToPlayer(new PlayerKillHostileMob(), (ServerPlayerEntity) event.getSource().getEntity());
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerHealedEvent(LivingHealEvent event)
    {
        if (event.getEntity() instanceof PlayerEntity)
        {
            if(event.getEntity().level.isClientSide)
            {
                Network.getNetwork().sendToPlayer(new PlayerHealedPacket(event.getAmount()), (ServerPlayerEntity) event.getEntity());
            }
        }
    }
}
