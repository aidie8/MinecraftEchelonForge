package org.aidie8.minecraftechelonforge.Networking;

public class Network {

    public static final EchelonPacketHandler handler = new EchelonPacketHandler();


    public static EchelonPacketHandler getNetwork() {
        return handler;
    }
}
