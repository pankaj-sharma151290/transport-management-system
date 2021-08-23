package com.elemica.tms.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Entity
public @Data
class Tariff {

    @Id
    private String name;

    @Column(nullable = false)
    @Min(0)
    private BigDecimal rate;

    @Min(0)
    @Max(100)
    private BigDecimal discount;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "TARIFF_APPLICABLE_VEHICLE ",
               joinColumns={@JoinColumn(name = "TARIFF_NAME")},
               inverseJoinColumns = {@JoinColumn(name = "VEHICLE_NAME")}
    )
    private List<Vehicle> applicableVehicles;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;

}
