package controller;

import model.Game;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.event.KeyEvent;
import java.io.IOException;


public class DOSWrapper {
    private Robot robot;
    private Tesseract1 ocr;
    private Process dosbox;
    private static final List<String> dataTypes = List.of("UTILIDAD", "ARCADE", "CONVERSACIONAL", "VIDEOAVENTRA",
                                                          "SIMULADOR", "JUEGO DE MESA", "S. DEPORTIVO", "ESTRATEGIA");
    private static final List<String> ORDER_TYPES = List.of("NOMBRE", "TIPO", "CINTA", "ANTIGUEDAD", "-------");
    private static final String CMD_LAUNCH_DOSBOX = "cmd /c cd C:\\Users\\danie\\IdeaProjects\\SL-P3-MSDOS-WEB\\Database-MSDOS & start /max database.bat";
    private static final int DOSBOX_EXEC_TIME = 5000;
    private static final int ORDER_TIME = 30000;
    private static final int SEARCH_TIME = 1500;
    private static final int ROBOT_DELAY = 100;
    private static boolean isRunning;


    public static int nGames = 0;

    public DOSWrapper(boolean isRunning) throws IOException, InterruptedException, AWTException, TesseractException {
        if (!isRunning) {
            launchDOSBox();
            robot = new Robot();
            initializeOCR();
        }
    }

    private void martinQuiereEntrenarMierda() throws TesseractException, InterruptedException {
        //List<String> trainers = List.of("spa-danimartin", "spa-danimejora", "spa-martinmejora", "spa-solodani", "spa-solomartin", "spa-solomejora", "spa-total");
        List<String> trainers = List.of("spa");

        for (String trainer : trainers) {
            System.out.println("--- " + trainer + " ----");
            ocr.setLanguage(trainer);
            System.out.println(doScreensCapture());
            sendKeys("6", true, false);
            Thread.sleep(500);

           // sendKeys("u", false, false);

            for(int i = 0; i < 9; i++) {
                sendKeyEvent(KeyEvent.VK_SPACE);
            }
            System.out.println(doScreensCapture());
            Thread.sleep(500);
        }
        System.exit(0);
    }

    private void initializeOCR() {
        ocr = new Tesseract1();
        ocr.setLanguage("spa");
    }

    private void launchDOSBox() throws IOException, InterruptedException {
        dosbox = Runtime.getRuntime().exec(CMD_LAUNCH_DOSBOX);
        Thread.sleep(DOSBOX_EXEC_TIME);
        isRunning = true;
    }

    public void killDOSBox() {
        dosbox.destroy();
    }

    public static void main(String[] args) throws IOException, InterruptedException, AWTException, TesseractException {
        new DOSWrapper(false);
    }

    public int getFilesNumber() throws TesseractException, InterruptedException {
        sendKeys("4", false, false);
        Thread.sleep(100);
        String infoDb = doScreensCapture();
        int line = getNLinesContent(infoDb, "CONTIENE");
        System.out.println(infoDb + "xd " + line);
        nGames = Integer.parseInt(infoDb.split("\n")[line].split(" ")[1]);
        sendKeyEvent(KeyEvent.VK_ENTER);

        return nGames;
    }

