package api.more_factory;

import mindustry.gen.Building;
import mindustry.type.ItemStack;

// 物品消耗的具体实现
public class ConsumeItems implements Consume {
    private ItemStack[] items;

    public ConsumeItems(ItemStack[] items) {
        this.items = items;
    }

    @Override
    public boolean valid(Building build) {
        // 检查建筑是否有足够的物品
        MFactory.CustomCrafterBuild crafter = (MFactory.CustomCrafterBuild) build;
        for (ItemStack stack : items) {
            if (crafter.items.get(stack.item) < stack.amount) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void consume(Building build) {
        // 实际消耗物品
        MFactory.CustomCrafterBuild crafter = (MFactory.CustomCrafterBuild) build;
        for (ItemStack stack : items) {
            crafter.items.remove(stack.item, stack.amount);
        }
    }
}
