package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sharacter")
public class Sharacter extends BaseEntity {
    /**
     * Identifier of the {@link Anime} this sharacter belongs to. Only the id is
     * stored to avoid holding a full object reference.
     */
    @Column(name = "anime_id")
    private Integer animeId;
}
