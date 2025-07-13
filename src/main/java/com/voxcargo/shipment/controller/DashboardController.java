package com.voxcargo.shipment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.voxcargo.shipment.entity.ShipmentStage;
import com.voxcargo.shipment.service.ShipmentDashboardService;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

	@Autowired
	private ShipmentDashboardService dashboardService;

	@GetMapping("/hourly-count")
	public Map<Integer, Long> getHourlyShipments() {
		return dashboardService.getShipmentsByHourToday();
	}

	@GetMapping("/not-picked")
	public long getNotPickedDefault() {
		return dashboardService.getShipmentsNotPickedWithinDays(2);
	}

	@GetMapping("/dashboard/status-count/{stage}")
	public long getCountByStatus(@PathVariable ShipmentStage stage) {
		return dashboardService.countShipmentsWithLatestStatus(stage);
	}

	@GetMapping("/summary")
	public Map<String, Object> getDashboardSummary(@RequestParam(defaultValue = "day") String period) throws Exception {
		return dashboardService.getDashboardSummary(period);
	}

	@GetMapping("/volume")
	public Map<Integer, Long> getShipmentVolume(@RequestParam(defaultValue = "day") String period) {
		return switch (period.toLowerCase()) {
		case "month" -> dashboardService.getShipmentsGroupedByDayThisMonth();
		case "year" -> dashboardService.getShipmentsGroupedByMonthThisYear();
		default -> dashboardService.getHourlyShipmentsToday();
		};
	}
}
