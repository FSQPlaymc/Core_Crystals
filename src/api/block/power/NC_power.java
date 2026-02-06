package api.block.power;

import api.Item_void.GGItemStack;
import api.block.ConsumeRecipe;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import arc.struct.EnumSet;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Scaling;
import arc.util.Strings;
import arc.util.Time;
import content.GGItems;
import content.GG_Block.GG_Powers;
import content.GG_Block.GG_walls;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.core.UI;
import mindustry.ctype.UnlockableContent;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.type.PayloadStack;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.blocks.power.NuclearReactor;
import mindustry.world.meta.*;

import java.util.Arrays;

import static content.GG_Block.GG_walls.cs;
import static mindustry.world.meta.StatValues.withTooltip;

public class NC_power extends NuclearReactor {
    //------------------------------------------------------新加内容-----------------------------------------------------------------
        public Seq<Recipe_NC> recipes = new Seq<>();
        public boolean HaveOutputItems=true;//是否有物品输出
        public boolean AutomaticOutPutLiquids=true;//流体自动向周围输出.返回true液体管道与工厂有贴图
        public static int P_recipeIndex;
        //public Floatf<Building> multiplier_2 = b -> 1f;

        public void addInput(Object...objects) {
            Recipe_NC recipe = new Recipe_NC(objects);
        recipes.add( recipe);
    }
        public boolean outputsItems(){//返回true传送带与工厂有贴图
        return HaveOutputItems;
    }

        public StatValue display() {
        return table -> {
            table.row();
            table.table(cont -> {
                for (int i = 0; i < recipes.size; i++){
                    Recipe_NC recipe = recipes.get(i);
                    int finalI = i;
                    cont.table(t -> {
                        t.left().marginLeft(12f).add("[accent][" + (finalI + 1) + "]:[]").width(48f);
                        t.table(inner -> {
                            inner.table(row -> {
                                row.left();
                                recipe.inputItem.each(stack -> row.add(display(stack.GG_NC_item, stack.amount, 1 / recipe.boostScl)));
                                recipe.inputLiquid.each(stack -> row.add(StatValues.displayLiquid(stack.liquid, stack.amount * Time.toSeconds, true)));
                                //recipe.inputPayload.each(stack -> row.add(display(stack.item, stack.amount, craftTime / recipe.boostScl)));
                            }).growX();
                            if (inner.getPrefWidth() > 320f) inner.row();
                            inner.table(row -> {
                                row.left();
                                row.image(Icon.right).size(32f).padLeft(8f).padRight(12f);
                                recipe.outputItem.each(stack -> row.add(display(stack.GG_NC_item, stack.amount, 1 / recipe.boostScl)));
                                recipe.outputLiquid.each(stack -> row.add(StatValues.displayLiquid(stack.liquid, stack.amount * Time.toSeconds, true)));
                                if (outputItems != null) {
                                    for (var stack: outputItems){
                                        row.add(display(stack.item, Mathf.round(stack.amount * recipe.craftScl), 1 / recipe.boostScl));
                                    }
                                }
                            }).growX();
                        });
                    }).fillX();
                    cont.row();
                }
            });
        };
    }

