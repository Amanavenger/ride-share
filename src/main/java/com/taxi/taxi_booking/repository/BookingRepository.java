package com.taxi.taxi_booking.repository;

import com.taxi.taxi_booking.entity.Booking;
import com.taxi.taxi_booking.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    public List<Booking> findByStatus(Status status);

}
