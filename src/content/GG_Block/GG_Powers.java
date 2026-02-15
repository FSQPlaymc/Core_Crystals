package content.GG_Block;

import api.block.power.GG_ConsumeGenerator;
import api.block.power.NC_change_power;
import api.block.power.NC_power;
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
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.power.ImpactReactor;
import mindustry.world.blocks.power.NuclearReactor;
import mindustry.world.blocks.power.SolarGenerator;
import mindustry.world.consumers.ConsumeItemExplode;
import mindustry.world.consumers.ConsumeItemFlammable;
import mindustry.world.draw.*;

import static mindustry.type.ItemStack.with;

public class GG_Powers {
    public static ImpactReactor baoranchongji;
    public static NC_power ffff,CCCC;
    public  static ConsumeGenerator reanshaoshi,fenlifadian;
    public static SolarGenerator solarPanel;
    public static NuclearReactor hefanyingdui,jinghuaqi;
    public static void Power(){
        CCCC=new NC_power("CCCC"){{
            requirements(Category.power, with(Items.lead, 20, Items.silicon, 32));
            baseheat=1.0f;
            ambientSound = Sounds.loopThoriumReactor;
            ambientSoundVolume = 0.24f;
            size=1;
            hasItems=true;
            itemCapacity=10;
            health = 700;
            itemDuration = 360f;
            powerProduction = 15f;
            heating = 0.02f;
            this.basepower=1.3f;
            drawer = new DrawMulti(new DrawDefault(),  new DrawRegion("-top"){{
                buildingRotate = false;//贴图不转
            }});
            addInput(GGItems.fuel_BUT,1,0,GGItems.depleted_fuel_BUT,1,1);
            addInput(GGItems.fuel_uranium_233,1,0,GGItems.depleted_fuel_uranium_233,1,1);
            addInput(GGItems.fuel_uranium_235,1,0,GGItems.depleted_fuel_uranium_235,1,1);
            consumeLiquid(Liquids.cryofluid, heating / coolantPower).update(false);
        }};
        baoranchongji=new ImpactReactor("爆燃冲击反应堆"){{
            size=4;
            health = 900;
            powerProduction = 130f;
            itemDuration = 140f;
            ambientSound = Sounds.loopPulse;
            ambientSoundVolume = 0.07f;
            liquidCapacity = 80f;
            requirements(Category.power, with(Items.lead, 500, Items.silicon, 300, Items.graphite, 400, Items.thorium, 100, Items.surgeAlloy, 250, Items.metaglass, 250));

            consumePower(7200/60f);
            consumeLiquids(LiquidStack.with(Liquids.oil, 5/60f,GG_Liquids.os,5/6f));
        }};
        fenlifadian=new GG_ConsumeGenerator("分离发电室"){{
            requirements(Category.power, with(Items.lead, 300, Items.silicon, 200, Items.graphite, 150, Items.thorium, 150, Items.metaglass, 50,GGItems.TanNaMiHeXing,200));
            size=4;
            itemDuration = 60f;
            hasItems=hasLiquids=true;
            powerProduction=10000/60f;
            outputItems= ItemStack.with(Items.plastanium,2,Items.metaglass,1);
            consumeLiquid(GG_Liquids.zaisuye,10/60f);
        }};
        jinghuaqi=new NuclearReactor("晶化器"){{
            requirements(Category.power, with(Items.copper,20,GGItems.Sijingti,20));
            size=2;
            this.fuelItem = GGItems.Sijingti;//消耗物
            itemCapacity =10;//最大物品容量
            health=300;
            heating=0f;
            itemDuration = 360f;
            this.explosionRadius = 0;
            this.explosionDamage = 0;
            powerProduction = 18/6f;
            consumeItem(GGItems.Sijingti,1);
        }};
        hefanyingdui=new NC_change_power("核反应堆"){{
            requirements(Category.power, with(Items.lead, 300, Items.silicon, 200, Items.graphite, 150, Items.thorium, 150, Items.metaglass, 50,GGItems.TanNaMiHeXing,200));
            ambientSound = Sounds.loopThoriumReactor;
            ambientSoundVolume = 0.74f;
            size = 5;
            health = 1700;
            itemCapacity =40;//最大物品容量
            itemDuration = 360f;
            powerProduction = 40000/6f;
            heating = 0.09f;
            this.explosionRadius = 39;
            this.explosionDamage = 150000;
            consumeItems(with(Items.thorium,4,Items.graphite,2));
            consumeLiquid(Liquids.water, heating / coolantPower).update(false);
        }};
        ffff=new NC_power("ffff"){{
            requirements(Category.power, with(Items.lead, 20, Items.silicon, 32));
            ambientSound = Sounds.loopThoriumReactor;
            ambientSoundVolume = 0.24f;
            size=1;
            outputItem=new ItemStack(Items.silicon,1);
            health = 700;
            itemDuration = 360f;
            powerProduction = 15f;
            heating = 0.02f;
            this.basepower=1.3f;
            drawer = new DrawMulti(new DrawDefault(),  new DrawRegion("-top"){{
                buildingRotate = false;//贴图不转
            }});
            consumeItem(Items.thorium);
            consumeLiquid(Liquids.cryofluid, heating / coolantPower).update(false);
        }};
         solarPanel = new SolarGenerator("GG-solar-panel") {{
            requirements(Category.power, with(Items.lead, 20, Items.silicon, 32,GGItems.Sijingti,20));
            powerProduction = 0.72f;
            this.size=2;
        }};
        reanshaoshi = new ConsumeGenerator("steam-generator"){{
            requirements(Category.power, with(Items.copper, 35, Items.graphite, 25, Items.lead, 40, Items.silicon, 30));
            powerProduction = 5.5f;
            itemDuration = 90f;
            consumeLiquid(Liquids.water, 0.1f);
            hasLiquids = true;
            size = 2;
            generateEffect = Fx.generatespark;

            ambientSound = Sounds.loopThoriumReactor;
            ambientSoundVolume = 0.06f;
            // 消耗物品条件系统：
            consume(new ConsumeItemFlammable());  // 要求输入物品具有可燃性属性
            consume(new ConsumeItemExplode());    // 要求输入物品具有爆炸性属性

            /*drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawWarmupRegion(),
                    new DrawRegion("-turbine"){{
                        rotateSpeed = 2f;
                    }},
                    new DrawRegion("-turbine"){{
                        rotateSpeed = -2f;
                        rotation = 45f;
                    }},
                    new DrawRegion("-cap"),
                    new DrawLiquidRegion()
            );*/
        }};
    }
}
