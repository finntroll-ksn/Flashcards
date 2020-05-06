package flashcards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.Random;

class InputParameters {
    private String importFile = "";
    private String exportFile = "";

    public String getImportFile() {
        return importFile;
    }

    public void setImportFile(String importFile) {
        this.importFile = importFile;
    }

    public String getExportFile() {
        return exportFile;
    }

    public void setExportFile(String exportFile) {
        this.exportFile = exportFile;
    }

    protected void initParams(String[] args) {
        for (int i = 0; i < args.length; i += 2) {
            switch (args[i]) {
                case "-import":
                    this.setImportFile(args[i + 1]);
                    break;
                case "-export":
                    this.setExportFile(args[i + 1]);
                    break;
                default:
                    break;
            }
        }
    }
}

class FlashcardProcessor {
    final static Scanner scanner = new Scanner(System.in);

    void processFlashcard(InputParameters input) {
        boolean askAction = true;
        ArrayList<String> logger = new ArrayList<>();
        Flashcards cards = new Flashcards(scanner, logger);
        String message;

        if (!input.getImportFile().equals("")) {
            cards.importCards(input.getImportFile());
        }

        do {
            message = "Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):";

            System.out.println(message);
            logger.add(message);

            message = scanner.nextLine();
            logger.add(message);

            switch (message) {
                case "add":
                    cards.addCard();
                    break;
                case "remove":
                    cards.removeCard();
                    break;
                case "import":
                    cards.importCards();
                    break;
                case "export":
                    cards.exportCards();
                    break;
                case "ask":
                    cards.askFlashcard();
                    break;
                case "log":
                    cards.exportLog();
                    break;
                case "hardest card":
                    cards.printHardestCard();
                    break;
                case "reset stats":
                    cards.resetStatistics();
                    break;
                case "exit":
                    if (!input.getExportFile().equals("")) {
                        askAction = cards.exit(input.getExportFile());
                    } else {
                        askAction = cards.exit();
                    }
                    break;
                default:
                    return;
            }
        } while (askAction);
    }
}

class Card {
    protected Map<String, Map<String, Integer>> map;
    Scanner scanner;
    ArrayList<String> logger;
    String message;

    Card(Scanner scanner, ArrayList<String> logger) {
        map = new HashMap<>();
        this.scanner = scanner;
        this.logger = logger;
    }

    public boolean exit(String fileName) {
        message = "Bye bye!";

        System.out.println(message);
        logger.add(message);

        exportCards(fileName);

        return false;
    }

    public boolean exit() {
        message = "Bye bye!";

        System.out.println(message);
        logger.add(message);

        return false;
    }

    public void addCard() {
        String key, value;
        message = "The card:";

        System.out.println(message);
        logger.add(message);

        key = scanner.nextLine();
        logger.add(key);

        if (checkKeyExists(key)) {
            message = String.format("The card \"%s\" already exists.", key);

            System.out.printf("%s%n%n", key);
            logger.add(message);

            return;
        }

        message = "The definition of the card:";

        System.out.println(message);
        logger.add(message);

        value = scanner.nextLine();
        logger.add(value);

        if (checkValueExists(value)) {
            message = String.format("The definition \"%s\" already exists.", value);

            System.out.printf("%s%n%n", key);
            logger.add(message);

            return;
        }

        Map<String, Integer> subMap = new HashMap<>();
        subMap.put(value, 0);

        map.put(key, subMap);
        message = String.format("The pair (\"%s\":\"%s\") has been added.", key, value);

        System.out.printf("%s%n%n", message);
        logger.add(message);
    }

    public void removeCard() {
        String key;
        message = "The card:";

        System.out.println(message);
        logger.add(message);

        key = scanner.nextLine();
        logger.add(key);

        if (!checkKeyExists(key)) {
            message = String.format("Can't remove \"%s\": there is no such card.", key);

            System.out.printf("%s%n%n", message);
            logger.add(message);

            return;
        }

        map.remove(key);
        message = "The card has been removed.";

        System.out.printf("%s%n%n", message);
        logger.add(message);
    }

    public void exportLog() {
        message = "File name:";

        System.out.println(message);
        logger.add(message);

        String fileName = scanner.nextLine();
        logger.add(fileName);

        File file = new File(fileName);

        try (PrintWriter printWriter = new PrintWriter(file)) {
            for (String logString : logger) {
                printWriter.printf("%s%n", logString);
            }

            message = "The log has been saved.";

            System.out.printf("%s%n%n", message);
            logger.add(message);
        } catch (IOException e) {
            message = String.format("An exception occurs %s", e.getMessage());

            System.out.println(message);
            logger.add(message);
        }
    }

    public void resetStatistics() {
        for (Map<String, Integer> mapValue : map.values()) {
            mapValue.replaceAll((k, v) -> 0);
        }

        message = "Card statistics has been reset.";

        System.out.printf("%s%n%n", message);
        logger.add(message);
    }

    void importCards() {
        message = "File name:";

        System.out.println(message);
        logger.add(message);

        String fileName = scanner.nextLine();
        logger.add(fileName);

        importCards(fileName);
    }

