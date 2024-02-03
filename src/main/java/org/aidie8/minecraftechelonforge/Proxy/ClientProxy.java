package org.aidie8.minecraftechelonforge.Proxy;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.aidie8.minecraftechelonforge.Echelon.EchelonUser;
@OnlyIn(Dist.CLIENT)
public class ClientProxy {


    public ClientProxy()
    {
        user = new EchelonUser();
    }
    private EchelonUser user;


    public EchelonUser getUser() {
        return user;
    }
}
