package com.cinebee.service.impl;

import com.cinebee.entity.Ticket;
import com.cinebee.service.QRCodeService;
import com.cinebee.service.TicketEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.imageio.ImageIO;
import jakarta.mail.internet.MimeMessage;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class TicketEmailServiceImpl implements TicketEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private QRCodeService qrCodeService;

    @Override
    public void sendTicketConfirmationEmail(Ticket ticket) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Email details
            helper.setTo(ticket.getUser().getEmail());
            helper.setSubject("🎬 Xác nhận đặt vé thành công - CineBee");
            helper.setFrom("noreply@cinebee.vn");

            // Prepare template data
            Context context = new Context();
            context.setVariable("customerName", ticket.getUser().getFullName());
            context.setVariable("ticketId", ticket.getId());
            context.setVariable("movieTitle", ticket.getShowtime().getMovie().getTitle());
            context.setVariable("theaterName", ticket.getShowtime().getRoom().getTheater().getName());
            context.setVariable("roomName", ticket.getShowtime().getRoom().getName());
            context.setVariable("showtime", ticket.getShowtime().getStartTime()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm")));
            context.setVariable("seatNumbers", ticket.getSeat().getSeatNumber()); // Single seat
            context.setVariable("totalPrice", String.format("%,.0f VNĐ", ticket.getPrice()));

            // Generate HTML content
            String htmlContent = templateEngine.process("ticket-confirmation", context);
            helper.setText(htmlContent, true);

            // Generate and attach QR code
            BufferedImage qrImage = qrCodeService.generateTicketQRCode(ticket.getId());
            ByteArrayOutputStream qrOutputStream = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", qrOutputStream);
            
            helper.addInline("qrcode", new ByteArrayResource(qrOutputStream.toByteArray()), "image/png");

            // Send email
            mailSender.send(message);
            log.info("✅ Ticket confirmation email sent successfully to {} for ticket {}", 
                ticket.getUser().getEmail(), ticket.getId());

        } catch (Exception e) {
            log.error("❌ Failed to send ticket confirmation email for ticket {}: {}", 
                ticket.getId(), e.getMessage());
            throw new RuntimeException("Failed to send ticket confirmation email", e);
        }
    }
}
