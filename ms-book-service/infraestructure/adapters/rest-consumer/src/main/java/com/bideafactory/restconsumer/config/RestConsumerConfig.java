package com.bideafactory.restconsumer.config;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import com.bideafactory.restconsumer.DiscountValidatorAdapter;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import reactor.netty.http.client.HttpClient;
@Slf4j
@Configuration
public class RestConsumerConfig {

    @Value("${adapter.restconsumer.timeout}")
    private int timeout;
    @Value("${adapter.restconsumer.ssl.keystore-pass}")
    private String keyStorePass;

    @Bean
    WebClient getCient(){
        return WebClient.builder()
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .clientConnector(getClientHttpConnector())
        .build();

    }
    private ClientHttpConnector getClientHttpConnector() {
        return new ReactorClientHttpConnector(HttpClient.create()
                .secure(context -> {
                    try {
                        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                        trustStore.load(DiscountValidatorAdapter.class.getClassLoader()
                        .getResourceAsStream("KeyStoreMockAPI"), keyStorePass.toCharArray());
                        context.sslContext(
                            SslContextBuilder.forClient()
                                .trustManager((X509Certificate) trustStore.getCertificate("mockapi.io"))
                                .build()
                        );
                    } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
                        log.error("Error while creating rest client: ", e);
                    }
                })
                .compress(true)
                .keepAlive(true)
                .option(CONNECT_TIMEOUT_MILLIS, timeout)
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(timeout, MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(timeout, MILLISECONDS));
                }));
    }
}
