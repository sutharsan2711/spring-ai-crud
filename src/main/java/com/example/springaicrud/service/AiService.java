package com.example.springaicrud.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatModel chatModel;

    public String askAi(String question) {
        return chatModel.call(new Prompt(question))
                .getResult()
                .getOutput()
                .getContent();
    }

    public String generatePersonSummary(String name, String address) {
        String promptText = String.format(
                "Generate a short professional bio for a person named %s who lives in %s. Keep it under 3 sentences.",
                name, address
        );
        return askAi(promptText);
    }
}