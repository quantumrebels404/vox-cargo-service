package com.voxcargo.shipment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

//public class ShipmentRequest {
//	@NotBlank(message = "Sender is required")
//	private String sender;
//
//	@NotBlank(message = "Receiver is required")
//	private String receiver;
//
//	@NotBlank(message = "Address is required")
//	private String address;
//
//	@NotBlank(message = "Contact number is required")
//	private String contactNumber;
//
//	@NotBlank(message = "Shipment type is required")
//	private String shipmentType;
//
//	public ShipmentRequest() {
//	}
//
//	public ShipmentRequest(String sender, String receiver, String address, String contactNumber, String shipmentType) {
//		this.sender = sender;
//		this.receiver = receiver;
//		this.address = address;
//		this.contactNumber = contactNumber;
//		this.shipmentType = shipmentType;
//	}
//
//	public String getSender() {
//		return sender;
//	}
//
//	public void setSender(String sender) {
//		this.sender = sender;
//	}
//
//	public String getReceiver() {
//		return receiver;
//	}
//
//	public void setReceiver(String receiver) {
//		this.receiver = receiver;
//	}
//
//	public String getAddress() {
//		return address;
//	}
//
//	public void setAddress(String address) {
//		this.address = address;
//	}
//
//	public String getContactNumber() {
//		return contactNumber;
//	}
//
//	public void setContactNumber(String contactNumber) {
//		this.contactNumber = contactNumber;
//	}
//
//	public String getShipmentType() {
//		return shipmentType;
//	}
//
//	public void setShipmentType(String shipmentType) {
//		this.shipmentType = shipmentType;
//	}
//}

@Data
public class ShipmentRequest {

    // Consignor Details
    @Email(message = "Invalid email format")
    @NotBlank(message = "Sender Email is required")
    private String senderEmail;

    @NotBlank(message = "Sender name is required")
    @Size(max = 100)
    private String senderName;

    @Pattern(regexp = "^\\+\\d{1,3} \\d{3} \\d{3} \\d{4}$", message = "Phone format must be like +1 718 222 2222")
    @NotBlank(message = "{Phone number is required")
    private String phone;

    @NotBlank(message = "Sender address is required")
    @Size(max = 150)
    private String senderAddress;

    @Pattern(regexp = "^\\d{5}$", message = "Sender ZIP must be 5 digits")
    private String senderZip;

    @NotBlank(message = "Content type is required")
    @Pattern(regexp = "Document|Parcel", message = "Content type must be either 'Document' or 'Parcel'")
    private String contentType;

    // Parcel Details (Only if contentType = Parcel)
    private String parcelContent; // ENUM: Electronics, Food items, etc.

    @DecimalMin(value = "0.1", message = "Weight must be greater than 0")
    private Double parcelWeight; // in pounds

    @Pattern(regexp = "Small|S|Medium|M|Large|L|Extra Large|EL", message = "Invalid parcel size")
    private String parcelSize;

    // Consignee Details
    @NotBlank
    @Size(max = 100)
    private String consigneeName;

    @NotBlank
    @Size(max = 200)
    private String consigneeAddress;

    @Pattern(regexp = "^\\d{5}$", message = "Consignee ZIP must be 5 digits")
    private String consigneeZip;

    @Pattern(regexp = "^\\+\\d{1,3} \\d{3} \\d{3} \\d{4}$", message = "Consignee phone format must be like +1 718 222 2222")
    private String consigneePhone;
}