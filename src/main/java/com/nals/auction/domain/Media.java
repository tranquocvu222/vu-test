package com.nals.auction.domain;

import com.nals.utils.enums.MediaType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import static javax.persistence.EnumType.STRING;

@Entity
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "media")
public class Media
    extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_id", nullable = false)
    private Long sourceId;

    @Column
    private String name;

    @Enumerated(STRING)
    @Column(nullable = false)
    private MediaType type;

    public Media(final Long sourceId, final String name, final MediaType type) {
        this.sourceId = sourceId;
        this.name = name;
        this.type = type;
    }
}
