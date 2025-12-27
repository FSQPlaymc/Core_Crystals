package api.block.power;

import arc.Events;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.game.EventType;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.world.blocks.power.NuclearReactor;

import static mindustry.Vars.tilesize;

public class NC_change_power extends NuclearReactor {
    public Item fuelItem2 = Items.graphite;
    public NC_change_power(String name) {
        super(name);
    }
    @Override
    public void setBars(){
        super.setBars();
        addBar("heat", (NC_change_power_build entity) -> new Bar("bar.heat", Pal.lightOrange, () -> entity.heat));
    }
    public class NC_change_power_build extends NuclearReactorBuild{
        public void updateTile(){
            int fuel = items.get(fuelItem);
            int fuel2=items.get(fuelItem2);
            float fullness = (float)fuel / itemCapacity;
            productionEfficiency = fullness;

            if(fuel > 0 && enabled &&fuel2>1){
                heat += fullness * heating * Math.min(delta(), 4f);

                if(timer(timerFuel, itemDuration / timeScale)){
                    consume();
                }
            }else{
                productionEfficiency = 0f;
                heat = Math.max(0f, heat - Time.delta / ambientCooldownTime);
            }

            if(heat > 0){
                float maxUsed = Math.min(liquids.currentAmount(), heat / coolantPower);
                heat -= maxUsed * coolantPower;
                liquids.remove(liquids.current(), maxUsed);
            }

            if(heat > smokeThreshold){
                float smoke = 1.0f + (heat - smokeThreshold) / (1f - smokeThreshold); //ranges from 1.0 to 2.0
                if(Mathf.chance(smoke / 20.0 * delta())){
                    Fx.reactorsmoke.at(x + Mathf.range(size * tilesize / 2f),
                            y + Mathf.range(size * tilesize / 2f));
                }
            }

            heat = Mathf.clamp(heat);
            heatProgress = heatOutput > 0f ? Mathf.approachDelta(heatProgress, heat * heatOutput * (enabled ? 1f : 0f), heatWarmupRate * delta()) : 0f;

            if(heat >= 0.999f){
                Events.fire(EventType.Trigger.thoriumReactorOverheat);
                kill();
            }
        }
    }
}
