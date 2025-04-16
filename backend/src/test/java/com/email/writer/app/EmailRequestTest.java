package com.email.writer.app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailRequestTest {

    @Test
    void testSettersAndGetters() {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setEmailContent("Hello, I have a billing issue.");
        emailRequest.setTone("Professional");

        assertEquals("Hello, I have a billing issue.", emailRequest.getEmailContent());
        assertEquals("Professional", emailRequest.getTone());
    }

    @Test
    void testEmptyFields() {
        EmailRequest emailRequest = new EmailRequest();
        assertNull(emailRequest.getEmailContent());
        assertNull(emailRequest.getTone());
    }
}
