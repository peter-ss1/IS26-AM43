package it.polimi.ingsw.am43;

public class FinalBuilding extends Building {
    private final FinalEffect effect;

    public FinalBuilding(int era, int cost, int prestigePoints, FinalEffect effect) {
        super(era,cost, prestigePoints);
        this.effect = effect;
    }

    @Override
    public void tribeEntranceEffect(Player player) {
        player.getTribe().addBuilding(this);
        player.alterFood(-(this.getCost()));
    }

    public void FinalBuildingEffect(Player player) {
       this.effect.manifest(player);
    }
}
