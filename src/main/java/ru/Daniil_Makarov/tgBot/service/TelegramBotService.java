package ru.Daniil_Makarov.tgBot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.Daniil_Makarov.tgBot.entity.*;
import jakarta.annotation.PostConstruct;

import java.util.*;

@Service
public class TelegramBotService {
    private final ProductService productService;
    private final ClientService clientService;
    private final CategoryService categoryService;
    private TelegramBot bot;

    @Value("${telegram.bot.token}")
    private String botToken;

    public TelegramBotService(
            ProductService productService,
            ClientService clientService,
            CategoryService categoryService) {
        this.productService = productService;
        this.clientService = clientService;
        this.categoryService = categoryService;
    }

    @PostConstruct
    public void init() {
        bot = new TelegramBot(botToken);
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::processUpdate);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void processUpdate(Update update) {
        if (update.callbackQuery() != null) {
            processCallback(update);
        } else if (update.message() != null) {
            processMessage(update);
        }
    }

    private void processCallback(Update update) {
        String callbackData = update.callbackQuery().data();
        long chatId = update.callbackQuery().from().id();

        if (callbackData.startsWith("product:")) {
            long productId = Long.parseLong(callbackData.split(":")[1]);
            addProductToOrder(chatId, productId);
        } else if (callbackData.startsWith("category:")) {
            long categoryId = Long.parseLong(callbackData.split(":")[1]);
            showCategoryProducts(chatId, categoryId);
        } else if (callbackData.equals("back_to_categories")) {
            sendCategoriesMenu(chatId);
        }
    }

    private void addProductToOrder(long chatId, long productId) {
        Product product = productService.addProductToCart(chatId, productId);
        if (product == null) {
            bot.execute(new SendMessage(chatId, "Товар не найден"));
            return;
        }
        bot.execute(new SendMessage(chatId, "Товар " + product.getName() + " добавлен в корзину"));
    }

    private void showCategoryProducts(long chatId, long categoryId) {
        Category category = categoryService.findById(categoryId);
        if (category == null) {
            bot.execute(new SendMessage(chatId, "Категория не найдена"));
            return;
        }

        List<Category> subCategories = categoryService.findSubCategories(categoryId);

        if (!subCategories.isEmpty()) {
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            for (Category sub : subCategories) {
                markup.addRow(new InlineKeyboardButton(sub.getName())
                    .callbackData("category:" + sub.getId()));
            }
            markup.addRow(new InlineKeyboardButton("Назад к категориям")
                .callbackData("back_to_categories"));
            bot.execute(new SendMessage(chatId, "Выберите подкатегорию:").replyMarkup(markup));
            return;
        }

        List<Product> products = productService.findByCategoryId(categoryId);
        bot.execute(new SendMessage(chatId, "В категории найдено товаров: " + products.size()));

        if (products.isEmpty()) {
            bot.execute(new SendMessage(chatId, "В данной категории нет товаров"));
            return;
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for (Product product : products) {
            markup.addRow(new InlineKeyboardButton(
                String.format("%s - %.2f руб.", product.getName(), product.getPrice()))
                .callbackData("product:" + product.getId()));
        }
        markup.addRow(new InlineKeyboardButton("Назад к категориям")
            .callbackData("back_to_categories"));

        bot.execute(new SendMessage(chatId, "Выберите товар:")
            .replyMarkup(markup));
    }

    private void processMessage(Update update) {
        String text = update.message().text();
        long chatId = update.message().chat().id();

        switch (text.toLowerCase()) {
            case "/start":
                sendMainMenu(chatId);
                break;
            case "меню":
                sendCategoriesMenu(chatId);
                break;
            case "корзина":
                showCart(chatId);
                break;
            case "оформить заказ":
                createOrder(chatId);
                break;
            default:
                processMenuNavigation(chatId, text);
        }
    }

    private void sendMainMenu(long chatId) {
        KeyboardButton[] row1 = {new KeyboardButton("Меню")};
        KeyboardButton[] row2 = {new KeyboardButton("Корзина")};
        KeyboardButton[] row3 = {new KeyboardButton("Оформить заказ")};
        
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(row1, row2, row3);
        markup.resizeKeyboard(true);
        
        bot.execute(new SendMessage(chatId, "Добро пожаловать! Выберите действие:")
            .replyMarkup(markup));
    }

    private void sendCategoriesMenu(long chatId) {
        List<Category> categories = categoryService.findRootCategories();
        if (categories.isEmpty()) {
            bot.execute(new SendMessage(chatId, "Категории не найдены"));
            return;
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for (Category category : categories) {
            markup.addRow(new InlineKeyboardButton(category.getName())
                .callbackData("category:" + category.getId()));
        }

        bot.execute(new SendMessage(chatId, "Выберите категорию:")
            .replyMarkup(markup));
    }

    private void showCart(long chatId) {
        String cartText = productService.getCartText(chatId);
        bot.execute(new SendMessage(chatId, cartText));
    }

    private void createOrder(long chatId) {
        ClientOrder cart = clientService.getOrCreateCart(chatId);
        List<Product> products = clientService.getCartProducts(cart);
        
        if (products.isEmpty()) {
            bot.execute(new SendMessage(chatId, "Корзина пуста"));
            return;
        }

        clientService.submitOrder(cart);
        bot.execute(new SendMessage(chatId, "Заказ успешно оформлен!"));
    }

    private void processMenuNavigation(long chatId, String text) {
        if (text.equals("Назад")) {
            sendMainMenu(chatId);
        } else {
            sendCategoriesMenu(chatId);
        }
    }
}



