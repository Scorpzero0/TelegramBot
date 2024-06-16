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

/**
 * Обработка команды отмены подписки на курс валюты
 */
@Service
@Slf4j
@AllArgsConstructor
public class UnsubscribeCommand implements IBotCommand {

    private final SubscribeRepository repository;

    @Override
    public String getCommandIdentifier() {
        return "unsubscribe";
    }

    @Override
    public String getDescription() {
        return "Отменяет подписку пользователя";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {

        var chatId = message.getChatId();
        Subscribe subscribe = repository.findById(chatId).orElseThrow();

        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());
        answer.setText(cancelSubscribeAndGetMessageAnswerText(subscribe));

        try {
            absSender.execute(answer);
            log.info("Пользователь {} удалил свою текущую подписку", message.getChat().getUserName());
        } catch (TelegramApiException e) {
            log.error("Error occurred in /unsubscribe command", e);
        }

    }

    private String cancelSubscribeAndGetMessageAnswerText(Subscribe subscribe) {

        if (subscribe.getSubscribeCost() == null) {
            return "Активные подписки отсутствуют";
        } else {
            subscribe.setSubscribeCost(null);
            repository.save(subscribe);
            return "Подписка отменена";
        }
    }
}