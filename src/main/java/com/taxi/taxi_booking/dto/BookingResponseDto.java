package com.taxi.taxi_booking.dto;

import com.taxi.taxi_booking.entity.BookingType;
import com.taxi.taxi_booking.entity.Status;

public record BookingResponseDto(long bookingId, String pickedLocation, String dropLocation, BookingType bookingType, Status status) {
}
