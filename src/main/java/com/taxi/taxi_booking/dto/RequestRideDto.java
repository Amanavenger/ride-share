package com.taxi.taxi_booking.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestRideDto {
   private Long riderId;
    private String pickedLocation;
    private String dropLocation;
    private String bookingType;
}
