package api;

import mindustry.type.*;

public class NewRecipe {
    public String name;
    public ItemStack[] inputItems;    // 物品输入
    public LiquidStack[] inputLiquids; // 流体输入
    public ItemStack[] outputItems;   // 物品输出
    public LiquidStack[] outputLiquids; // 流体输出
    public float craftTime;           // 生产时间
    public float powerUse;            // 电力消耗

    // 全参数构造函数（支持多物品+多流体）
    public NewRecipe(String name, ItemStack[] inputItems, LiquidStack[] inputLiquids,
                     ItemStack[] outputItems, LiquidStack[] outputLiquids,
                     float craftTime, float powerUse) {
        this.name = name;
        this.inputItems = inputItems;
        this.inputLiquids = inputLiquids;
        this.outputItems = outputItems;
        this.outputLiquids = outputLiquids;
        this.craftTime = craftTime;
        this.powerUse = powerUse;
    }

    // 简化构造（单物品+单流体输入）
    public NewRecipe(String name, ItemStack inputItem, LiquidStack inputLiquid,
                     ItemStack outputItem, LiquidStack outputLiquid,
                     float craftTime, float powerUse) {
        this(name,
                new ItemStack[]{inputItem},
                new LiquidStack[]{inputLiquid},
                new ItemStack[]{outputItem},
                new LiquidStack[]{outputLiquid},
                craftTime, powerUse);
    }
}
