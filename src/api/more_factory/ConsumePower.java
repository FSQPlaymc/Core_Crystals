package api.more_factory;

import mindustry.gen.Building;

// 修正后的电力消耗实现类
public class ConsumePower implements Consume {
    private float amount;

    public ConsumePower(float amount) {
        this.amount = amount;
    }

    @Override
    public boolean valid(Building build) {
        // 强制转换为自定义建筑类以获取正确的电力网络
        if (build instanceof CustomBuilding customBuild) {
            // 检查电力网络是否存在且电力充足
            return customBuild.power.graph != null && customBuild.power.graph.hasPower(amount);
        }
        return false; // 非自定义建筑不支持电力检查
    }

    @Override
    public void consume(Building build) {
        if (build instanceof CustomBuilding customBuild && customBuild.power.graph != null) {
            // 消耗电力
            customBuild.power.graph.usePower(amount);
        }
    }
}

