package com.elemica.tms.repository;

import com.elemica.tms.entity.Shipment;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentRepository extends CrudRepository<Shipment, String> {

}
