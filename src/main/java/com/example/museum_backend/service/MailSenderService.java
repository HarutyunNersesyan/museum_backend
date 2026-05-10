package com.example.museum_backend.service;

import com.example.museum_backend.exceptions.CustomExceptions;
import com.example.museum_backend.model.dto.ForgotPassword;
import com.example.museum_backend.model.entity.User;
import com.example.museum_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MailSenderService {

    private final JavaMailSender mailSender;
    private final UserService userService;
    private final ForgotPassword forgotPassword;
    private final UserRepository userRepository;

    @Value("$(Museum)")
    private String fromMail;

    public void sendEmail(String mail, String subject, String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromMail);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        simpleMailMessage.setTo(mail);
        mailSender.send(simpleMailMessage);
    }

    public String handlePinRequest(String email, boolean checkVerifyMail) {
        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new CustomExceptions.UserNotFoundException("User not found with email: " + email));

        if (checkVerifyMail && !user.getVerifyMail()) {
            throw new CustomExceptions.UserNotActiveException("User is not active. Please verify your email.");
        }

        String pin = forgotPassword.generatePin();
        user.setPin(pin);
        userService.update(user);

        sendEmail(email, "Verify code", pin + "\n\n Բարի գալուստ։ Բացահայտեք արվեստի, մշակույթի և պատմության գեղեցկությունը՝ մեկ վայրում");

        schedulePinReset(email);

        return "A verification code has been sent to your email.";
    }


    public String resendVerificationCode(String email) {
        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new CustomExceptions.UserNotFoundException("User not found with email: " + email));

        if (user.getVerifyMail()) {
            throw new CustomExceptions.AlreadyVerifiedEmail("Email is already verified.");
        }

        String pin = forgotPassword.generatePin();
        user.setPin(pin);
        userService.update(user);

        sendEmail(email, "New Verification Code",
                "Your new verification code is: " + pin + "\n\nThis code will expire in 30 minutes.");

        return "A new verification code has been sent to your email.";
    }

    // MailSenderService.java - ավելացնել նոր մեթոդ
    // MailSenderService.java - ավելացնել այս մեթոդը

    public void sendTicketConfirmation(String toEmail, String userName, Map<String, Object> bookingDetails) {
        String subject = "Ձեր տոմսի հաստատում - " + bookingDetails.get("eventName");

        String message = buildTicketEmailContent(userName, bookingDetails);

        sendEmail(toEmail, subject, message);
    }

    private String buildTicketEmailContent(String userName, Map<String, Object> booking) {
        StringBuilder sb = new StringBuilder();
        sb.append("Բարև Ձեզ, ").append(userName).append("!\n\n");
        sb.append("Շնորհակալություն գնման համար։ Ստորև ներկայացված են ձեր տոմսի տվյալները.\n\n");
        sb.append("═══════════════════════════════════════════\n");
        sb.append("🎫 ՏՈՄՍԻ ՀԱՍՏԱՏՈՒՄ\n");
        sb.append("═══════════════════════════════════════════\n\n");
        sb.append("📌 Ամրագրման համար՝ ").append(booking.get("bookingId")).append("\n");
        sb.append("🎭 Միջոցառում՝ ").append(booking.get("eventName")).append("\n");
        sb.append("🏛️ Թանգարան՝ ").append(booking.get("museumName")).append("\n");
        sb.append("📍 Վայր՝ ").append(booking.get("location")).append("\n");
        sb.append("🎫 Տոմսերի քանակ՝ ").append(booking.get("ticketQuantity")).append("\n");
        sb.append("💰 Տոմսի գին՝ ").append(booking.get("ticketPrice")).append(" ֏\n");
        if ((Boolean) booking.get("includeGuide")) {
            sb.append("🎯 Ուղեցույցի ծառայություն՝ Ներառված է (+").append(booking.get("guidePrice")).append(" ֏)\n");
        }
        sb.append("💵 Ընդհանուր գումար՝ ").append(booking.get("totalAmount")).append(" ֏\n");
        sb.append("\n═══════════════════════════════════════════\n");
        sb.append("📧 Տոմսը ուղարկվել է ձեր էլ. հասցեին:\n");
        sb.append("   ").append(booking.get("email")).append("\n");
        sb.append("═══════════════════════════════════════════\n\n");
        sb.append("Մանրամասների համար այցելեք ձեր անձնական էջ։\n");
        sb.append("Հարգանքներով՝ Թանգարանների Համակարգ\n");

        return sb.toString();
    }


    public void schedulePinReset(String mail) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            userService.findUserByEmail(mail)
                    .ifPresent(user -> {
                        if (!user.getVerifyMail()) {
                            userRepository.delete(user);
                        } else {
                            userService.update(user);
                        }
                    });
        }, 30, TimeUnit.MINUTES);
    }
}