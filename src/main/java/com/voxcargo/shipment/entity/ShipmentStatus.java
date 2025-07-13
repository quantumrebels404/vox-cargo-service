package com.voxcargo.shipment.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ShipmentStatus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private ShipmentStage status; // CREATED, PICKED, etc.

	private LocalDateTime updatedAt;

	@ManyToOne
	@JoinColumn(name = "shipment_id")
	private Shipment shipment;
}