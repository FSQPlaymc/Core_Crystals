package content;

import arc.Core;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.entities.UnitSorts;
import mindustry.entities.bullet.ArtilleryBulletType;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.RailBulletType;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.meta.Env;

import static mindustry.type.ItemStack.with;

public class GGNewPT {
    //public static Block NGM;
    public static ItemTurret bibao;
    public  static ItemTurret xiaogangpao;
    public static ItemTurret forefshadow;
    public static void NewP(){
        bibao = new ItemTurret("biao"){{
            requirements(Category.turret,with(Items.silicon,13,
                   // GGItems.zeta,1145,
                    Items.copper,20
            ));
            targetAir = false;// 是否以空中单位为目标
            ammo(
                    Items.silicon, new ArtilleryBulletType(1.5f, 20){{// 创建新的炮弹类型(基础伤害3, 初始弹药量20)
                        knockback = 0.8f;          // 击退力度：0.8
                        lifetime = 100f;           // 子弹存在时间：100帧
                        width = height = 11f;      // 碰撞箱尺寸：11x11
                        collidesTiles = false;     // 不与环境方块碰撞
                        splashDamageRadius = 25f * 0.75f; // 溅射半径：18.75格
                        splashDamage = 120f;        // 溅射伤害：33
                        reloadMultiplier = 1.2f;   // 装填时间倍率：1.2倍(更慢)
                        ammoMultiplier = 2f;       // 单次装填消耗弹药倍率：3倍
                        homingPower = 0.2f;       // 自动追踪强度：0.08
                        homingRange = 50f;         // 自动追踪范围：50格
                        trailLength = 7;           // 尾迹长度：7单位
                        trailWidth = 3f;           // 尾迹宽度：3单位
                        buildingDamageMultiplier = 0.5f;//建筑减伤
                        hitColor = backColor = trailColor = Pal.siliconAmmoBack; // 命中/背景/尾迹色：硅弹药背景色
                        frontColor = Pal.siliconAmmoFront; // 弹头颜色：硅弹药前景色
                        despawnEffect = Fx.hitBulletColor; // 消失特效：子弹命中颜色特效
                    }},
                    Items.graphite, new ArtilleryBulletType(2.5f, 20){{
                        knockback = 0.8f;
                        lifetime = 80f;
                        width = height = 11f;
                        collidesTiles = false;
                        splashDamageRadius = 25f * 0.75f;
                        splashDamage = 45f;

                        hitColor = backColor = trailColor = Pal.graphiteAmmoBack;
                        frontColor = Pal.graphiteAmmoFront;
                        despawnEffect = Fx.hitBulletColor;
                    }});
            reload = 40f;               // 重新装填时间（帧）
            size=2;
            range = 270;//射程
            ammoPerShot = 1;             // 每次射击消耗弹药
            rotateSpeed = 4f;            // 旋转速度
            // 生命值和冷却系统
            scaledHealth = 500;
            coolantMultiplier = 0.8f;
            coolant = consumeCoolant(1f);
            //consumePower(10f);  电力
        }};
        xiaogangpao=new ItemTurret("gp"){{
            fullIcon = Core.atlas.find("gp");
            requirements(Category.turret,with(mindustry.content.Items.metaglass,1145,
                    GGItems.zeta,1145,
                    mindustry.content.Items.copper,1145
            ));
            ammo(
                    GGItems.itemCZzw,new BasicBulletType(3.5f,1145){{//speed：飞行速度 damage参数表示子弹的基础伤害值
                        pierceDamageFactor=0.7f;
                        splashDamageRadius = 25f * 0.75f; // 溅射半径：18.75格
                        splashDamage = 1145f;        // 溅射伤害：33
                        buildingDamageMultiplier=2.0f;
                        height=8f;
                        width=8f;
                lifetime=900;
                damage=114514;
                hitEffect = despawnEffect = Fx.hitBulletColor;

                    }}
            );
            size=1;
            range = 114514;//射程
            ammoPerShot = 1;             // 每次射击消耗弹药
            rotateSpeed = 4f;            // 旋转速度
            // 生命值和冷却系统
            scaledHealth = 1500;
            coolantMultiplier = 0.8f;
            coolant = consumeCoolant(1f);
            consumePower(10f);

        }};

        forefshadow = new ItemTurret("foreshadow"){{
            // 设置射程并保存引用
            float brange = range = 500f;

            // 建造需求
            requirements(Category.turret, with(
                    GGItems.itemCZzw,10,
                    Items.copper,9
            ));

            // 弹药配置
            ammo(
                    GGItems.zeta, new RailBulletType(){{
                        // 特效设置
                        shootEffect = Fx.instShoot;
                        hitEffect = Fx.instHit;
                        pierceEffect = Fx.railHit;
                        smokeEffect = Fx.smokeCloud;
                        pointEffect = Fx.instTrail;
                        despawnEffect = Fx.instBomb;

                        // 子弹属性
                        pointEffectSpace = 20f;
                        damage = 1350;
                        buildingDamageMultiplier = 0.2f;
                        maxDamageFraction = 0.6f;
                        pierceDamageFactor = 1f;
                        length = brange; // 使用之前设置的射程
                        hitShake = 6f;
                        ammoMultiplier = 1f;
                    }}
            );

            // 炮台属性
            maxAmmo = 40;                // 最大弹药量
            ammoPerShot = 5;             // 每次射击消耗弹药
            rotateSpeed = 2f;            // 旋转速度
            reload = 200f;               // 装弹时间
            ammoUseEffect = Fx.casing3Double;  // 弹药使用特效
            recoil = 5f;                 // 后坐力
            cooldownTime = reload;       // 冷却时间
            shake = 4f;                  // 震动强度
            size = 4;                    // 尺寸
            shootCone = 2f;              // 射击散布角度
            shootSound = Sounds.rain; // 射击音效
            unitSort = UnitSorts.strongest; // 单位排序方式
            envEnabled |= Env.space;     // 可用环境(太空)


            // 生命值和冷却系统
            scaledHealth = 150;
            coolantMultiplier = 0.4f;

            // 资源消耗
            coolant = consumeCoolant(1f);
            consumePower(10f);
        }};
    }
}
