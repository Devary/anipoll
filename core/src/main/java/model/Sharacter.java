package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Sharacter extends BaseEntity {
    /**
     * Identifier of the {@link Anime} this sharacter belongs to. Only the id is
     * stored to avoid holding a full object reference.
     */
    private Long animeId;
}
