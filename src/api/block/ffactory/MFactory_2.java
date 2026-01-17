package api.block.ffactory;

import api.block.AdaptCrafter;
import api.block.ConsumeRecipe;
import arc.math.Mathf;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Scaling;
import arc.util.Strings;
import arc.util.Time;
import mindustry.core.UI;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.type.PayloadStack;
import mindustry.ui.Styles;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValue;
import mindustry.world.meta.StatValues;

import static mindustry.world.meta.StatValues.withTooltip;

public class MFactory_2 extends AdaptCrafter {
    public Seq<Recipe_2> recipes = new Seq<>();
    public boolean HaveOutputItems=true;//是否有物品输出
    public boolean AutomaticOutPutLiquids=true;//流体自动向周围输出
    public static int P_recipeIndex;
    //public Floatf<Building> multiplier_2 = b -> 1f;
    public MFactory_2(String name) {
        super(name);
        this.dumpTime=4;
        this.rotate = false;//贴图不转
        this.canMirror=true;//是否镜像
        consume(new ConsumeRecipe(MFactory_2.RecipeGenericCrafterBuild_2::getRecipe, MFactory_2.RecipeGenericCrafterBuild_2::getDisplayRecipe));
    }

    public void addInput(Object...objects) {
        Recipe_2 recipe = new Recipe_2(objects);
        recipes.add((Recipe_2) recipe);
    }
    public boolean outputsItems(){//返回true传送带与工厂有贴图
        return HaveOutputItems;
    }
    public boolean outputsLiquid = false;//返回true液体管道与工厂有贴图

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.input, display());
        stats.remove(Stat.output);
    }
    @Override
    public void setBars(){
        super.setBars();

        //set up liquid bars for liquid outputs
        if(P_recipeIndex>-1){
            //no need for dynamic liquid bar
            removeBar("liquid");
            //then display input buffer
            for(var stack : recipes.get(P_recipeIndex).inputLiquid) {
                if (stack!=null) addLiquidBar(stack.liquid);
            }
                //then display output buffer
            for(var stack : recipes.get(P_recipeIndex).outputLiquid){
                if (stack!=null) addLiquidBar(stack.liquid);
            }
        }
    }



    public StatValue display() {
        return table -> {
            table.row();
            table.table(cont -> {
                for (int i = 0; i < recipes.size; i++){
                    Recipe_2 recipe = recipes.get(i);
                    int finalI = i;
                    cont.table(t -> {
                        t.left().marginLeft(12f).add("[accent][" + (finalI + 1) + "]:[]").width(48f);
                        t.table(inner -> {
                            inner.table(row -> {
                                row.left();
                                recipe.inputItem.each(stack -> row.add(display(stack.item, stack.amount, craftTime / recipe.boostScl)));
                                recipe.inputLiquid.each(stack -> row.add(StatValues.displayLiquid(stack.liquid, stack.amount * Time.toSeconds, true)));
                                //recipe.inputPayload.each(stack -> row.add(display(stack.item, stack.amount, craftTime / recipe.boostScl)));
                            }).growX();
                            if (inner.getPrefWidth() > 320f) inner.row();
                            inner.table(row -> {
                                row.left();
                                row.image(Icon.right).size(32f).padLeft(8f).padRight(12f);
                                recipe.outputItem.each(stack -> row.add(display(stack.item, stack.amount, craftTime / recipe.boostScl)));
                                recipe.outputLiquid.each(stack -> row.add(StatValues.displayLiquid(stack.liquid, stack.amount * Time.toSeconds, true)));
                                if (outputItems != null) {
                                    for (var stack: outputItems){
                                        row.add(display(stack.item, Mathf.round(stack.amount * recipe.craftScl), craftTime / recipe.boostScl));
                                    }
                                }
                                if (outputLiquids != null) {
                                    for (var stack: outputLiquids){
                                        row.add(display(stack.liquid, stack.amount * craftTime * recipe.craftScl, craftTime / recipe.boostScl));
                                    }
                                }
                                if (outputPayloads != null) {
                                    for (var stack: outputPayloads){
                                        row.add(display(stack.item, Mathf.round(stack.amount * recipe.craftScl), craftTime / recipe.boostScl));
                                    }
                                }
                            }).growX();
                        });
                    }).fillX();
                    cont.row();
                }
            });
        };
    }

    public static Table display(UnlockableContent content, float amount, float timePeriod){
        Table table = new Table();
        Stack stack = new Stack();

        stack.add(new Table(o -> {
            o.left();
            o.add(new Image(content.uiIcon)).size(32f).scaling(Scaling.fit);
        }));

        if(amount != 0){
            stack.add(new Table(t -> {
                t.left().bottom();
                t.add(amount >= 1000 ? UI.formatAmount((int)amount) : Strings.autoFixed(amount, 2)).style(Styles.outlineLabel);
                t.pack();
            }));
        }

        withTooltip(stack, content);

        table.add(stack);
        table.add((content.localizedName + "\n") + "[lightgray]" + Strings.autoFixed(amount / (timePeriod / 60f), 2) + StatUnit.perSecond.localized()).padLeft(2).padRight(5).style(Styles.outlineLabel);
        return table;
    }

    @Override
    public void init() {
        super.init();
        recipes.each(recipe -> {
            recipe.inputItem.each(stack -> itemFilter[stack.item.id] = true);
            recipe.inputLiquid.each(stack -> liquidFilter[stack.liquid.id] = true);//设置过滤判断需要物品或流体
            //recipe.outputItem.each(stack -> itemFilter[stack.item.id] = true);
            //recipe.outputLiquid.each(stack -> liquidFilter[stack.liquid.id] = true);
            //recipe.inputPayload.each(stack -> payloadFilter.add(stack.item));
        });
    }

    public class RecipeGenericCrafterBuild_2 extends AdaptCrafterBuild {
        public int recipeIndex = -1;

        public Recipe_2 getRecipe() {
            if (recipeIndex < 0 || recipeIndex >= recipes.size) return null;
            return recipes.get(recipeIndex);
        }

        public Recipe_2 getDisplayRecipe() {
            if (recipeIndex < 0 && recipes.size > 0) {
                return recipes.first();
            }
            return getRecipe();
        }

        @Override
        public float getPowerProduction() {
            return super.getPowerProduction();
        }

        public void updateRecipe() {
            for (int i = 0; i < recipes.size; i++) {//是指配方数
                boolean valid = true;

                for (ItemStack input : recipes.get(i).inputItem) {
                    if (items.get(input.item) < input.amount) {
                        valid = false;
                        break;
                    }
                }

                for (LiquidStack input : recipes.get(i).inputLiquid) {
                    if (liquids.get(input.liquid) < input.amount * Time.delta) {
                        valid = false;
                        break;
                    }
                }

                for (PayloadStack input : recipes.get(i).inputPayload) {
                    if (getPayloads().get(input.item) < input.amount) {
                        valid = false;
                        break;
                    }
                }

                if (valid) {
                    recipeIndex = i;
                    return;
                }
            }
            recipeIndex = -1;
        }

        public boolean validRecipe() {
            if (recipeIndex < 0) return false;
            for (ItemStack input : recipes.get(recipeIndex).inputItem) {
                if (items.get(input.item) < input.amount) {
                    return false;
                }
            }

            for (LiquidStack input : recipes.get(recipeIndex).inputLiquid) {
                if (liquids.get(input.liquid) < input.amount * Time.delta) {
                    return false;
                }
            }

            for (ItemStack output : recipes.get(recipeIndex).outputItem) {
              if (items.get(output.item) < output.amount) {
                  return false;
              }
            }

            for (PayloadStack input : recipes.get(recipeIndex).inputPayload) {
                if (getPayloads().get(input.item) < input.amount) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void updateTile() {
            if (!validRecipe()) updateRecipe();
            P_recipeIndex=recipeIndex;
            Recipe_output_Liquid();
            if (timer(timerDump, dumpTime / timeScale)) dumpOutputs();
            super.updateTile();
        }

        @Override
        public float getProgressIncrease(float baseTime) {
            float scl = 0f;
            if (!(recipeIndex < 0 || recipeIndex >= recipes.size)) scl = recipes.get(recipeIndex).boostScl;
            return super.getProgressIncrease(baseTime) * scl;
        }

        @Override
        public void craft() {
            consume();
            if (getRecipe() == null) return;

            for (var stack:recipes.get(recipeIndex).outputItem){
                if (stack.item!=null) {
                    System.out.println(stack.item+","+stack.amount);
                    new_offload(stack.item, stack.amount);
                }
            }
            if(outputItems != null){
                for(var output : outputItems){
                    for(int i = 0; i < Mathf.round(output.amount * getRecipe().craftScl); i++){
                        offload(output.item);
                    }
                }
            }

            if(outputPayloads != null){
                for(PayloadStack output : outputPayloads){
                    payloads.add(output.item, Mathf.round(output.amount * getRecipe().craftScl));
                }
            }

            if(wasVisible){
                craftEffect.at(x, y);
            }

            progress %= 1f;

            updateRecipe();
        }

        public void new_offload(Item item ,int amount) {
            produced(item, amount);
            int dump = dumpIndex,a=amount,b;
            for (int i = 0; i < linkProximityMap.size; i++) {
                incrementDumpIndex(linkProximityMap.size);
                int idx = (i + dump) % linkProximityMap.size;
                Building[] pair = linkProximityMap.get(idx);
                Building target = pair[0];
                Building source = pair[1];
                if (target.acceptItem(source, item) && canDump(target, item)) {
                    for (int j=0;j<amount;j++) {
                        target.handleItem(source, item);
                    }
                    return;
                }
            }
            //System.out.print("调用了a"+item);
            for (int i=0;i<amount;i++){
            handleItem(this, item);
            //System.out.print("调用了"+i);
            }
        }
        public void dumpOutputs() {
            for (int i = 0; i < recipes.size; i++) {
                for (ItemStack outputs : recipes.get(i).outputItem) {
                    if (outputs != null ) {
                        dump(outputs.item);
                    }
                }
                for (LiquidStack outLiquids:recipes.get(i).outputLiquid){
                    if (outLiquids != null) {
                        if (AutomaticOutPutLiquids){
                            int dir = MFactory_2.this.liquidOutputDirections.length > i ? MFactory_2.this.liquidOutputDirections[i] : -1;
                            this.dumpLiquid(outLiquids.liquid, 2.0F, dir);
                        }
                    }
                }
            }
        }
        public void Recipe_output_Liquid(){
            if (recipeIndex>-1 && shouldConsume()){
                if (recipes.get(recipeIndex).outputLiquid != null){
                    float inc = getProgressIncrease(1f);
                    for (LiquidStack stack:recipes.get(recipeIndex).outputLiquid){
                        handleLiquid(this,stack.liquid,Math.min(stack.amount * inc, liquidCapacity - liquids.get(stack.liquid)));
                    }//stack.amount * this.edelta() * multiplier_2.get(this)
                }
            }
        }

        public boolean shouldConsume(){
            if (recipeIndex>-1){
                for (ItemStack output:recipes.get(recipeIndex).outputItem) {
                    if (output != null) {
                        if (items.get(output.item) + output.amount > itemCapacity) {
                                return false;
                        }
                    }
                }
            }
            super.shouldConsume();
            return enabled;
        }
    }
}
