package com.pagely.userservice;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pagely.userservice.domain.model.Gender;
import com.pagely.userservice.domain.model.User;
import com.pagely.userservice.domain.model.vo.Password;
import com.pagely.userservice.infrastructure.messaging.event.UserCreatedEvent;
import com.pagely.userservice.infrastructure.security.BCryptPasswordEncoder;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class UserEventSerializationTest {

    @Test
    void userCreatedEvent_직렬화_테스트() throws Exception {
        // 1. ObjectMapper 설정 (Java 8 날짜 지원)
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 2. 테스트 데이터 준비 (LocalDateTime 생성 방식 수정)
        LocalDate birthDate = LocalDate.of(2000, 10, 30);
        User user = User.create(
                "alice", "alice@test.com", Password.of("1234567890", new BCryptPasswordEncoder()), "김이박",
                "이상한나라", "010-2345-6789", Gender.FEMALE, birthDate
        );

        // 3. 실행
        UserCreatedEvent event = UserCreatedEvent.from(user);
        String jsonResult = mapper.writeValueAsString(event);

        // 4. 검증 (단순 출력 대신 assert 사용)
        System.out.println("Generated JSON: " + jsonResult);

        assertThat(jsonResult).contains("alice@test.com");
        assertThat(jsonResult).contains("이상한나라");
    }
}
