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

/**
 * Data Transfer Object for tribe deck cards (characters and events) as stored in
 * the configuration file. Each concrete subtype knows how to create the matching
 * {@link Card} instance from the deserialized data.
 */
public abstract class TribeDeckCardDTO {
    public Integer era;
    public Integer id;
    public Integer minPlayer;
    public boolean isFinal=false;

    /**
     * @return a new {@link Card} built from this DTO's data
     */
    public abstract Card createCard();

    /** DTO that creates an {@link Artist}. */
    public static class ArtistDTO extends TribeDeckCardDTO {
        /** {@inheritDoc} */
        @Override
        public Card createCard(){
            return new Artist(this.era,this.id);
        }
    }

    /** DTO that creates a {@link Builder}. */
    public static class BuilderDTO extends TribeDeckCardDTO {
        public Integer buildingDiscount;
        public Integer prestigePoint;
        /** {@inheritDoc} */
        @Override
        public Card createCard(){
            return new Builder(this.era,this.id, this.buildingDiscount, this.prestigePoint);
        }
    }

    /** DTO that creates a {@link Gatherer}. */
    public static class GathererDTO extends TribeDeckCardDTO {
        /** {@inheritDoc} */
        @Override
        public Card createCard(){
            return new Gatherer(this.era,this.id);
        }
    }

    /** DTO that creates a {@link Hunter}. */
    public static class HunterDTO extends TribeDeckCardDTO {
        public Boolean active;
        /** {@inheritDoc} */
        @Override
        public Card createCard(){
            return new Hunter(this.era,this.id,this.active);
        }
    }

    /** DTO that creates an {@link Inventor}. */
    public static class InventorDTO extends TribeDeckCardDTO {
        public InventorSymbol symbol;
        /** {@inheritDoc} */
        @Override
        public Card createCard(){
            return new Inventor(this.era,this.id,this.symbol);
        }
    }

    /** DTO that creates a {@link Shaman}. */
    public static class ShamanDTO extends TribeDeckCardDTO {
        public Integer shamansStars;
        /** {@inheritDoc} */
        @Override
        public Card createCard(){
            return new Shaman(this.era,this.id, this.shamansStars);
        }
    }

    /** DTO that creates a {@link HuntEvent}. */
    public static class HuntDTO extends TribeDeckCardDTO {
        /** {@inheritDoc} */
        @Override
        public Card createCard(){
            return new HuntEvent(this.era,this.id);
        }
    }

    /** DTO that creates a {@link PaintingEvent}. */
    public static class PaintDTO extends TribeDeckCardDTO {
        /** {@inheritDoc} */
        @Override
        public Card createCard(){
            return new PaintingEvent(this.era,this.id);
        }
    }

    /** DTO that creates a {@link RitualEvent}. */
    public static class RitualDTO extends TribeDeckCardDTO {
        public Integer malus;
        /** {@inheritDoc} */
        @Override
        public Card createCard(){
            return new RitualEvent(this.era,this.id,this.malus);
        }
    }

    /** DTO that creates a {@link SustenanceEvent}. */
    public static class SustenanceDTO extends TribeDeckCardDTO {
        /** {@inheritDoc} */
        @Override
        public Card createCard(){
            return new SustenanceEvent(this.era,this.id);
        }
    }


}
