package api;
import mindustry.Vars;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;
public class GG_HXg extends CoreBlock {
    public GG_HXg(String name) {
        super(name);
    }
    public boolean canBreak(Tile tile) {
        return Vars.state.teams.cores(tile.team()).size>1;
    }
}
