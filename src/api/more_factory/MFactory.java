package api.more_factory;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.ui.Bar;
import mindustry.ui.ReqImage;
import mindustry.ui.Styles;
import mindustry.world.blocks.production.GenericCrafter;


import mindustry.world.consumers.ConsumePower;

//import static com.sun.org.apache.xalan.internal.lib.ExsltMath.power;

public class MFactory extends GenericCrafter {
    public Seq<NewRecipe> recipes = new Seq<>();

    public MFactory(String name) {
        super(name);
        configurable = true;
        saveConfig = true;
        sync = true;
        hasLiquids = true;
        acceptsItems = true;
        //consume(new ConsumePower(0f, 0f, false));
        consume(new ConsumePower(0f,0f,false)); // 初始值，后续通过配方动态更新
        config(int.class, (build, value) -> {
            if (build instanceof CustomCrafterBuild) {
                CustomCrafterBuild crafter = (CustomCrafterBuild) build;
                crafter.currentRecipe = value;
                crafter.needsRecipeUpdate = true;
            }
        });

    }

    public void init() {
        super.init();
        recipes.each(recipe -> {
            if (recipe.inputItems != null) {
                for (ItemStack stack : recipe.inputItems) {
                    itemFilter[stack.item.id] = true;
                }
            }
            if (recipe.inputLiquids != null) {
                for (LiquidStack stack : recipe.inputLiquids) {
                    liquidFilter[stack.liquid.id] = true;
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

        public float liquidCapacity = 100f; // 示例值，可根据游戏平衡调整
        public int currentRecipe = 0;
        private boolean needsRecipeUpdate = false;
        private float currentCraftTime = craftTime;
        private ConsumePower activePowerConsumer;

        // 修复1：添加生产进度和资源消耗状态跟踪
        private float progress = 0f;
        private boolean resourcesConsumed = false;

        @Override
        public void updateTile() {
            if (needsRecipeUpdate) {
                applyCurrentRecipe();
                needsRecipeUpdate = false;
                progress = 0f;
                resourcesConsumed = false; // 切换配方时强制重置状态
            }

            if (recipes.isEmpty()) return;
            NewRecipe recipe = recipes.get(currentRecipe);

            if (consValid() && efficiency > 0) {
                // 1. 确保资源已消耗（未消耗则尝试消耗）
                if (!resourcesConsumed) {
                    // 消耗资源前再次检查，避免资源不足时误消耗
                    if (canConsumeResources(recipe)) {
                        consumeResources();
                        resourcesConsumed = true;
                    } else {
                        // 资源不足时重置状态，等待下次检查
                        progress = 0f;
                        return;
                    }
                }

                // 2. 计算进度（限制单次累加的最大值，避免跳变）
                float maxAdd = currentCraftTime - progress; // 最多累加到1
                float add = (delta() * efficiency) / currentCraftTime;
                progress += Math.min(add, maxAdd);

                // 3. 进度达标且资源已消耗，才触发产出
                if (progress >= 6f && resourcesConsumed) {
                    craft();
                    progress = 0f;
                    resourcesConsumed = false; // 产出后重置状态
                }
            } else {
                // 条件不满足时，重置状态
                progress = 0f;
                resourcesConsumed = false;
            }
        }

        // 新增：检查是否可以消耗资源（避免consValid()的潜在延迟）
        private boolean canConsumeResources(NewRecipe recipe) {
            // 验证物品
            if (recipe.inputItems != null) {
                for (ItemStack stack : recipe.inputItems) {
                    if (this.items.get(stack.item) < stack.amount) {
                        return false;
                    }
                }
            }
            // 验证液体
            if (recipe.inputLiquids != null) {
                for (LiquidStack stack : recipe.inputLiquids) {
                    if (liquids.get(stack.liquid) < stack.amount) {
                        return false;
                    }
                }
            }
            // 验证电力
            if (recipe.powerUse > 0 && power.status <= 0.1f) {
                return false;
            }
            return true;
        }


        // 修复3：确保资源消耗方法正确执行
        private void consumeResources() {
            if (recipes.isEmpty()) return;
            NewRecipe recipe = recipes.get(currentRecipe);

            // 消耗物品
            if (recipe.inputItems != null) {
                for (ItemStack stack : recipe.inputItems) {
                    this.items.remove(stack.item, stack.amount);
                }
            }

            // 消耗液体
            if (recipe.inputLiquids != null) {
                for (LiquidStack stack : recipe.inputLiquids) {
                    liquids.remove(stack.liquid, stack.amount);
                }
            }

            // 消耗电力（基于配方需求）
            if (recipe.powerUse > 0) {
                // 这里使用电力状态作为消耗依据，实际消耗已在电力网络中处理
                power.status = Math.max(0, power.status - (recipe.powerUse ) );
            }
        }
        private Object[] instanceConsumers;
        private void applyCurrentRecipe() {
            if (recipes.isEmpty()) return;
            // 修复：添加 instanceConsumers 变量定义


            currentRecipe = Mathf.clamp(currentRecipe, 0, recipes.size - 1);
            NewRecipe recipe = recipes.get(currentRecipe);

            currentCraftTime = recipe.craftTime;

            int newSize = 0;
            if (recipe.inputItems != null && recipe.inputItems.length > 0) newSize++;
            if (recipe.inputLiquids != null && recipe.inputLiquids.length > 0) newSize += recipe.inputLiquids.length;
            if (recipe.powerUse > 0) newSize++;

            instanceConsumers = new Object[newSize];
            int index = 0;

            if (recipe.inputItems != null && recipe.inputItems.length > 0) {
                instanceConsumers[index++] = recipe.inputItems;
            }

            if (recipe.inputLiquids != null && recipe.inputLiquids.length > 0) {
                for (LiquidStack stack : recipe.inputLiquids) {
                    instanceConsumers[index++] = stack;
                }
            }


            // 电力消耗更新
            // 修复：通过 ConsumePower 接口消耗电力，关联 PowerGraph
            if (recipe.powerUse > 0) {
                MFactory.this.consume(new ConsumePower((recipe.powerUse / 60f),2f,false)) ;// 关联 CustomBuilding 的 PowerGraph
        }}

        public boolean consValid() {
            if (recipes.isEmpty()) return false;
            NewRecipe recipe = recipes.get(currentRecipe);

            // 验证物品
            if (recipe.inputItems != null) {
                for (ItemStack stack : recipe.inputItems) {
                    if (this.items.get(stack.item) < stack.amount) {
                        return false;
                    }
                }
            }

            // 验证液体
            if (recipe.inputLiquids != null) {
                for (LiquidStack stack : recipe.inputLiquids) {
                    if (liquids.get(stack.liquid) < stack.amount) {
                        return false;
                    }
                }
            }

            // 验证电力
            if (activePowerConsumer != null) {
                return power.status > 0.1f;
            }

            return true;
        }

        public void craft() {
            super.craft();
            NewRecipe recipe = recipes.get(currentRecipe);
            if (recipe == null) return;

            // 产出物品
            if (recipe.outputItems != null) {
                for (ItemStack stack : recipe.outputItems) {
                    for (int i = 0; i < stack.amount; i++) {
                        offload(stack.item);
                    }
                }
            }

            // 产出液体
            if (recipe.outputLiquids != null) {
                for (LiquidStack stack : recipe.outputLiquids) {
                    float remaining = liquidCapacity - liquids.get(stack.liquid);
                    if (remaining >= stack.amount - 0.001f) {
                        liquids.add(stack.liquid, stack.amount);
                    } else {
                        dumpLiquid(stack.liquid, stack.amount);
                    }
                }
            }
        }
        @Override
        public void displayBars(Table table) {
            super.displayBars(table);

            // 显示生产进度条
            table.add(new Bar(
                    () -> "生产进度",
                    () -> Color.green,
                    () -> progress
            )).growX().row();

            if (activePowerConsumer != null) {
                table.add(new Bar(
                        () -> "电力状态",
                        () -> Color.yellow,
                        () -> power.status
                )).growX().row();
            }

            if (hasLiquids && liquids != null) {
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
        //public int currentRecipe = 0;
        public void buildConfiguration(Table table) {

            table.table(Styles.black5, t -> {
                t.add("配方选择").color(Color.yellow).center().row();
                t.image().color(Color.gray).height(2f).fillX().pad(5f).row();
                int a = 1;
                for (int i = 0; i < recipes.size; i++) {
                    int index = i;
                    NewRecipe recipe = recipes.get(i);
                    if (a % 10 == 0) {
                        t.button(btn -> buildRecipeButton(btn, recipe, index),
                                        Styles.flatToggleMenut, () -> {
                                            currentRecipe = index;
                                            needsRecipeUpdate = true;
                                            configure(index);
                                        }).update(btn -> btn.setChecked(index == currentRecipe))
                                .pad(2f).fillX().row();
                    } else {
                        t.button(btn -> buildRecipeButton(btn, recipe, index),
                                        Styles.flatToggleMenut, () -> {
                                            currentRecipe = index;
                                            needsRecipeUpdate = true;
                                            configure(index);
                                        }).update(btn -> btn.setChecked(index == currentRecipe))
                                .pad(2f).fillX();
                    }
                    a++;
                }
            }).fillX();
        }

        private void buildRecipeButton(Table btn, NewRecipe recipe, int index) {
            btn.table(b -> {
                b.add(recipe.name).color(index == currentRecipe ? Color.green : Color.white).row();

                if (recipe.inputItems != null && recipe.inputItems.length > 0) {
                    b.table(inputs -> {
                        inputs.add("物品输入: ");
                        for (ItemStack stack : recipe.inputItems) {
                            inputs.add(new ReqImage(stack.item.uiIcon,
                                            () -> items.get(stack.item) >= stack.amount))
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

                if (recipe.powerUse > 0) {
                    b.table(power -> {
                        power.add("电力消耗: " + recipe.powerUse + " 单位/分钟");
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

                if (recipe.outputLiquids != null && recipe.outputLiquids.length > 0) {
                    b.table(outputs -> {
                        outputs.add("流体存储: ");
                        for (LiquidStack stack : recipe.outputLiquids) {
                            outputs.image(stack.liquid.uiIcon).size(20f).pad(2f);
                            outputs.add(stack.amount + "/t ").padRight(4f);
                        }
                    }).left().row();
                }
            });
        }
        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            // 读取保存的配方索引（与 config 方法注册的类型一致）
            currentRecipe = read.i();
            needsRecipeUpdate = true; // 加载后触发配方更新
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            // 写入当前配方索引
            write.i(currentRecipe);
        }

    }
}
