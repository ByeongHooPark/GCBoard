package com.green.nowon.chess.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker //MessageBroker가 지원하는 WebSocket 메세지 처리를 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	
	
  //접속시 필요한 주소
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
	//js : new SockJS('/my-ws') >> 이런 js에서 요청하는 주소랑 일치시켜야함
    registry.addEndpoint("/my-ws").withSockJS();
    //나중에 security쓰면 위의 주소를 security에 등록해야한다 
  }
  
  //접속 이후
  //메모리 기반 메세지 브로커가 
  //접두사가 붙은 목적지에서 클라이언트에게 인사말 메세지를 다시 전달 할 수 있도록 호출하는것
  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
	//구독한 사람들 전부에게 메세지를 보낸다
    config.enableSimpleBroker("/topic/chess");
    //클라이언트(웹 페이지) -> 서버에 메세지 전송시 요청하는 Prefixes
    config.setApplicationDestinationPrefixes("/app");//ws://localhost:8080/app/uri --->
  }
  
}