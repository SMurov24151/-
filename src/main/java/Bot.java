import entity.Currency;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import service.CurrencyModeService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Bot extends TelegramLongPollingBot {

    private final CurrencyModeService currencyModeService = CurrencyModeService.getInstance();

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){
            handleMessege(update.getMessage());
        }
    }
    @SneakyThrows
    private void handleMessege(Message message) {
        if (message.hasText() && message.hasEntities()){
            Optional<MessageEntity> commandEntity =
            message.getEntities().stream().filter(e -> "bod_command".equals(e.getType())).findFirst();
            if (commandEntity.isPresent()){
                String command =
                        message.getText()
                                .substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
                switch (command){
                    case "/set_currency":
                        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                        Currency originalCurrency = currencyModeService.getOriginalCurrency(message.getChatId());
                        Currency targetCurrency = currencyModeService.getTargetCurrency()Currency(message.getChatId());
                        for (Currency currency : Currency.values()) {
                            buttons.add(
                                    Arrays.asList(
                                            InlineKeyboardButton.builder()
                                                    .text(currency.name())
                                                    .callbackData("ORIGINAL" + currency)
                                                    .build(),
                                            InlineKeyboardButton.builder()
                                                    .text(currency.name())
                                                    .callbackData("TARGET" + currency)
                                                    .build()));
                        }
                        execute(SendMessage.builder()
                                .text("Введите че нибудь")
                                .chatId(message.getChatId().toString())
                                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                                .build());
                        return;
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "";
    }

    @Override
    public String getBotToken(){
        return "";
    }
    @SneakyThrows
    public static void main(String[] args) {

        Bot bot = new Bot();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);
    }
}