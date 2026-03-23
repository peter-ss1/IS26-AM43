package it.polimi.ingsw.am43.model.cards;

public abstract class CharacterCard extends TribeCard {
    private final String characterType;

    public CharacterCard(int era,int id, String characterType) {
        super(era,id);
        this.characterType= characterType ;
    }

    public String getCharacterType() {
        return characterType;
    }




}
