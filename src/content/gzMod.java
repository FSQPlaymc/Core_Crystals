package content;

import content.GG_Block.GG_Block;
import content.GG_Block.GG_Powers;
import content.GG_Block.GG_factory;
import content.GG_Block.GG_walls;
import mindustry.mod.Mod;

public class gzMod extends  Mod{

    // 声明炮台变量


    @Override
    public void loadContent() {
        GG_Liquids.GGLiquids();
        GG_walls.walls();
        GGItems.aloud();//加载方式
        GGNewPT.NewP();
        GG_units.units();
        GG_Block.Ore();
        GG_factory.factorys();
        GG_Powers.Power();
        GG_tree.tree();
        //Itemschange.chi();
        // 实例化 foreshadow 炮台
}}