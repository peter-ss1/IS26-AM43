package it.polimi.ingsw.am43.model.cards;

public abstract class CharacterCard extends TribeCard {
    private final String characterType;

    public CharacterCard(int era, String characterType) {
        super(era);
        this.characterType= characterType ;
    }

    public String getCharacterType() {
        return characterType;
    }




}