    private static int getNLinesContent(String screen, String containWord) {
        String[] lines = screen.split("\n");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].contains(containWord)) {
                return i;
            }
        }

        return -1;
    }

    public List<Game> getGames() throws TesseractException {
        sendKeys("6", true, false);
        List<Game> games = new ArrayList<>();
        int gameIndex = 1;
        String screen = doScreensCapture();
        String[] linesPage = screen.split("\n");
        int firstLine = getNLinesContent(screen, "NOMBRE");
        System.out.println("Fist Line: " + firstLine);

        do {
            for (int i = firstLine + 1; i < linesPage.length - 3; i++) {
                String[] gameFields = linesPage[i].split(" ");
                if(!gameFields[0].isBlank()) { // Última página, líneas blancas
                    Game game = getGameInfo(gameFields);
                    game.setId(String.valueOf(gameIndex));
                    games.add(game);
                    gameIndex++;
                }
            }

            sendKeyEvent(KeyEvent.VK_SPACE);
            screen = doScreensCapture();
            linesPage = screen.split( "\n");
            firstLine = getNLinesContent(screen, "NOMBRE");
        } while (!isOnMainMenu(screen));
        sendKeys("u", false, false);
        return games;
    }

    private boolean isOnMainMenu(String screen) {
        return screen.contains("M E N U");
    }

    public List<Game> searchByName(String name) throws TesseractException {
        List<Game> gamesResultSearch = new ArrayList<>();

        sendKeys("7", false, false);
        sendKeys("N", true, true);
        sendKeys(name, true, false);

        Game game = verifySearchResult();

        int id = 1;
        boolean add = true;
        while(game != null) {
            game.setId(String.valueOf(id));
            for (Game gameOfList : gamesResultSearch) {
                if (gameOfList.equals(game)) {
                    add = false;
                    break;
                }
            }
            if (add) {
                gamesResultSearch.add(game);
            }
            add = true;
            System.out.println("Hey!" + game.toJson());
            game = verifySearchResult();
            id++;
        }

        return gamesResultSearch;
    }

    public List<Game> searchByCassette(String cassette) throws TesseractException {
        List<Game> games = getGames();
        List<Game> gamesResult = new ArrayList<>();
        for (Game game : games) {
            if (game.hasCassette(cassette)) {
                gamesResult.add(game);
            }
        }

        return gamesResult;
    }


    private Game verifySearchResult() throws TesseractException {
        String[] lines = doScreensCapture().split("\n");
        for (String line : lines) System.out.println("l" + line);

        if (lines.length > 6) {
            sendKeyEvent(KeyEvent.VK_ENTER);
            sendKeys("N",true, true);
            sendKeys("N",true, true);
            return null;
        }  else {
            sendKeys("N",true, true);
            return getGameInfoSearch(lines[2]);
        }
    }

    private Game getGameInfoSearch(String searchResult) throws TesseractException {
        String[] fields = searchResult.split(" ");
        String register = fields[0];

        String type;
        String name;
        System.out.println("Field: " + fields[2]);
        switch (fields[fields.length - 2]) {
            case "MESA":
                type = "JUEGO DE MESA";
                name = String.join(" ", Arrays.copyOfRange(fields, 2, fields.length - 4));
                break;
            case "DEPORTIVO":
                type = "S. DEPORTIVO";
                name = String.join(" ", Arrays.copyOfRange(fields, 1, fields.length - 3));
                break;
            default:
                type = fields[fields.length - 2];
                name = String.join(" ", Arrays.copyOfRange(fields, 1, fields.length - 2)).substring(1).trim();
        }

        List<String> cassette = getCassette(fields[fields.length - 1].substring(6), name);
        return new Game(name, type, cassette, register);
    }

    private static Game getGameInfo(String[] gameFields) {
        for (String gameField : gameFields) System.out.println(gameField);

        String register = patchRegisterNumber(gameFields[gameFields.length - 1]);

        List<String> nameType = getTypeNameGame(gameFields);
        List<String> cassette = getCassette(gameFields[gameFields.length - 2], nameType.get(1));

        Game game = new Game(nameType.get(1), nameType.get(0), cassette, register);
        game.setId(register);
        return game;
    }

    private static String patchRegisterNumber(String register) {
        if (register.equals("7EE")) {
            return "700";
        } else if (register.equals("6EE")) {
            return "600";
        } else if (register.equals("5EE")) {
            return "500";
        } else if (register.equals("4EE")) {
            return "400";
        } else if (register.equals("3EE")) {
            return "400";
        } else if (register.equals("ZEE")) {
            return "200";
        } else if (register.equals("1EE")) {
            return "100";
        }
        return register;
    }
    private static List<String> getCassette(String entireCasetteField, String gameName) {
        List<String> cassette = new ArrayList<>();
        if (entireCasetteField.length() > 2) {
            if (gameName.equals("BEACH HEAD II")) {
                cassette.add("-31");
            } else if (gameName.equals("BUBBLER")) {
                cassette.add("13");
                cassette.add("26");
            } else {
                cassette.add(String.valueOf(entireCasetteField.charAt(0)));
                cassette.add(entireCasetteField.substring(2));
            }

        } else {
            cassette.add(entireCasetteField);
        }
        return cassette;
    }

    // Type = list[0], Name = list[1]
    private static List<String> getTypeNameGame(String[] gameFields) {
        String type = "";
        String name = "";
        switch (gameFields[gameFields.length - 3]) {
            case "MESA":
                type = "JUEGO DE MESA";
                name = String.join(" ", Arrays.copyOfRange(gameFields, 1, gameFields.length - 5));
                break;
            case "DEPORTIVO":
                type = "S. DEPORTIVO";
                name = String.join(" ", Arrays.copyOfRange(gameFields, 1, gameFields.length - 4));
                break;
            default:
                type = gameFields[gameFields.length - 3];
                name = String.join(" ", Arrays.copyOfRange(gameFields, 1, gameFields.length - 3));
        }

        return List.of(type, name);
    }


    private String doScreensCapture() throws TesseractException {
        BufferedImage capture = robot.createScreenCapture(new Rectangle(0, 0, 9999, 9999));
        return ocr.doOCR(capture);
    }

    private void sendKeys(String keys, boolean enter, boolean mayus) {
        if (mayus) {
            robot.keyPress(KeyEvent.VK_SHIFT);
        }
        for (char c : keys.toCharArray()) {
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
            if (KeyEvent.CHAR_UNDEFINED == keyCode) {
                throw new RuntimeException("Key code not found for character " + c);
            }
            if (!Character.isDigit(c)) {
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.delay(ROBOT_DELAY);
            }
            sendKeyEvent(keyCode);
            if (!Character.isDigit(c)) {
                robot.keyRelease(KeyEvent.VK_SHIFT);
            }

        }

        if (mayus) {
            robot.keyRelease(KeyEvent.VK_SHIFT);
        }

        if (enter) {
            sendKeyEvent(KeyEvent.VK_ENTER);
        }
    }

    private void sendKeyEvent(int keyEvent) {
        robot.keyPress(keyEvent);
        robot.delay(ROBOT_DELAY);
        robot.keyRelease(keyEvent);
        robot.delay(ROBOT_DELAY);
    }
}
