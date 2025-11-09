package com.midas.shootpointer.domain.progress.service;

import com.midas.shootpointer.domain.progress.dto.SseEvent;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProgressSseEmitter {
    @Value("${sse.ttl}")
    private long ttlMillis;

    @Value("${sse.event-name}")
    private String name;

    //유저당 보관 가능 최대 이벤트 수 : 360(5초당 이벤트 발행 -> 30분 : 360개)
    @Value("${sse.cache-max-size}")
    private int cacheMaxSize;

    @PostConstruct
    private void init(){
        log.info("ProgressSseEmitter start : {} {} {}",ttlMillis,name,cacheMaxSize);
    }
    //memberId -> emitter
    private static Map<String, SseEmitter> emitters=new ConcurrentHashMap<>();

    //memberId -> 최근 이벤트 (오름차순 : 맨 뒤가 최신)
    private static Map<String, Deque<SseEvent>> eventCache=new ConcurrentHashMap<>();

    /**
     * 구독 생성
     * @param memberId : 멤버 Id
     * @Param lastEventId : 클라이언트가 보낸 Last-Event-ID
     */
    public SseEmitter createEmitter(String memberId, String lastEventId, String jobId){
        SseEmitter emitter=new SseEmitter(ttlMillis);
        String sseKey=buildKey(memberId,jobId);
        emitters.put(sseKey,emitter);

        emitter.onCompletion(()-> {
            emitters.remove(sseKey);
            log.debug("SSE completed and removed {}",sseKey);
        });

        emitter.onTimeout(()-> {
            emitters.remove(sseKey);
            log.debug("SSE timeout and removed {}",sseKey);
        });

        emitter.onError(e-> {
            emitters.remove(sseKey);
            log.warn("SSE error message {}",e.getMessage());
        });

        if (lastEventId!=null && !lastEventId.isBlank()){
            try {
                long lastId=Long.parseLong(lastEventId);
                Deque<SseEvent> deque=eventCache.get(sseKey);

                if (deque!=null && !deque.isEmpty()){
                    //재전송
                    deque.stream()
                            .filter(e->e.eventId() > lastId)
                            .forEach(e->sendToEvent(emitter,e));
                }
            }catch (NumberFormatException ex){
                //잘못된 이벤트 id 입력 시
                log.warn("Invalid Last-Event-Id : {}",lastEventId);
                throw new CustomException(ErrorCode.NOT_INVALID_EVENT_ID);
            }
        }
        return emitter;
    }

    //실제 호출 시 메서드 적용 -> 클라이언트로 전송
    public void sendToClient(String jobId,String memberId,Object data){
        //Event Id는 TimeMillis() 사용
        String sseKey=buildKey(memberId,jobId);
        long eventId= Instant.now().toEpochMilli();
        SseEvent event=new SseEvent(eventId,name,data);

        /**
         * 1. 캐시에 저장.
         */
        eventCache.computeIfAbsent(sseKey,k-> new ArrayDeque<>());
        Deque<SseEvent> deque=eventCache.get(sseKey);

        synchronized (deque){
            deque.addLast(event);

            while (deque.size()>cacheMaxSize) deque.removeFirst();
        }

        /**
         * 2. 현재 연결되어 있는 emitter 전송
         */
        SseEmitter emitter=emitters.get(sseKey);
        if (emitter!=null){
            sendToEvent(emitter,event);
        }else {
            log.debug("No active SSE emitter for {} event cached for replay",sseKey);
        }
    }


    /**
     * cache 정리
     */
    @Scheduled(fixedRateString = "${sse.clean-up-interval}")
    public void cleanUp(){
        long expireBefore=Instant.now().toEpochMilli()-ttlMillis;
        eventCache.forEach((memberId,deque)->{
            synchronized (deque){
                while (!deque.isEmpty() && deque.peekFirst().eventId() < expireBefore){
                    deque.removeFirst();
                }
                if (deque.isEmpty() && !emitters.containsKey(memberId)){
                    eventCache.remove(memberId);
                }
            }
        });
    }

    private void sendToEvent(SseEmitter emitter,SseEvent event){
        try {
            emitter.send(SseEmitter.event()
                    .id(String.valueOf(event.eventId()))
                    .name(event.name())
                    .data(event.data()));
        } catch (Exception e){
            log.warn("Failed to send SSE event id = {} name = {} message = {}",event.eventId(),event.name(),e.getMessage());
            emitter.complete();
        }
    }

    private String buildKey(String memberId,String jobId){
        return String.format("%s:%s",memberId,jobId);
    }

}