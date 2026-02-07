package api.Item_void;

import api.GG_Stat;
import arc.graphics.Color;
import mindustry.type.Item;
import mindustry.world.meta.Stat;

public class GGItem_NC extends Item {
    public int BasicBurnTime;//基础燃烧时间
    public int BasalHeatProduction;//基础产热
    public float BasalPower;
    public GGItem_NC(String name, Color color) {
        super(name, color);
        this.BasicBurnTime=0;
        this.BasalHeatProduction=0;
        this.BasalPower=0;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.addPercent(GG_Stat.BasicBurnTime,BasicBurnTime);
        stats.addPercent(GG_Stat.BasalHeatProduction,BasalHeatProduction);
    }
}
