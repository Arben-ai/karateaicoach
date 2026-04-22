package ch.zhaw.karateaicoach.config;

import ch.zhaw.karateaicoach.tools.KarateTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAiConfig {

    @Bean
    public ChatClient chatClient(OpenAiChatModel model, KarateTools karateTools) {
        return ChatClient.builder(model)
                .defaultTools(karateTools)
                .build();
    }
}
