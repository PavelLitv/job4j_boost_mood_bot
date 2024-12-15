package ru.job4j;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.job4j.bmb.model.Award;
import ru.job4j.bmb.model.DailyAdvice;
import ru.job4j.bmb.model.MoodContent;
import ru.job4j.bmb.repository.AwardRepository;
import ru.job4j.bmb.repository.DailyAdviceRepository;
import ru.job4j.bmb.repository.MoodContentRepository;
import ru.job4j.bmb.repository.MoodRepository;

import java.io.InputStream;
import java.util.List;

@EnableScheduling
@SpringBootApplication
public class Main {
    ClassLoader cl = getClass().getClassLoader();
    ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            var bot = ctx.getBean(TelegramLongPollingBot.class);
            var botsApi = new TelegramBotsApi(DefaultBotSession.class);
            try {
                botsApi.registerBot(bot);
                System.out.println("Bot registered!");
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        };
    }

    @Bean
    CommandLineRunner loadDatabase(MoodRepository moodRepository,
                                   MoodContentRepository moodContentRepository,
                                   AwardRepository awardRepository,
                                   DailyAdviceRepository dailyAdviceRepository) {
        return args -> {
            if (moodRepository.findAll().isEmpty()) {
                try (InputStream inputStream = cl.getResourceAsStream("moodContent.json")) {
                    List<MoodContent> moodContent = mapper.readValue(inputStream, new TypeReference<>() { });
                    moodRepository.saveAll(moodContent.stream().map(MoodContent::getMood).toList());
                    moodContentRepository.saveAll(moodContent);
                }
            }
            if (awardRepository.findAll().isEmpty()) {
                try (InputStream inputStream = cl.getResourceAsStream("awards.json")) {
                List<Award> awards = mapper.readValue(inputStream, new TypeReference<>() { });
                awardRepository.saveAll(awards);
                }
            }
            if (dailyAdviceRepository.findAll().isEmpty()) {
                try (InputStream inputStream = cl.getResourceAsStream("dailyAdvice.json")) {
                    List<DailyAdvice> dailyAdvices = mapper.readValue(inputStream, new TypeReference<>() { });
                    dailyAdviceRepository.saveAll(dailyAdvices);
                }
            }
        };
    }
}
