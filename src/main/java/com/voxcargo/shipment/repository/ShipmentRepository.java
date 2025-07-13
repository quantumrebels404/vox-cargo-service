package com.voxcargo.shipment.repository;

import com.voxcargo.shipment.entity.Shipment;
import com.voxcargo.shipment.entity.ShipmentStage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

	long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

	List<Shipment> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
	
	Optional<Shipment> findByTrackingNumber(String trackingNumber);

	@Query("""
			  SELECT COUNT(s)
			  FROM Shipment s
			  WHERE s.id IN (
			    SELECT ss.shipment.id
			    FROM ShipmentStatus ss
			    WHERE ss.updatedAt = (
			      SELECT MAX(sub.updatedAt)
			      FROM ShipmentStatus sub
			      WHERE sub.shipment.id = ss.shipment.id
			    )
			    AND ss.status = :stage
			  )
			  AND s.createdAt >= :fromDate
			""")
	long countByLatestStatusAndCreatedAfter(@Param("stage") ShipmentStage stage,
			@Param("fromDate") LocalDateTime fromDate);

	long countByCreatedAtAfter(LocalDateTime fromDate);
}