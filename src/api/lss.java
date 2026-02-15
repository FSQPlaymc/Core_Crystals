package api;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
//import mindustry.gen.Direction;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.ui.Bar;
import mindustry.ui.ReqImage;
import mindustry.ui.Styles;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.consumers.ConsumePower;

public class lss extends GenericCrafter {
    public Seq<NewRecipe> recipes = new Seq<>();

    public lss(String name) {
        super(name);
        configurable = true;
        saveConfig = true;
        sync = true;
        hasLiquids = true; // 确保启用液体存储
        // 允许接受物品和液体输入
        acceptsItems = true;
    }
    public void init() {
        super.init();
        // 为所有配方的输入物品和液体设置过滤
        recipes.each(recipe -> {
            if (recipe.inputItems != null) {
                for (ItemStack stack : recipe.inputItems) {
                    itemFilter[stack.item.id] = true; // 允许该物品输入
                }
            }
            if (recipe.inputLiquids != null) {
                for (LiquidStack stack : recipe.inputLiquids) {
                    liquidFilter[stack.liquid.id] = true; // 允许该液体输入
                }
            }
        });
    }

    public void addRecipe(String name,
                          ItemStack[] inputItems,
                          LiquidStack[] inputLiquids,
                          ItemStack[] outputItems,
                          LiquidStack[] outputLiquids,
                          float craftTime,
                          float powerUse) {
        recipes.add(new NewRecipe(name, inputItems, inputLiquids, outputItems, outputLiquids, craftTime, powerUse));
    }

    public void addRecipe(String name,
                          ItemStack inputItem,
                          LiquidStack[] inputLiquids,
                          ItemStack outputItem,
                          LiquidStack[] outputLiquids,
                          float craftTime,
                          float powerUse) {
        addRecipe(name,
                new ItemStack[]{inputItem},
                inputLiquids,
                new ItemStack[]{outputItem},
                outputLiquids,
                craftTime, powerUse);
    }

    public static class NewRecipe {
        public String name;
        public ItemStack[] inputItems;
        public LiquidStack[] inputLiquids;
        public ItemStack[] outputItems;
        public LiquidStack[] outputLiquids;
        public float craftTime;
        public float powerUse;

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
    }

    public class CustomCrafterBuild extends GenericCrafterBuild {
        public int currentRecipe = 0;
        private boolean needsRecipeUpdate = false;

        // 直接使用实例变量存储配方配置（不依赖父类getter）
        private Consume[] consumers; // 替代父类的consumers
        private float craftTime;     // 替代父类的craftTime
        private ItemStack outputItem;// 替代父类的outputItem

        @Override
        public void updateTile() {
            if (needsRecipeUpdate) {
                applyCurrentRecipe();
                needsRecipeUpdate = false;
            }

            // 移除对consValid的依赖，直接在生产前检查消耗
            super.updateTile();
        }

        private void applyCurrentRecipe() {
            if (recipes.isEmpty()) return;

            currentRecipe = Mathf.clamp(currentRecipe, 0, recipes.size - 1);
            NewRecipe recipe = recipes.get(currentRecipe);

            // 直接修改实例变量
            craftTime = recipe.craftTime;
            outputItem = recipe.outputItems.length > 0 ? recipe.outputItems[0] : null;

            // 计算消耗数组大小
            int newSize = 0;
            if (recipe.inputItems.length > 0) newSize++;
            if (recipe.inputLiquids.length > 0) newSize += recipe.inputLiquids.length;
            if (recipe.powerUse > 0) newSize++;

            // 构建消耗数组
            Consume[] newConsumers = new Consume[newSize];
            int index = 0;

            if (recipe.inputItems.length > 0) {
                newConsumers[index++] = new ConsumeItems(recipe.inputItems){{
                    update = true;
                }};
            }

            if (recipe.inputLiquids.length > 0) {
                for (LiquidStack stack : recipe.inputLiquids) {
                    newConsumers[index++] = new ConsumeLiquid(stack.liquid, stack.amount){{
                        update = true;
                    }};
                }
            }

            if (recipe.powerUse > 0) {
                newConsumers[index++] = new ConsumePower(recipe.powerUse / 60f, 1f, false){{
                    update = true;
                }};
            }

            // 验证消耗数组无null
            for (Consume cons : newConsumers) {
                if (cons == null) {
                    throw new RuntimeException("Consumers array contains null! Recipe: " + recipe.name);
                }
            }

            // 直接赋值给实例的consumers变量
            //this.consumers = newConsumers;
            this.consumers = newConsumers;
            progress = 0f; // 仅重置进度，移除consValid相关代码
        }

