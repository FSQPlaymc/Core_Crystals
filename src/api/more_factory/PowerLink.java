package api.more_factory;

// 新增电力连接辅助类，解决Building与PowerGraph的关联问题
public class PowerLink {
    public PowerGraph graph; // 关联电力网络
    
    // 初始化电力网络
    public void init(float maxPower) {
        this.graph = new PowerGraph(maxPower);
    }
}
    