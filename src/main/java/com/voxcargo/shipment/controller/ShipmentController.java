package com.voxcargo.shipment.controller;

import com.voxcargo.shipment.dto.*;
import com.voxcargo.shipment.entity.Shipment;
import com.voxcargo.shipment.service.ShipmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;

//    @Autowired
//    private VoiceAIService voiceAIService;

//    @PostMapping("/validate")
//    public ValidationResponse<ShipmentRequest> validate(
//            @Valid @RequestBody ShipmentRequest request,
//            BindingResult result) {
//
//        if (result.hasErrors()) {
//            Map<String, String> errors = new HashMap<>();
//            result.getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
//            return new ValidationResponse<>(false, errors, null);
//        }
//
//        return new ValidationResponse<>(true, Collections.emptyMap(), request);
//    }
    
    @PostMapping("/validate")
    public ResponseEntity<ValidationResponse<ShipmentRequest>> validate(
            @Valid @RequestBody ShipmentRequest request,
            BindingResult result) {

        Map<String, String> errors = new HashMap<>();

        if (result.hasErrors()) {
            result.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
            );

            //Return the original request even if errors exist
            shipmentService.cacheRequest(request.getSenderEmail(), request);
            System.out.println("Cached Value: "+shipmentService.getCachedRequest(request.getSenderEmail()));
            return ResponseEntity.ok(new ValidationResponse<>(false, errors, request));
        }

        return ResponseEntity.ok(new ValidationResponse<>(true, Collections.emptyMap(), request));
    } 
    
    @PostMapping("/create")
    public Object create(@Valid @RequestBody ShipmentRequest request, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
            return new ValidationResponse<>(false, errors, null);
        }
        
        shipmentService.clearCache(request.getSenderEmail());
        Shipment shipment = shipmentService.createShipment(request);
        return shipment;
    }
    
    
    @GetMapping("/track/{trackNum}")
    public ResponseEntity<?> getStatusByTrackingNumber(@PathVariable String trackNum) {
        String statusOpt = shipmentService.getShipmentStatusByTrackingNumber(trackNum);
 
        if(statusOpt!=null) {
            return ResponseEntity.ok(Map.of(
                "trackingNumber", trackNum,
                "status", statusOpt
            ));
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "Shipment not found"));
        }
    }


//    @PostMapping("/voice")
//    public Object extractFromVoice(@RequestBody String voiceText) {
//        ShipmentRequest extracted = voiceAIService.extractFromVoice(voiceText);
//        return new ValidationResponse<>(true, Collections.emptyMap(), extracted);
//    }
}