        public static Table display(UnlockableContent content, float amount, float timePeriod){
        Table table = new Table();
        Stack stack = new Stack();

        stack.add(new Table(o -> {
            o.left();
            o.add(new Image(content.uiIcon)).size(32f).scaling(Scaling.fit);
        }));

        if(amount != 0){
            stack.add(new Table(t -> {
                t.left().bottom();
                t.add(amount >= 1000 ? UI.formatAmount((int)amount) : Strings.autoFixed(amount, 2)).style(Styles.outlineLabel);
                t.pack();
            }));
        }

        withTooltip(stack, content);

        table.add(stack);
        table.add((content.localizedName + "\n") + "[lightgray]" + Strings.autoFixed(amount / (timePeriod / 60f), 2) + StatUnit.perSecond.localized()).padLeft(2).padRight(5).style(Styles.outlineLabel);
        return table;
    }//------------------------------------------------------新加内容-----------------------------------------------------------------
    public static Thread t;
    private static Building OutPutBuilding;
    @Nullable
    public ItemStack outputItem;
    @Nullable
    public ItemStack[] outputItems;
    public float baseheat;
    public float basepower;
    public float cold;
    public final int timerFuel;
    public Color lightColor;
    public Color coolColor;
    public Color hotColor;
    public float itemDuration;
    public float heating;
    public float smokeThreshold;
    public float flashThreshold;
    public float coolantPower;
    public Item fuelItem;
    public TextureRegion topRegion;
    public TextureRegion lightsRegion;
    public NC_power(String name) {
        super(name);
        this.cold=1;
        this.baseheat=1.0f;
        this.basepower=1.0f;
        update = true;
        solid = true;
        configurable = true;
        this.timerFuel = this.timers++;
        this.lightColor = Color.valueOf("7f19ea");
        this.coolColor = new Color(1.0F, 1.0F, 1.0F, 0.0F);
        this.hotColor = Color.valueOf("ff9575a3");
        this.heating = 0.01F;
        this.smokeThreshold = 0.3F;
        this.flashThreshold = 0.46F;
        this.coolantPower = 0.5F;
        this.fuelItem = Items.thorium;
        this.itemCapacity = 30;
        this.liquidCapacity = 30.0F;
        this.hasItems = true;
        this.hasLiquids = true;
        this.rebuildable = false;
        this.emitLight = true;
        this.flags = EnumSet.of(new BlockFlag[]{BlockFlag.reactor, BlockFlag.generator});
        this.schematicPriority = -5;
        this.envEnabled = -1;
        this.explosionShake = 6.0F;
        this.explosionShakeDuration = 16.0F;
        this.explosionRadius = 19+DWS*3;    // 爆炸范围（半径）
        this.explosionDamage = 5000+500*DWS;  // 爆炸伤害值
        this.explodeEffect = Fx.reactorExplosion;
        this.explodeSound = Sounds.explosionReactor;//音效
        // 添加基础发电量设置（关键！）
        this.powerProduction = 100f; // 示例值，可根据平衡调整
        this.dumpTime=4;
        this.rotate = false;//贴图不转
        consume(new ConsumeRecipe(NC_power.NC_powerBuid::getRecipe, NC_power.NC_powerBuid::getDisplayRecipe));
    }
    // 定义计时器ID（可自定义，只要唯一即可）
    private static final int UPDATE_TIMER = 1;
    // 定义调用间隔（单位： ticks，60ticks = 1秒）
    private static final float UPDATE_INTERVAL = 60f; // 1秒调用一次
    private int factoryX,CV= 0;
    private int factoryY = 0;
    private int DWS,smk,jsmk,ProjectedHeat;//单元数,石墨块,产热
    private float SQQ,H;
    private float fare;
    private float xiaolu =0;//冷却量
    private float SDQ;
    @Override
    public void init() {//过滤不正常
        super.init();//1. itemFilter 数组初始化itemFilter = new boolean[content.items().size];
        //这创建了一个布尔数组，长度等于游戏中所有物品的数量。这个数组用于标记哪些物品可以被该方块接受。
        if (this.outputItems == null && this.outputItem != null) {
            this.outputItems = new ItemStack[]{this.outputItem};
        }
        if (this.outputItems != null) {
            this.hasItems = true;
        }
        outputsLiquid = AutomaticOutPutLiquids;
        recipes.each(recipe -> {
            recipe.RecipeIn.each(stack -> itemFilter[stack.item.id] = true);
            recipe.inputLiquid.each(stack -> liquidFilter[stack.liquid.id] = true);//设置过滤判断需要物品或流体
            //recipe.outputItem.each(stack -> itemFilter[stack.item.id] = true);
            //recipe.outputLiquid.each(stack -> liquidFilter[stack.liquid.id] = true);
            //recipe.inputPayload.each(stack -> payloadFilter.add(stack.item));
        });
    }

