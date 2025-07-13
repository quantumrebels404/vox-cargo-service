package com.voxcargo.shipment.service;

import com.voxcargo.shipment.dto.ShipmentRequest;
import com.voxcargo.shipment.entity.Shipment;
import com.voxcargo.shipment.entity.ShipmentStage;
import com.voxcargo.shipment.entity.ShipmentStatus;
import com.voxcargo.shipment.repository.ShipmentRepository;
import com.voxcargo.shipment.repository.ShipmentStatusRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ShipmentService {

	@Autowired
	private ShipmentRepository shipmentRepository;

	@Autowired
	private ShipmentStatusRepository shipmentStatusRepository;

	private static final SecureRandom random = new SecureRandom();

	public Shipment createShipment(ShipmentRequest request) {
		Shipment shipment = new Shipment();

		// Consignor details
		shipment.setSenderEmail(request.getSenderEmail());
		shipment.setSenderName(request.getSenderName());
		shipment.setPhone(request.getPhone());
		shipment.setSenderAddress(request.getSenderAddress());
		shipment.setSenderZip(request.getSenderZip());

		// Content info
		shipment.setContentType(request.getContentType());
		shipment.setParcelContent(request.getParcelContent());
		shipment.setParcelWeight(request.getParcelWeight());
		shipment.setParcelSize(request.getParcelSize());

		// Consignee details
		shipment.setConsigneeName(request.getConsigneeName());
		shipment.setConsigneeAddress(request.getConsigneeAddress());
		shipment.setConsigneeZip(request.getConsigneeZip());
		shipment.setConsigneePhone(request.getConsigneePhone());

		// System info
		shipment.setTrackingNumber("UPS" + generateNumericTrackingNumber(7));
		shipment.setCreatedAt(LocalDateTime.now());
		shipment = shipmentRepository.save(shipment);

		// Add initial status
		ShipmentStatus createdStatus = new ShipmentStatus();
		createdStatus.setShipment(shipment);
		createdStatus.setStatus(ShipmentStage.CREATED);
		createdStatus.setUpdatedAt(LocalDateTime.now());

		shipmentStatusRepository.save(createdStatus);
		return shipment;
	}
	
	public String getShipmentStatusByTrackingNumber(String trackingNumber) {
	    return shipmentRepository.findByTrackingNumber(trackingNumber).get().getStatusHistory().stream().findFirst().get().getStatus().name();
	}

	public static String generateNumericTrackingNumber(int length) {
		StringBuilder sb = new StringBuilder();
		sb.append(random.nextInt(9) + 1); // First digit non-zero
		for (int i = 1; i < length; i++) {
			sb.append(random.nextInt(10));
		}
		return sb.toString();
	}

	@CachePut(value = "shipmentRequestCache", key = "#email")
	public ShipmentRequest cacheRequest(String email, ShipmentRequest request) {
		return request;
	}

	@Cacheable(value = "shipmentRequestCache", key = "#email")
	public ShipmentRequest getCachedRequest(String email) {
		return null;
	}

	@CacheEvict(value = "shipmentRequestCache", key = "#email")
	public void clearCache(String email) {
		// Entry removed from cache
	}

	public boolean cancelShipment(Long shipmentId) {
		Optional<Shipment> shipmentOpt = shipmentRepository.findById(shipmentId);

		if (shipmentOpt.isPresent()) {
			Shipment shipment = shipmentOpt.get();

			boolean delStatus = shipment.getStatusHistory().stream()
					.anyMatch(s -> s.getStatus().name().equals("DELIVERED"));
			// Only allow cancel if not delivered or cancelled already
			if (!delStatus) {
				// shipment.setLatestStatus("CANCELLED");
				// shipment.setUpdatedAt(LocalDateTime.now());
				shipmentRepository.save(shipment);

				// Optionally log to shipment_status history
				ShipmentStatus history = new ShipmentStatus();
				history.setShipment(shipment);
				history.setStatus(ShipmentStage.CANCELLED);
				history.setUpdatedAt(LocalDateTime.now());
				shipmentStatusRepository.save(history);

				return true;
			}
		}
		return false;
	}

}