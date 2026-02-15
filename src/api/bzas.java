package api;
/*
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import mindustry.entities.Damage;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.Styles;
import mindustry.world.blocks.production.*;
import mindustry.content.*;

import java.io.FileDescriptor;

import static mindustry.Vars.tilesize;

public class BloodFactory extends GenericCrafter {
    // 工作时的生命损失率 (每秒损失的生命百分比)
    public float healthDrainRate = 0.02f;
    // 最低安全生命值 (低于此值自动停机)
    public float minSafeHealth = 0.3f;
    // 过热警告阈值
    public float warningThreshold = 0.5f;
    // 冷却时的生命恢复率
    public float healthRecoveryRate = 0.01f;

    public BloodFactory(String name) {
        super(name);

        // 基础工厂配置
        size = 3;
        health = 400; // 更高的基础生命值
        outputItem = new ItemStack(Items.silicon, 3);
        craftTime = 80f;
        consumeItems(ItemStack.with(
                Items.coal, 2,
                Items.sand, 4
        ));
        consumePower(2f);

        // 特殊属性
        ambientSound = Sounds.hiss;
        ambientSoundVolume = 0.08f;
    }

    public class BloodFactoryBuild extends GenericCrafterBuild {
        // 过热状态
        boolean overheated = false;
        // 警告闪烁计时器
        float flashTimer = 0f;

        @Override
        public void updateTile() {
            // 首先检查是否需要恢复生命值
            FileDescriptor cons;
            if (!cons.valid() && healthf() < 1f) {
                // 不工作时缓慢恢复生命值
                heal(healthRecoveryRate * maxHealth * Time.delta / 60f);
            }

            super.updateTile();

            // 只有工作时才消耗生命值
            if (productionEfficiency > 0 && !overheated) {
                // 计算生命损失量
                float damageAmount = healthDrainRate * maxHealth * Time.delta / 60f;

                // 应用伤害
                damage(damageAmount);

                // 创建过热粒子效果
                if (Mathf.chanceDelta(0.3f)) {
                    Fx.steam.at(
                            x + Mathf.range(size * 4f),
                            y + Mathf.range(size * 4f)
                    );
                }

                // 检查是否过热
                if (healthf() < minSafeHealth) {
                    overheated = true;
                    // 播放过热音效
                    Sounds.explosion.at(x, y, 0.8f);
                }
            }

            // 过热状态处理
            if (overheated) {
                // 创建更多粒子效果
                if (Mathf.chanceDelta(0.5f)) {
                    Fx.fire.at(
                            x + Mathf.range(size * 5f),
                            y + Mathf.range(size * 5f)
                    );
                }

                // 如果生命值恢复到安全水平以上，恢复正常
                if (healthf() > minSafeHealth + 0.1f) {
                    overheated = false;
                }
            }

            // 更新闪烁计时器
            flashTimer += Time.delta;
        }

        @Override
        public boolean shouldConsume() {
            // 过热时停止工作
            if (overheated) return false;

            // 生命值过低时自动停机
            if (healthf() < minSafeHealth) return false;

            return super.shouldConsume();
        }

        @Override
        public void draw() {
            super.draw();

            // 绘制生命值警告
            if (healthf() < warningThreshold) {
                // 闪烁效果
                float alpha = Mathf.absin(flashTimer, 6f, 0.8f) * (overheated ? 1f : 0.6f);

                // 根据状态选择颜色
                Color color = overheated ? Color.red : Color.orange;

                // 绘制警告框
                Draw.color(color, alpha);
                Lines.stroke(2f);
                Lines.square(x, y, size * tilesize / 2f + 2f);
                Draw.reset();

                // 添加警告图标
                if (overheated || Mathf.chanceDelta(0.3f)) {
                    Draw.color(Color.red, alpha);
                    Draw.rect(Icon.warning.getRegion(), x, y + size * tilesize / 2f + 10f, 12f, 12f);
                    Draw.reset();
                }
            }

            // 绘制生命值条
            if (healthf() < 0.95f) {
                drawHealthBar();
            }
        }

        // 自定义生命值条绘制
        @Override
        public void drawHealth() {
            // 不绘制默认生命值条
        }

        private void drawHealthBar() {
            float barWidth = size * tilesize - 4f;
            float barHeight = 6f;
            float barX = x - barWidth / 2f;
            float barY = y - size * tilesize / 2f - 5f;

            // 背景条
            Draw.color(Color.darkGray);
            Fill.crect(barX, barY, barWidth, barHeight);

            // 生命值条
            float healthWidth = barWidth * healthf();

            // 根据生命值选择颜色
            if (healthf() > 0.6f) {
                Draw.color(Color.green);
            } else if (healthf() > 0.3f) {
                Draw.color(Color.orange);
            } else {
                Draw.color(Color.red);
            }

            Fill.crect(barX, barY, healthWidth, barHeight);

            // 边框
            Draw.color(Color.lightGray);
            Lines.stroke(1f);
            Lines.rect(barX, barY, barWidth, barHeight);

            Draw.reset();
        }

        @Override
        public void onRemoved() {
            // 被摧毁时触发爆炸
            if (health <= 0) {
                Fx.dynamicExplosion.at(x, y, size / 2f);
                Damage.damage(team, x, y, size * 8f, maxHealth * 0.5f);
                Sounds.explosionBig.at(x, y);
            }
            super.onRemoved();
        }

        // 添加配置选项
        @Override
        public void buildConfiguration(Table table) {
            table.button(Icon.refresh, Styles.cleari, () -> {
                // 强制冷却
                if (overheated) {
                    overheated = false;
                    heal(maxHealth * 0.1f); // 恢复10%生命值
                    Fx.healBlockFull.at(x, y, size, Color.blue);
                }
            }).size(40f).tooltip("强制冷却");
        }
    }
}*/