    public void setStats() {
        this.stats.timePeriod = this.itemDuration;
        super.setStats();
        if (this.hasItems && this.itemCapacity > 0 || this.outputItems != null) {
            this.stats.add(Stat.productionTime, this.itemDuration / 60.0F, StatUnit.seconds);
        }

        if (this.outputItems != null) {
            this.stats.add(Stat.output, StatValues.items(this.itemDuration, this.outputItems));
        }
        super.setStats();
        stats.add(Stat.input, display());
        stats.remove(Stat.output);
    }
    @Override
    public void setBars(){
        super.setBars();
        addBar("heats", (NC_powerBuid entitys)
                -> new Bar("bar.heats{{{{："+SDQ, Pal.lightOrange, () -> entitys.heat)
        );
    }
    public class NC_powerBuid extends NuclearReactorBuild {
        public float SQl;
        public void jance() {
            int BasalHeatProduction=0;
            if (recipeIndex>-1) for (GGItemStack input : recipes.get(recipeIndex).inputItem) {BasalHeatProduction=input.GG_NC_item.BasalHeatProduction;}
            // 获取建筑所在的主 Tile 坐标
            int tileX = tile.x;  // 网格坐标 X
            int tileY = tile.y;  // 网格坐标 Y
            ////System.out.println(tileX);
            ////System.out.println(tileY);
            // 获取建筑中心的世界坐标（像素）
            float worldX = x;    // 世界坐标 X (像素)
            float worldY = y;    // 世界坐标 Y (像素)

            // 获取当前 Tile 的世界坐标（左上角）
            float tileWorldX = tile.worldx();
            float tileWorldY = tile.worldy();

            Building neighborL = Vars.world.build(tileX - 2, tileY); // 左
            Building neighborS = Vars.world.build(tileX, tileY + 2); // 上
            int checkX = tileX;
            int checkY = tileY;
            if (neighborL != null && neighborL.block == cs) {
                // 邻居是 GG_walls.cs 方块
                // 循环检测左侧N个方块
                for (int i = 1; i <= 64; i++) {
                    checkX = checkX - 1; // 左侧第i个位置
                    Building neighbor = Vars.world.build(checkX, tileY);

                    // 检查方块是否存在且为GG_walls.cs
                    if (neighbor != null &&  (neighbor.block == cs || neighbor.block == GG_walls.glass)) {
                        factoryX=factoryX-1;
                        // 在这里添加对每个检测到的工厂的操作
                        // 例如：GG_walls.GG_wallsBuild factory = (GG_walls.GG_wallsBuild) neighbor;
                        // factory.doSomething();
                    } else {
                        checkX = checkX +1;
                        // 遇到非工厂方块或空位置，停止检测
                        break;
                    }
                }
            }else {
                // 循环检测右侧N个方块
                for (int i = 1; i <= 64; i++) {
                    checkX = checkX + 1; // 左侧第i个位置
                    Building neighbor = Vars.world.build(checkX, tileY);
                    System.out.println(checkX +","+ tileY);
                    System.out.println(neighbor);
                    // 检查方块是否存在且为GG_walls.cs
                    if (neighbor != null &&  (neighbor.block == cs || neighbor.block == GG_walls.glass)) {
                        factoryX=factoryX+1;
                        // 在这里添加对每个检测到的工厂的操作
                        // 例如：GG_walls.GG_wallsBuild factory = (GG_walls.GG_wallsBuild) neighbor;
                        // factory.doSomething();
                    } else {
                        checkX = checkX -1;
                        // 遇到非工厂方块或空位置，停止检测
                        break;
                    }
                }
            }System.out.println(neighborS);if (neighborS != null && neighborS.block == cs) {
                // 邻居是 GG_walls.cs 方块
                // 循环检测上侧N个方块
                for (int i = 1; i <= 64; i++) {
                    checkY = checkY + 1; // 上侧第i个位置
                    Building neighbor = Vars.world.build(tileX, checkY);
                    // 检查方块是否存在且为GG_walls.cs
                    System.out.println(tileX+","+ checkY);
                    System.out.println(neighbor);
                    if (neighbor != null &&  (neighbor.block == cs || neighbor.block == GG_walls.glass)) {
                        factoryY = factoryY +1;
                        // 在这里添加对每个检测到的工厂的操作
                        // 例如：GG_walls.GG_wallsBuild factory = (GG_walls.GG_wallsBuild) neighbor;
                        // factory.doSomething();
                    } else {
                        checkY = checkY -1;
                        // 遇到非工厂方块或空位置，停止检测
                        break;
                    }
                }
            }else {
                // 循环检测下侧N个方块
                for (int i = 1; i <= 64; i++) {
                    checkY = checkY - 1; // 下侧第i个位置
                    Building neighbor = Vars.world.build(tileX, checkY);
                    // 检查方块是否存在且为GG_walls.cs
                    if (neighbor != null &&  (neighbor.block == cs || neighbor.block == GG_walls.glass)) {
                        factoryY = factoryY -1;
                        // 在这里添加对每个检测到的工厂的操作
                        // 例如：GG_walls.GG_wallsBuild factory = (GG_walls.GG_wallsBuild) neighbor;
                        // factory.doSomething();
                    } else {
                        checkY = checkY +1;
                        // 遇到非工厂方块或空位置，停止检测
                        break;
                    }
                }
            }
            int FL = 0;//宽
            FL=factoryX>0?factoryX:-factoryX;
            //if (factoryX<0){
            //    FL = factoryX*-1;}else{
            //    FL =factoryX;}
            FL=FL+1;
            int FS = 0;//高
            if (factoryY <0){
                FS = factoryY *-1;}else{
                FS =factoryY;}
            FS=FS+1;

            // 再创建新数组
            int[][] asdf;
            asdf =new int[FS][FL];
            DWS=0;
            SQQ=0f;
            int Minx,Miny;
            int L=0;
            int S=0;
            int X= checkX;
            int Y= checkY;
            int [] cx,cy;
            int ceshi=0;
            cx=new int[1028];
            cy=new int[1028];
            System.out.println("??????????"+FS);
            System.out.println("??????????"+FL);
            if (FL!=1&&FS!=1) {
                int w, s, a, d,l=0,m=0,n=0;
                boolean tj1, tj2;
                 smk = 0;
                 Minx=Math.min(tileX, checkX);
                 Miny=Math.min(tileY, checkY);
                 X=Minx;Y=Miny;
                 for (int[] ab :asdf){
                     for (int e:ab){
                         Building neighboru = Vars.world.build(X, Y);
                         if (neighboru != null && (neighboru.block == cs || neighboru.block == GG_walls.glass )) {
                             asdf[n][m] = 90;
                         }else if (neighboru != null &&(neighboru.block == GG_Powers.ffff||neighboru.block ==GG_Powers.CCCC)){
                             asdf[n][m] = 99;
                         }else if (neighboru != null && neighboru.block == GG_walls.SL) {
                             asdf[n][m] = 1;
                             SQQ += 60F;//测试用
                             //this.heat += 1+((DWS-1)*0.8) * NC_power.this.heating * Math.min(this.delta(), 4.0F);
                             //System.out.println(SQQ);
                         } else if (neighboru != null && neighboru.block == GG_walls.fanying) {
                             asdf[n][m] = 80;
                             DWS++;
                             //System.out.println(DWS);
                         } else if (neighboru != null && neighboru.block == GG_walls.jansuji) {//石墨
                             asdf[n][m] = 81;
                             cx[ceshi]=n;
                             cy[ceshi]=m;
                             ceshi++;
                         } else if (neighboru != null && neighboru.block == GG_walls.hongshi) {
                             asdf[n][m] = 2;
                         } else if (neighboru != null && neighboru.block == GG_walls.shiying) {
                             asdf[n][m] = 4;
                         } else if (neighboru != null && neighboru.block == GG_walls.qinjingshi) {
                             asdf[n][m] = 6;
                         } else if (neighboru != null && neighboru.block == GG_walls.ynishi) {
                             asdf[n][m] = 8;
                         } else if (neighboru != null && neighboru.block == GG_walls.linbin) {
                             asdf[n][m] = 10;
                         } else if (neighboru != null && neighboru.block == GG_walls.lubaoshi) {
                             asdf[n][m] = 12;
                         } else {
                             asdf[n][m] = -1;
                             //System.out.println("?");
                         }
                         m++;X++;
                     }
                     n++;m=0;Y++;X=Minx;
                 }
                 Y=Miny;n=0;
                if (ceshi>0){
                    for (int k=0;k<ceshi;k++){
                        S=cx[k];
                        L=cy[k];
                        smk++;
                        //System.out.println(cx);
                        w = asdf[S - 1][L];
                        s = asdf[S + 1][L];
                        a = asdf[S][L - 1];
                        d = asdf[S][L + 1];
                        if (d == 80 || a == 80 || s == 80 || w == 80) {
                            jsmk++;
                            asdf[S][L] = 91;
                        }
                    }
                }
                System.out.println("数组：" + Arrays.deepToString(asdf));
                xiaolu = fare = 0;
                for (int[] ab :asdf) {
                    for (int e : ab) {
                        tj1 = tj2 = false;
                        if (m == 0 || n == 0 || m == (FL - 1) || n == (FS - 1)) {
                            if (e == 90 || e == 99) {
                                if (e == 99) {
                                    l++;
                                    if (l > 2) {
                                        SDQ = 99999999;
                                        DWS = 0;
                                        break;
                                    }
                                }
                            }
                        }
                        switch (e) {
                            case 80: {
                                CV = 0;
                                w = asdf[n - 1][m];
                                if (w == 80 || w == 81 || w == 91) CV++;
                                s = asdf[n + 1][m];
                                if (s == 80 || s == 81 || s == 91) CV++;
                                a = asdf[n][m - 1];
                                if (a == 80 || a == 81 || a == 91) CV++;
                                d = asdf[n][m + 1];
                                if (d == 80 || d == 81 || d == 91) CV++;
                                xiaolu += (CV + 1) * NC_power.this.basepower;
                                fare += ((float) ((CV + 1) * (CV + 2)) / 2) * NC_power.this.baseheat*BasalHeatProduction;
                                System.out.println("没问题");
                            }break;
                            case 4:{//shiying
                                w = asdf[n - 1][m];
                                s = asdf[n + 1][m];
                                a = asdf[n][m - 1];
                                d = asdf[n][m + 1];
                                if (d == 91 || w == 91 || s == 91 || a == 91) {
                                    asdf[n][m] = 5;
                                    tj1 = true;
                                }
                                if (tj1) {
                                    SQQ += GG_walls.shiying.colod;
                                }
                            }break;
                            case 2:{//hongshi
                                w = asdf[n - 1][m];
                                s = asdf[n + 1][m];
                                a = asdf[n][m - 1];
                                d = asdf[n][m + 1];
                                if (d == 80 || w == 80 || s == 80 || a == 80) {
                                    asdf[n][m] = 3;
                                    tj1 = true;
                                }
                                if (tj1) {
                                    SQQ += GG_walls.hongshi.colod;
                                }
                            }break;
                            case 6:{//qinjingshi
                                w = asdf[n - 1][m];
                                s = asdf[n + 1][m];
                                a = asdf[n][m - 1];
                                d = asdf[n][m + 1];
                                if (d == 80 || w == 80 || s == 80 || a == 80) {
                                    tj1 = true;
                                }
                                if (d == 90 || w == 90 || s == 90 || a == 90) {
                                    tj2 = true;
                                }
                                if (tj1 && tj2) {
                                    asdf[n][m] = 7;
                                    SQQ += GG_walls.qinjingshi.colod;
                                }
                            }break;
                            case 8:{//yinshi
                                CV = 0;
                                w = asdf[n - 1][m];
                                if (w == 91) CV++;
                                s = asdf[n + 1][m];
                                if (s == 91) CV++;
                                a = asdf[n][m - 1];
                                if (a == 91) CV++;
                                d = asdf[n][m + 1];
                                if (d == 91) CV++;
                                if (CV > 1) {
                                    asdf[n][m] = 9;
                                    SQQ += GG_walls.ynishi.colod;
                                }
                            }break;
                            case 10:{//linbin
                                CV = 0;
                                w = asdf[n - 1][m];
                                if (w == 80) CV++;
                                s = asdf[n + 1][m];
                                if (s == 80) CV++;
                                a = asdf[n][m - 1];
                                if (a == 80) CV++;
                                d = asdf[n][m + 1];
                                if (d == 80) CV++;
                                if (CV > 1) {
                                    SQQ += GG_walls.linbin.colod;
                                }}break;
                            case 12:{//lubaoshi
                                w = asdf[n - 1][m];
                                s = asdf[n + 1][m];
                                a = asdf[n][m - 1];
                                d = asdf[n][m + 1];
                                if (d == 91 || w == 91 || s == 91 || a == 91) {
                                    tj1 = true;
                                }
                                if (d == 80 || w == 80 || s == 80 || a == 80) {
                                    tj2 = true;
                                }
                                if (tj1 && tj2) {
                                    //asdf[n][m] = 7;
                                    //SQQL += GG_walls.lubaoshi.colod;
                                    SQQ += GG_walls.lubaoshi.colod;
                                }}break;
                        }
                        m++;X++;
                    }
                    System.out.println("循环了"+n);
                    n++;m = 0;Y++;X = Minx;
                }
                fare+=smk*NC_power.this.baseheat;
            }
            ////System.out.println("单元数"+DWS);
            factoryX=0;
            factoryY=0;
            if (DWS>0){
                if (DWS<=15){
                    H=DWS*20;
                }else {
                    H=300;
                }
                if (DWS>15){H+= (float) Math.pow((DWS-15)*20,1.0/1.5);}
            }
        }
        @Override
        public void updateTile(){
            float coldc=SQQ*cold;
            this.productionEfficiency=0.0f;
            //--------------------------------------------------------------------------------
            int fuel=0;
            if (!validRecipe()) updateRecipe();
            P_recipeIndex=recipeIndex;
            if (timer(timerDump, dumpTime / timeScale)) dumpOutputs();
            if (recipeIndex>-1){
                for (GGItemStack stack:recipes.get(recipeIndex).inputItem){
                     fuel = this.items.get(stack.GG_NC_item);
                     System.out.println("燃料"+fuel);
                }
            }
            //--------------------------------------------------------------------------------
            // 计时器逻辑：每隔 UPDATE_INTERVAL 时间触发一次
            // 1. 获取当前燃料（钍）的数量，计算燃料满度（占总容量的比例）
            //int fuel = this.items.get(NC_power.this.fuelItem);上方已做更改
            float fullness = fare;
            this.productionEfficiency = xiaolu; // 发电效率与燃料满度挂钩
            // 2. 燃料燃烧逻辑：若有燃料且反应堆启用，则产生热量并消耗燃料
            if (fuel > 0 && this.enabled) {
                // 热量随燃料满度和时间增加（delta()是本帧耗时，限制最大4ms防止跳变）
                this.heat += fullness * NC_power.this.heating * Math.min(this.delta(), 4.0F);
                double w=jsmk*30;
                System.out.println("减少燃烧时间"+H);
                // 定时消耗燃料：当燃料计时器达到设定值（itemDuration / 时间缩放加单元数）时，消耗1单位燃料
                if (this.timer( (NC_power.this.timerFuel), (float) (NC_power.this.itemDuration-H+w / (this.timeScale)))) {
                    this.consume();
                    this.craft();
                }
            } else {
                // 无燃料或未启用时，发电效率为0
                this.productionEfficiency = 0.0F;
            }
            float asd=coldc* Math.min(this.delta(), 4.0F) * NC_power.this.heating;
            // 原代码：heat -= SQQ;
            heat -= asd; // 关联每帧时间
            // 3. 冷却逻辑：若有冷却液，消耗冷却液并降低热量
            SDQ= fare-coldc;
            if (this.heat > 0.0F) {
                // 计算最大可使用的冷却剂量（不超过当前液体量，且不超过当前热量可冷却的量）
                float maxUsed = Math.min(this.liquids.currentAmount(), this.heat / coolantPower);
                this.heat -= maxUsed * coolantPower; // 热量降低 = 冷却液量 * 冷却效率
                this.liquids.remove(this.liquids.current(), maxUsed); // 消耗对应量的冷却液
            }else {
                heat=0.0f;
            }

            // 4. 烟雾效果：当热量超过烟雾阈值时，随机产生烟雾
            if (this.heat > NC_power.this.smokeThreshold) {
                // 烟雾概率随热量升高而增加（heat越高，smoke值越大，概率越高）
                float smoke = 1.0F + (this.heat - NC_power.this.smokeThreshold) / (1.0F - NC_power.this.smokeThreshold);
                if (Mathf.chance((double)smoke / (double)20.0F * (double)this.delta())) {
                    // 在反应堆范围内随机位置产生烟雾效果
                    Fx.reactorsmoke.at(
                            this.x + Mathf.range((float)(NC_power.this.size * 8) / 2.0F),
                            this.y + Mathf.range((float)(NC_power.this.size * 8) / 2.0F)
                    );
                }
            }

            // 5. 热量限制：确保热量在10~0之间
            if (this.heat>=11)this.heat= 10.99999F;
            // 6. 过热爆炸：当热量接近最大值（≥0.999）时，触发过热事件并销毁反应堆
            if (this.heat >= 10.999F) {
                // 触发全局过热事件
                Events.fire(EventType.Trigger.thoriumReactorOverheat);
                explosionRadius = 19+DWS*3;
                kill(); // 销毁自身（会触发爆炸效果）
            }
            if (timer(UPDATE_TIMER, UPDATE_INTERVAL)) {
                // 定时调用 jance() 方法
                smk=jsmk=0;
                t=new  Thread(new Runnable(){//线程调用
                    @Override
                    public void run() {
                        jance();
                    }
                });
                ////System.out.println("开始\\----------------------------------------------------------");
                t.start();
                ////System.out.println("数量："+fuel);
                //System.out.println(heat);
            }
        }
        public void dumpOutputs() {
            if (NC_power.this.outputItems != null && this.timer(NC_power.this.timerDump, (float) NC_power.this.dumpTime / this.timeScale)) {
                for (ItemStack output : NC_power.this.outputItems) {
                    this.dump(output.item);
                }
            }
            for (int i = 0; i < recipes.size; i++) {//来自下方
                for (GGItemStack outputs : recipes.get(i).outputItem) {
                    if (outputs != null ) {
                        dump(outputs.GG_NC_item);
                    }
                }
            }
        }

//            if (NC_power.this.outputLiquids != null) {
//                for(int i = 0; i < NC_power.this.outputLiquids.length; ++i) {
//                    int dir = NC_power.this.liquidOutputDirections.length > i ? NC_power.this.liquidOutputDirections[i] : -1;
//                    this.dumpLiquid(NC_power.this.outputLiquids[i].liquid, 2.0F, dir);
//                }
//            }
            //------------------------------------------------------新加内容-----------------------------------------------------------------
public int recipeIndex = -1;

