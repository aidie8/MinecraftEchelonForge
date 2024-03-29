package org.aidie8.minecraftechelonforge;

import com.EchelonSDK.APIResponse;
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
        Reference.file = new TestFile();
        APIResponse test = new APIResponse();

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


    @SubscribeEvent
    public void onFinalLoading(FMLLoadCompleteEvent loadCompleteEvent)
    {
        Reference.file = new TestFile();
        Reference.file.InitEchelon();

    }
    public static Logger LOGGER() {
        return LOGGER;
    }


    public static ClientProxy getClientProxy()
    {
       return MinecraftEchelonForge.proxy;
    }



}
