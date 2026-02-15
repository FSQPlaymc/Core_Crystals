package api;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Font;
import arc.math.Mathf;
import arc.util.Align;
import arc.util.Log;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.gen.Sounds;
import mindustry.mod.Mod;
import mindustry.ui.Fonts;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.tilesize;

public class gailubao extends Mod {

    public void ExplosiveFactoryMod() {
        // 初始化日志
        Log.info("Loaded ExplosiveFactoryMod constructor.");
    }


    // 自定义工厂类
    public static class gailubaoFactory extends GenericCrafter {
        public float damagePerProduction = 8f; // 每次生产时扣除的血量
        public float damageInterval = 60f; // 伤害间隔（帧数）
        public float timer;
        // 爆炸参数
        public float explosionChance = 0.0001f;
        public float explosionDamage = 500f;
        public float explosionRadius = 5f;
        public Color explosionColor = Color.red;
        private int health;

        public gailubaoFactory(String name) {
            super(name);
            buildType = ExplosiveFactoryBuild::new;
        }

        @Override
        public void setStats() {
            super.setStats();
            stats.add(Stat.productionTime, craftTime / 60f, StatUnit.seconds);
            stats.add(Stat.explosiveness, explosionChance * 100f, StatUnit.percent);
        }

        @Override
        public void drawPlace(int x, int y, int rotation, boolean valid) {
            super.drawPlace(x, y, rotation, valid);

            // 显示爆炸警告
            float width = 10f, height = 3f;
            float offset = -height * 2f;
            Draw.color(explosionColor);
            Draw.rect(Core.atlas.find("warning"), x * tilesize + offset, y * tilesize, width, height);
            Draw.color();
             final int tilesize = 3;
            // 显示爆炸概率
            Font font = Fonts.outline;
            font.setColor(explosionColor);
            font.draw("爆炸概率: " + (int)(explosionChance * 100) + "%",

                    x * tilesize + offset,
                    y * tilesize - height * 0.05f,
                    Align.center);
            font.setColor(Color.white);
        }

        public class ExplosiveFactoryBuild extends GenericCrafterBuild {
            public void craft(){
                super.craft();
                damage(damagePerProduction);
                if (health <= 4) {
                    kill();
                }
            }
            @Override
            public void updateTile() {
                super.updateTile();
//                // 只有在工作时才可能爆炸
//                if (efficiency > 0) {
//                    // 随机检查是否爆炸
//                    if (Mathf.chance(explosionChance)) {
//                        explode();
//                    }
//                }
            }

            // 爆炸处理
            public void explode() {
                // 创建爆炸效果
                Fx.dynamicExplosion.at(x, y, explosionRadius/2f, explosionColor);

                // 应用爆炸伤害
                Damage.damage(team, x, y, explosionRadius, explosionDamage);

                // 破坏工厂
                kill();

                // 播放音效
                Sounds.explosion.at(this);

                // 日志记录（调试用）
                Log.info("Factory exploded at (@, @)", tile.x, tile.y);
            }
        }
    }
}
