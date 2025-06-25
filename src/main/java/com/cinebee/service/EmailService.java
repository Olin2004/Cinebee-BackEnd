package com.cinebee.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendRegistrationSuccess(String to, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("noreply@cinemazino.com", "Cinema Zino");
            helper.setTo(to);
            helper.setSubject("Welcome to Cinema Zino!");

            String html = """
                        <div style=\"max-width:420px;margin:0 auto;background:#fff;border-radius:12px;box-shadow:0 2px 12px #0001;padding:32px 24px;text-align:center;font-family:sans-serif;\">
                            <img src=\"https://fra.cloud.appwrite.io/v1/storage/buckets/6845cc7e001aee7a1a90/files/6845cc9b0035780eff76/view?project=6845cc1d00398970a1c9&mode=admin\" alt=\"Cinema Zino\" width=\"120\" style=\"margin:20px 0 12px 0;display:block;margin-left:auto;margin-right:auto;\">
                            <h2 style=\"color:#1cb0f6;font-size:2rem;margin:0 0 8px 0;letter-spacing:1px;\">Cinema Zino</h2>
                            <h3 style=\"color:#222;font-size:1.2rem;margin:0 0 18px 0;font-weight:normal;\">Hello, " + name + "!</h3>
                            <p style=\"color:#444;font-size:1rem;margin:0 0 24px 0;line-height:1.6;\">Welcome to <b>Cinema Zino</b>.<br>Your registration was successful!<br>Enjoy your movie experience with us.</p>
                            <a href=\"https://cinemazino.com\" style=\"display:inline-block;padding:14px 32px;background:#1cb0f6;color:#fff;text-decoration:none;border-radius:8px;font-weight:bold;font-size:1rem;box-shadow:0 2px 8px #1cb0f633;transition:background 0.2s;\">START YOUR EXPERIENCE</a>
                        </div>
                    """;

            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
            // Handle exception (log or ignore)
        }
    }
}
