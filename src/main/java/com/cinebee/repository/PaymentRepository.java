package com.cinebee.repository;

import com.cinebee.entity.Payment;
import com.cinebee.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(String orderId);
    boolean existsByTicketAndPaymentStatus(Ticket ticket, Payment.PaymentStatus status);
}
