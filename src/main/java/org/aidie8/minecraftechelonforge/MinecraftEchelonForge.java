package org.aidie8.minecraftechelonforge;

import Proxy.ClientProxy;
import com.EchelonSDK.Echelon;
import com.EchelonSDK.EchelonTwitchController;
import com.EchelonSDK.Responses.TwitchResponses;
import com.google.gson.Gson;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jline.utils.Log;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("minecraftechelonforge")
public class MinecraftEchelonForge {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();


    private static ClientProxy proxy;
    public MinecraftEchelonForge() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().options);
        MinecraftEchelonForge.proxy = new ClientProxy();
        proxy.getUser().initialiseEchelonSystem(Echelon.Environment.DEVELOPMENT,
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6NzcsImdhbWUiOjQ3LCJ0" +
                        "eXBlIjoiZGV2LXByaXYifQ.cEzrUM-GoZRYWJsx7o_XU7cE-yoQmGo6UlakwSpghvM",true,(success,error) ->{
            if(!success){
                Echelon.logger.warn("Echelon System could not be Initialised " + error);
            }
            else
            {
                Echelon.logger.warn("Echelon System Initialised successfully");

            }

        });


        //TODO update these to the correct event hooks
        proxy.getUser().getEchelon().onPlayerRewardClaimed = this::onPlayerRewardClaimed;
        proxy.getUser().getEchelon().onAuthCompletedEvents.add(this::onAuthCompleted);
        EchelonTwitchController.onTwitchAuthCompleted.add(this::onAuthCompletedTriggered);
    }

    private void onAuthCompleted(TwitchResponses.ClientToken tokenData, boolean success) {
        Echelon.logger.info("Twitch user Authorized: " + tokenData.name + " id " + tokenData.id + " data " + new Gson().toJson(tokenData));
        proxy.getUser().setPlayerData(tokenData);


        //region startRewardsListener
        ArrayList<String> testPlayers = new ArrayList<>();
        testPlayers.add(tokenData.uid);

        boolean rewardsListenerSuccess = proxy.getUser().getEchelon().startPlayerRewardsListener(testPlayers);
        LOGGER.info("Checking for unlocked rewards (" + rewardsListenerSuccess + ")");
        //endregion

    }

    private void onAuthCompletedTriggered(TwitchResponses.ClientToken tokenData, boolean fromStoredCredentials)
    {
        for(EchelonTwitchController.onAuthComplete complete: proxy.getUser().getEchelon().onAuthCompletedEvents)
        {
            complete.run(tokenData,fromStoredCredentials);
        }
    }

    private void onPlayerRewardClaimed(String playerUID, String rewardId,String value, String rewardToken) {

    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("minecraftechelonforge", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event) {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }



    public static ClientProxy getClientProxy()
    {
       return MinecraftEchelonForge.proxy;
    }



}
