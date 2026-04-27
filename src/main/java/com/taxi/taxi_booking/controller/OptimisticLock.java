package com.taxi.taxi_booking.controller;

import com.taxi.taxi_booking.dto.BookingResponseDto;
import com.taxi.taxi_booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OptimisticLock {

    private final BookingService bookingService;
    BookingResponseDto bookingResponseDto;

    public BookingResponseDto testOptimisticLock(long bookingId, long driverId) {

        Thread t1 = new Thread(() -> {
            try {
                System.out.println("Thread 1 trying to book...");
                bookingResponseDto = bookingService.acceptRide(bookingId, driverId);
                System.out.println("Thread 1 booked successfully ✅");
            } catch (Exception e) {
                System.out.println("Thread 1 failed to book ❌ Reason: " + e.getMessage());
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                System.out.println("Thread 2 trying to book...");
                bookingResponseDto = bookingService.acceptRide(bookingId, driverId);
                System.out.println("Thread 2 booked successfully ✅");
            } catch (Exception e) {
                System.out.println("Thread 2 failed to book ❌ Reason: " + e.getMessage());
            }
        });

        t1.start(); t2.start();

        t1.start();t2.start();

        System.out.println("Booked!!!!");
        return bookingResponseDto;
    }
}
