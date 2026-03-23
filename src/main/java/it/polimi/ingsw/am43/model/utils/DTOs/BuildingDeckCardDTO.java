package it.polimi.ingsw.am43.model.utils.DTOs;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am43.model.cards.*;
import it.polimi.ingsw.am43.model.utils.DTOs.BuildingDeckCardDTO.*;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "subClass"
)

@JsonSubTypes({
        @JsonSubTypes.Type(value = FinalBuildingDTO.class, name = "FINAL"),
        @JsonSubTypes.Type(value = TribeBuildingDTO.class, name = "TRIBE"),
        @JsonSubTypes.Type(value = TribeBuildingDTO.class, name = "TIMED"),
        @JsonSubTypes.Type(value = HuntEventBuildingDTO.class, name = "HUNT_BUILDING"),
        @JsonSubTypes.Type(value = RitualEventBuildingDTO.class, name = "RITUAL_BUILDING"),
        @JsonSubTypes.Type(value = SustenanceEventBuilding.class, name = "SUSTENANCE_BUILDING"),
        @JsonSubTypes.Type(value = PaintingEventBuildingDTO.class, name = "PAINTING_BUILDING"),

})

public abstract class BuildingDeckCardDTO {
    public Integer era;
    public Integer id;
    public Integer cost;
    public Integer prestigePoints;

    public abstract Building createBuilding();

    public static class FinalBuildingDTO extends BuildingDeckCardDTO{
        public FinalEffect effect;

        @Override
        public Building createBuilding() {
            return new FinalBuilding(this.era,this.id,this.cost,this.prestigePoints,this.effect);
        }
    }

    public static class TimedBuildingDTO extends BuildingDeckCardDTO{
        public TimedEffect effect;

        @Override
        public Building createBuilding() {
            return new TimedBuilding(this.era,this.id,this.cost,this.prestigePoints,this.effect);
        }
    }

    public static class TribeBuildingDTO extends BuildingDeckCardDTO{
        public Integer lastGivenBonus;
        public TribeBonus bonus;

        @Override
        public Building createBuilding() {
            return new TribeBuilding(this.era,this.id,this.cost,this.prestigePoints,this.bonus);
        }
    }

    public static class HuntEventBuildingDTO extends BuildingDeckCardDTO{
        public EventEffect effect;
        @Override
        public Building createBuilding() {
            return new HuntEventBuilding(this.era,this.id,this.cost,this.prestigePoints,this.effect);
        }
    }

    public static class PaintingEventBuildingDTO extends BuildingDeckCardDTO {
        public EventEffect effect;
        @Override
        public Building createBuilding() {
            return new PaintingEventBuilding(this.era,this.id,this.cost,this.prestigePoints,this.effect);
        }
    }

    public static class RitualEventBuildingDTO extends BuildingDeckCardDTO {
        public EventEffect effect;
        @Override
        public Building createBuilding() {
            return new RitualEventBuilding(this.era,this.id,this.cost,this.prestigePoints,this.effect);
        }
    }

    public static class SustenanceEventBuildingDTO extends BuildingDeckCardDTO{
        public EventEffect effect;
        @Override
        public Building createBuilding() {
            return new SustenanceEventBuilding(this.era,this.id,this.cost,this.prestigePoints,this.effect);
        }
    }



}
