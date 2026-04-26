package com.taxi.taxi_booking.dto;

import java.time.LocalDateTime;

public record ErrorResponse(LocalDateTime timestamp, int httpStatusCode, String message) {
}
