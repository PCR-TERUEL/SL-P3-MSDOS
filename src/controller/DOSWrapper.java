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
    private static Robot robot;
    private static Tesseract1 ocr;
    private static final List<String> dataTypes = List.of("UTILIDAD", "ARCADE", "CONVERSACIONAL", "VIDEOAVENTRA",
                                                          "SIMULADOR", "JUEGO DE MESA", "S. DEPORTIVO", "ESTRATEGIA");
    private static final List<String> ORDER_TYPES = List.of("NOMBRE", "TIPO", "CINTA", "ANTIGUEDAD", "-------");
    private static final String CMD_LAUNCH_DOSBOX = "cmd /c cd C:\\Users\\danie\\IdeaProjects\\SL-P3-MSDOS-WEB\\Database-MSDOS & start /max database.bat";
    private static final int DOSBOX_EXEC_TIME = 5000;
    private static final int ORDER_TIME = 30000;
    private static final int SEARCH_TIME = 1500;
    private static final int ROBOT_DELAY = 100;

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
        Runtime.getRuntime().exec(CMD_LAUNCH_DOSBOX);
        Thread.sleep(DOSBOX_EXEC_TIME);
    }



    public static void main(String[] args) throws IOException, InterruptedException, AWTException, TesseractException {
        new DOSWrapper(false);
    }

    public static void insertData(String name, String type, String cassette) {
        sendKeys("1", false, false);
        sendKeys(name, true, false);
        sendKeys(type, true, false);
        sendKeys(cassette, true, false);
        sendKeyEvent(KeyEvent.VK_ENTER);
        sendKeyEvent(KeyEvent.VK_ENTER);
    }

    public static void order(String orden) throws InterruptedException {
        sendKeys("3", false, false);
        sendKeys(String.valueOf(ORDER_TYPES.indexOf(orden) + 1),true, false);
        Thread.sleep(ORDER_TIME);
        sendKeyEvent(KeyEvent.VK_ENTER);
    }

    public int getFilesNumber() throws TesseractException {
        sendKeys("4", false, false);
        String infoDb = doScreensCapture();
        System.out.println(infoDb);
        int nFiles = Integer.parseInt(infoDb.split("\n")[3].split(" ")[1]);
        sendKeyEvent(KeyEvent.VK_ENTER);

        return nFiles;
    }

    public List<Game> getGames() throws TesseractException {
        sendKeys("6", true, false);

        List<Game> games = new ArrayList<>();
        int gameIndex = 1;
        String[] linesPage = doScreensCapture().split("\n");
        do {
            for (int i = 4; i < linesPage.length - 3; i++) {
                String[] gameFields = linesPage[i].split(" ");
                if(!gameFields[0].isBlank()) { // Última página, líneas blancas
                    Game game = getGameInfo(gameFields);
                    game.setId(gameIndex);
                    games.add(game);
                    gameIndex++;
                }
            }

            sendKeyEvent(KeyEvent.VK_SPACE);
            linesPage = doScreensCapture().split("\n");
        } while (!linesPage[1].trim().equals("M E N U"));
        sendKeys("u", false, false);
        return games;
    }

    public List<Game> searchByName(String name) throws TesseractException, InterruptedException {
        List<Game> gamesResultSearch = new ArrayList<>();

        sendKeys("7", false, false);
        sendKeys("N", true, true);
        sendKeys(name, true, false);

        Game game = verifySearchResult();
        int id = 0;
        while(game != null) {
            game.setId(id);
            gamesResultSearch.add(game);
            game = verifySearchResult();
            id++;
        }

        return gamesResultSearch;
    }

    public Game searchByIndex(String index) throws TesseractException {
        sendKeys("7", false, false);
        sendKeys("S", true, true);
        sendKeys(index, true, false);
        String searchResult = doScreensCapture().split("\n")[1];
        return getGameInfoSearch(searchResult);
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

    public void editCurrentEntry(String name, String type, String cassette) {
        sendKeys("S", true, true);
        sendKeys("S", true, true);
        sendKeys(name, true, true);
        sendKeys(type, true, true);
        sendKeys(cassette, true, true);
        sendKeyEvent(KeyEvent.VK_ENTER);
        sendKeys("N", true, true);
    }

    private Game verifySearchResult() throws TesseractException {
        String[] lines = doScreensCapture().split("\n");
        for (String line : lines) System.out.println(line);
        if (lines.length > 3) {
            sendKeyEvent(KeyEvent.VK_ENTER);
            sendKeys("N",true, true);
            return null;
        }  else {
            sendKeys("N",true, true);
            return getGameInfoSearch(lines[1]);
        }
    }

    private Game getGameInfoSearch(String searchResult) throws TesseractException {
        String[] fields = searchResult.split(" ");
        int register = Integer.parseInt(fields[0]);

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

    private Game getGameInfo(String[] gameFields) {
        for(String field : gameFields) System.out.println(field);
        int register = Integer.parseInt(patchRegisterNumber(gameFields[gameFields.length - 1]));
        List<String> nameType = getTypeNameGame(gameFields);
        List<String> cassette = getCassette(gameFields[gameFields.length - 2], nameType.get(1));

        Game game = new Game(nameType.get(0), nameType.get(1), cassette, register);
        game.setId(register);
        return game;
    }

    private String patchRegisterNumber(String register) {
        if (register.equals("6EE")) {
            return "600";
        }
        return register;
    }
    private List<String> getCassette(String entireCasetteField, String gameName) {
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


    private static String doScreensCapture() throws TesseractException {
        BufferedImage capture = robot.createScreenCapture(new Rectangle(0, 0, 9999, 9999));
        return ocr.doOCR(capture);
    }

    private static void sendKeys(String keys, boolean enter, boolean mayus) {
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

    private static void sendKeyEvent(int keyEvent) {
        robot.keyPress(keyEvent);
        robot.delay(ROBOT_DELAY);
        robot.keyRelease(keyEvent);
        robot.delay(ROBOT_DELAY);
    }
}
