package com.ecommerce.emailservice.consumers;

import com.ecommerce.emailservice.DTOs.SendEmailEventDto;
import com.ecommerce.emailservice.Utils.EmailUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import javax.mail.Session;
import java.util.Properties;

@Service
public class SendEmailEventConsumer {

    private ObjectMapper objectMapper;

    public SendEmailEventConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    @KafkaListener(topics = "sendEmail", groupId = "EmailService")//I have created a kafka listener to listen to the sendEmail topic
    public void handleSendEmailEvent(String message) throws JsonProcessingException {
        //I will receive the message i.e json string from the topic and convert it to SendEmailEventDto
        SendEmailEventDto event = objectMapper.readValue(message, SendEmailEventDto.class);

        String to = event.getTo();
        String from = event.getFrom();
        String subject = event.getSubject();
        String body = event.getBody();

        System.out.println("TLSEmail Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("team.ecommerce8181@gmail.com", "iygnfdcqwgrcshws");
            }
        };
        Session session = Session.getInstance(props, auth);

        EmailUtil.sendEmail(session, to,subject, body);

    }
}
