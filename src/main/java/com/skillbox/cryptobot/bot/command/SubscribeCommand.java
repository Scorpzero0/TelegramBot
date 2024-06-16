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

/**
 * Обработка команды подписки на курс валюты
 */
@Service
@Slf4j
@AllArgsConstructor
public class SubscribeCommand implements IBotCommand {

    private final SubscribeRepository repository;
    private final GetPriceCommand getPriceCommand;

    @Override
    public String getCommandIdentifier() {
        return "subscribe";
    }

    @Override
    public String getDescription() {
        return "Подписывает пользователя на стоимость биткоина";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {

        var chatId = message.getChatId();
        Subscribe subscribe = repository.findById(chatId).orElseThrow();

        BigDecimal subscribeCost = getUserSubscribeCost(message);
        subscribe.setSubscribeCost(subscribeCost);
        repository.save(subscribe);

        getPriceCommand.processMessage(absSender, message, arguments);

        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());
        answer.setText("Новая подписка создана на стоимость " + subscribeCost + " USD");

        try {
            absSender.execute(answer);
            log.info("Создана подписка пользователя: {} на {} USD", message.getChat().getUserName(), subscribeCost);
        } catch (TelegramApiException e) {
            log.error("Error occurred in /subscribe command", e);
        }
    }

    private BigDecimal getUserSubscribeCost(Message message) {

        String messageText = message.getText();
        String subscribeCost = messageText.replaceAll(",", ".");

        return new BigDecimal(subscribeCost.replaceAll("[^\\d.]+|\\.(?!\\d)", ""));
    }
}