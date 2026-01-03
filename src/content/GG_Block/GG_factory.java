package content.GG_Block;

import api.block.ffactory.MFactory_2;
import api.factory;
import arc.graphics.Color;
import content.GGItems;
import content.GG_Liquids;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.production.AttributeCrafter;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.Separator;
import mindustry.world.consumers.ConsumeLiquids;
import mindustry.world.draw.*;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

import static mindustry.type.ItemStack.with;

public class GG_factory {
    public static AttributeCrafter cultivator;
    public static MFactory_2 aaaaaaaaa;
    public static factory SmallGlassKiln,plastaniumYsji;
    public static factory SGfacto;
    public static factory surgeAlloyF;
    public static factory baozif,TnmXinpian;
    public static GenericCrafter ksbl,sitichun,Ctiqu,Sichunghua,pulverizer,electrolyzer,boli,tuduizhuang,SYoilY,MKhejin;
    public static Separator separator,Bigfenli;
    public static void factorys(){
        aaaaaaaaa=new MFactory_2("aaaaawd"){{
            size=2;
            hasItems=hasLiquids=true;
            itemCapacity=10;
            requirements(Category.crafting,with(Items.surgeAlloy,25,Items.plastanium,30,Items.metaglass,40,Items.thorium,30,Items.graphite,50));
            addInput(Items.copper,2,0,Items.lead,2,1);
            addInput(Items.coal,1,0,Items.beryllium,1,1);
        }};
        MKhejin=new GenericCrafter("模块化合金冶炼炉"){{
            size=5;
            craftTime=80f;
            requirements(Category.crafting,with(Items.surgeAlloy,25,Items.plastanium,30,Items.metaglass,40,Items.thorium,30,Items.graphite,50));
            hasItems=hasPower=hasLiquids=true;
            outputItems=ItemStack.with(Items.surgeAlloy,4,Items.scrap,5);
            consumeItem(GGItems.Sijingti,15);
            consumePower(108/6f);
            consumeLiquids(LiquidStack.with(Liquids.water,10/6f,Liquids.slag,40/60f));
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(),new DrawDefault());
        }};
        plastaniumYsji=new factory("重型塑钢压缩器"){{
            size=4;
            craftTime=70;
            requirements(Category.crafting,with(Items.graphite,65,Items.plastanium,30,Items.metaglass,40,Items.thorium,30));
            hasItems=hasPower=hasLiquids=true;
            outputItem=new ItemStack(Items.plastanium,8);
            outputLiquid=new LiquidStack(GG_Liquids.os,20/6f);
            addInput(Items.beryllium,5,Items.titanium,3,Liquids.oil,3/6f);
            addInput(Items.beryllium,12,Liquids.oil,4/6f);
            addInput(Items.titanium,14,Liquids.oil,3/6f);
            consumePower(144/6f);
        }};
        SYoilY=new GenericCrafter("石油压缩机"){{
            size=2;
            requirements(Category.crafting,with(Items.surgeAlloy,25,Items.plastanium,30,Items.metaglass,40,Items.thorium,30));
            hasItems=hasPower=hasLiquids=true;
            craftTime=40;
            health=200;
            outputItem=new ItemStack(Items.graphite,4);
            consumeLiquid(Liquids.oil,15/60f);
            consumePower(19/6f);
        }};
        tuduizhuang=new GenericCrafter("钍对撞机"){{
            size=5;
            requirements(Category.crafting, with(Items.silicon, 60, Items.thorium, 30,GGItems.ThuaKuangShi,40,Items.titanium,20));
            hasPower=hasItems=true;
            health=250;
            craftTime=120f;
            outputItems=ItemStack.with(Items.thorium,15,Items.scrap,5);
            consumePower(2880);
            consumeItems(with(Items.surgeAlloy,1,Items.lead,20,Items.scrap,20));
        }};
        Bigfenli=new Separator("大型矿渣分解机"){{
            size=4;
            craftTime=78;
            health=400;
            liquidCapacity =30;//最大
            requirements(Category.crafting, with(Items.silicon, 60, Items.graphite, 90,Items.plastanium,50,GGItems.TanNaMiHeXing,40,Items.thorium,70));
            results = with(
                    Items.copper,3,
                    Items.lead, 3,
                    Items.graphite, 3,
                    Items.titanium, 2,
                    GGItems.Sijingti,3,
                    Items.surgeAlloy,1,
                    Items.thorium,2,
                    Items.tungsten,2,
                    Items.beryllium,2
            );
            hasPower = true;
            hasItems=true;
            consumePower(90/6f);
            consumeLiquid(Liquids.slag,60/6f);
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(), new DrawRegion("-spinner", 3, true), new DrawDefault());
        }};
        boli=new GenericCrafter("小型玻璃厂"){{
            requirements(Category.crafting, with(Items.copper, 60, GGItems.Sijingti, 30, Items.lead, 30));
            craftEffect = Fx.smeltsmoke;
            outputItem = new ItemStack(Items.metaglass, 1);
            craftTime = 30f;
            size = 2;
            hasPower = hasItems = true;
            drawer = new DrawMulti(new DrawDefault(), new DrawFlame(Color.valueOf("ffc099")));
            ambientSound = Sounds.loopSmelter;
            ambientSoundVolume = 0.07f;

            consumeItems(with(Items.lead, 1, Items.sand, 1));
            consumePower(0.60f);
        }};
        electrolyzer = new GenericCrafter("电解室"){
            {
                requirements(Category.crafting, with(Items.silicon, 50, Items.graphite, 40, Items.beryllium, 130, Items.tungsten, 80));
                size = 3;

                researchCostMultiplier = 1.2f;
                craftTime = 10f;
                rotate = true;
                invertFlip = true;
                group = BlockGroup.liquids;
                itemCapacity = 0;

                liquidCapacity = 50f;

                consumeLiquid(Liquids.water, 40f / 60f);
                consumePower(200/60f);
                drawer = new DrawMulti(
                        new DrawRegion("-bottom"),
                        new DrawLiquidTile(Liquids.water, 2f),
                        new DrawBubbles(Color.valueOf("7693e3")) {{
                            sides = 10;
                            recurrence = 3f;
                            spread = 6;
                            radius = 1.5f;
                            amount = 20;
                        }},
                        new DrawRegion(),
                        new DrawLiquidOutputs(),
                        new DrawGlowRegion() {{
                            alpha = 0.7f;
                            color = Color.valueOf("c4bdf3");
                            glowIntensity = 0.3f;
                            glowScale = 6f;
                        }}
                );
                ambientSound = Sounds.loopElectricHum;
                ambientSoundVolume = 0.08f;

                regionRotated1 = 3;
                consumeItem(Items.graphite,1);
                outputLiquids = LiquidStack.with(GG_Liquids.oo, 16f / 60, Liquids.hydrogen, 24f / 60);
                liquidOutputDirections = new int[]{1, 3};
            }};
        separator = new Separator("矿渣分离机"){{//配方要改
            requirements(Category.crafting, with(Items.silicon, 30, Items.graphite, 25));
            results = with(
                    Items.copper, 5,
                    Items.lead, 3,
                    Items.graphite, 2,
                    Items.titanium, 2,
                    Items.beryllium,2
            );
            hasPower = true;
            craftTime = 75f;
            size = 2;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(), new DrawRegion("-spinner", 3, true), new DrawDefault());
            consumePower(2.1f);
            consumeLiquid(Liquids.slag, 2f / 6f);

            //drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(), new DrawRegion("-spinner", 3, true), new DrawDefault());
        }};

        pulverizer = new GenericCrafter("pulverizer"){{
            requirements(Category.crafting, with(Items.copper, 30, Items.lead, 25));
            outputItem = new ItemStack(GGItems.Sijingti, 3);
            craftEffect = Fx.pulverize;
            craftTime = 40f;
            updateEffect = Fx.pulverizeSmall;
            hasItems = hasPower = true;
            drawer = new DrawMulti(new DrawDefault(), new DrawRegion("-rotator"){{
                spinSprite = true;
                rotateSpeed = 2f;
            }}, new DrawRegion("-top"));
            ambientSound = Sounds.none;
            ambientSoundVolume = 0.025f;

            consumeItem(Items.silicon, 2);//GGItems.Sifenmo
            consumePower(0.50f);
        }};
        Sichunghua=new GenericCrafter("硅纯化器"){{
            requirements(Category.crafting, with(Items.graphite,24, Items.titanium, 25, Items.silicon, 10));
            size=2;
            health=200;
            hasPower = true;
            hasItems = true;
            this.craftTime=50f;
            consumeItem(GGItems.Sijingti,3);
            consumePower(100/60f);
            outputItem=new ItemStack(Items.silicon,2);
        }};
        cultivator = new AttributeCrafter("cultivator"){{
            requirements(Category.production, with(Items.graphite,24, GGItems.Sijingti, 25, Items.silicon, 10,Items.plastanium,12));
            outputItem = new ItemStack(Items.sporePod, 5);
            craftTime = 100;
            size = 3;
            hasLiquids = true;
            hasPower = true;
            hasItems = true;

            liquidCapacity = 80f;
            craftEffect = Fx.none;
            envRequired |= Env.spores;
            attribute = Attribute.spores;

            legacyReadWarmup = true;
            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile(Liquids.water),
                    new DrawDefault(),
                    new DrawCultivator(),
                    new DrawRegion("-top")
            );
            maxBoost = 2f;

            consumePower(320f / 60f);
            consumeLiquid(Liquids.water, 54f / 60f);
        }};
        TnmXinpian=new factory("纳米芯片打印机"){{
            hasItems=hasPower=true;
            requirements(Category.crafting,with(Items.beryllium,74,Items.graphite,57,Items.lead,74,GGItems.Sijingti,58,Items.metaglass,60,Items.titanium,40));
            size=4;
            craftTime=60f;
            craftEffect = Fx.pulverizeMedium;
            consumePower(73/60f);
            addInput(Items.graphite,4,Items.titanium,6,Items.silicon,8);//配方A：4石墨，6钛，8硅;配方B：6石墨，4铍，12硅
            addInput(Items.graphite,6,Items.beryllium,4,Items.silicon,12);
            outputItem=new ItemStack(GGItems.TanNaMiHeXing,1);
        }};
        Ctiqu=new GenericCrafter("碳提取器"){{
            hasItems=hasLiquids=hasPower=true;
            requirements(Category.crafting,with(Items.copper,26,Items.beryllium,14,Items.graphite,13,Items.lead,34,GGItems.Sijingti,18));
            size=3;
            craftTime=30f;
            consumePower(73/30f);
            consumeItem(GGItems.ThuaKuangShi,5);
            outputItem=new ItemStack(Items.graphite,4);
            outputLiquid=new LiquidStack(Liquids.slag, (float) 2 /3);
        }};
        sitichun=new GenericCrafter("guitichunchang"){{
            requirements(Category.crafting,with(Items.copper,43,Items.beryllium,38,GGItems.Sijingti,50,Items.lead,32,Items.plastanium,12));
            size=3;
            itemCapacity =50;//最大物品容量
            health=500;
            craftTime=120;
            hasItems=true;
            hasPower=true;
            craftEffect = Fx.smeltsmoke;
            drawer = new DrawMulti(new DrawDefault(), new DrawFlame(Color.valueOf("ffce4a")));
            consumePower(50/6f);
            consumeItems(with(Items.sporePod,6,Items.coal,16,Items.lead,18,Items.sand,22));
            outputItem=new ItemStack(Items.silicon,20);

        }};
        ksbl=new GenericCrafter("kesug"){{
            itemCapacity = 30;//最大物品容量
            requirements(Category.crafting,with(Items.copper,23,Items.beryllium,28,GGItems.Sijingti,25,Items.lead,12,Items.plastanium,12));
            hasItems=true;
            hasLiquids=hasPower=true;
            size=4;
            craftEffect = Fx.smeltsmoke;
            drawer = new DrawMulti(new DrawDefault(), new DrawFlame(Color.valueOf("ffef99")));
            ambientSound = Sounds.wind;//环境音效
            ambientSoundVolume = 0.07f;//环境音效大小
            health=1350;
            craftTime=50;
            consumePower(3.56f);
            consumeItems(with(Items.plastanium, 2,Items.metaglass,3));
            consumeLiquid(Liquids.water,(float) 1 /6);
            outputLiquid=new LiquidStack(GG_Liquids.zaisuye, (float) 2 /60);
        }};
        baozif=new factory("baozifsc"){{
            this.canMirror=false;
            size=2;
            health=1000;
            craftTime=90f;
            itemCapacity=20;
            requirements(Category.crafting,with(GGItems.Sijingti,22,Items.metaglass,16,Items.lead,24,Items.copper,25));
            hasPower = hasItems =hasLiquids =true;
            consumePower(0.75f);//*60
           consumeItem(Items.sporePod,3);
            craftEffect = Fx.none;
            rotate = false;//贴图不转
            drawer = new DrawMulti(
                    new DrawRegion("-bottom"){{
                        buildingRotate = false;//贴图不转
                    }},
                    new DrawPistons(){{
                        sinMag = 1f;
                    }},
                    new DrawDefault(),
                    new DrawLiquidRegion(),
                    new DrawRegion("-top"){{buildingRotate = false;}}
            );

            outputLiquid=new LiquidStack(Liquids.oil,0.6667f);
        }};
        surgeAlloyF = new factory("surgeslloy") {{
            health =260;
            size =4;
            craftTime=120f;
            itemCapacity=20;
            requirements(Category.crafting,with(GGItems.Sijingti,22,Items.graphite,35,Items.plastanium,14,Items.copper,35,Items.carbide,12));
            hasPower = hasItems =hasLiquids =true;
            consumePower(7.5f);//*60
            drawer = new DrawMulti(new DrawDefault(), new DrawFlame());
            addInput(Items.lead,4,Items.beryllium,4,GGItems.Sijingti,2,Liquids.water,14/60f);
            addInput(Items.lead,4,Items.beryllium,4,Items.silicon,5,Liquids.water,21/60f);
            outputItem =new ItemStack(Items.surgeAlloy,2);
            researchCostMultiplier = 0.6f;
        }};
        SGfacto=new factory("plastanium-factory"){{
            health = 180;
            size=3;
            craftTime=80f;
            itemCapacity=20;
            requirements(Category.crafting, with(Items.silicon,34, GGItems.Sijingti,32,Items.tungsten,27,Items.graphite,24,GGItems.ThuaKuangShi,13));
            hasPower = hasItems =hasLiquids =true;
            consumePower(3.6f);
            addInput(Items.beryllium,5,Liquids.ozone,8/60f);
            addInput(Items.beryllium,5,Liquids.oil ,1/6f);
            outputItem = new ItemStack(Items.plastanium,2);
            outputLiquid =new LiquidStack(GG_Liquids.os,10/6f);
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(), new DrawDefault());
        }};

        SmallGlassKiln=new factory("smallkiln") {{
size=2;
            requirements(Category.crafting, with(Items.silicon,16,Items.beryllium,36,Items.lead,17,Items.graphite,24));
            health = 320;
            craftTime = 60f;
            itemCapacity = 20;//最大物品容量
            hasPower = hasItems = true;
            consumePower(1.8f);
            addInput(Items.sand,1,Items.lead,1);
            outputItem=new ItemStack(Items.metaglass,1);
            outputLiquid=new LiquidStack(Liquids.water,0.05f);
        }};
    }
}
/* 1. scrap -> 碎片
 2. copper -> 铜
 3. lead -> 铅
 4. graphite -> 石墨
 5. coal -> 煤
 6. titanium -> 钛
 7. thorium -> 钍
 8. silicon -> 硅
 9. plastanium -> 塑钢
10. phaseFabric -> 相织物质
11. surgeAlloy -> 冲击合金
12. sporePod -> 孢子囊
13. sand -> 沙
14. blastCompound -> 爆炸化合物
15. pyratite -> 燃烧混合物
16. metaglass -> 金属玻璃
17. beryllium -> 铍（注意：原版没有，但mod常用）
18. tungsten -> 钨（原版没有，但mod常用）
19. oxide -> 氧化物（原版没有，但mod常用）
20. carbide -> 碳化物（原版没有，但mod常用）
21. fissileMatter -> 裂变物质（原版没有，但mod常用）
22. dormantCyst -> 休眠孢囊（原版没有，但根据意思翻译）*/