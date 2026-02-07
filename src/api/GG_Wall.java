package api;

import mindustry.world.blocks.defense.Wall;

public class GG_Wall extends Wall {
    public float colod;
    public GG_Wall(String name) {
        super(name);
        this.colod=0.0f;
    }
    @Override
    public void setStats(){
        super.setStats();
        stats.addPercent(GG_Stat.colod,colod);

    }
}
