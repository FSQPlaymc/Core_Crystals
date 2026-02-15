package content;

import api.Item_void.GGItem_NC;
import arc.Core;
import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.type.Item;

import static mindustry.content.Items.*;

public class GGItems {
    public static Item zeta;
    public static Item itemCZzw;
    public static Item Sijingti;
    public static Item Sifenmo;
    public static Item ThuaKuangShi;
    public static Item TanNaMiHeXing;
    public static Item zaisu;
    public static  Item hejing;
    public static GGItem_NC U,thorium_230,thorium_232,fuel_BUT,depleted_fuel_BUT,uranium_233,uranium_235,uranium_238,neptunium_236,neptunium_237;
    public static GGItem_NC depleted_fuel_uranium_233,depleted_fuel_uranium_235,fuel_uranium_233,fuel_uranium_235;
    public static final Seq<Item> GGItemsj = new Seq();
    public GGItems(){}
    public static void aloud(){
        neptunium_236=new GGItem_NC("neptunium_236",Color.valueOf("54f936"));
        neptunium_237=new GGItem_NC("neptunium_237",Color.valueOf("54f936"));
        uranium_233=new GGItem_NC("uranium_233",Color.valueOf("54f936"));
        uranium_235=new GGItem_NC("uranium_235",Color.valueOf("54f936"));
        uranium_238=new GGItem_NC("uranium_238",Color.valueOf("54f936"));
        depleted_fuel_uranium_233=new GGItem_NC("depleted_fuel_uranium_233",Color.valueOf("54f936"));
        depleted_fuel_uranium_235=new GGItem_NC("depleted_fuel_uranium_235",Color.valueOf("54f936"));
        depleted_fuel_BUT= new GGItem_NC("depleted_fuel_BUT",Color.valueOf("54f936"));
        fuel_BUT=new GGItem_NC("BUT",Color.valueOf("54f936")){{
            BasalHeatProduction=22;
            BasicBurnTime=120*30;
            BasalPower=84f;//1=900NC处理
            //id=1145;//-32768到32767
        }};
        fuel_uranium_233=new GGItem_NC("fuel_uranium_233",Color.valueOf("54f936")){{
            BasalHeatProduction=60;
            BasicBurnTime=75.6f*30;
            BasalPower=201.6f;//1=900NC处理
            //id=1145;//-32768到32767
        }};
        fuel_uranium_235=new GGItem_NC("fuel_uranium_235",Color.valueOf("54f936")){{
            BasalHeatProduction=18;
            BasicBurnTime=62.5f*30;
            BasalPower=168f;//1=900NC处理
            //id=1145;//-32768到32767
        }};
        thorium_230= new GGItem_NC("thorium_230",Color.valueOf("54f936"));
        thorium_232= new GGItem_NC("thorium_232",Color.valueOf("54f936"));
        U=new GGItem_NC("U",Color.valueOf("54f936")){{

        }};
        Sijingti = new Item("guijingti", Color.valueOf("031817")) {{
            this.hardness = 3;
            this.explosiveness = 0.0F;//爆炸 = 0.0F;可燃性
            this.flammability = 0.0F;
            this.charge = 0.0F;//电性
        }};
        ThuaKuangShi = new Item("tanhuakuangshi",Color.valueOf("b81163")){{
            this.hardness =4;
            this.flammability = 2.0f;
            this.radioactivity = 1.55f;//放射性
        }};
        TanNaMiHeXing = new Item("tanmnimihexing",Color.valueOf("0bb330")){{
            this.hardness = 0;
        }};
        zaisu = new Item("zaisu",Color.valueOf("3661fb")){{
            this.flammability = 0.5f;
            this.explosiveness = 1.0f;
            this.charge =250.0f;
            this.healthScaling = 3.1f;//建筑生命值倍率
        }};
        hejing=new Item("hexingjingti",Color.valueOf("a227ec")){{
            this.flammability= 3.0f;
            this.explosiveness = 15.0f;
            this.radioactivity = 13.0f;
            this.charge = 30.0f;
            this.cost = 5.4f;//建筑建造时间值倍率
            this.healthScaling = 1.0f;//建筑生命值倍率
        }};
        zeta=new Item("Zeta", Color.valueOf("e07123")){{//come from NewHorizon
            this.hardness=4;
        this.flammability=0.5f;
        this.explosiveness=99.9f;
        this.radioactivity=222.2f;
        this.fullIcon = Core.atlas.find("Zeta");
        }};
        itemCZzw=new Item("Zwangzaozhiwu",Color.valueOf("ed0e21")){{
            this.hardness=90;
            this.explosiveness=1000;
            this.charge=1000;
            this.fullIcon = Core.atlas.find("Zwangzaozhiwu");

        }};
        carbide.hardness=5;
        surgeAlloy.healthScaling=1.5f;
        surgeAlloy.hardness=4;
        GGItemsj.addAll(new Item[]{scrap, copper, lead, graphite, coal, titanium, thorium, silicon, plastanium,
                phaseFabric, surgeAlloy, sporePod, sand, blastCompound, pyratite, metaglass,itemCZzw,hejing,ThuaKuangShi,TanNaMiHeXing,
                Sijingti});
    }

}
/*
差点忘了
核晶
燃30%
放130%
爆150%
电300%
public Color color; //颜色
public float explosiveness; //爆炸性
public float flammability; //可燃性
public float radioactivity; //放射性
public float charge; //电荷量
public int hardness; //硬度
public float cost; //成本
public float healthScaling; //生命值缩放比例
public boolean lowPriority; //低优先级
public int frames; //帧数
public int transitionFrames; //过渡帧数
public float frameTime; //帧时间
public boolean buildable; //可建造
public boolean hidden; //隐藏状态
        this.explosiveness = 0.0F;
        this.flammability = 0.0F;
        this.charge = 0.0F;
        this.hardness = 0;
        this.cost = 1.0F;
        this.healthScaling = 0.0F;
        this.frames = 0;
        this.transitionFrames = 0;
        this.frameTime = 5.0F;
        this.buildable = true;
        this.hidden = false;
        this.color = color;
爆炸性 = 0.0F;
可燃性 = 0.0F;
电荷 = 0.0F;
硬度 = 0;
成本 = 1.0F;
健康缩放 = 0.0F;
帧 = 0;
过渡帧 = 0;
帧时间 = 5.0F;
可构建 = true;
隐藏 = false;
color = 颜色;*/