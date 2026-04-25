package com.taxi.taxi_booking.controller;

import com.taxi.taxi_booking.dto.RequestRideDto;
import com.taxi.taxi_booking.entity.Booking;
import com.taxi.taxi_booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BookingController {

   private final BookingService bookingService;

    @PostMapping("/request")
    public ResponseEntity<Booking> booking(@RequestBody RequestRideDto requestRideDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.requestRide(requestRideDto));
    }

    @PatchMapping("/{bookingId}/accept")
    public ResponseEntity<Booking> acceptRide(@PathVariable long bookingId, @RequestParam long driverId) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.acceptRide(bookingId, driverId));
    }

    @PatchMapping("/{bookingId}/complete")
    public ResponseEntity<Booking> completeRide(@PathVariable long bookingId) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.completeRide(bookingId));
    }
}