        public Recipe_NC getRecipe() {
            if (recipeIndex < 0 || recipeIndex >= recipes.size) return null;
            return recipes.get(recipeIndex);
        }

        public Recipe_NC getDisplayRecipe() {
            if (recipeIndex < 0 && recipes.size > 0) {
                return recipes.first();
            }
            return getRecipe();
        }

        @Override
        public float getPowerProduction() {
            return super.getPowerProduction();
        }

        public void updateRecipe() {
            for (int i = 0; i < recipes.size; i++) {//是指配方数
                boolean valid = true;

                for (GGItemStack input : recipes.get(i).inputItem) {
                    //System.out.println(GGItems.fuel_BUT.id);//23
                    //System.out.println(input.GG_NC_item.id);//26
                    if (items.get(input.GG_NC_item) < input.amount) {
                        valid = false;
                        break;
                    }
                }
/*
                for (LiquidStack input : recipes.get(i).inputLiquid) {
                    if (liquids.get(input.liquid) < input.amount * Time.delta) {
                        valid = false;
                        break;
                    }
                }

                for (PayloadStack input : recipes.get(i).inputPayload) {
                    if (getPayloads().get(input.item) < input.amount) {
                        valid = false;
                        break;
                    }
                }
*/
                if (valid) {
                    recipeIndex = i;
                    return;
                }
            }
            recipeIndex = -1;
        }

