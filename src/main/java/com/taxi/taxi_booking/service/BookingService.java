package com.taxi.taxi_booking.service;

import com.taxi.taxi_booking.dto.BookingResponseDto;
import com.taxi.taxi_booking.dto.RequestRideDto;
import com.taxi.taxi_booking.entity.Booking;
import com.taxi.taxi_booking.entity.Status;
import com.taxi.taxi_booking.entity.BookingType;
import com.taxi.taxi_booking.exception.ResourceNotFoundException;
import com.taxi.taxi_booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingResponseDto requestRide(RequestRideDto requestRideDto) {
        Booking booking = Booking.builder()
                .riderId(requestRideDto.getRiderId())
                .pickedLocation(requestRideDto.getPickedLocation())
                .dropLocation(requestRideDto.getDropLocation())
                .bookingType(BookingType.valueOf(requestRideDto.getBookingType().toUpperCase()))
                .status(Status.REQUESTED)
                .bookingTime(LocalDateTime.now())
                .build();

        return bookingToBookingResponseDto(bookingRepository.save(booking));
    }

    public BookingResponseDto acceptRide(long bookingId, long driverId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking Not Found: " + bookingId));

        if(booking.getStatus() != Status.REQUESTED) {
            throw new IllegalStateException("Ride is no longer available. Current status: " + booking.getStatus());
        }
        booking.setDriverId(driverId);
        booking.setStatus(Status.MATCHED);

        return bookingToBookingResponseDto(bookingRepository.save(booking));
    }


    public BookingResponseDto completeRide(long bookingId){
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking Not Found: " + bookingId));

        if(booking.getStatus() != Status.MATCHED && booking.getStatus() != Status.IN_TRANSIT) {
            throw new IllegalStateException("Cannot complete ride. Invalid current status: " + booking.getStatus());
        }
        booking.setStatus(Status.COMPLETED);
        return bookingToBookingResponseDto(bookingRepository.save(booking));
    }

    private BookingResponseDto bookingToBookingResponseDto(Booking booking){

        return new BookingResponseDto(
                booking.getBookingId(),
                booking.getPickedLocation(),
                booking.getDropLocation(),
                booking.getBookingType(),
                booking.getStatus()
        );
    }

}
