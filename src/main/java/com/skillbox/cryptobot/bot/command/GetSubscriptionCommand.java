package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.model.Subscribe;
import com.skillbox.cryptobot.repositories.SubscribeRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;

@Service
@Slf4j
@AllArgsConstructor
public class GetSubscriptionCommand implements IBotCommand {

    private final SubscribeRepository repository;

    @Override
    public String getCommandIdentifier() {
        return "get_subscription";
    }

    @Override
    public String getDescription() {
        return "Возвращает текущую подписку";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {

        var chatId = message.getChatId();
        Subscribe subscribe = repository.findById(chatId).orElseThrow();

        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());
        answer.setText(getMessageAnswerText(subscribe));

        try {
            absSender.execute(answer);
            log.info("Запрос пользователя {} текущей подписки", message.getChat().getUserName());
        } catch (TelegramApiException e) {
            log.error("Error occurred in /get_subscription command", e);
        }
    }

    private String getMessageAnswerText(Subscribe subscribe) {

        BigDecimal subscribeCost = subscribe.getSubscribeCost();

        if (subscribeCost == null) {
            return "Активные подписки отсутствуют";
        } else {
            return "Вы подписаны на стоимость биткоина "
                    + subscribeCost + " USD";
        }
    }
}