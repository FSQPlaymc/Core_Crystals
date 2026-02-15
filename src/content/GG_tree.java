package content;

import api.gailubao;
import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.game.Objectives;
import mindustry.type.ItemStack;

import static content.GG_Block.GG_Block.*;
import static content.GG_Block.GG_factory.*;
import static content.GG_Block.GG_Powers.*;
import static content.GG_Block.GG_walls.*;
import static content.GG_units.*;
import static mindustry.content.Blocks.coreShard;
import static mindustry.content.TechTree.node;
import static mindustry.content.TechTree.nodeRoot;

public class GG_tree {
    public static void tree(){
        Planets.erekir.techTree=Planets.serpulo.techTree = nodeRoot("core-crystals", coreShard, () -> {
            //node();
            node(groundFactory,ItemStack.with(Items.copper,2000,Items.lead,1000,Items.graphite,700,Items.silicon,500),()->{
                node(tizhizhu,ItemStack.with(Items.copper,900,Items.lead,500,Items.graphite,300,Items.silicon,200,GGItems.Sijingti,300),()->{});
            });
            node(pulverizer,ItemStack.with(Items.copper,500,Items.lead,500,Items.graphite,200),()->{
                node(separator,ItemStack.with(Items.graphite,900,Items.silicon,600,GGItems.Sijingti,200),()->{});
                node(GGxiaoxinggui,ItemStack.with(Items.copper,2000,Items.lead,2000,Items.graphite,1000),()->{
                    node(tuduizhuang);
                    node(surgeAlloyF,()->{
                        node(MKhejin);
                        node(plastaniumYsji);
                        node(SYoilY);
                    });
                    node(TnmXinpian,ItemStack.with(Items.graphite,700,Items.silicon,1000,GGItems.Sijingti,800,Items.titanium,600),()->{
                        node(Bigfenli,ItemStack.with(GGItems.TanNaMiHeXing,400,Items.thorium,1000,Items.plastanium,500),()->{
                        });
                    });
                    node(SGfacto,ItemStack.with(Items.tungsten,1000,Items.graphite,2000,GGItems.Sijingti,1000,Items.silicon,2000),Seq.with(new Objectives.Research(Items.tungsten)),()->{});
                    node(Sichunghua,ItemStack.with(Items.titanium,2000,Items.silicon,3000,Items.graphite,1000),()->{});
                });
            });
            node(jinghuaqi,ItemStack.with(Items.copper,400,GGItems.Sijingti,500),()->{//发电---------------------------------------------------------------------------------------
                node(solarPanel,ItemStack.with(Items.silicon,600,GGItems.Sijingti,700,Items.lead,300),()->{

                });
                node(ffff, ItemStack.with(Items.graphite,500,Items.silicon,500,GGItems.Sijingti,1000),()-> {//hongshi,ynishi,shiying,qinjingshi,linbin,lubaoshi
                    node(cs,()->{
                        node(fanying);
                        node(SL);
                        node(hongshi);
                        node(ynishi);
                        node(shiying);
                        node(qinjingshi);
                        node(linbin);
                        node(lubaoshi);
                    });
                });
            });
            //物品树-------------------------------------------------------------------------------------------------------------------------
                node(GGItems.ThuaKuangShi);
                node(GGItems.Sijingti,()->{
                    node(GGItems.TanNaMiHeXing, Seq.with(new Objectives.Research(GGItems.ThuaKuangShi)),()->{});
                });
        });
    }
}
