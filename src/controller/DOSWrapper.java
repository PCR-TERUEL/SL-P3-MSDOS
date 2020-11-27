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

    public DOSWrapper() throws IOException, InterruptedException, AWTException, TesseractException {
        launchDOSBox();
        robot = new Robot();
        initializeOCR();
        boolean quiereEntrenarMartin = false;

        if (quiereEntrenarMartin) {
            martinQuiereEntrenarMierda();
        }


        List<Game> games = searchByName("II");
        for (Game game : games) {
            System.out.println(game.toJson());
        }
        //System.out.println(getFilesNumber());

        //getGames();
        //insertData("Prueba", "SIMULADOR", "1");
        //ordenar("NOMBRE");
        //searchGames(null,"PA");
        //nextEntrySearch();
        //editCurrentEntry("PRUEBA", "SIMULADOR", "A");
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
        new DOSWrapper();
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
        int k = 0;
        do {
            for (int i = 3; i < linesPage.length - 3; i++) {
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
            k++;
        } while (k < 2);
    // while (!linesPage[2].trim().equals("M E N U"));
        sendKeys("u", false, false);
        return games;
    }

    public static List<Game> searchByName(String name) throws TesseractException, InterruptedException {
        List<Game> gamesResultSearch = new ArrayList<>();

        sendKeys("7", false, false);
        sendKeys("N", true, true);
        sendKeys(name, true, false);

        Game game = verifySearchResult();

        while(game != null) {
            gamesResultSearch.add(game);
            game = verifySearchResult();
        }

        return gamesResultSearch;
    }

    public static Game searchByIndex(String index) throws TesseractException {
        sendKeys("7", false, false);
        sendKeys("S", true, true);
        sendKeys(index, true, false);
        String searchResult = doScreensCapture().split("\n")[1];
        return getGameInfoSearch(searchResult);
    }

    public static void editCurrentEntry(String name, String type, String cassette) {
        sendKeys("S", true, true);
        sendKeys("S", true, true);
        sendKeys(name, true, true);
        sendKeys(type, true, true);
        sendKeys(cassette, true, true);
        sendKeyEvent(KeyEvent.VK_ENTER);
        sendKeys("N", true, true);
    }

    private static Game verifySearchResult() throws TesseractException, InterruptedException {
        String[] lines = doScreensCapture().split("\n");
        System.out.println(doScreensCapture());
        if (lines.length > 6) {
            sendKeyEvent(KeyEvent.VK_ENTER);
            sendKeys("N",true, true);
            return null;
        }  else {
            sendKeys("N",true, true);
            return getGameInfoSearch(lines[2]);
        }
    }

    private static Game getGameInfoSearch(String searchResult) throws TesseractException {

        String[] fields = searchResult.split(" ");
        int register = Integer.parseInt(fields[0]);
        String cassette = fields[fields.length - 1].substring(6, fields[fields.length - 1].length());
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

        return new Game(name, type, cassette, register);
    }

    private Game getGameInfo(String[] gameFields) {

        String cassette = gameFields[gameFields.length - 2];
        int register = Integer.parseInt(gameFields[gameFields.length - 1]);
        List<String> nameType = getTypeNameGame(gameFields);

        Game game = new Game(nameType.get(0), nameType.get(1), cassette, register);
        game.setId(register);
        return game;
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
            if (mayus && Character.isDigit(c)) {
                robot.keyPress(KeyEvent.VK_SHIFT);
                sendKeyEvent(keyCode);
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
