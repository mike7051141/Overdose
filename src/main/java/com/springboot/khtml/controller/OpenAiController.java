package com.springboot.khtml.controller;

import com.springboot.khtml.dto.openAiDto.RequestOpenAi;
import com.springboot.khtml.dto.openAiDto.ResponseOpenAi;
import com.springboot.khtml.entity.Message;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class OpenAiController {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    private final RestTemplate template;

    private final Logger LOGGER = LoggerFactory.getLogger(OpenAiController.class);

    @GetMapping("/api/v1/main/getGptApi")
    public ResponseEntity<Map<String,String>> getGptApi(@RequestParam(name = "prompt") String prompt, @ApiIgnore HttpSession session) {
        // 세션에서 이전 대화 내용 가져오기
        List<Message> messages = (List<Message>) session.getAttribute("messages");

        // 세션에 대화 내용이 없는 경우 초기 메시지 설정
        if (messages == null) {
            messages = new ArrayList<>();
            // 초기 메시지 설정
//            messages.add(new Message("system", "다음은 정신건강 상담 상황에서의 대화 흐름 예시입니다. 사용자가 질문을 하면 적절한 반응을 제공해주세요. 같이 해결해 본다고 하지말고 너가 해결책을 내려줘"));
//            messages.add(new Message("user", "안녕하세요."));
//            messages.add(new Message("assistant", "안녕하세요! 오늘 기분은 어떠신가요?"));
//            messages.add(new Message("user", "지금 너무 힘들어요. 모든 게 무너지는 것 같아요."));
//            messages.add(new Message("assistant", "지금 많이 힘드시겠어요. 괜찮아요, 여기서 도와드릴 수 있어요. 최근에 마약을 사용한 적이 있나요?"));
//            messages.add(new Message("user", "네, 어제 사용했어요."));
//            messages.add(new Message("assistant", "힘든 순간을 보내셨군요. 다음엔 어떻게 하면 다시 사용하지 않을 수 있을지 같이 생각해볼까요?"));
            messages.add(new Message("system", "다음은 정신건강 상담 상황에서의 대화 흐름 예시입니다. 사용자가 질문을 하면 적절한 반응을 제공해주세요." +
                    "질문: \"오늘 기분은 어떠신가요?\"\n" +
                    "답변: \"지금 너무 힘들어요. 모든 게 무너지는 것 같아요.\"\n" +
                    "반응: \"지금 많이 힘드시겠어요. 괜찮아요, 여기서 도와드릴 수 있어요.\"\n" +
                    "질문: \"최근에 마약을 사용한 적이 있나요?\"\n" +
                    "답변: \"네, 어제 사용했어요.\"\n" +
                    "반응: \"힘든 순간을 보내셨군요. 다음엔 어떻게 하면 다시 사용하지 않을 수 있을지 같이 생각해볼까요?\"" +
                    "물음표로 너무 많이 끝내지말고 함께 해결해보자 보다는 격려의 말 혹은 해결책을 제시해주는게 좋아보여"));
            // 세션에 초기 메시지 저장
            session.setAttribute("messages", messages);
        }

        // 로그에 현재 메시지 내용 출력
        LOGGER.info("Current messages: {}", messages);

        // 현재 메시지 추가
        messages.add(new Message("user", prompt));

        // 요청 생성
        RequestOpenAi request = new RequestOpenAi(model, messages);

        // API 호출
        ResponseOpenAi chatGPTResponse = template.postForObject(apiURL, request, ResponseOpenAi.class);
        String responseContent = chatGPTResponse.getChoices().get(0).getMessage().getContent();

        // AI 응답 메시지 추가
        messages.add(new Message("assistant", responseContent));

        // 세션에 대화 내용 저장
        session.setAttribute("messages", messages);
        Map<String,String> response = new HashMap<>();
        response.put("message",responseContent);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
