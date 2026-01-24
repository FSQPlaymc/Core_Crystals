package api;

import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import api.Item_void.GGItem_NC;

public class GG_Stat extends Stat implements Comparable<Stat>{
    public static final Stat
    BasicBurnTime=new Stat("BasicBurnTime"),
    BasalHeatProduction=new Stat("BasalHeatProduction");
    public GG_Stat(String name, StatCat category) {
        super(name, category);
    }
    public GG_Stat(String name){
        this(name, StatCat.general);
    }

}
