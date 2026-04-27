package com.taxi.taxi_booking.service;

import com.taxi.taxi_booking.dto.BookingResponseDto;
import com.taxi.taxi_booking.dto.RequestRideDto;
import com.taxi.taxi_booking.entity.Booking;
import com.taxi.taxi_booking.entity.Status;
import com.taxi.taxi_booking.entity.BookingType;
import com.taxi.taxi_booking.exception.ResourceNotFoundException;
import com.taxi.taxi_booking.repository.BookingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @Transactional
    public BookingResponseDto acceptRide(long bookingId, long driverId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking Not Found: " + bookingId));

        if(booking.getStatus() != Status.REQUESTED) {
            throw new IllegalStateException("Ride is no longer available. Current status: " + booking.getStatus());
        }
        booking.setDriverId(driverId);
        booking.setStatus(Status.MATCHED);

        return bookingToBookingResponseDto(bookingRepository.save(booking));
    }

    public List<BookingResponseDto> getAvailableRides() {
        List<Booking> bookings = bookingRepository.findByStatus(Status.REQUESTED);

        return bookings.stream().map(b -> bookingToBookingResponseDto(b)).collect(Collectors.toList());
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

    @Transactional
    public BookingResponseDto startRide(long bookingId, long driverId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking Not Found: " + bookingId));

        if(booking.getStatus() != Status.MATCHED) {
            throw new IllegalStateException("Cannot start ride. Invalid current status: " + booking.getStatus());
        }
        if(booking.getDriverId() == null || booking.getDriverId() != driverId) {
            throw new SecurityException("Unauthorized: You are not the assigned driver for this ride.");
        }
        booking.setStatus(Status.IN_TRANSIT);

       return bookingToBookingResponseDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponseDto completeRide(long bookingId, long driverId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking Not Found: " + bookingId));

        if(booking.getStatus() == Status.MATCHED || booking.getStatus() == Status.CANCELLED) {
            throw new IllegalStateException("Cannot complete ride. Invalid current status: " + booking.getStatus());
        }
        if(booking.getDriverId() == null || booking.getDriverId() != driverId) {
            throw new SecurityException("Unauthorized: You are not the assigned driver for this ride.");
        }
        booking.setStatus(Status.COMPLETED);
        return bookingToBookingResponseDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponseDto cancelRide(long bookingId, long driverId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking Not Found: " + bookingId));

        if(booking.getStatus() == Status.CANCELLED || booking.getStatus() == Status.COMPLETED) {
            throw new IllegalStateException("Cannot cancel ride. Invalid current status: " + booking.getStatus());
        }
        if(booking.getDriverId() != null && booking.getDriverId() != driverId) { //automatic cancel state after no response after some min
            throw new SecurityException("Unauthorized: You are not the assigned driver for this ride.");
        }
        booking.setStatus(Status.CANCELLED);
        return bookingToBookingResponseDto(bookingRepository.save(booking));
    }

}
