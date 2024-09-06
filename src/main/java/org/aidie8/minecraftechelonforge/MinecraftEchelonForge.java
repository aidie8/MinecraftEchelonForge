package org.aidie8.minecraftechelonforge;

import com.EchelonSDK.Echelon;
import com.EchelonSDK.EchelonTwitchController;
import com.EchelonSDK.Responses.APIResponse;
import com.EchelonSDK.Responses.TwitchResponses;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.fml.event.lifecycle.*;
import org.aidie8.minecraftechelonforge.Proxy.ClientProxy;

import com.google.gson.Gson;
import net.minecraft.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.aidie8.minecraftechelonforge.Configs.EchelonConfigs;
import org.aidie8.minecraftechelonforge.Echelon.RewardsEnum;
import org.aidie8.minecraftechelonforge.GUI.EchelonConfigGui;
import org.aidie8.minecraftechelonforge.Networking.Network;
import org.aidie8.minecraftechelonforge.Networking.Packets.PlayerClaimedRewardPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.util.ResolverUtil;

import javax.net.ssl.SSLContext;
import java.util.ArrayList;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("minecraftechelonforge")
public class MinecraftEchelonForge {
    private static ClientProxy proxy;
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public MinecraftEchelonForge() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EchelonConfigs.getSpec(),"echelon.toml");
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

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY,  () -> (mc, screen) -> new EchelonConfigGui(screen)));
        Reference.echelon = this;
        proxy = new ClientProxy();
        Gson gson = new Gson();
        String testJson = gson.toJson(new APIResponse());
        System.out.println("Test Json Output Text " + testJson);
        APIResponse response = gson.fromJson(testJson,new TypeToken<APIResponse>(){}.getType() );
        System.out.print("Converting it back test " + response);
        InitEchelon();



    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        Network.handler.init();

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        //LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().options);
        LOGGER.info("Doing Client Stuff");
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


    public static Logger LOGGER() {
        return LOGGER;
    }


    public static ClientProxy getClientProxy()
    {
       return MinecraftEchelonForge.proxy;
    }

    public void InitEchelon()
    {

        getClientProxy().getUser().initialiseEchelonSystem(Echelon.Environment.DEVELOPMENT,
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6NzcsImdhbWUiOjQ3LCJ0" +
                        "eXBlIjoiZGV2LXByaXYifQ.cEzrUM-GoZRYWJsx7o_XU7cE-yoQmGo6UlakwSpghvM",true,(success,error) ->{
                    if(!success){
                        Echelon.INSTANCE.getLogger().warn("Echelon System could not be Initialised " + error);
                    }
                    else
                    {
                        Echelon.INSTANCE.getLogger().warn("Echelon System Initialised successfully");

                    }

                });




        //TODO update these to the correct event hooks
        getClientProxy().getUser().getEchelon().onPlayerRewardClaimed = this::onPlayerRewardClaimed;
        getClientProxy().getUser().getEchelon().onAuthCompletedEvents.add(this::onAuthCompleted);
        EchelonTwitchController.onTwitchAuthCompleted.add(this::onAuthCompletedTriggered);
    }
    private void onAuthCompleted(TwitchResponses.ClientToken tokenData, boolean success) {
        Echelon.INSTANCE.logger.info("Twitch user Authorized: " + tokenData.name + " id " + tokenData.id + " data " + new Gson().toJson(tokenData));
        getClientProxy().getUser().setPlayerData(tokenData);


        //region startRewardsListener
        ArrayList<String> testPlayers = new ArrayList<>();
        testPlayers.add(tokenData.uid);

        boolean rewardsListenerSuccess = getClientProxy().getUser().getEchelon().startPlayerRewardsListener(testPlayers);
        MinecraftEchelonForge.LOGGER().info("Checking for unlocked rewards (" + rewardsListenerSuccess + ")");
        //endregion

    }

    private void onAuthCompletedTriggered(TwitchResponses.ClientToken tokenData, boolean fromStoredCredentials)
    {
        for(EchelonTwitchController.onAuthComplete complete: getClientProxy().getUser().getEchelon().onAuthCompletedEvents)
        {
            complete.run(tokenData,fromStoredCredentials);
        }
    }

    private void onPlayerRewardClaimed(String playerUID, String rewardId,String rewardToken,String value) {
        Network.getNetwork().sendToServer(new PlayerClaimedRewardPacket(RewardsEnum.getByReward(Integer.parseInt(rewardId),value)));
    }

}
