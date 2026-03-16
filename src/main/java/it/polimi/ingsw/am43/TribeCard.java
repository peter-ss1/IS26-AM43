package it.polimi.ingsw.am43;

public abstract class TribeCard extends Card{

    public TribeCard(int era) {
        super(era);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    public abstract void tribeEntranceEffect(Player player);

    public abstract InsertionStrategy getInsertionStrategy();
}
