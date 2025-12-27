package content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.struct.*;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.type.ammo.*;

public class GG_units
        //extends UnitEntity
{
    public static UnitType tizhizhu;
    public static Weapon 我不到;
    public static void units(){
        tizhizhu=new UnitType("tizhizhu"){{
            constructor = LegsUnit::create;
            this.speed = 1.0F;//移速
            this.drag = 0.4F;
            this.hitSize = 13.0F;
            this.rotateSpeed = 3.0F;
            this.targetAir = false;
            this.health = 500.0F;
            this.immunities = ObjectSet.with(new StatusEffect[]{StatusEffects.burning, StatusEffects.melting});
            this.legCount = 4;
            this.legLength = 9.0F;
            this.legForwardScl = 0.6F;
            this.legMoveSpace = 1.5F;
            this.hovering = true;
            this.armor = 3.0F;
            this.ammoType = new ItemAmmoType(Items.coal);
            this.shadowElevation = 0.2F;
            this.groundLayer = 74.0F;
            // 加载武器贴图
            this.weapons.add(我不到=new Weapon("core-db-我不到") {{//要加自己“模组名-”
                shootStatus = StatusEffects.slow;
                shootStatusDuration = reload + 1f;
                    System.out.println(this.name);
                    //this.shootSound = Sounds.missile;
                    this.mirror = false;//镜像
                    //this.showStatSprite = false;
                    this.display=true;
                    this.top = false;
//                - recoil: 后坐力
//                        - recoils: 后坐力数量（用于多管武器）
//                - recoilTime: 后坐力恢复时间
//                        - recoilPow: 后坐力曲线指数

                    this.shootY = 2.0F;
                    this.shootX=4.3f;
                    this.reload = 105F;
                    this.ejectEffect = Fx.none;
                    this.recoil = 4.0F;//显示的
                    this.x = 0.0F;
                    this.y=3.3f;
                    this.shootSound = Sounds.shootQuad;
                this.bullet = new ArtilleryBulletType(3.0F, 60.0F) {
                    {
                        this.shootEffect = new MultiEffect(new Effect[]{Fx.shootSmallColor, new Effect(9.0F, (e) -> {
                            Draw.color(Color.white, e.color, e.fin());
                            Lines.stroke(0.7F + e.fout());
                            Lines.square(e.x, e.y, e.fin() * 5.0F, e.rotation + 45.0F);
                            Drawf.light(e.x, e.y, 23.0F, e.color, e.fout() * 0.7F);
                        })});
                        this.collidesTiles = true;
                        this.collides = true; // 进行碰撞检测
                        this.backColor = this.hitColor = Pal.techBlue;
                        this.frontColor = Color.white;
                        this.knockback = 0.8F;
                        this.lifetime = 102.2F;
                        this.width = this.height = 9.0F;
                        this.trailLength = 27;
                        this.trailWidth = 2.5F;
                        this.trailEffect = Fx.none;
                        this.trailColor = this.backColor;
                        this.trailInterp = Interp.slope;
                        this.shrinkX = 0.6F;
                        this.shrinkY = 0.2F;
                        this.splashDamageRadius = 45.0F;
                        this.splashDamage = 40.0F;
                        this.hitEffect = this.despawnEffect = new MultiEffect(new Effect[]{Fx.hitSquaresColor, new WaveEffect() {
                            {
                                this.colorFrom = this.colorTo = Pal.techBlue;
                                this.sizeTo = splashDamageRadius + 2.0F;
                                this.lifetime = 9.0F;
                                this.strokeFrom = 2.0F;
                            }
                        }});
                    }
                };
            }});
        }};

    }
}
/*super(name);
this.healColor = Pal.heal; // 治疗颜色设置为调色板中的治疗色
this.lightColor = Pal.powerLight; // 光源颜色设置为调色板中的能量光色
this.deathSound = Sounds.bang; // 死亡音效设置为爆炸声
this.loopSound = Sounds.none; // 循环音效设置为无
this.loopSoundVolume = 0.5F; // 循环音效音量设置为0.5
this.fallEffect = Fx.fallSmoke; // 坠落特效设置为坠落烟雾
this.fallEngineEffect = Fx.fallSmoke; // 引擎坠落特效设置为坠落烟雾
this.deathExplosionEffect = Fx.dynamicExplosion; // 死亡爆炸特效设置为动态爆炸
this.parts = new Seq(DrawPart.class); // 初始化绘制部件序列
this.engines = new Seq(); // 初始化引擎序列
this.useEngineElevation = true; // 使用引擎高度
this.engineColor = null; // 引擎颜色设为空
this.engineColorInner = Color.white; // 引擎内部颜色设为白色
this.trailLength = 0; // 尾迹长度设为0
this.flowfieldPathType = -1; // 流场路径类型设为-1
this.targetFlags = new BlockFlag[]{null}; // 目标标志设为空数组
this.allowChangeCommands = true; // 允许改变命令
this.commands = new Seq(); // 初始化命令序列
this.stances = new Seq(); // 初始化姿态序列
this.outlineColor = Pal.darkerMetal; // 轮廓颜色设置为调色板中的深金属色
this.outlineRadius = 3; // 轮廓半径设为3
this.outlines = true; // 启用轮廓
this.itemCapacity = -1; // 物品容量设为-1
this.ammoCapacity = -1; // 弹药容量设为-1
this.ammoType = new ItemAmmoType(Items.copper); // 弹药类型设为铜币物品弹药
this.mineTier = -1; // 挖掘等级设为-1
this.mineSpeed = 1.0F; // 挖掘速度设为1.0
this.mineWalls = false; // 不能挖掘墙壁
this.mineFloor = true; // 可以挖掘地板
this.mineHardnessScaling = true; // 启用挖掘硬度缩放
this.mineSound = Sounds.minebeam; // 挖掘音效设置为采矿光束声
this.mineSoundVolume = 0.6F; // 挖掘音效音量设为0.6
this.mineItems = Seq.with(new Item[]{Items.copper, Items.lead, Items.titanium, Items.thorium}); // 可挖掘物品包括铜、铅、钛和钍
this.legCount = 4; // 腿数量设为4
this.legGroupSize = 2; // 腿组大小设为2
this.legLength = 10.0F; // 腿长度设为10.0
this.legSpeed = 0.1F; // 腿速度设为0.1
this.legForwardScl = 1.0F; // 腿向前缩放设为1.0
this.legBaseOffset = 0.0F; // 腿基础偏移设为0.0
this.legMoveSpace = 1.0F; // 腿移动空间设为1.0
this.legExtension = 0.0F; // 腿伸展设为0.0
this.legPairOffset = 0.0F; // 腿对偏移设为0.0
this.legLengthScl = 1.0F; // 腿长度缩放设为1.0
this.legStraightLength = 1.0F; // 腿伸直长度设为1.0
this.legMaxLength = 1.75F; // 腿最大长度设为1.75
this.legMinLength = 0.0F; // 腿最小长度设为0.0
this.legSplashDamage = 0.0F; // 腿溅射伤害设为0.0
this.legSplashRange = 5.0F; // 腿溅射范围设为5.0
this.baseLegStraightness = 0.0F; // 基础腿直度设为0.0
this.legStraightness = 0.0F; // 腿直度设为0.0
this.legBaseUnder = false; // 腿基础不在下方
this.lockLegBase = false; // 不锁定腿基础
this.flipBackLegs = true; // 翻转后腿
this.flipLegSide = false; // 不翻转腿侧
this.emitWalkSound = true; // 发出行走声音
this.emitWalkEffect = true; // 发出行走特效
this.mechLandShake = 0.0F; // 机械着陆震动设为0.0
this.mechSideSway = 0.54F; // 机械侧向摆动设为0.54
this.mechFrontSway = 0.1F; // 机械前后摆动设为0.1
this.mechStride = -1.0F; // 机械步幅设为-1.0
this.mechStepParticles = false; // 不生成机械脚步粒子
this.mechLegColor = Pal.darkMetal; // 机械腿颜色设为调色板中的暗金属色
this.treadRects = new Rect[0]; // 履带矩形设为空数组
this.treadFrames = 18; // 履带帧数设为18
this.treadPullOffset = 0; // 履带拉动偏移设为0
this.segments = 0; // 段数设为0
this.segmentUnits = 1; // 段单位设为1
this.segmentLayerOrder = true; // 段层顺序设为true
this.segmentMag = 2.0F; // 段幅度设为2.0
this.segmentScl = 4.0F; // 段缩放设为4.0
this.segmentPhase = 5.0F; // 段相位设为5.0
this.segmentRotSpeed = 1.0F; // 段旋转速度设为1.0
this.segmentMaxRot = 30.0F; // 段最大旋转设为30.0
this.segmentSpacing = -1.0F; // 段间距设为-1.0
this.segmentRotationRange = 80.0F; // 段旋转范围设为80.0
this.crawlSlowdown = 0.5F; // 爬行减速设为0.5
this.crushDamage = 0.0F; // 碾压伤害设为0.0
this.crawlSlowdownFrac = 0.55F; // 爬行减速分数设为0.55
this.lifetime = 300.0F; // 生命周期设为300.0
this.homingDelay = 10.0F; // 制导延迟设为10.0
this.buildTime = -1.0F; // 建造时间设为-1.0
this.constructor = EntityMapping.map(this.name); // 构造函数通过实体映射获取
this.selectionSize = 30.0F; // 选择大小设为30.0

 */
