package com.voxcargo.shipment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.voxcargo.shipment.entity.ShipmentStage;
import com.voxcargo.shipment.entity.ShipmentStatus;
import com.voxcargo.shipment.repository.ShipmentRepository;
import com.voxcargo.shipment.repository.ShipmentStatusRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class ShipmentDashboardService {

	@Autowired
	private ShipmentRepository shipmentRepository;

	@Autowired
	private ShipmentStatusRepository statusRepository;

	public long getTotalShipmentsToday() {
		LocalDateTime start = LocalDate.now().atStartOfDay();
		LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
		return shipmentRepository.countByCreatedAtBetween(start, end);
	}

	public long getTotalShipmentsThisMonth() {
		LocalDateTime start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
		return shipmentRepository.countByCreatedAtBetween(start, LocalDateTime.now());
	}

	public long getTotalShipmentsThisYear() {
		LocalDateTime start = LocalDate.now().withDayOfYear(1).atStartOfDay();
		return shipmentRepository.countByCreatedAtBetween(start, LocalDateTime.now());
	}

	@Async
	public Map<Integer, Long> getShipmentsByHourToday() {
		LocalDateTime start = LocalDate.now().atStartOfDay();
		Map<Integer, Long> result = shipmentRepository.findAllByCreatedAtBetween(start, LocalDateTime.now()).stream()
				.collect(Collectors.groupingBy(s -> s.getCreatedAt().getHour(), Collectors.counting()));
		return result;
	}

	public long getShipmentsPicked() {
		return statusRepository.countByStatus(ShipmentStage.PICKED);
	}

	public long getShipmentsShipped() {
		return statusRepository.countByStatus(ShipmentStage.SHIPPED);
	}

	public long getShipmentsDelivered() {
		return statusRepository.countByStatus(ShipmentStage.DELIVERED);
	}

	public long countUnshippedOlderThanDays(int days) {
		LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
		return statusRepository.countUnshippedBefore(cutoff);
	}

	public Map<String, Long> getChartSummary() {
		List<ShipmentStatus> all = statusRepository.findAll();
		return all.stream().collect(Collectors.groupingBy(s -> s.getStatus().toString(), Collectors.counting()));
	}

	public List<ShipmentStatus> getStatusByStage(ShipmentStage stage) {
		return statusRepository.findByStatus(stage);
	}

	public long getShipmentsNotPickedWithinDays(int days) {
		LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
		return statusRepository.countByStatusNotAndUpdatedAtBefore(ShipmentStage.PICKED, cutoff);
	}
    
	public long getNotShipmentsPicked() {
		return statusRepository.countByStatus(ShipmentStage.NOT_PICKED);
	}
	
	public long getShipmentsNotShippedWithinDays(int days) {
		LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
		return statusRepository.countByStatusNotAndUpdatedAtBefore(ShipmentStage.SHIPPED, cutoff);
	}

	public long countShipmentsWithLatestStatus(ShipmentStage stage) {
		return statusRepository.countLatestByStatus(ShipmentStage.valueOf(stage.toString()));
	}

	public long countShipmentsWithLatestStatus(ShipmentStage stage, LocalDateTime fromDate) {
		return shipmentRepository.countByLatestStatusAndCreatedAfter(stage, fromDate);
	}

	public long countShipmentsCreatedAfter(LocalDateTime fromDate) {
		return shipmentRepository.countByCreatedAtAfter(fromDate);
	}

	public Map<Integer, Long> getHourlyShipmentsToday() {
		LocalDate today = LocalDate.now();
		LocalDateTime start = today.atStartOfDay();
		LocalDateTime end = start.plusDays(1);

		return shipmentRepository.findAllByCreatedAtBetween(start, end).stream()
				.collect(Collectors.groupingBy(s -> s.getCreatedAt().getHour(), Collectors.counting()));
	}

	// 👇 Group by date in current month
	public Map<Integer, Long> getShipmentsGroupedByDayThisMonth() {
		LocalDate today = LocalDate.now();
		LocalDateTime start = today.withDayOfMonth(1).atStartOfDay();
		LocalDateTime end = today.with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59);

		return shipmentRepository.findAllByCreatedAtBetween(start, end).stream()
				.collect(Collectors.groupingBy(s -> s.getCreatedAt().getDayOfMonth(), Collectors.counting()));
	}

	// 👇 Group by month in current year
	public Map<Integer, Long> getShipmentsGroupedByMonthThisYear() {
		LocalDate today = LocalDate.now();
		LocalDateTime start = today.withDayOfYear(1).atStartOfDay();
		LocalDateTime end = today.withMonth(12).withDayOfMonth(31).atTime(23, 59, 59);

		return shipmentRepository.findAllByCreatedAtBetween(start, end).stream()
				.collect(Collectors.groupingBy(s -> s.getCreatedAt().getMonthValue(), // 1 for Jan, 2 for Feb...
						Collectors.counting()));
	}

	public Map<String, Object> getDashboardSummary(String period) throws Exception {
		Map<String, Object> response = new HashMap<>();

		LocalDateTime fromDate = switch (period.toLowerCase()) {
		case "month" -> LocalDate.now().withDayOfMonth(1).atStartOfDay();
		case "year" -> LocalDate.now().withDayOfYear(1).atStartOfDay();
		default -> LocalDate.now().atStartOfDay(); // "day"
		};

		Executor executor = Executors.newFixedThreadPool(4); // Or inject your custom executor bean

		CompletableFuture<Long> totalShipmentsFuture = CompletableFuture.supplyAsync(() -> {
			return switch (period.toLowerCase()) {
			case "month" -> getTotalShipmentsThisMonth();
			case "year" -> getTotalShipmentsThisYear();
			default -> getTotalShipmentsToday();
			};
		}, executor);

		CompletableFuture<Map<Integer, Long>> hourlyFuture = "day".equalsIgnoreCase(period)
				? CompletableFuture.supplyAsync(this::getShipmentsByHourToday, executor)
				: CompletableFuture.completedFuture(Collections.emptyMap());

		CompletableFuture<Long> createdFuture = CompletableFuture
				.supplyAsync(() -> countShipmentsWithLatestStatus(ShipmentStage.CREATED, fromDate), executor);

		CompletableFuture<Long> pickedButNotShippedFuture = CompletableFuture
				.supplyAsync(() -> getShipmentsNotShippedWithinDays(2), executor);

//		CompletableFuture<Long> notPickedFuture = CompletableFuture
//				.supplyAsync(() -> getShipmentsNotPickedWithinDays(2), executor);
				
		CompletableFuture<Long> notPickedFuture = CompletableFuture
				.supplyAsync(() -> getNotShipmentsPicked(), executor);

		CompletableFuture<Long> inProgressFuture = CompletableFuture
				.supplyAsync(() -> countShipmentsWithLatestStatus(ShipmentStage.PICKED, fromDate), executor);

		CompletableFuture<Long> delayedFuture = CompletableFuture
				.supplyAsync(() -> countShipmentsWithLatestStatus(ShipmentStage.DELAYED, fromDate), executor);

		CompletableFuture<Long> deliveredFuture = CompletableFuture
				.supplyAsync(() -> countShipmentsWithLatestStatus(ShipmentStage.DELIVERED, fromDate), executor);

		CompletableFuture<Long> shippedFuture = CompletableFuture
				.supplyAsync(() -> countShipmentsWithLatestStatus(ShipmentStage.SHIPPED, fromDate), executor);

		CompletableFuture.allOf(totalShipmentsFuture, hourlyFuture, createdFuture, pickedButNotShippedFuture,
				notPickedFuture, inProgressFuture, delayedFuture, deliveredFuture, shippedFuture).join();

		response.put("totalShipments", totalShipmentsFuture.get());
		response.put("hourlyShipments", hourlyFuture.get());
		response.put("created", createdFuture.get());
		response.put("pickedButNotShipped", pickedButNotShippedFuture.get());
		response.put("notPicked", notPickedFuture.get());
		response.put("inProgress", inProgressFuture.get());
		response.put("delayed", delayedFuture.get());
		response.put("delivered", deliveredFuture.get());
		response.put("shipped", shippedFuture.get());

		return response;
	}
}
