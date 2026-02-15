package api;

import mindustry.Vars;
import mindustry.game.Team;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

public class GG_HX extends CoreBlock {
    public GG_HX(String name) {
        super(name);
    }
    public boolean canBreak(Tile tile) {
        return Vars.state.teams.cores(tile.team()).size>1;
    }
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        if (tile == null) {
            return false;
        } else if (Vars.state.isEditor()) {
            return true;
        }
        else if (Vars.state.teams.cores(team).size>9){
            return false;
        }else if (!Vars.state.isEditor()){
            return true;
        } else {
            CoreBuild core = team.core();
            tile.getLinkedTilesAs(this, tempTiles);
            if (!tempTiles.contains((o) -> !o.floor().allowCorePlacement || o.block() instanceof CoreBlock)) {
                return true;
            } else if (core != null && (Vars.state.rules.infiniteResources || core.items.has(this.requirements, Vars.state.rules.buildCostMultiplier))) {
                return tile.block() instanceof CoreBlock && this.size > tile.block().size && (!this.requiresCoreZone || tempTiles.allMatch((o) -> o.floor().allowCorePlacement));
            } else {
                return false;
            }
        }
    }

}
