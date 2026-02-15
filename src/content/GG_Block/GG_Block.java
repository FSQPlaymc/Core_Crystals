package content.GG_Block;


import api.*;
import api.more_factory.MFactory;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Interp;
import arc.struct.Seq;
import content.GGItems;
import content.GG_units;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.content.UnitTypes;
import mindustry.entities.Effect;
import mindustry.graphics.Layer;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.units.UnitFactory;

import static content.GGItems.Sifenmo;
import static content.GGItems.Sijingti;
import static mindustry.type.ItemStack.with;

public class GG_Block {
    public static Block groundFactory;
public static DirectionalLiquidUnloader gasd;
    public static GenericCrafter chuangZaolu;
    public static gailubao.gailubaoFactory GGxiaoxinggui;
    //public static lsd GGd;
    public static GG_HX hx;
    public static GG_HXg ghg;
    public static OreBlock oreZeta;
    public static OreBlock oreThuawu;
    public static OreBlock oreGuijingti;
    public static OreBlock oreCarbide;
    public static MFactory myCrafter;
    public static OreBlock oreSurge;
    public static OreBlock oreHejing;
    public static Drill luoxuanzuan;
    public static void Ore(){
//---------------------------------------单位厂--------------------------------------------------------------------------------------------------------------------------------//
        groundFactory = new UnitFactory("陆军工厂"){{
            requirements(Category.units, with(Items.copper, 50, Items.lead, 120, Items.silicon, 80,Items.graphite, 100));
            plans = Seq.with(
                    new UnitPlan(UnitTypes.crawler, 60f * 10, with(Items.silicon, 8, Items.coal, 10)),
                    new UnitPlan(GG_units.tizhizhu, 60f * 30, with(Items.silicon, 20, Items.graphite, 30,Items.copper,20, Sijingti,10))
            );
            size = 2;
            consumePower(1.2f);
            researchCostMultiplier = 0.5f;
        }};
//------------------------------------------单位厂-----------------------------------------------------------------------------------------------------------------------------//
        ghg=new GG_HXg("g1hx"){{
            requirements(Category.effect,with(Items.copper,4000,Items.lead,4000,Items.carbide,200, Sijingti,1000));
            size=4;
            incinerateNonBuildable = false; // 是否销毁不可建造物品
            health=4500;
        }};
        hx=new GG_HX("hx"){{
            requirements(Category.effect, with(Items.copper, 3000, Items.lead, 3000, Items.silicon, 2000));
            isFirstTier=true; // 是否为一级核心（初始核心）
            requiresCoreZone = false; // 是否需要核心区域放置
           size=2;
            incinerateNonBuildable = false; // 是否销毁不可建造物品
        }};
        gasd=new DirectionalLiquidUnloader("fesef"){{
            requirements(Category.liquid, with(Items.copper, 20));
            health = 120;
            speed = 4f;
            //solid = false;
            underBullets = true;
            //regionRotated1 = 1;
            consumePower(0.3f);
        }};
        myCrafter = new MFactory("advanced-factory") {{
            requirements(Category.crafting, with(GGItems.zeta,30,
                    Items.copper,50,
                    Items.surgeAlloy,10,
                    Items.silicon,40,
                    Items.titanium,70,
                    Items.plastanium,30
            ));
            // 设置工厂的基本属性
            itemCapacity = 20;//物品数
            liquidCapacity=30f;
            health = 1000; // 生命值
            size = 3;      // 尺寸（3x3格）
            localizedName = "高级工厂"; // 显示名称
            description = "可以生产多种物品的高级工厂，支持多配方切换。";

            // 2. 添加配方
            // 配方1：生产金属碎片
            addRecipe(
                    "金属碎片生产",
                    new ItemStack[]{new ItemStack(Items.copper, 2)}, // 输入：2铜
                    new LiquidStack[]{new LiquidStack(Liquids.water, 10)}, // 输入：10水
                    new ItemStack[]{new ItemStack(Items.scrap, 3)}, // 输出：3金属碎片
                    null,
                    67f, // 生产时间4秒
                    20f // 电力消耗20单位/分钟
            );
            // 配方2：生产塑料
            addRecipe(
                    "塑料生产",
                    new ItemStack[]{new ItemStack(Items.coal, 1), new ItemStack(Items.lead, 1)}, // 输入：1煤+1铅
                    new LiquidStack[]{new LiquidStack(Liquids.oil, 15)}, // 输入：15油
                    new ItemStack[]{new ItemStack(Items.plastanium, 1)}, // 输出：1塑料
                    null,
                    6f, // 生产时间6秒
                    40f // 电力消耗40单位/分钟
            );

            // 配方3：生产冷却液
            addRecipe(
                    "冷却液生产",
                    (ItemStack[]) null,
                    new LiquidStack[]{
                            new LiquidStack(Liquids.water, 30),
                            new LiquidStack(Liquids.oil, 5)
                    }, // 输入：30水+5汞
                    null,
                    new LiquidStack[]{new LiquidStack(Liquids.cryofluid, 20)}, // 输出：20冷却液
                    8f, // 生产时间8秒
                    30f // 电力消耗30单位/分钟
            );
        }};
//new LiquidRouter


        luoxuanzuan =new Drill("luoxuanzuan"){{
            requirements(Category.production, with(Items.copper, 35, Items.graphite, 30, Items.silicon, 30, Items.titanium, 20));
            drillTime = 280;
            size = 3;
            hasPower = true;
            tier = 4;
            updateEffect = Fx.pulverizeMedium;
            drillEffect = Fx.mineBig;
            blockedItems = Seq.with(Sifenmo);//不挖的物品

            consumePower(1.10f);
            consumeLiquid(Liquids.water, 0.08f).boost();
        }};
        factory sandCracker = new factory("sand-cracker") {{
            size = 2;
            requirements(Category.crafting, with(Items.silicon,28,Items.copper,36,Items.lead,17,Items.graphite,24));
            health = 320;
            craftTime = 50f;
            itemCapacity = 60;//最大物品容量
            hasPower = hasItems = true;
            consumePower(0.4f);
            updateEffect = new Effect(80f, e -> { // 持续80帧的动态粒子效果
                Fx.rand.setSeed(e.id); // 基于实体ID的随机种子
                // 颜色插值：浅灰→灰，随进度变化
                Draw.color(Color.lightGray, Color.gray, e.fin());
                // 生成4个方向随机的粒子
                Angles.randLenVectors(e.id, 4, 2.0F + 12.0F * e.fin(Interp.pow3Out), (x, y) -> {
                    // 绘制圆形粒子，尺寸随进度减小
                    Fill.circle(e.x + x, e.y + y, e.fout() * Fx.rand.random(1, 2.5f));
                });
            }).layer(Layer.blockOver + 1); // 渲染层级：建筑上层
addInput(Sifenmo,6,Items.coal,2);
addInput(Sifenmo,5,Liquids.oil,0.17f);
addInput(Items.silicon,1,Items.coal,2);
            outputItem = new ItemStack(Sijingti, 2);
        }};
        oreHejing = new OreBlock("hejing-wall", GGItems.hejing){{//巨浪合金
            this.variants = 1;
        }};
        oreSurge = new OreBlock("surge-wall",Items.surgeAlloy){{//巨浪合金
            wallOre =true;
            this.variants = 1;
        }};
        oreGuijingti = new OreBlock("silicon-crystal-slag-wall"){{
            itemDrop = Sijingti;
            wallOre = true;//墙矿
        }};
        oreGuijingti = new OreBlock("silicon-crystal-slag"){{
            itemDrop = Sijingti;
             oreThreshold = 0.41f;//控制矿石生成的稀疏程度。值越大（越接近1），矿石越稀少；值越小（越接近0），矿石越密集。
             oreScale = 6.1f;//控制矿石分布图案的频率。值越大，矿石分布越密集（图案更小更频繁）；值越小，矿石分布越稀疏（图案更大更平缓）
            variants = 2;
        }};
        GGxiaoxinggui =new gailubao.gailubaoFactory("xiaoxinggui"){{
            requirements(Category.crafting, with(Items.copper,24,Items.lead,21));
            outputItem = new ItemStack(Items.silicon,1);
            craftTime = 60f;
            itemCapacity = 20;
            size =3;
            hasItems = true;//消耗物品
            hasLiquids = false;//消耗流体
            hasPower = true;//消耗电力
            craftEffect = Fx.trailFade;
            consumePower(45/60f);//*100
            consumeItem(Sijingti, 5);//Sifenmo,1
        }};
        oreThuawu = new OreBlock("orethuawu-wall"){{
            itemDrop = GGItems.ThuaKuangShi;
           oreDefault = true;
            oreThreshold = 0.83f;
            oreScale = 7.0f;
            wallOre = true;//墙矿
        }};
        oreThuawu = new OreBlock("orethuawu"){{
            itemDrop = GGItems.ThuaKuangShi;
            oreDefault = true;
            oreThreshold = 0.83f;
            oreScale = 7.0f;
            //wallOre = true;//墙矿
        }};


        oreZeta=new OreBlock("zeta"){{
            itemDrop = GGItems.zeta; // 掉落物（必须）
          //  oreDefault = true;
           // oreThreshold = 0.71f;//控制矿石生成的稀疏程度。值越大（越接近1），矿石越稀少；值越小（越接近0），矿石越密集。
           // oreScale = 10.47619f;//控制矿石分布图案的频率。值越大，矿石分布越密集（图案更小更频繁）；值越小，矿石分布越稀疏（图案更大更平缓）
            variants = 3;//三种贴图在blocks/environment中
            //fullIcon=Core.atlas.find("custom-ores/ore_zeta");
        }};
        chuangZaolu = new GenericCrafter("chuangzaolu"){{//贴图在
        requirements(Category.crafting, with(GGItems.zeta,30,
                Items.copper,50,
                Items.surgeAlloy,10,
                Items.silicon,40,
                Items.titanium,70,
                Items.plastanium,30
                ));
            craftEffect = Fx.pulverizeMedium;
            outputItem = new ItemStack(GGItems.itemCZzw, 2);//产出物品
            craftTime = 300f;//生产时间
            itemCapacity = 20;//物品数
            size = 4;
            hasItems = true;//消耗物品
            hasLiquids = true;//消耗流体
            hasPower = true;//消耗电力

            consumePower(90.0f);//*100
            consumeItem(GGItems.zeta, 10);
            consumeLiquid(Liquids.oil, 1.9f);

        }};
    }
}
/*重要原则：
当使用 new OreBlock("name") 时，必须在 sprites/blocks/ores/ 下有对应的 ore-name.png 文件。
若使用自定义路径，则必须显式声明 sprite = Core.atlas.find("your/path")。
二者必选其一否则矿石无法显示贴图。*/