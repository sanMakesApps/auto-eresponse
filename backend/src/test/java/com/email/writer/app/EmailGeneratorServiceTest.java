package com.email.writer.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

class EmailGeneratorServiceTest {

    private EmailGeneratorService emailGeneratorService;

    private WebClient mockWebClient;
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        // Mock the WebClient and its chainable methods
        mockWebClient = mock(WebClient.class);
        WebClient.Builder mockBuilder = mock(WebClient.Builder.class);
        when(mockBuilder.build()).thenReturn(mockWebClient);

        emailGeneratorService = new EmailGeneratorService(mockBuilder);

        // Chained mocks for the WebClient methods
        requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);  // Use raw type here
        responseSpec = mock(WebClient.ResponseSpec.class);

        // Set up the mock behavior for the WebClient's chainable methods
        when(mockWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.header(anyString(), anyString())).thenReturn(requestBodyUriSpec);

        // Use doReturn to mock the bodyValue chaining behavior
        doReturn(requestHeadersSpec).when(requestBodyUriSpec).bodyValue(ArgumentMatchers.any());

        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(
                "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"This is a reply.\"}]}}]}"
        ));
    }

    @Test
    void testGenerateEmailReply() {
        EmailRequest request = new EmailRequest();
        request.setEmailContent("Hello?");
        request.setTone("Professional");

        String result = emailGeneratorService.generateEmailReply(request);
        assert result.contains("This is a reply.");
    }

    @Test
    void testGenerateEmailReplyHandlesBadJson() {
        // Simulate an error in the API response
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("INVALID_JSON"));

        EmailRequest request = new EmailRequest();
        request.setEmailContent("Hello?");
        request.setTone("Professional");

        String result = emailGeneratorService.generateEmailReply(request);
        assert result.contains("Error processing request");
    }
}
