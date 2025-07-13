package com.voxcargo.shipment.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class TestController {
	@GetMapping("/ups/test")
	public String test() {
		return "Test UPS Shipment Service";
	}

}