        public boolean validRecipe() {
            if (recipeIndex < 0) return false;
            for (GGItemStack input : recipes.get(recipeIndex).inputItem) {
                if (items.get(input.GG_NC_item) < input.amount) {
                    return false;
                }
            }

            for (LiquidStack input : recipes.get(recipeIndex).inputLiquid) {
                if (liquids.get(input.liquid) < input.amount * Time.delta) {
                    return false;
                }
            }

            for (GGItemStack output : recipes.get(recipeIndex).outputItem) {
                if (items.get(output.GG_NC_item) < output.amount) {
                    return false;
                }
            }

            for (PayloadStack input : recipes.get(recipeIndex).inputPayload) {
                if (getPayloads().get(input.item) < input.amount) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public float getProgressIncrease(float baseTime) {
            float scl = 0f;
            if (!(recipeIndex < 0 || recipeIndex >= recipes.size)) scl = recipes.get(recipeIndex).boostScl;
            return super.getProgressIncrease(baseTime) * scl;
        }

        //@Override
        public void craft() {
            //consume();
            if (getRecipe() == null) return;

            for (GGItemStack stack:recipes.get(recipeIndex).outputItem){
                if (stack.GG_NC_item!=null) {
                    System.out.println(stack.GG_NC_item+","+stack.amount);
                    new_offload(stack.GG_NC_item, stack.amount);
                }
            }
            if(outputItems != null){
                for(var output : outputItems){
                    for(int i = 0; i < Mathf.round(output.amount * getRecipe().craftScl); i++){
                        offload(output.item);
                    }
                }
            }
            updateRecipe();
        }

        public void new_offload(Item item ,int amount) {
            produced(item, amount);
            //System.out.print("调用了a"+item);
            for (int i=0;i<amount;i++){
                handleItem(this, item);
                //System.out.print("调用了"+i);
            }
        }

        public boolean shouldConsume(){
            if (recipeIndex>-1){
                for (GGItemStack output:recipes.get(recipeIndex).outputItem) {
                    if (output != null) {
                        if (items.get(output.GG_NC_item) + output.amount > itemCapacity) {
                            return false;
                        }
                    }
                }
            }
            return enabled;
        }
            //------------------------------------------------------新加内容-----------------------------------------------------------------
    }
}