        // 替换原validateConsumers方法，使用正确的消耗检查方式
//        private boolean checkConsumers() {
//            if (consumers == null) return false;
//            // 假设Consume的正确检查方法是isValid()，而非valid()
//            for (Consume cons : consumers) {
//                // 若isValid()仍不存在，可尝试cons.getValidator().validate(this)
//                if (cons == null || !cons.getClass()) {
//                    return false;
//                }
//            }
//            return true;
//        }




        public void craft() {
            super.craft();
            NewRecipe recipe = recipes.get(currentRecipe);
            if (recipe == null) return;

            // 处理物品输出（循环卸载，每次1个）
            if (recipe.outputItems != null) {
                for (ItemStack stack : recipe.outputItems) {
                    int amount = stack.amount; // 需要输出的总数量
                    // 循环调用 offload()，每次卸载1个，直到完成指定数量
                    for (int i = 0; i < amount; i++) {
                        offload(stack.item); // 正确用法：仅传入物品类型
                    }
                }
            }
            // 处理流体输出（支持多流体）
            if (recipe.outputLiquids != null) {
                for (LiquidStack stack : recipe.outputLiquids) {
                    // 检查流体存储是否有空间
                    float remaining = liquidCapacity - liquids.get(stack.liquid);
                    if (remaining >= stack.amount - 0.001f) {
                        liquids.add(stack.liquid, stack.amount);
                    } else {
                        // 空间不足时尝试卸载到相邻建筑
                        dumpLiquid(stack.liquid, stack.amount);
                    }
                }
            }
        }





        public void buildConfiguration(Table table) {
            table.table(Styles.black5, t -> {
                t.add("配方选择").color(Color.yellow).center().row();
                t.image().color(Color.gray).height(2f).fillX().pad(5f).row();

                for (int i = 0; i < recipes.size; i++) {
                    int index = i;
                    NewRecipe recipe = recipes.get(i);

                    t.button(btn -> {
                                btn.table(b -> {
                                    b.add(recipe.name).color(index == currentRecipe ? Color.green : Color.white).row();

                                    if (recipe.inputItems != null && recipe.inputItems.length > 0) {
                                        b.table(inputs -> {
                                            inputs.add("物品输入: ");
                                            for (ItemStack stack : recipe.inputItems) {
                                                inputs.add(new ReqImage(stack.item.uiIcon,
                                                                () -> items.has(stack.item, stack.amount)))
                                                        .size(20f).pad(2f);
                                                inputs.add(stack.amount + " ").padRight(4f);
                                            }
                                        }).left().row();
                                    }

                                    if (recipe.inputLiquids != null && recipe.inputLiquids.length > 0) {
                                        b.table(liquids -> {
                                            liquids.add("流体输入: ");
                                            for (LiquidStack stack : recipe.inputLiquids) {
                                                liquids.add(new ReqImage(stack.liquid.uiIcon,
                                                                () -> this.liquids.get(stack.liquid) >= stack.amount))
                                                        .size(20f).pad(2f);
                                                liquids.add(stack.amount + "/t ").padRight(4f);
                                            }
                                        }).left().row();
                                    }

                                    if (recipe.outputItems != null && recipe.outputItems.length > 0) {
                                        b.table(outputs -> {
                                            outputs.add("物品输出: ");
                                            for (ItemStack stack : recipe.outputItems) {
                                                outputs.image(stack.item.uiIcon).size(20f).pad(2f);
                                                outputs.add(stack.amount + " ").padRight(4f);
                                            }
                                        }).left().row();
                                    }

                                    // 保留流体输出显示（表明会产生流体）
                                    if (recipe.outputLiquids != null && recipe.outputLiquids.length > 0) {
                                        b.table(outputs -> {
                                            outputs.add("流体存储: "); // 修改标签为"流体存储"更准确
                                            for (LiquidStack stack : recipe.outputLiquids) {
                                                outputs.image(stack.liquid.uiIcon).size(20f).pad(2f);
                                                outputs.add(stack.amount + "/t ").padRight(4f);
                                            }
                                        }).left().row();
                                    }
                                });
                            }, Styles.flatToggleMenut, () -> {
                        // 切换配方时标记需要更新
                        currentRecipe = index;
                        needsRecipeUpdate = true;
                    }).size(200, 60).pad(2f).row();
                }
            }).fillX();
        }

        @Override
        public void displayBars(Table table) {
            super.displayBars(table);
            // 保留流体存储显示，方便查看当前存储量
            if (hasLiquids && liquids != null) {
                // 使用LiquidModule的each()方法遍历所有非零流体
                liquids.each((liquid, amount) -> {
                    if (amount > 0.001f) {
                        table.add(new Bar(
                                () -> liquid.localizedName,
                                () -> liquid.color,
                                () -> amount / liquidCapacity
                        )).growX().row();
                    }
                });
            }
        }
        @Override
        public void configure(Object value) {
            if (value instanceof Byte) {
                currentRecipe = (byte) value;
                needsRecipeUpdate = true;
            }
        }
    }
}