package it.polimi.ingsw.am43.model.utils.DTOs;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.am43.model.cards.*;
import it.polimi.ingsw.am43.model.enums.InventorSymbol;
import it.polimi.ingsw.am43.model.utils.DTOs.TribeDeckCardDTO.*;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "classTribe"
)

@JsonSubTypes({
        @JsonSubTypes.Type(value = HunterDTO.class, name = "HUNTER"),
        @JsonSubTypes.Type(value = InventorDTO.class, name = "INVENTOR"),
        @JsonSubTypes.Type(value = BuilderDTO.class, name = "BUILDER"),
        @JsonSubTypes.Type(value = ArtistDTO.class, name = "ARTIST"),
        @JsonSubTypes.Type(value = GathererDTO.class, name = "GATHERER"),
        @JsonSubTypes.Type(value = ShamanDTO.class, name = "SHAMAN"),
        @JsonSubTypes.Type(value = SustenanceDTO.class, name = "SUSTENANCE"),
        @JsonSubTypes.Type(value = HuntDTO.class, name = "HUNT"),
        @JsonSubTypes.Type(value = PaintDTO.class, name = "PAINT"),
        @JsonSubTypes.Type(value = RitualDTO.class, name = "RITUAL"),
})

public abstract class TribeDeckCardDTO {
    public Integer era;
    public Integer id;
    public Integer minPlayer;
    public boolean isFinal=false;

    public abstract Card createCard();

    public static class ArtistDTO extends TribeDeckCardDTO {
        @Override
        public Card createCard(){
            return new Artist(this.era,this.id);
        }
    }

    public static class BuilderDTO extends TribeDeckCardDTO {
        public Integer buildingDiscount;
        public Integer prestigePoint;
        @Override
        public Card createCard(){
            return new Builder(this.era,this.id, this.buildingDiscount, this.prestigePoint);
        }
    }

    public static class GathererDTO extends TribeDeckCardDTO {
        @Override
        public Card createCard(){
            return new Gatherer(this.era,this.id);
        }
    }

    public static class HunterDTO extends TribeDeckCardDTO {
        public Boolean active;
        @Override
        public Card createCard(){
            return new Hunter(this.era,this.id,this.active);
        }
    }

    public static class InventorDTO extends TribeDeckCardDTO {
        public InventorSymbol symbol;
        @Override
        public Card createCard(){
            return new Inventor(this.era,this.id,this.symbol);
        }
    }

    public static class ShamanDTO extends TribeDeckCardDTO {
        public Integer shamansStars;
        @Override
        public Card createCard(){
            return new Shaman(this.era,this.id, this.shamansStars);
        }
    }

    public static class HuntDTO extends TribeDeckCardDTO {
        @Override
        public Card createCard(){
            return new HuntEvent(this.era,this.id);
        }
    }

    public static class PaintDTO extends TribeDeckCardDTO {
        @Override
        public Card createCard(){
            return new PaintingEvent(this.era,this.id);
        }
    }

    public static class RitualDTO extends TribeDeckCardDTO {
        public Integer malus;
        @Override
        public Card createCard(){
            return new RitualEvent(this.era,this.id,this.malus);
        }
    }

    public static class SustenanceDTO extends TribeDeckCardDTO {
        @Override
        public Card createCard(){
            return new SustenanceEvent(this.era,this.id);
        }
    }


}
