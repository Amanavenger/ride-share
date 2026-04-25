package com.taxi.taxi_booking.service;

import com.taxi.taxi_booking.dto.RequestRideDto;
import com.taxi.taxi_booking.entity.Booking;
import com.taxi.taxi_booking.entity.Status;
import com.taxi.taxi_booking.entity.BookingType;
import com.taxi.taxi_booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    public Booking requestRide(RequestRideDto requestRideDto) {
        Booking booking = Booking.builder()
                .riderId(requestRideDto.getRiderId())
                .pickedLocation(requestRideDto.getPickedLocation())
                .dropLocation(requestRideDto.getDropLocation())
                .bookingType(BookingType.valueOf(requestRideDto.getBookingType().toUpperCase()))
                .status(Status.REQUESTED)
                .bookingTime(LocalDateTime.now())
                .build();

        return bookingRepository.save(booking);
    }

    public Booking acceptRide(long bookingId, long driverId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking Not Found: " + bookingId));

        if(booking.getStatus() != Status.REQUESTED) {
            throw new IllegalStateException("Ride is no longer available. Current status: " + booking.getStatus());
        }

        booking.setDriverId(driverId);
        booking.setStatus(Status.MATCHED);

        return bookingRepository.save(booking);
    }


    public Booking completeRide(long bookingId){
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking Not Found: " + bookingId));

        if(booking.getStatus() != Status.MATCHED && booking.getStatus() != Status.IN_TRANSIT) {
            throw new IllegalStateException("Cannot complete ride. Invalid current status: " + booking.getStatus());
        }

        booking.setStatus(Status.COMPLETED);
        return bookingRepository.save(booking);
    }

}
