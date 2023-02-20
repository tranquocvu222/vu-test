package com.nals.auction.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "companies")
public class Company
    extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "town_id", nullable = false)
    private Long townId;

    @Column(nullable = false)
    private String name;

    @Column(name = "image_name")
    private String imageName;

    @Column(nullable = false)
    private String address;

    @Column
    private String website;

    @Column(length = 500)
    private String description;

    @Column(name = "contact_name", nullable = false)
    private String contactName;

    @Column(name = "contact_email", nullable = false)
    private String contactEmail;

    @Column(name = "phone_number", length = 20, nullable = false)
    private String phoneNumber;

    @Column(name = "fax_number")
    private String faxNumber;

    @Column
    @Builder.Default
    private boolean approved = false;

    @ToString.Exclude
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private Set<Product> products;

    @ToString.Exclude
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private Set<Auction> auctions;

    public Company(final Long companyId, final String companyName, final String imageName, final String address,
                   final String website, final String description, final String contactName, final String contactEmail,
                   final String phoneNumber, final String faxNumber) {
        this.id = companyId;
        this.name = companyName;
        this.imageName = imageName;
        this.address = address;
        this.website = website;
        this.description = description;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.phoneNumber = phoneNumber;
        this.faxNumber = faxNumber;
    }
}
