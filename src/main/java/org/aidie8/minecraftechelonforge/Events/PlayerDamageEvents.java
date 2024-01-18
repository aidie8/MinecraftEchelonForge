package org.aidie8.minecraftechelonforge.Events;

import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectType;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.aidie8.minecraftechelonforge.MinecraftEchelonForge;

@Mod.EventBusSubscriber
public class PlayerDamageEvents {



    @SubscribeEvent
    public static void onPlayerDamageEvent(LivingHurtEvent event) // this happens after things like armor is taken into account
    {
        if (event.getEntity().level.isClientSide){
            if(event.getEntity() instanceof PlayerEntity)
            {
                MinecraftEchelonForge.getClientProxy().getUser().playerDamaged(event.getAmount()); // send to sever modifying the points
            }
        }
    }


    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event)
    {
        if (event.getEntity() instanceof PlayerEntity){
            if (((PlayerEntity) event.getEntity()).isDeadOrDying())
            {
                MinecraftEchelonForge.getClientProxy().getUser().playerDied();
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerPotionApplied(PotionEvent.PotionAddedEvent event)
    {
        if (event.getEntity() instanceof PlayerEntity){
            if(!event.getEntity().level.isClientSide){
                if (event.getPotionEffect().getEffect().getCategory() == EffectType.HARMFUL)
                {
                    MinecraftEchelonForge.getClientProxy().getUser().playerGotDebuff();
                }
            }
        }

    }

    @SubscribeEvent
    public static void onPlayerAttackHostileMobEvent(LivingDeathEvent event)
    {

        if (event.getSource().getEntity() instanceof PlayerEntity)
        {
            if(!event.getSource().getEntity().level.isClientSide)
            {
                MinecraftEchelonForge.getClientProxy().getUser().killAggressiveMob();
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerHealedEvent(LivingHealEvent event)
    {
        if (event.getEntity() instanceof PlayerEntity)
        {
            if(event.getEntity().level.isClientSide)
            {
                MinecraftEchelonForge.getClientProxy().getUser().healHealth(event.getAmount());
            }
        }
    }
}
