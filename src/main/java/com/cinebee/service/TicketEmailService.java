package com.cinebee.service;

import com.cinebee.entity.Ticket;

public interface TicketEmailService {
    void sendTicketConfirmationEmail(Ticket ticket);
}
