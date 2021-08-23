package com.elemica.tms.repository;

import com.elemica.tms.entity.Tariff;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TariffRepository extends CrudRepository<Tariff, String> {

}
