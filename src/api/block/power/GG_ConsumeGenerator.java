package api.block.power;

import arc.Events;
import arc.math.Mathf;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.game.EventType;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

public class GG_ConsumeGenerator extends ConsumeGenerator {
    @Nullable
    public ItemStack outputItem;
    @Nullable
    public ItemStack[] outputItems;
    @Nullable
    public LiquidStack[] outputLiquids;
    public boolean dumpExtraLiquid = true;
    public boolean ignoreLiquidFullness = false;
    public Effect craftEffect= Fx.none; // 提供默认值;
    public Effect updateEffect= Fx.none;
    public int[] liquidOutputDirections = new int[]{-1};
    public GG_ConsumeGenerator(String name) {
        super(name);
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
        if (this.outputLiquids != null) {
            this.stats.add(Stat.output, StatValues.liquids(1.0F, this.outputLiquids));
        }
    }
    public void setBars() {
        super.setBars();
        if (this.outputLiquids != null && this.outputLiquids.length > 0) {
            this.removeBar("liquid");

            for(LiquidStack stack : this.outputLiquids) {
                this.addLiquidBar(stack.liquid);
            }
        }

    }
    public void init() {
        if (this.outputItems == null && this.outputItem != null) {
            this.outputItems = new ItemStack[]{this.outputItem};
        }

        if (this.outputLiquids == null && this.outputLiquid != null) {
            this.outputLiquids = new LiquidStack[]{this.outputLiquid};
        }

        if (this.outputLiquid == null && this.outputLiquids != null && this.outputLiquids.length > 0) {
            this.outputLiquid = this.outputLiquids[0];
        }

        this.outputsLiquid = this.outputLiquids != null;
        if (this.outputItems != null) {
            this.hasItems = true;
        }

        if (this.outputLiquids != null) {
            this.hasLiquids = true;
        }

        super.init();
    }
    public class GG_ConsumeGeneratorBuild extends ConsumeGeneratorBuild{
        public float progress;
        public float totalProgress;
        public float warmup;
        public boolean shouldConsume() {
            if (GG_ConsumeGenerator.this.outputItems != null) {
                for(ItemStack output : GG_ConsumeGenerator.this.outputItems) {
                    if (this.items.get(output.item) + output.amount > GG_ConsumeGenerator.this.itemCapacity) {
                        return false;
                    }
                }
            }

            if (GG_ConsumeGenerator.this.outputLiquids != null && !GG_ConsumeGenerator.this.ignoreLiquidFullness) {
                boolean allFull = true;

                for(LiquidStack output : GG_ConsumeGenerator.this.outputLiquids) {
                    if (this.liquids.get(output.liquid) >= GG_ConsumeGenerator.this.liquidCapacity - 0.001F) {
                        if (!GG_ConsumeGenerator.this.dumpExtraLiquid) {
                            return false;
                        }
                    } else {
                        allFull = false;
                    }
                }

                if (allFull) {
                    return false;
                }
            }

            return this.enabled;
        }
        public void updateTile(){
            boolean valid = this.efficiency > 0.0F;
            this.warmup = Mathf.lerpDelta(this.warmup, valid ? 1.0F : 0.0F, GG_ConsumeGenerator.this.warmupSpeed);
            this.productionEfficiency = this.efficiency * this.efficiencyMultiplier;
            this.totalTime += this.warmup * Time.delta;
            if (valid && Mathf.chanceDelta((double)GG_ConsumeGenerator.this.effectChance)) {
                GG_ConsumeGenerator.this.generateEffect.at(this.x + Mathf.range(GG_ConsumeGenerator.this.generateEffectRange), this.y + Mathf.range(GG_ConsumeGenerator.this.generateEffectRange));
            }

            if (GG_ConsumeGenerator.this.filterItem != null && valid && GG_ConsumeGenerator.this.itemDurationMultipliers.size > 0 && GG_ConsumeGenerator.this.filterItem.getConsumed(this) != null) {
                this.itemDurationMultiplier = GG_ConsumeGenerator.this.itemDurationMultipliers.get(GG_ConsumeGenerator.this.filterItem.getConsumed(this), 1.0F);
            }

            if (GG_ConsumeGenerator.this.hasItems && valid && this.generateTime <= 0.0F) {
                this.consume();
                this.craft();
                GG_ConsumeGenerator.this.consumeEffect.at(this.x + Mathf.range(GG_ConsumeGenerator.this.generateEffectRange), this.y + Mathf.range(GG_ConsumeGenerator.this.generateEffectRange));
                this.generateTime = 1.0F;
            }

            if (GG_ConsumeGenerator.this.outputLiquid != null) {
                float added = Math.min(this.productionEfficiency * this.delta() * GG_ConsumeGenerator.this.outputLiquid.amount, GG_ConsumeGenerator.this.liquidCapacity - this.liquids.get(GG_ConsumeGenerator.this.outputLiquid.liquid));
                this.liquids.add(GG_ConsumeGenerator.this.outputLiquid.liquid, added);
                this.dumpLiquid(GG_ConsumeGenerator.this.outputLiquid.liquid);
                if (GG_ConsumeGenerator.this.explodeOnFull && this.liquids.get(GG_ConsumeGenerator.this.outputLiquid.liquid) >= GG_ConsumeGenerator.this.liquidCapacity - 0.01F) {
                    this.kill();
                    Events.fire(new EventType.GeneratorPressureExplodeEvent(this));
                }
            }
            this.dumpOutputs();
            this.generateTime -= this.delta() / (GG_ConsumeGenerator.this.itemDuration * this.itemDurationMultiplier);
        }
        public void craft() {
            this.consume();
            if (GG_ConsumeGenerator.this.outputItems != null) {
                for(ItemStack output : GG_ConsumeGenerator.this.outputItems) {
                    for(int i = 0; i < output.amount; ++i) {
                        this.offload(output.item);
                    }
                }
            }

            if (this.wasVisible) {
                GG_ConsumeGenerator.this.craftEffect.at(this.x, this.y);
            }

            this.progress %= 1.0F;
        }

        public void dumpOutputs() {
            if (GG_ConsumeGenerator.this.outputItems != null && this.timer(GG_ConsumeGenerator.this.timerDump, (float)GG_ConsumeGenerator.this.dumpTime / this.timeScale)) {
                for(ItemStack output : GG_ConsumeGenerator.this.outputItems) {
                    this.dump(output.item);
                }
            }

            if (GG_ConsumeGenerator.this.outputLiquids != null) {
                for(int i = 0; i < GG_ConsumeGenerator.this.outputLiquids.length; ++i) {
                    int dir = GG_ConsumeGenerator.this.liquidOutputDirections.length > i ? GG_ConsumeGenerator.this.liquidOutputDirections[i] : -1;
                    this.dumpLiquid(GG_ConsumeGenerator.this.outputLiquids[i].liquid, 2.0F, dir);
                }
            }

        }
    }
}
