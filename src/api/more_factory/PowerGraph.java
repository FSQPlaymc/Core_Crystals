package api.more_factory;

public class PowerGraph {
    // 电力存储相关属性
    private float storedPower; // 当前存储的电力
    private float maxPower;    // 最大可存储电力

    // 构造函数，初始化最大电力存储容量
    public PowerGraph(float maxPower) {
        this.maxPower = maxPower;
        this.storedPower = 0f;
    }

    // 检查是否有足够的电力满足需求
    public boolean hasPower(float amount) {
        // 确保需求为正数且当前存储的电力足够
        return amount > 0 && storedPower >= amount;
    }

    // 消耗指定数量的电力
    public void usePower(float amount) {
        // 只有在电力充足的情况下才消耗
        if (hasPower(amount)) {
            storedPower -= amount;
            // 确保电力不会低于0
            if (storedPower < 0) {
                storedPower = 0;
            }
        }
    }

    // 补充电力（供发电设备使用）
    public void addPower(float amount) {
        if (amount <= 0) return;

        storedPower += amount;
        // 确保电力不会超过最大存储容量
        if (storedPower > maxPower) {
            storedPower = maxPower;
        }
    }

    // 获取当前存储的电力
    public float getStoredPower() {
        return storedPower;
    }

    // 获取最大可存储电力
    public float getMaxPower() {
        return maxPower;
    }

    // 计算电力填充比例（0-1），用于UI显示
    public float getFillRatio() {
        return maxPower > 0 ? storedPower / maxPower : 0f;
    }
}
    