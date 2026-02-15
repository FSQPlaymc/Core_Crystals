package api.more_factory;

//import mindustry.gen.Direction;


//import static com.sun.org.apache.xalan.internal.lib.ExsltMath.power;
//import static mindustry.world.meta.StatValues.items;

/*public class ls extends GenericCrafter {
    public Seq<NewRecipe> recipes = new Seq<>();

    public ls(String name) {
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
        // 1. 定义控制变量（新增这行）
        private boolean needsRecipeUpdate = false;
        // 存储当前实例的消耗配置（每个建筑独立）
        private Consume[] instanceConsumers;
        // 添加这行：实例化生产时间变量
        private float currentCraftTime = 1f;  // 默认值确保不出现除以0错误
       // private float instanceCraftTime;
        private ItemStack instanceOutputItem;
        // 更新tile逻辑（使用修正后的consValid()）
        @Override
        public void updateTile() {
            if (needsRecipeUpdate) {
                applyCurrentRecipe();
                needsRecipeUpdate = false;
            }

            if (consValid() && efficiency > 0) {
                // 使用实例化的生产时间
                progress += delta() / currentCraftTime;
                if (progress >= 1f) {
                    craft();
                    progress %= 1f;
                }
            } else {
                progress = 0f;
            }
        }

        @Override
        public float getProgressIncrease(float baseTime) {
            // 使用实例化的生产时间
            return efficiency * delta() / currentCraftTime;
        }
        // 修复1：重写资源消耗方法，使用正确的消费逻辑
        // 修复1：正确的物品消耗逻辑（使用items()而非StatValue）
        private void consumeItems(ItemStack[] items) {
            for (ItemStack stack : items) {
                // 使用items().remove()的正确参数形式
                items().remove(stack.item, stack.amount);
            }
        }

        // 修复2：正确的液体消耗逻辑
        private void consumeLiquids(LiquidStack stack) {
            liquids.remove(stack.liquid, stack.amount);
        }

        // 修复3：正确的电力消耗逻辑（power()需要参数）
        private void consumePower(float amount) {
            // power()方法需要传入Tile参数，使用this.tile
            power(this.tile).use(amount * delta());
        }

        private void applyCurrentRecipe() {
            if (recipes.isEmpty()) return;

            currentRecipe = Mathf.clamp(currentRecipe, 0, recipes.size - 1);
            NewRecipe recipe = recipes.get(currentRecipe);

            // 关键修复：使用配方的生产时间覆盖实例变量
            currentCraftTime = recipe.craftTime;  // 替换原 craftTime 赋值
            instanceOutputItem = recipe.outputItems.length > 0 ? recipe.outputItems[0] : null;

            // 重新计算消耗数组（关键修复：使用实例的consumers而非block的）
            int newSize = 0;
            if (recipe.inputItems != null && recipe.inputItems.length > 0) newSize++;
            if (recipe.inputLiquids != null && recipe.inputLiquids.length > 0) newSize += recipe.inputLiquids.length;
            if (recipe.powerUse > 0) newSize++;

            instanceConsumers = new Consume[newSize];
            int index = 0;

            // 添加物品消耗（设置正确的验证器）
            if (recipe.inputItems != null && recipe.inputItems.length > 0) {
                instanceConsumers[index++] = new ConsumeItems(recipe.inputItems){{
                    update = true;
                    // 显式设置验证器
                }};
            }

            // 4. 添加液体消耗
            if (recipe.inputLiquids != null && recipe.inputLiquids.length > 0) {
                for (LiquidStack stack : recipe.inputLiquids) {
                    instanceConsumers[index++] = new ConsumeLiquid(stack.liquid, stack.amount){{
                        update = true;
                    }};
                }
            }

            // 5. 添加电力消耗（仅当powerUse > 0时）
            // 修复2：正确构造电力消耗
            if (recipe.powerUse > 0) {
                // 使用正确的ConsumePower构造方法
                instanceConsumers[index++] = new ConsumePower(1f, recipe.powerUse / 60f, false){{
                    update = true;
                }};
            }

            // 6. 验证并替换消耗数组
            for (Consume cons : instanceConsumers) {
                if (cons == null) {
                    throw new RuntimeException("Consumers array contains null! Recipe: " + recipe.name);
                }
            }
            consumers = instanceConsumers;
        }
        // 修复5：重写资源验证方法，使用正确的验证逻辑
        // 修复4：正确的资源验证逻辑（使用items()而非StatValue）
        public boolean consValid() {
            if (instanceConsumers == null) return false;

            for (Object consumer : instanceConsumers) {
                if (consumer instanceof ItemStack[]) {
                    for (ItemStack stack : (ItemStack[]) consumer) {
                        // 使用items().get()的正确形式
                        if (items().get(stack.item) < stack.amount) {
                            return false;
                        }
                    }
                }
                else if (consumer instanceof LiquidStack) {
                    LiquidStack stack = (LiquidStack) consumer;
                    if (liquids.get(stack.liquid) < stack.amount) {
                        return false;
                    }
                }
                else if (consumer instanceof Float) {
                    // 电力验证：power()需要传入Tile参数
                    if (power(this.tile).buffered() < (Float) consumer) {
                        return false;
                    }
                }
            }
            return true;
        }

        public void craft() {
            super.craft();
            NewRecipe recipe = recipes.get(currentRecipe);
            if (recipe == null) return;

            // 关键：先消耗资源
            consumeResources();

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





            @Override
        public void buildConfiguration(Table table) {
            table.table(Styles.black5, t -> {
                t.add("配方选择").color(Color.yellow).center().row();
                t.image().color(Color.gray).height(2f).fillX().pad(5f).row();
                int a=1;
                for (int i = 0; i < recipes.size; i++) {


                    int index = i;
                    NewRecipe recipe = recipes.get(i);
                    if (a%10==0){t.button(btn -> {
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
                    },
                    Styles.flatToggleMenut, () -> {
                    currentRecipe = index;
                    needsRecipeUpdate = true; // 配方切换时设置标志
                    configure((byte) index);
                    }).update(btn -> btn.setChecked(index == currentRecipe))
                    .pad(2f).fillX().row();}else {t.button(btn -> {
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
                    },
                    Styles.flatToggleMenut, () -> {
                    currentRecipe = index;
                    needsRecipeUpdate = true; // 配方切换时设置标志
                    configure((byte) index);
                    }).update(btn -> btn.setChecked(index == currentRecipe))
                    .pad(2f).fillX();}
                    a++;
                }

                //.row()
                    /*t.button(btn -> {
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
                            }
                            ,
                            Styles.flatToggleMenut, () -> {
                                currentRecipe = index;
                                needsRecipeUpdate = true; // 配方切换时设置标志
                                configure((byte) index);
                            }).update(btn -> btn.setChecked(index == currentRecipe))
                            .pad(2f).fillX().row();


            }).fillX();}


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
    }
}*/