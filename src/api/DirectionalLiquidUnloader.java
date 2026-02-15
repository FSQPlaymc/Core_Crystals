package api;

import api.more_factory.MFactory;
import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.layout.Table;
import arc.util.Eachable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.blocks.liquid.LiquidBlock;
import mindustry.world.meta.BlockGroup;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.blocks.storage.CoreBlock;
import arc.graphics.g2d.Draw;
import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.ui.ReqImage;
import mindustry.ui.Styles;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class DirectionalLiquidUnloader extends Block {
    // 在此处添加字段定义
    public Liquid unloadLiquid = null; // 目标卸载流体
    // 纹理资源（保持原结构，可根据流体特性调整）
    // 纹理资源字段（已定义，补充加载逻辑）
    public TextureRegion centerRegion;
    public TextureRegion topRegion;
    public TextureRegion arrowRegion;
    // 卸载速度（每秒传输的流体单位，原代码为物品/秒，这里改为液体单位/秒）
    public float speed = 10f;
    // 是否允许从核心卸载流体
    public boolean allowCoreUnload = false;

    public DirectionalLiquidUnloader(String name) {
        super(name);
        this.group = BlockGroup.transportation;
        this.update = true;
        this.solid = true;
        // 支持流体传输
        this.hasLiquids = true;
        this.configurable = true;
        this.saveConfig = true;
        this.rotate = true;
        // 不需要物品容量
        this.itemCapacity = 0;
        this.noUpdateDisabled = true;
        this.unloadable = false;
        this.isDuct = true;
        this.envDisabled = 0;
        this.clearOnDoubleTap = true;
        this.priority = -1.0F;

        // 配置逻辑：允许指定卸载的流体类型
        this.config(Liquid.class, (DirectionalLiquidUnloaderBuild tile, Liquid liquid) -> tile.unloadLiquid = liquid);
        this.configClear((DirectionalLiquidUnloaderBuild tile) -> tile.unloadLiquid = null);
    }
    public void load() {
        super.load();
        // 加载基础纹理（底座）
        region = Core.atlas.find(name);
        // 加载顶部纹理（旋转部分）
        topRegion = Core.atlas.find(name + "-top");
        // 加载中心纹理（显示流体颜色用）
        centerRegion = Core.atlas.find(name + "-center");
        // 加载箭头纹理（默认状态显示）
        arrowRegion = Core.atlas.find(name + "-arrow");

        // 容错处理：如果某个纹理未找到，使用默认纹理避免空指针
        if (region == null) region = Core.atlas.find("clear");
        if (topRegion == null) topRegion = region;
        if (centerRegion == null) centerRegion = Core.atlas.find("white-pixel");
        if (arrowRegion == null) arrowRegion = Core.atlas.find("arrow-right");
    }

    // 加载图标（用于UI显示）
    @Override
    public void loadIcon() {
        // 优先使用专用图标，否则使用底座+顶部纹理组合
        fullIcon = uiIcon = Core.atlas.find(name + "-icon");
    }

    // 更新统计信息（显示流体传输速率）
    public void setStats() {
        super.setStats();
        this.stats.add(Stat.speed, speed, StatUnit.liquidSecond); // 直接显示每秒传输的流体单位
    }

    // 蓝图渲染（保持原逻辑，纹理可调整）
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.rect(this.region, plan.drawx(), plan.drawy());
        Draw.rect(this.topRegion, plan.drawx(), plan.drawy(), (float)(plan.rotation * 90));
        this.drawPlanConfig(plan, list);
    }

    public void drawPlanConfig(BuildPlan plan, Eachable<BuildPlan> list) {
        this.drawPlanConfigCenter(plan, plan.config, "duct-unloader-center");
    }

    // 移除物品相关的显示条（因为处理流体）
    public void setBars() {
        super.setBars();
        this.removeBar("items");
        // 可添加流体传输状态条（可选）
        this.addBar("liquid", (DirectionalLiquidUnloaderBuild entity) ->
                new Bar(
                        () -> "传输流体: " + (entity.unloadLiquid == null ? "全部" : entity.unloadLiquid.localizedName),
                        () -> entity.unloadLiquid == null ? Color.lightGray : entity.unloadLiquid.color,
                        () -> 1f // 简单显示为满状态，可根据实际传输效率调整
                )
        );
    }

    public TextureRegion[] icons() {
        return new TextureRegion[]{this.region, this.topRegion, this.arrowRegion};
    }

    public class DirectionalLiquidUnloaderBuild extends Building {
        // 1. 【修复：定义unloadLiquid字段】
        // 错误位置：原代码中未显式定义该字段，导致"无法解析符号 'unloadLiquid'"
        public Liquid unloadLiquid = null;
        public float unloadTimer = 0.0F;
        public int offset = 0;
        @Override
        public void updateTile() {
            if ((unloadTimer += edelta()) >= 1f) {
                Building front = this.front();
                Building back = this.back();

                // 2. 【修复：hasLiquids属于block属性】
                // 错误位置：原代码使用back.hasLiquids，正确应为back.block.hasLiquids
                if (front != null && back != null && back.block.hasLiquids &&
                        front.team == this.team && back.team == this.team && back.canUnload()) {

                    // 检查是否允许从核心卸载
                    if (!allowCoreUnload) {
                        if (back instanceof CoreBlock.CoreBuild) {
                            // 禁止从核心卸载，跳过
                            unloadTimer %= 1f;
                            return;
                        }
                        if (back instanceof StorageBlock.StorageBuild) {
                            StorageBlock.StorageBuild sb = (StorageBlock.StorageBuild) back;
                            if (sb.linkedCore != null) {
                                // 禁止从关联核心的存储卸载，跳过
                                unloadTimer %= 1f;
                                return;
                            }
                        }
                    }

                    // 传输流体逻辑
                    if (unloadLiquid != null) {
                        // 模式1：指定流体传输
                        transferLiquid(back, front, unloadLiquid);
                    } else {
                        // 模式2：自动传输所有流体（循环检查）
                        Seq<Liquid> liquids = Vars.content.liquids();
                        int total = liquids.size;
                        for (int i = 0; i < total; i++) {
                            Liquid liquid = liquids.get((i + offset) % total);
                            if (back.liquids.get(liquid) > 0.001f) { // 后方有该流体
                                if (transferLiquid(back, front, liquid)) {
                                    offset = (offset + 1) % total; // 下次从下一个流体开始检查
                                    break;
                                }
                            }
                        }
                    }
                }
                unloadTimer %= 1f; // 重置计时器
            }
        }

        // 实际传输流体的方法
        private boolean transferLiquid(Building source, Building target, Liquid liquid) {
            float amount = speed * 1f;
            float available = source.liquids.get(liquid);
            if (available <= 0.001f) return false;
            amount = Math.min(amount, available);

            if (!target.acceptLiquid(this, liquid)) return false;

            // 修复：根据目标建筑类型获取液体容量
            float targetCapacity = 0f;
            boolean hasCapacity = false;

            // 1. 处理自定义制造机（你的代码中定义了 liquidCapacity）

            // 2. 处理 adasd 包下的 CustomCrafterBuild
            if (target instanceof MFactory.CustomCrafterBuild) {
                targetCapacity = ((MFactory.CustomCrafterBuild) target).liquidCapacity - target.liquids.get(liquid);
                hasCapacity = true;
            }
            // 3. 处理 AdaptCrafter 建筑（你的代码中使用了 liquidCapacity）
            // 4. 处理 Mindustry 原生液体存储建筑（如 LiquidBlock）
            else if (target instanceof mindustry.world.blocks.liquid.LiquidBlock.LiquidBuild) {
                // 引用外部类 LiquidBlock 的 liquidCapacity 字段
                targetCapacity = target.block.liquidCapacity - target.liquids.get(liquid);
                hasCapacity = true;
            }
            // 5. 处理链接块（将容量计算委托给主建筑）
            else if (target instanceof api.block.LinkBlock.LinkBuild) {
                Building mainBuild = ((api.block.LinkBlock.LinkBuild) target).linkBuild;
                if (mainBuild instanceof MFactory.CustomCrafterBuild) {
                    targetCapacity = ((MFactory.CustomCrafterBuild) mainBuild).liquidCapacity - target.liquids.get(liquid);
                    hasCapacity = true;
                }
                else if (mainBuild instanceof LiquidBlock.LiquidBuild) {
                    targetCapacity = mainBuild.block.liquidCapacity - target.liquids.get(liquid);
                    hasCapacity = true;
                }
            }

            // 不支持的建筑类型或容量计算失败
            if (!hasCapacity) return false;

            if (targetCapacity < 0.001f) return false;

            float transferred = Math.min(amount, targetCapacity);

            source.liquids.remove(liquid, transferred);
            target.liquids.add(liquid, transferred);

            //float transferred = Math.min(amount, targetCapacity);

            source.liquids.remove(liquid, transferred);
            target.liquids.add(liquid, transferred);

            // 3. 【修复：移除或替换liquidTaken方法】
            // 错误位置：原代码调用source.liquidTaken()，但Building类无此方法
            // 解决方案1：移除该调用（推荐，因非必需逻辑）
            // 解决方案2：仅在特定类型建筑中调用（如下）
            /*
            if (source instanceof LiquidBlock.Build liquidBuild) {
                liquidBuild.liquidTaken(liquid, transferred);
            }
            */

            return true;
        }

        @Override
        public void draw() {
            // 绘制底座和顶部纹理
            Draw.rect(DirectionalLiquidUnloader.this.region, x, y);
            Draw.rect(DirectionalLiquidUnloader.this.topRegion, x, y, rotdeg());

            // 根据配置显示不同状态：指定流体则显示流体颜色，否则显示箭头
            if (unloadLiquid != null) {
                Draw.color(unloadLiquid.color);
                Draw.rect(DirectionalLiquidUnloader.this.centerRegion, x, y);
                Draw.color(); // 重置颜色
            } else {
                Draw.rect(DirectionalLiquidUnloader.this.arrowRegion, x, y, rotdeg());
            }
        }

        @Override
        public void drawSelect() {
            super.drawSelect();
            // 选中时显示当前配置的流体（若有）
            if (unloadLiquid != null) {
                Drawf.select(x, y, size * 4f, unloadLiquid.color);
            }
        }

        // 构建配置界面：选择要卸载的流体
        //@Override
        public void buildConfiguration(Table table) {
            table.table(Styles.black5, t -> {
                t.add("选择流体").color(Color.yellow).center().row();
                t.image().color(Color.gray).height(2f).fillX().pad(5f).row();

                // 遍历所有流体，生成选择按钮
                Seq<Liquid> liquids = Vars.content.liquids();
                int cols = 4; // 每行显示4个流体
                Table content = new Table();
                t.add(content).left().row();

                for (int i = 0; i < liquids.size; i++) {
                    Liquid liquid = liquids.get(i);
                    int index = i;
                    // 每个流体按钮
                    content.button(btn -> {
                        btn.table(b -> {
                            b.add(new ReqImage(liquid.uiIcon, () -> true)).size(20f).pad(2f);
                            b.add(liquid.localizedName).color(unloadLiquid == liquid ? Color.green : Color.white);
                        });
                    }, () -> {
                        // 点击切换选中的流体（再次点击取消）
                        configure(unloadLiquid == liquid ? null : liquid);
                    }).size(120f, 30f);

                    if ((index + 1) % cols == 0) {
                        content.row();
                    }
                }
            }).pad(10f);
        }

        @Override
        public Object config() {
            return unloadLiquid;
        }

        // 保存数据
        @Override
        public void write(Writes write) {
            super.write(write);
            write.s(unloadLiquid == null ? -1 : unloadLiquid.id);
            write.s(offset);
        }

        // 读取数据
        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            int id = read.s();
            unloadLiquid = id == -1 ? null : Vars.content.liquids().get(id);
            offset = read.s();
        }
    }
}
