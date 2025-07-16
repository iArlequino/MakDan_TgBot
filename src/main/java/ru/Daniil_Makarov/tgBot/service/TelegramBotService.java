package ru.Daniil_Makarov.tgBot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.Daniil_Makarov.tgBot.entity.*;
import ru.Daniil_Makarov.tgBot.repository.CategoryRepository;
import ru.Daniil_Makarov.tgBot.repository.ClientRepository;
import ru.Daniil_Makarov.tgBot.repository.ClientOrderRepository;
import ru.Daniil_Makarov.tgBot.repository.OrderProductRepository;
import jakarta.annotation.PostConstruct;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TelegramBotService {
    private final ProductService productService;
    private final ClientService clientService;
    private final CategoryRepository categoryRepository;
    private final ClientRepository clientRepository;
    private final ClientOrderRepository clientOrderRepository;
    private final OrderProductRepository orderProductRepository;
    private TelegramBot bot;
    private final Map<Long, List<Product>> currentOrders = new HashMap<>();

    @Value("${telegram.bot.token}")
    private String botToken;

    public TelegramBotService(
            ProductService productService,
            ClientService clientService,
            CategoryRepository categoryRepository,
            ClientRepository clientRepository,
            ClientOrderRepository clientOrderRepository,
            OrderProductRepository orderProductRepository) {
        this.productService = productService;
        this.clientService = clientService;
        this.categoryRepository = categoryRepository;
        this.clientRepository = clientRepository;
        this.clientOrderRepository = clientOrderRepository;
        this.orderProductRepository = orderProductRepository;
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
        }
    }

    private void addProductToOrder(long chatId, long productId) {
        Product product = productService.findById(productId);
        if (product == null) {
            bot.execute(new SendMessage(chatId, "Товар не найден"));
            return;
        }
        List<Product> cart = currentOrders.computeIfAbsent(chatId, k -> new ArrayList<>());
        cart.add(product);
        bot.execute(new SendMessage(chatId, "Товар " + product.getName() + " добавлен в корзину"));
    }

    private void showCategoryProducts(long chatId, long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            bot.execute(new SendMessage(chatId, "Категория не найдена"));
            return;
        }

        List<Category> subCategories = categoryRepository.findAll().stream()
            .filter(c -> c.getParent() != null && c.getParent().getId().equals(categoryId))
            .collect(Collectors.toList());

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
        List<Category> categories = categoryRepository.findAll().stream()
                .filter(c -> c.getParent() == null)
                .collect(Collectors.toList());
                
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
        List<Product> cart = currentOrders.get(chatId);
        if (cart == null || cart.isEmpty()) {
            bot.execute(new SendMessage(chatId, "Корзина пуста"));
            return;
        }
        StringBuilder message = new StringBuilder("Ваш заказ:\n");
        double total = 0;
        for (Product product : cart) {
            message.append(String.format("%s = %.2f руб.\n", product.getName(), product.getPrice()));
            total += product.getPrice();
        }
        message.append(String.format("\nИтого: %.2f руб.", total));
        bot.execute(new SendMessage(chatId, message.toString()));
    }

    private void createOrder(long chatId) {
        List<Product> cart = currentOrders.get(chatId);
        if (cart == null || cart.isEmpty()) {
            bot.execute(new SendMessage(chatId, "Корзина пуста"));
            return;
        }

        Client client = clientService.findByExternalId(chatId);
        if (client == null) {
            client = new Client();
            client.setExternalId(chatId);
            client.setFullName("User " + chatId); // Временное решение
            client.setPhoneNumber("Unknown");
            client.setAddress("Unknown");
            client = clientRepository.save(client);
        }

        ClientOrder order = new ClientOrder();
        order.setClient(client);
        order.setStatus(1);
        double total = cart.stream()
            .mapToDouble(Product::getPrice)
            .sum();
        order.setTotal(total);
        order = clientOrderRepository.save(order);

        for (Product product : cart) {
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setClientOrder(order);
            orderProduct.setProduct(product);
            orderProduct.setCountProduct(1);
            orderProductRepository.save(orderProduct);
        }

        currentOrders.remove(chatId);
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
