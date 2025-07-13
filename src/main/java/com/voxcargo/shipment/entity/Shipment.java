package com.voxcargo.shipment.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Consignor details ---
    private String senderEmail;

    @Column(length = 100)
    private String senderName;

    private String phone;

    @Column(length = 150)
    private String senderAddress;

    private String senderZip;

    // --- Content info ---
    private String contentType; // Document or Parcel

    private String parcelContent; // If parcel: Electronics, Food items, etc.

    private Double parcelWeight;

    private String parcelSize;

    // --- Consignee details ---
    @Column(length = 100)
    private String consigneeName;

    @Column(length = 200)
    private String consigneeAddress;

    private String consigneeZip;

    private String consigneePhone;

    // --- System-generated ---
    private String trackingNumber;

    private BigDecimal totalRate;

    private String selectedServiceCode; // e.g., "02" (UPS Ground)

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShipmentStatus> statusHistory = new ArrayList();
}
