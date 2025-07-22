package com.cinebee.service;

import java.awt.image.BufferedImage;

public interface QRCodeService {
    BufferedImage generateTicketQRCode(Long ticketId);
    String generateQRCodeData(Long ticketId);
}
