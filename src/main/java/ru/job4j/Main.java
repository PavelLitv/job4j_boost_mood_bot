package ru.job4j;

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
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodContent;
import ru.job4j.bmb.repository.AwardRepository;
import ru.job4j.bmb.repository.MoodContentRepository;
import ru.job4j.bmb.repository.MoodRepository;

import java.util.List;

@EnableScheduling
@SpringBootApplication
public class Main {
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

    @SuppressWarnings({"checkstyle:MethodLength"})
    @Bean
    CommandLineRunner loadDatabase(MoodRepository moodRepository,
                                   MoodContentRepository moodContentRepository,
                                   AwardRepository awardRepository) {
        return args -> {
            var moods = moodRepository.findAll();
            if (!moods.isEmpty()) {
                return;
            }
            List<MoodContent> data = List.of(
                    new MoodContent(
                            new Mood("Воодушевленное настроение \uD83C\uDF1F", true),
                            "Великолепно! Вы чувствуете себя на высоте. Продолжайте в том же духе."
                    ),
                    new MoodContent(
                            new Mood("Успокоение и гармония \uD83E\uDDD8\u200D♂\uFE0F", true),
                            "Потрясающе! Вы в состоянии внутреннего мира и гармонии."
                    ),
                    new MoodContent(
                            new Mood("В состоянии комфорта ☺\uFE0F", true),
                            "Отлично! Вы чувствуете себя уютно и спокойно."
                    ),
                    new MoodContent(
                            new Mood("Легкое волнение \uD83C\uDF88", true),
                            "Замечательно! Немного волнения добавляет жизни краски."
                    ),
                    new MoodContent(
                            new Mood("Сосредоточенное настроение \uD83C\uDFAF", true),
                            "Хорошо! Ваш фокус на высоте, используйте это время эффективно."
                    ),
                    new MoodContent(
                            new Mood("Тревожное настроение \uD83D\uDE1F", false),
                            "Не волнуйтесь, всё пройдет. Попробуйте расслабиться и найти источник вашего беспокойства."
                    ),
                    new MoodContent(
                            new Mood("Разочарованное настроение \uD83D\uDE1E", false),
                            "Бывает. Не позволяйте разочарованию сбить вас с толку, всё наладится."
                    ),
                    new MoodContent(
                            new Mood("Усталое настроение \uD83D\uDE34", false),
                            "Похоже, вам нужен отдых. Позаботьтесь о себе и отдохните."
                    ),
                    new MoodContent(
                            new Mood("Вдохновенное настроение \uD83D\uDCA1", true),
                            "Потрясающе! Вы полны идей и энергии для их реализации."
                    ),
                    new MoodContent(
                            new Mood("Раздраженное настроение \uD83D\uDE20", false),
                            "Попробуйте успокоиться и найти причину раздражения, чтобы исправить ситуацию."
                    )
            );
            moodRepository.saveAll(data.stream().map(MoodContent::getMood).toList());
            moodContentRepository.saveAll(data);

            List<Award> awards = List.of(
                    new Award("Смайлик дня", "За 1 день хорошего настроения. Награда: Веселый смайлик "
                            + "или стикер, отправленный пользователю в качестве поощрения.", 1),
                    new Award("Настроение недели", "За 7 последовательных дней хорошего или отличного "
                            + "настроения. Награда: Специальный значок или иконка, отображаемая в профиле пользователя "
                            + "в течение недели.", 7),
                    new Award("Бонусные очки", "За каждые 3 дня хорошего настроения. Награда: Очки, "
                            + "которые можно обменять на виртуальные предметы или функции внутри приложения.", 3),
                    new Award("Персонализированные рекомендации", "После 5 дней хорошего настроения. "
                            + "Награда: Подборка контента или активности на основе интересов пользователя.", 5),
                    new Award("Достижение 'Солнечный луч", "За 10 дней непрерывного хорошего настроения. "
                            + "Награда: Разблокировка новой темы оформления или фона в приложении.", 10),
                    new Award("Виртуальный подарок", "После 15 дней хорошего настроения. Награда: "
                            + "Возможность отправить или получить виртуальный подарок внутри приложения.", 15),
                    new Award("Титул 'Лучезарный", "За 20 дней хорошего или отличного настроения. "
                            + "Награда: Специальный титул, отображаемый рядом с именем пользователя.", 20),
                    new Award("Доступ к премиум-функциям", "После 30 дней хорошего настроения. Награда: "
                            + "Временный доступ к премиум-функциям или эксклюзивному контенту.", 30),
                    new Award("Участие в розыгрыше призов", "За каждую неделю хорошего настроения. "
                            + "Награда: Шанс выиграть призы в ежемесячных розыгрышах.", 7),
                    new Award("Эксклюзивный контент", "После 25 дней хорошего настроения. Награда: "
                            + "Доступ к эксклюзивным статьям, видео или мероприятиям.", 25),
                    new Award("Награда 'Настроение месяца", "За поддержание хорошего или отличного "
                            + "настроения в течение целого месяца. Награда: Специальный значок, признание в сообществе "
                            + "или дополнительные привилегии.", 31),
                    new Award("Физический подарок", "После 60 дней хорошего настроения. Награда: "
                            + "Возможность получить небольшой физический подарок, например, открытку или фирменный "
                            + "сувенир.", 60),
                    new Award("Коучинговая сессия", "После 45 дней хорошего настроения. Награда: "
                            + "Бесплатная сессия с коучем или консультантом для дальнейшего улучшения благополучия.",
                            45),
                    new Award("Разблокировка мини-игр", "ЗПосле 14 дней хорошего настроения. Награда: "
                            + "Доступ к развлекательным мини-играм внутри приложения.", 14),
                    new Award("Персональное поздравление", "За значимые достижения (например, 50 дней "
                            + "хорошего настроения). Награда: Персонализированное сообщение от команды приложения или "
                            + "вдохновляющая цитата.", 50)
            );
            awardRepository.saveAll(awards);
        };
    }
}
