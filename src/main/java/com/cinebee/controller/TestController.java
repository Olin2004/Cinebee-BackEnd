package com.cinebee.controller;

import com.cinebee.entity.Ticket;
import com.cinebee.repository.TicketRepository;
import com.cinebee.service.TicketEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private TicketEmailService ticketEmailService;

    @Autowired
    private TicketRepository ticketRepository;

    @PostMapping("/send-ticket-email/{ticketId}")
    public ResponseEntity<String> testSendTicketEmail(@PathVariable Long ticketId) {
        try {
            Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
            
            ticketEmailService.sendTicketConfirmationEmail(ticket);
            return ResponseEntity.ok("✅ Email sent successfully to " + ticket.getUser().getEmail());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Failed to send email: " + e.getMessage());
        }
    }
}