    void importCards(String fileName) {
        File file = new File(fileName);
        int counter = 0;

        try (Scanner scanner = new Scanner(file)) {
            String[] elements;

            while (scanner.hasNext()) {
                elements = scanner.nextLine().split(";");
                Map<String, Integer> value = new HashMap<>();

                value.put(elements[1], Integer.parseInt(elements[2]));
                map.put(elements[0], value);
                counter++;
            }

            message = String.format("%d cards have been loaded.", counter);

            System.out.printf("%s%n%n", message);
            logger.add(message);
        } catch (FileNotFoundException e) {
            message = "File not found.";

            System.out.println(message);
            logger.add(message);
        }
    }

    void exportCards() {
        message = "File name:";

        System.out.println(message);
        logger.add(message);

        String fileName = scanner.nextLine();
        logger.add(fileName);

        exportCards(fileName);
    }

    void exportCards(String fileName) {
        File file = new File(fileName);
        int counter = 0;

        try (PrintWriter printWriter = new PrintWriter(file)) {
            for (Map.Entry<String, Map<String, Integer>> pair  : map.entrySet()) {
                for (Map.Entry<String, Integer> secondMap  : pair.getValue().entrySet()) {
                    printWriter.printf("%s;%s;%d%n", pair.getKey(), secondMap.getKey(), secondMap.getValue());
                    counter++;
                }
            }

            message = String.format("%d cards have been saved.", counter);

            System.out.printf("%s%n%n", message);
            logger.add(message);
        } catch (IOException e) {
            message = String.format("An exception occurs %s", e.getMessage());

            System.out.println(message);
            logger.add(message);
        }
    }

    private boolean checkKeyExists(String key) {
        return map.containsKey(key);
    }

    private boolean checkValueExists(String value) {
        for (Map<String, Integer> mapValue : map.values()) {
            if (mapValue.containsKey(value)) {
                return true;
            }
        }

        return false;
    }

    public void printHardestCard() {
        int max = 0;
        ArrayList<String> cardNames = new ArrayList<>();

        for (Map.Entry<String, Map<String, Integer>> pair  : map.entrySet()) {
            for (Map.Entry<String, Integer> secondMap  : pair.getValue().entrySet()) {
                if (max < secondMap.getValue()) {
                    max = secondMap.getValue();
                    cardNames.clear();
                    cardNames.add(pair.getKey());
                } else if (max > 0 && max == secondMap.getValue()) {
                    cardNames.add(pair.getKey());
                }
            }
        }

        switch (cardNames.size()) {
            case 0:
                message = "There are no cards with errors.";

                System.out.printf("%s%n%n", message);
                logger.add(message);
                break;
            case 1:
                message = String.format("The hardest card is \"%s\". You have %d errors answering it.", cardNames.get(0), max);

                System.out.printf("%s%n%n", message);
                logger.add(message);
                break;
            default:
                message = String.format("The hardest card are \"%s\". You have %d errors answering it.",
                        String.join("\", \"", cardNames),
                        max);

                System.out.printf("%s%n%n", message);
                logger.add(message);
                break;
        }
    }

    public void askFlashcard() {
        message = "How many times to ask?";
        Random random = new Random();
        String[] keys = map.keySet().toArray(new String[map.size()]);
        String key, stringResult, value;

        System.out.println(message);
        logger.add(message);

        Integer numberOfCards = Integer.parseInt(scanner.nextLine());
        logger.add(numberOfCards.toString());

        for (int i = 0; i < numberOfCards; i++) {
            key = keys[random.nextInt(map.size())];

            message = String.format("Print the definition of \"%s\":.", key);

            System.out.printf("%s%n", message);
            logger.add(message);

            value = scanner.nextLine();
            logger.add(value);

            stringResult = correctAnswer(key);

            if (value.equals(stringResult)) {
                message= "Correct answer.";

                System.out.printf("%s%n%n", message);
                logger.add(message);
            } else {
                Map<String, Integer> mapValues = map.get(key);

                for (Map.Entry<String, Integer> mapValue : mapValues.entrySet()) {
                    mapValues.put(mapValue.getKey(), mapValue.getValue() + 1);
                }

                if (checkValueExists(value)) {
                    message = String.format("Wrong answer. The correct one is \"%s\", you've just written the definition of \"%s\".", stringResult, getRightKeyOfFlashcard(value));

                    System.out.printf("%s%n", message);
                    logger.add(message);
                } else {
                    message = String.format("Wrong answer. The correct one is \"%s\".", stringResult);

                    System.out.printf("%s%n", message);
                    logger.add(message);
                }
            }
        }
    }

    private String getRightKeyOfFlashcard(String value) {
        for (Map.Entry<String, Map<String, Integer>> pair  : map.entrySet()) {
            for (Map.Entry<String, Integer> secondMap  : pair.getValue().entrySet()) {
                if (secondMap.getKey().equals(value)) {
                    return pair.getKey();
                }
            }
        }

        return null;
    }

    private String correctAnswer(String key) {
        Map<String, Integer> pair = map.get(key);

        return pair.keySet().toArray(new String[pair.size()])[0];
    }
}

class Flashcards extends Card {

    Flashcards(Scanner scanner, ArrayList<String> logger) {
        super(scanner, logger);
    };
}

public class Main {

    public static void main(String[] args) {
        InputParameters input = new InputParameters();
        input.initParams(args);

        FlashcardProcessor flashcardProcessor = new FlashcardProcessor();
        flashcardProcessor.processFlashcard(input);
    }
}
