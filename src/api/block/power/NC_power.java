package api.block.power;

import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.EnumSet;
import arc.util.Nullable;
import content.GG_Block.GG_Powers;
import content.GG_Block.GG_walls;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.Bar;
import mindustry.world.blocks.power.NuclearReactor;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

import java.util.Arrays;

import static content.GG_Block.GG_walls.cs;

public class NC_power extends NuclearReactor {
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
    }
    // 定义计时器ID（可自定义，只要唯一即可）
    private static final int UPDATE_TIMER = 1;
    // 定义调用间隔（单位： ticks，60ticks = 1秒）
    private static final float UPDATE_INTERVAL = 90f; // 1秒调用一次
    private int factoryX,CV= 0;
    private int factoryY = 0;
    private int[][] asdf;
    private int checkX;
    private int DWS;//单元数
    private float SQQ;
    private float fare;
    private float xiaolu =0;//冷却量
    private int checkY;
    private float SDQ;
    public boolean outputsItems() {
        return this.outputItems != null;
    }
    public void init() {
        if (this.outputItems == null && this.outputItem != null) {
            this.outputItems = new ItemStack[]{this.outputItem};
        }
        if (this.outputItems != null) {
            this.hasItems = true;
        }
        super.init();
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
    }
    @Override
    public void setBars(){
        super.setBars();
        addBar("heats", (NC_powerBuid entitys)
                -> new Bar("bar.heats{{{{："+SDQ, Pal.lightOrange, () -> entitys.heat)
        );
    }
    public class NC_powerBuid extends NuclearReactorBuild{
        public float SQl;
        public void jance() {
            // 获取建筑所在的主 Tile 坐标
            int tileX = tile.x;  // 网格坐标 X
            int tileY = tile.y;  // 网格坐标 Y
            //System.out.println(tileX);
            //System.out.println(tileY);
            // 获取建筑中心的世界坐标（像素）
            float worldX = x;    // 世界坐标 X (像素)
            float worldY = y;    // 世界坐标 Y (像素)

            // 获取当前 Tile 的世界坐标（左上角）
            float tileWorldX = tile.worldx();
            float tileWorldY = tile.worldy();

            Building neighborL = Vars.world.build(tileX - 2, tileY); // 左
            Building neighborS = Vars.world.build(tileX, tileY + 2); // 上
            if (neighborL != null && neighborL.block == cs) {
                // 邻居是 GG_walls.cs 方块
                // 循环检测左侧N个方块
                for (int i = 2; i <= 64; i=i+2) {
                    checkX = tileX - i; // 左侧第i个位置
                    Building neighbor = Vars.world.build(checkX, tileY);

                    // 检查方块是否存在且为GG_walls.cs
                    if (neighbor != null &&  (neighbor.block == cs || neighbor.block == GG_walls.glass||neighbor.block==GG_Powers.ffff)) {
                        factoryX=factoryX-1;
                        // 在这里添加对每个检测到的工厂的操作
                        // 例如：GG_walls.GG_wallsBuild factory = (GG_walls.GG_wallsBuild) neighbor;
                        // factory.doSomething();
                    } else {
                        checkX=checkX+2;
                        // 遇到非工厂方块或空位置，停止检测
                        break;
                    }
                }
            }else {
                // 循环检测右侧N个方块
                for (int i = 2; i <= 64; i=i+2) {
                    checkX = tileX + i; // 左侧第i个位置
                    Building neighbor = Vars.world.build(checkX, tileY);

                    // 检查方块是否存在且为GG_walls.cs
                    if (neighbor != null &&  (neighbor.block == cs || neighbor.block == GG_walls.glass||neighbor.block==GG_Powers.ffff)) {
                        factoryX=factoryX+1;
                        // 在这里添加对每个检测到的工厂的操作
                        // 例如：GG_walls.GG_wallsBuild factory = (GG_walls.GG_wallsBuild) neighbor;
                        // factory.doSomething();
                    } else {
                        checkX=checkX-2;
                        // 遇到非工厂方块或空位置，停止检测
                        break;
                    }
                }
            }if (neighborS != null && neighborS.block == cs) {
                // 邻居是 GG_walls.cs 方块
                // 循环检测上侧N个方块
                for (int i = 2; i <= 64; i=i+2) {
                    checkY = tileY + i; // 上侧第i个位置
                    Building neighbor = Vars.world.build(tileX, checkY);
                    // 检查方块是否存在且为GG_walls.cs
                    if (neighbor != null &&  (neighbor.block == cs || neighbor.block == GG_walls.glass||neighbor.block==GG_Powers.ffff)) {
                        factoryY = factoryY +1;
                        // 在这里添加对每个检测到的工厂的操作
                        // 例如：GG_walls.GG_wallsBuild factory = (GG_walls.GG_wallsBuild) neighbor;
                        // factory.doSomething();
                    } else {
                        checkY=checkY-2;
                        // 遇到非工厂方块或空位置，停止检测
                        break;
                    }
                }
            }else {
                // 循环检测下侧N个方块
                for (int i = 2; i <= 64; i=i+2) {
                    checkY = tileY - i; // 下侧第i个位置
                    Building neighbor = Vars.world.build(tileX, checkY);

                    // 检查方块是否存在且为GG_walls.cs
                    if (neighbor != null &&  (neighbor.block == cs || neighbor.block == GG_walls.glass||neighbor.block==GG_Powers.ffff)) {
                        factoryY = factoryY -1;
                        // 在这里添加对每个检测到的工厂的操作
                        // 例如：GG_walls.GG_wallsBuild factory = (GG_walls.GG_wallsBuild) neighbor;
                        // factory.doSomething();
                    } else {
                        checkY=checkY+2;
                        // 遇到非工厂方块或空位置，停止检测
                        break;
                    }
                }
            }
            int FL = 0;//宽
            if (factoryX<0){
                FL = factoryX*-1;}else{
                FL =factoryX;}
            FL=FL+1;
            int FS = 0;//高
            if (factoryY <0){
                FS = factoryY *-1;}else{
                FS =factoryY;}
            FS=FS+1;

            // 再创建新数组
            asdf =new int[FS][FL];
            DWS=0;
            SQQ=0f;
            int L=0;
            int S=0;
            int X=checkX;
            int Y=checkY;
            int [] cx,cy;
            int ceshi=0;
            cx=new int[1028];
            cy=new int[1028];
            if (FL!=1&&FS!=1) {
                int w, s, a, d,l=0;
                boolean tj1, tj2;
                int smk = 0;
                for (int i = 1; i < 1024; i++) {
                    Building neighboru = Vars.world.build(X, Y);
//                        System.out.println("方块是：" + neighboru);
//                        System.out.println("FL：" + FL);
//                        System.out.println("FS：" + FS);
//                        System.out.println("数组：" + Arrays.deepToString(asdf));
//                    System.out.println("最大x："+tileX);
//                    System.out.println("最小x："+checkX);
//                    System.out.println("最大Y："+checkY);
//                    System.out.println("x："+X);
//                    System.out.println("y："+Y);
                    if (neighboru != null && (neighboru.block == cs || neighboru.block == GG_walls.glass )) {
                        asdf[S][L] = 90;
                    }else if (neighboru != null &&neighboru.block == GG_Powers.ffff){
                        asdf[S][L] = 99;
                    }else if (neighboru != null && neighboru.block == GG_walls.SL) {
                        asdf[S][L] = 1;
                        SQQ += 0.6F;//测试用
                        //this.heat += 1+((DWS-1)*0.8) * NC_power.this.heating * Math.min(this.delta(), 4.0F);
                        System.out.println(SQQ);
                    } else if (neighboru != null && neighboru.block == GG_walls.fanying) {
                        asdf[S][L] = 80;
                        DWS++;
                        System.out.println(DWS);
                    } else if (neighboru != null && neighboru.block == GG_walls.jansuji) {//石墨
                        asdf[S][L] = 81;
                        cx[ceshi]=S;
                        cy[ceshi]=L;
                        ceshi++;
                    } else if (neighboru != null && neighboru.block == GG_walls.hongshi) {
                        asdf[S][L] = 2;
                    } else if (neighboru != null && neighboru.block == GG_walls.shiying) {
                        asdf[S][L] = 4;
                    } else if (neighboru != null && neighboru.block == GG_walls.qinjingshi) {
                        asdf[S][L] = 6;
                    } else if (neighboru != null && neighboru.block == GG_walls.ynishi) {
                        asdf[S][L] = 8;
                    } else if (neighboru != null && neighboru.block == GG_walls.linbin) {
                        asdf[S][L] = 10;
                    } else if (neighboru != null && neighboru.block == GG_walls.lubaoshi) {
                        asdf[S][L] = 12;
                    } else {
                        asdf[S][L] = -1;
                        System.out.println("?");
                    }
                    L++;
                    if (L > (FL - 1)) {
                        L = 0;
                        S++;
                        if (S > (FS - 1)) {
                            if (ceshi>0){
                                for (int k=0;k<ceshi;k++){
                                    S=cx[k];
                                    L=cy[k];
                                    smk++;
                                    System.out.println(cx);
                                    w = asdf[S - 1][L];
                                    s = asdf[S + 1][L];
                                    a = asdf[S][L - 1];
                                    d = asdf[S][L + 1];
                                    if (d == 80 || a == 80 || s == 80 || w == 80) asdf[S][L] = 91;
                                }
                                cx=cy=null;
                            }
                            System.out.println("数组跳出" + i);
                            break;
                        }
                    }
                    X = X + 2;
                    if (X > tileX) {
                        X = checkX;
                        Y = Y - 2;
                        if (Y < tileY) {
                            System.out.println("坐标跳出");
                            break;
                        }
                    }
                }
                L = 0;
                S = 0;
                xiaolu = fare = 0;
                //for (int i = 1; i < 1024; i++) {
                //tj1 = tj2 = false;
                //int e = asdf[S][L];
                //if (L == 0 || S == 0 || L == (FL - 1) || S == (FS - 1)) {//第一遍
                //if (e == 90||e==99) {
                //if (e==99) {
//                                    l++;
//                                    if (l>2) {
//                                        System.out.println("出问题1s" + S);
//                                        System.out.println("出问题1s" + L);
//                                        System.out.println("出问题1s" + asdf[S][L]);
//                                        SDQ = 99999999;
//                                        DWS = 0;
//                                        break;
//                                    }
//                                }
//                            }else {
//                                System.out.println("出问题" + S);
//                                System.out.println("出问题" + L);
//                                System.out.println("出问题" + asdf[S][L]);
//                                SDQ = 99999999;
//                                DWS = 0;
//                                break;
//                            }
//                        } else if (e == 80) {
//                            CV = 0;
//                            w = asdf[S - 1][L];
//                            if (w == 80 || w == 81 || w == 91) CV++;
//                            s = asdf[S + 1][L];
//                            if (s == 80 || s == 81 || s == 91) CV++;
//                            a = asdf[S][L - 1];
//                            if (a == 80 || a == 81 || a == 91) CV++;
//                            d = asdf[S][L + 1];
//                            if (d == 80 || d == 81 || d == 91) CV++;
//                            xiaolu += (CV + 1) * NC_power.this.basepower;
//                            fare += ((float) ((CV + 1) * (CV + 2)) / 2) * NC_power.this.baseheat;
//                            //System.out.println("没问题");
//                        }
////                        else if (e == 81) {
////                            smk++;
////                            w = asdf[S - 1][L];
////                            s = asdf[S + 1][L];
////                            a = asdf[S][L - 1];
////                            d = asdf[S][L + 1];
////                            if (d == 80 || a == 80 || s == 80 || w == 80) asdf[S][L] = 91;
////                        }
//                        L++;//宽
//                        if (L > (FL - 1)) {
//                            L = 0;
//                            S++;
//                            if (S > (FS - 1)) {//高
//                                System.out.println("数组跳出检测");
//                                System.out.println(Arrays.deepToString(asdf));
//                                S = 0;
//                                break;
//                            }
//                        }
//                    }
                for (int i = 1; i < 1024; i++) {//第二遍
                    tj1 = tj2 = false;
                    int e = asdf[S][L];
                    if (L == 0 || S == 0 || L == (FL - 1) || S == (FS - 1)) {
                        if (e == 90||e==99) {
                            if (e==99) {
                                l++;
                                if (l>2) {
                                    System.out.println("出问题2s" + S);
                                    System.out.println("出问题2s" + L);
                                    System.out.println("出问题2s" + asdf[S][L]);
                                    SDQ = 99999999;
                                    DWS = 0;
                                    break;
                                }
                            }
                        }else {
                            System.out.println("出问题" + S);
                            System.out.println("出问题" + L);
                            System.out.println("出问题" + asdf[S][L]);
                            SDQ = 99999999;
                            DWS = 0;
                            break;
                        }
                    } else if (e == 80) {
                        CV = 0;
                        w = asdf[S - 1][L];
                        if (w == 80 || w == 81 || w == 91) CV++;
                        s = asdf[S + 1][L];
                        if (s == 80 || s == 81 || s == 91) CV++;
                        a = asdf[S][L - 1];
                        if (a == 80 || a == 81 || a == 91) CV++;
                        d = asdf[S][L + 1];
                        if (d == 80 || d == 81 || d == 91) CV++;
                        xiaolu += (CV + 1) * NC_power.this.basepower;
                        fare += ((float) ((CV + 1) * (CV + 2)) / 2) * NC_power.this.baseheat;
                        //System.out.println("没问题");
                    } else if (e == 4) {//shiying
                        w = asdf[S - 1][L];
                        s = asdf[S + 1][L];
                        a = asdf[S][L - 1];
                        d = asdf[S][L + 1];
                        if (d == 91 || w == 91 || s == 91 || a == 91) {
                            asdf[S][L] = 5;
                            tj1 = true;
                        }
                        if (tj1) {
                            SQQ += GG_walls.shiying.colod;
                        }
                    } else if (e == 2) {//hongshi
                        w = asdf[S - 1][L];
                        s = asdf[S + 1][L];
                        a = asdf[S][L - 1];
                        d = asdf[S][L + 1];
                        if (d == 80 || w == 80 || s == 80 || a == 80) {
                            asdf[S][L] = 3;
                            tj1 = true;
                        }
                        if (tj1) {
                            SQQ += GG_walls.hongshi.colod;
                        }
                    } else if (e == 6) {//qinjingshi
                        w = asdf[S - 1][L];
                        s = asdf[S + 1][L];
                        a = asdf[S][L - 1];
                        d = asdf[S][L + 1];
                        if (d == 80 || w == 80 || s == 80 || a == 80) {
                            tj1 = true;
                        }
                        if (d == 90 || w == 90 || s == 90 || a == 90) {
                            tj2 = true;
                        }
                        if (tj1 && tj2) {
                            asdf[S][L] = 7;
                            SQQ += GG_walls.qinjingshi.colod;
                        }
                    } else if (e == 8) {//yinshi
                        CV = 0;
                        w = asdf[S - 1][L];
                        if (w == 91) CV++;
                        s = asdf[S + 1][L];
                        if (s == 91) CV++;
                        a = asdf[S][L - 1];
                        if (a == 91) CV++;
                        d = asdf[S][L + 1];
                        if (d == 91) CV++;
                        if (CV > 1) {
                            asdf[S][L] = 9;
                            SQQ += GG_walls.ynishi.colod;
                        }
                    } else if (e == 10) {//linbin
                        CV = 0;
                        w = asdf[S - 1][L];
                        if (w == 80) CV++;
                        s = asdf[S + 1][L];
                        if (s == 80) CV++;
                        a = asdf[S][L - 1];
                        if (a == 80) CV++;
                        d = asdf[S][L + 1];
                        if (d == 80) CV++;
                        if (CV > 1) {
                            SQQ += GG_walls.linbin.colod;
                        }
                    } else if (e == 12) {//lubaoshi
                        w = asdf[S - 1][L];
                        s = asdf[S + 1][L];
                        a = asdf[S][L - 1];
                        d = asdf[S][L + 1];
                        if (d == 91 || w == 91 || s == 91 || a == 91) {
                            tj1 = true;
                        }
                        if (d == 80 || w == 80 || s == 80 || a == 80) {
                            tj2 = true;
                        }
                        if (tj1 && tj2) {
                            //asdf[S][L] = 7;
                            //SQQL += GG_walls.lubaoshi.colod;
                            SQQ += GG_walls.lubaoshi.colod;
                        }
                    }
                    L++;//宽
                    if (L > (FL - 1)) {
                        L = 0;
                        S++;
                        if (S > (FS - 1)) {//高
                            System.out.println("数组跳出检测2");
                            System.out.println(Arrays.deepToString(asdf));
                            S = 0;
                            break;
                        }
                    }
                }
                fare+=smk*NC_power.this.baseheat;
            }
            System.out.println("单元数"+DWS);
            factoryX=0;
            factoryY=0;
            //if (asdf != null) {// 在创建新数组前，将旧数组引用置空
            asdf = null; // 解除旧数组的引用，使其成为GC回收目标
            //}
        }
        @Override
        public void updateTile(){
            this.dumpOutputs();
            float coldc=SQQ*cold;
            this.productionEfficiency=0.0f;
            //super.updateTile();
            // 计时器逻辑：每隔 UPDATE_INTERVAL 时间触发一次
            // 1. 获取当前燃料（钍）的数量，计算燃料满度（占总容量的比例）
            int fuel = this.items.get(NC_power.this.fuelItem);
            float fullness = fare;
            this.productionEfficiency = xiaolu; // 发电效率与燃料满度挂钩
            // 2. 燃料燃烧逻辑：若有燃料且反应堆启用，则产生热量并消耗燃料
            if (fuel > 0 && this.enabled) {
                // 热量随燃料满度和时间增加（delta()是本帧耗时，限制最大4ms防止跳变）
                this.heat += fullness * NC_power.this.heating * Math.min(this.delta(), 4.0F);

                // 定时消耗燃料：当燃料计时器达到设定值（itemDuration / 时间缩放加单元数）时，消耗1单位燃料
                if (this.timer(NC_power.this.timerFuel, NC_power.this.itemDuration / (this.timeScale))) {
                    this.consume();
                    if (NC_power.this.outputItems != null) {
                        for(ItemStack output : NC_power.this.outputItems) {
                            for(int i = 0; i < output.amount; ++i) {
                                this.offload(output.item);
                            }
                        }
                    }
                    // 内部会减少1单位fuelItem（钍）
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

            // 5. 热量限制：确保热量在0~1之间
            this.heat = Mathf.clamp(this.heat);
            // 6. 过热爆炸：当热量接近最大值（≥0.999）时，触发过热事件并销毁反应堆
            if (this.heat >= 0.999F) {
                // 触发全局过热事件
                Events.fire(EventType.Trigger.thoriumReactorOverheat);
                explosionRadius = 19+DWS*3;
                kill(); // 销毁自身（会触发爆炸效果）
            }
            if (timer(UPDATE_TIMER, UPDATE_INTERVAL)) {
                // 定时调用 jance() 方法
                System.out.println("开始\\----------------------------------------------------------");
                jance();
                //System.out.println("数量："+fuel);
                System.out.println(heat);
            }
        }
        public void dumpOutputs() {
            if (NC_power.this.outputItems != null && this.timer(NC_power.this.timerDump, (float)NC_power.this.dumpTime / this.timeScale)) {
                for(ItemStack output : NC_power.this.outputItems) {
                    this.dump(output.item);
                }
            }

//            if (NC_power.this.outputLiquids != null) {
//                for(int i = 0; i < NC_power.this.outputLiquids.length; ++i) {
//                    int dir = NC_power.this.liquidOutputDirections.length > i ? NC_power.this.liquidOutputDirections[i] : -1;
//                    this.dumpLiquid(NC_power.this.outputLiquids[i].liquid, 2.0F, dir);
//                }
//            }

        }
    }
}