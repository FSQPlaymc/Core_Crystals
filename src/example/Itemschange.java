package example;

import arc.graphics.Color;
import mindustry.content.Items;
import mindustry.type.Item;

public class Itemschange extends Items {
    public static void chi() {
        carbide = new Item("carbide", Color.valueOf("89769a")) {{
            cost = 1.4f;//建筑建造时间值倍率
            healthScaling = 1.1f;//建筑生命值倍率
            hardness = 4;
        }};//碳化物
        surgeAlloy = new Item("surge-alloy", Color.valueOf("f3e979")) {
            {
                this.cost = 1.2F;
                this.charge = 0.75F;
                this.healthScaling = 1.25F;
                this.hardness =5;
            }
        };
    }
}