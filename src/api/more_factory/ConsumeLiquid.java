package api.more_factory;

import mindustry.gen.Building;
import mindustry.type.Liquid;

// 液体消耗的具体实现
public class ConsumeLiquid implements Consume {
    private Liquid liquid;
    private float amount;
    
    public ConsumeLiquid(Liquid liquid, float amount) {
        this.liquid = liquid;
        this.amount = amount;
    }
    
    @Override
    public boolean valid(Building build) {
        // 检查液体容器是否有足够的液体
        MFactory.CustomCrafterBuild crafter = (MFactory.CustomCrafterBuild) build;
        return crafter.liquids.get(liquid) >= amount;
    }
    
    @Override
    public void consume(Building build) {
        // 实际消耗液体
        MFactory.CustomCrafterBuild crafter = (MFactory.CustomCrafterBuild) build;
        crafter.liquids.remove(liquid, amount);
    }
}
