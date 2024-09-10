package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LabyrinthBot extends TelegramLongPollingBot {
    private String green = "🟩";
    private String black = "⬛";
    private String red = "🟥";
    private String gold = "🟨";

    private String botUsername;
    private String botToken;

    public LabyrinthBot() {
        loadBotCredentials("token.txt");
    }

    private void loadBotCredentials(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            this.botUsername = reader.readLine();
            this.botToken = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Labyrinth labyrinth = new Labyrinth(10, 10);

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            String callbackData = update.getCallbackQuery().getData();
            int messageId = update.getCallbackQuery().getMessage().getMessageId();
            boolean moved = false;
            boolean gameOver = false;

            switch (callbackData) {
                case "up":
                    moved = labyrinth.movePlayer('N');
                    break;
                case "down":
                    moved = labyrinth.movePlayer('S');
                    break;
                case "left":
                    moved = labyrinth.movePlayer('W');
                    break;
                case "right":
                    moved = labyrinth.movePlayer('E');
                    break;
            }

            if (moved) {
                labyrinth.rebuildLabyrinth();
                gameOver = labyrinth.isGameOver();
            }

            if (gameOver) {
                sendMessage(chatId, "Вітаємо! Ви досягли золотого пікселя! Гра закінчена.\n", null);
            } else if (moved) {
                String response = labyrinth.displayLabyrinth();
                EditMessageText newMessage = new EditMessageText();
                newMessage.setChatId(String.valueOf(chatId));
                newMessage.setMessageId(messageId);
                newMessage.setText(response);
                newMessage.setReplyMarkup(getDirectionButtons());

                try {
                    execute(newMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if ("/start".equals(messageText) || "/restart".equals(messageText)) {
                labyrinth = new Labyrinth(10, 10);
                sendMessage(chatId, labyrinth.displayLabyrinth(), getDirectionButtons());
            }
        }
    }

    private void sendMessage(long chatId, String text, InlineKeyboardMarkup keyboardMarkup) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setReplyMarkup(keyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    private InlineKeyboardMarkup getDirectionButtons() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        InlineKeyboardButton upButton = new InlineKeyboardButton();
        upButton.setText("⬆️");
        upButton.setCallbackData("up");
        rowInline1.add(upButton);
        rowsInline.add(rowInline1);

        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        InlineKeyboardButton leftButton = new InlineKeyboardButton();
        leftButton.setText("⬅️");
        leftButton.setCallbackData("left");

        InlineKeyboardButton downButton = new InlineKeyboardButton();
        downButton.setText("⬇️");
        downButton.setCallbackData("down");

        InlineKeyboardButton rightButton = new InlineKeyboardButton();
        rightButton.setText("➡️");
        rightButton.setCallbackData("right");

        rowInline2.add(leftButton);
        rowInline2.add(downButton);
        rowInline2.add(rightButton);
        rowsInline.add(rowInline2);

        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    class Labyrinth {
        private String[][] grid;
        private int playerX, playerY;
        private int goalX, goalY;
        private final int width, height;
        private final Random random = new Random();

        public Labyrinth(int width, int height) {
            this.width = width;
            this.height = height;
            generateLabyrinth();
        }

        private void generateLabyrinth() {
            grid = new String[height][width];
            for (int i = 0; i < height; i++)
                for (int j = 0; j < width; j++)
                    grid[i][j] = random.nextBoolean() ? green : red;
            playerX = random.nextInt(width);
            playerY = random.nextInt(height);
            placeGoldPixel();
            grid[playerY][playerX] = black;
            ensureFreeCellNextToPlayer();
        }

        public void rebuildLabyrinth() {
            String[][] newGrid = new String[height][width];
            for (int i = 0; i < height; i++)
                for (int j = 0; j < width; j++)
                    if (i == playerY && j == playerX)
                        newGrid[i][j] = black;
                    else if (i == goalY && j == goalX)
                        newGrid[i][j] = gold;
                    else
                        newGrid[i][j] = random.nextBoolean() ? green : red;
            grid = newGrid;
            ensureFreeCellNextToPlayer();
        }

        private void placeGoldPixel() {
            do {
                goalX = random.nextInt(width);
                goalY = random.nextInt(height);
            } while (grid[goalY][goalX].equals(red) || (goalX == playerX && goalY == playerY));
            grid[goalY][goalX] = gold;
        }

        public boolean movePlayer(char direction) {
            int newX = playerX, newY = playerY;
            switch (direction) {
                case 'N': newY--; break;
                case 'S': newY++; break;
                case 'W': newX--; break;
                case 'E': newX++; break;
            }

            if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                if (grid[newY][newX].equals(gold)) {
                    grid[playerY][playerX] = green;
                    playerX = newX;
                    playerY = newY;
                    grid[playerY][playerX] = black;
                    return false;
                }
                if (grid[newY][newX].equals(green)) {
                    grid[playerY][playerX] = green;
                    playerX = newX;
                    playerY = newY;
                    grid[playerY][playerX] = black;
                    return true;
                }
            }
            return false;
        }

        public void ensureFreeCellNextToPlayer() {
            List<int[]> directions = new ArrayList<>();
            if (playerX > 0) directions.add(new int[]{playerX - 1, playerY});
            if (playerX < width - 1) directions.add(new int[]{playerX + 1, playerY});
            if (playerY > 0) directions.add(new int[]{playerX, playerY - 1});
            if (playerY < height - 1) directions.add(new int[]{playerX, playerY + 1});

            boolean hasFreeCell = false;
            for (int[] dir : directions)
                if (grid[dir[1]][dir[0]].equals(green)) {
                    hasFreeCell = true;
                    break;
                }
            if (!hasFreeCell) {
                if (playerX > 0) grid[playerY][playerX - 1] = green;
                if (playerX < width - 1) grid[playerY][playerX + 1] = green;
                if (playerY > 0) grid[playerY - 1][playerX] = green;
                if (playerY < height - 1) grid[playerY + 1][playerX] = green;
            }
        }

        public boolean isGameOver() {
            return playerX == goalX && playerY == goalY;
        }
        public String displayLabyrinth() {
            StringBuilder builder = new StringBuilder();
            for (String[] row : grid) {
                for (String cell : row)
                    builder.append(cell);
                builder.append("\n");
            }
            return builder.toString();
        }
    }
}