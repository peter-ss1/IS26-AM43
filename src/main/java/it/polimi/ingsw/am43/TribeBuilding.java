package it.polimi.ingsw.am43;

public class TribeBuilding extends Building {
    private final TribeBonus bonus;
    private int alreadyGivenBonus;

    public TribeBuilding(int era, int cost, int prestigePoints, TribeBonus bonus) {
        super(era,cost, prestigePoints);
        this.bonus = bonus;
        this.alreadyGivenBonus = 0;
    }

    @Override
    public void tribeEntranceEffect(Player player) {
        player.getTribe().addBuilding(this);
        player.alterFood(-(this.getCost()));
        alreadyGivenBonus = this.bonus.giveBonus(player);
    }

    public void TribeBuildingEffect(Player player) {
        player.alterFood(this.bonus.giveBonus(player) - this.alreadyGivenBonus);
    }
}
