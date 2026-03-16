package it.polimi.ingsw.am43.model.enums;

public enum GamePhase {
    PREPARATION,
    OFFER_TRACK_SELECTION,
    ACTION_RESOLUTION,
    ROUND_ENDING,
    DRAW_FROM_TOP_BONUS_ACTION,
    FINAL_COUNT;

    public GamePhase nextPhase(){return this;};

    public void resolvePhase(){};
}
