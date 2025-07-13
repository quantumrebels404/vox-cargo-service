package com.voxcargo.shipment.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.voxcargo.shipment.entity.ShipmentStage;
import com.voxcargo.shipment.entity.ShipmentStatus;
import com.voxcargo.shipment.service.ShipmentStatusService;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentStatusController {

	@Autowired
	private ShipmentStatusService statusService;

	@PostMapping("/{id}/status")
	public ShipmentStatus updateStatus(@PathVariable Long id, @RequestParam ShipmentStage stage,
			@RequestParam(required = false) String remarks) {
		return statusService.updateStatus(id, stage, remarks);
	}

	@GetMapping("/{id}/status")
	public List<ShipmentStatus> getStatusHistory(@PathVariable Long id) {
		return statusService.getStatusHistory(id);
	}

	@GetMapping("/{id}/status/current")
	public ShipmentStage getCurrentStatus(@PathVariable Long id) {
		return statusService.getCurrentStatus(id);
	}
}
