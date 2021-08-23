package com.elemica.tms.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Entity
public @Data
class Shipment {

    @Id
    private String name;

    @Column(nullable = false)
    @Positive
    private BigDecimal weight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "SHIPMENT_VEHICLE",
               joinColumns = {
                       @JoinColumn(name = "SHIPMENT_NAME", referencedColumnName = "name")
               },
               inverseJoinColumns = {
                       @JoinColumn(name = "VEHICLE_NAME", referencedColumnName = "name")
               }
    )
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "SHIPMENT_TARIFF",
               joinColumns = {
                       @JoinColumn(name = "SHIPMENT_NAME", referencedColumnName = "name")
               },
               inverseJoinColumns = {
                       @JoinColumn(name = "TARIFF_NAME", referencedColumnName = "name")
               }
    )
    private Tariff tariff;

    @Min(0)
    private BigDecimal cost;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;
}
