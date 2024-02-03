package org.aidie8.EchelonJavaSDK.EchelonSDK.Responses;


import org.aidie8.EchelonJavaSDK.EchelonSDK.Utils;

public class TwitchResponses {

    public static class AuthCode extends Utils.APIResponse {
        public String code;
    }


    public static class ClientToken extends Utils.APIResponse {
        public String id;
        public String uid;
        public String name;
        public float tokenVersion;

    }



}