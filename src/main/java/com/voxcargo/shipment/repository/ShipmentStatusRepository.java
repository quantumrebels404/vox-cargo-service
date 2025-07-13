package com.voxcargo.shipment.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.voxcargo.shipment.entity.ShipmentStage;
import com.voxcargo.shipment.entity.ShipmentStatus;

@Repository
public interface ShipmentStatusRepository extends JpaRepository<ShipmentStatus, Long> {
	List<ShipmentStatus> findByShipmentIdOrderByUpdatedAtAsc(Long shipmentId);

//	Optional<ShipmentStatus> findByShipmentIdOrderByUpdatedAtAsc(Long shipmentId);

	@Query("""
			SELECT COUNT(DISTINCT st.shipment.id)
			FROM ShipmentStatus st
			WHERE st.status = :status
			AND st.updatedAt = (
			    SELECT MAX(sst.updatedAt)
			    FROM ShipmentStatus sst
			    WHERE sst.shipment.id = st.shipment.id
			)
			AND st.shipment.createdAt < :before
			""")
	long countByLatestStatusBefore(@Param("status") ShipmentStage status, @Param("before") LocalDateTime before);

	List<ShipmentStatus> findByStatus(ShipmentStage stage);

	@Query("""
			    SELECT COUNT(ss)
			    FROM ShipmentStatus ss
			    WHERE ss.updatedAt < :dateTime
			      AND ss.status <> 'SHIPPED'
			      AND ss.updatedAt = (
			        SELECT MAX(sub.updatedAt)
			        FROM ShipmentStatus sub
			        WHERE sub.shipment.id = ss.shipment.id
			      )
			""")
	long countUnshippedBefore(@Param("dateTime") LocalDateTime dateTime);

	long countByStatus(ShipmentStage status);

	Long countByStatusNotAndUpdatedAtBefore(ShipmentStage status, LocalDateTime timestamp);

	@Query("""
			    SELECT COUNT(ss) FROM ShipmentStatus ss
			    WHERE ss.updatedAt = (
			        SELECT MAX(ss2.updatedAt) FROM ShipmentStatus ss2
			        WHERE ss2.shipment.id = ss.shipment.id
			    )
			    AND ss.status = :status
			""")
	long countLatestByStatus(@Param("status") ShipmentStage status);

//        @Query("SELECT COUNT(s) FROM ShipmentStatus s WHERE s.status <> 'SHIPPED' AND s.timestamp < :cutoff")
	// long countUnshippedBefore(LocalDateTime cutoff);

}
