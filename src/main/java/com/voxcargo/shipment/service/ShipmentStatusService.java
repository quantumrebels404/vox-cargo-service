package com.voxcargo.shipment.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voxcargo.shipment.entity.Shipment;
import com.voxcargo.shipment.entity.ShipmentStage;
import com.voxcargo.shipment.entity.ShipmentStatus;
import com.voxcargo.shipment.repository.ShipmentRepository;
import com.voxcargo.shipment.repository.ShipmentStatusRepository;

@Service
public class ShipmentStatusService {

	@Autowired
	private ShipmentStatusRepository statusRepo;

	@Autowired
	private ShipmentRepository shipmentRepo;

	public ShipmentStatus updateStatus(Long shipmentId, ShipmentStage stage, String remarks) {
		Shipment shipment = shipmentRepo.findById(shipmentId)
				.orElseThrow(() -> new RuntimeException("Shipment not found"));

		ShipmentStatus status = new ShipmentStatus();
		status.setShipment(shipment); 
		status.setUpdatedAt(LocalDateTime.now());

		return statusRepo.save(status);
	}

	public List<ShipmentStatus> getStatusHistory(Long shipmentId) {
		return statusRepo.findByShipmentIdOrderByUpdatedAtAsc(shipmentId);
	}

	public ShipmentStage getCurrentStatus(Long shipmentId) {
	    List<ShipmentStatus> statuses = statusRepo.findByShipmentIdOrderByUpdatedAtAsc(shipmentId);
	    return statuses.isEmpty()
	        ? ShipmentStage.CREATED
	        : statuses.get(statuses.size() - 1).getStatus();
	}
}
