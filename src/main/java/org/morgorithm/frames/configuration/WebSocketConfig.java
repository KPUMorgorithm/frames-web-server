package org.morgorithm.frames.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

//이 어노테이션은 해당 클래스가 Bean의 설정을 할 것이라는 것을 나타낸다.
@Configuration
//WebSocket 서버를 활성화하는 데 사용한다.
@EnableWebSocketMessageBroker
//implements WebSocketMessageBrokerConfigurer
//웹 소켓 연결을 구성하기 위한 메서드를 구현하고 제공한다.
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    //클라이언트가 웹 소켓 서버에 연결하는 데 사용할 웹 소켓 엔드 포인트를 등록
    //엔드 포인트 구성에 withSockJS()를 사용한다.
    //webSocket은 통신 프로토콜 일뿐입니다.
    // 특정 주제를 구독한 사용자에게만 메시지를 보내는 방법 또는 특정 사용자에게 메시지를 보내는 방법과 같은 내용은 정의하지 않습니다.
    // 이러한 기능을 위해서는 STOMP가 필요하다.
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        System.out.println("end point 연결!!!***");
        registry.addEndpoint("/ws").withSockJS();
    }

    //한 클라이언트에서 다른 클라이언트로 메시지를 라우팅 하는 데 사용될 메시지 브로커를 구성한다.
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        System.out.println("confiureMessageBroker 연동!!**");
        //"/app" 시작되는 메시지가 message-handling methods으로 라우팅 되어야 한다는 것을 명시
        registry.setApplicationDestinationPrefixes("/app");
        //"/topic" 시작되는 메시지가 메시지 브로커로 라우팅 되도록 정의한다.
        //메시지 브로커는 특정 주제를 구독 한 연결된 모든 클라이언트에게 메시지를 broadcast한다.
        registry.enableSimpleBroker("/topic");
    }



}
