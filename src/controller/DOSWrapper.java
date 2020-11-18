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
/*
        //List<String> trainers = List.of("spa-danimartin", "spa-danimejora", "spa-martinmejora", "spa-solodani", "spa-solomartin", "spa-solomejora", "spa-total");
        List<String> trainers = List.of("spa");

        for (String trainer : trainers) {
            System.out.println("--- " + trainer + " ----");
            ocr.setLanguage(trainer);
            System.out.println(doScreensCapture());
            sendKeys("6", true, false);
            Thread.sleep(500);
            System.out.println(doScreensCapture());
            sendKeys("u", false, false);
            Thread.sleep(500);
        }
*/
        //getGames();
        //getGames();
        //insertData("Prueba", "SIMULADOR", "1");
        //ordenar("NOMBRE");
        //searchGames(null,"PA");
        //nextEntrySearch();
        //editCurrentEntry("PRUEBA", "SIMULADOR", "A");
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

    public List<String> getDatabaseInfo() throws TesseractException {
        sendKeys("4", false, false);
        String infoDb = doScreensCapture();

        String nFiles = infoDb.split("\n")[3].split(" ")[1];
        String availableMem = infoDb.split("\n")[5].split(" ")[3];

        return List.of(nFiles, availableMem);
    }

    public List<Game> getGames() throws TesseractException {
        sendKeys("6", true, false);

        List<Game> games = new ArrayList<>();
        String[] linesPage;
        int indice = 0;
        int lineaComienzo = 4;
        String page = doScreensCapture();
        linesPage = page.split("\n");
        int k = 0;
        do {
            for (int i = lineaComienzo; i < linesPage.length - 1; i++) {
                String[] gameFields = linesPage[i].split(" ");
                if(!gameFields[0].isBlank()) { // Última página, líneas blancas
                    Game game = getGameInfoList(gameFields);
                    game.setId(indice);
                    games.add(game);
                    indice++;
                }
            }

            sendKeyEvent(KeyEvent.VK_SPACE);
            lineaComienzo = 5; // Primera página comenzamos en línea 4, el resto en línea 5
            page = doScreensCapture();
            linesPage = page.split("\n");
            k++;
        } while (k < 2);
    // while (!linesPage[2].trim().equals("M E M U"));

        return games;
    }

    public static Game searchGames(String index, String name) throws TesseractException, InterruptedException {
        sendKeys("7", false, false);
        if (index != null) {
            sendKeys("S", true, true);
            sendKeys(index, true, false);
        } else {
            sendKeys("N", true, true);
            sendKeys(name, true, true);
        }
        Thread.sleep(SEARCH_TIME);
        return verifySearchResult();
    }

    public static Game nextEntrySearch() throws TesseractException, InterruptedException {
        sendKeys("N", true, true);
        Thread.sleep(SEARCH_TIME);
        return verifySearchResult();
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
        Game game = getGameSearch(doScreensCapture());
        if (game == null) {
            sendKeyEvent(KeyEvent.VK_ENTER);
            sendKeys("N", true, true);
            System.out.println("Not found");
            return null;
        } else {
            return game;
        }
    }

    private static Game getGameSearch(String searchResult) throws TesseractException {
        String[] lines = searchResult.split("\n");

        if(lines.length > 4) {
            return null;
        }

        String[] fields = lines[2].split(" ");
        int register = Integer.parseInt(fields[fields.length - 1]);
        String cassette = fields[fields.length - 1].substring(6, fields[fields.length - 1].length());
        String type;
        String name;

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
                type = calssifyType(fields[fields.length - 2]);
                name = String.join(" ", Arrays.copyOfRange(fields, 1, fields.length - 2));
        }

        return new Game(name, type, cassette, register);
    }

    private Game getGameInfoList(String[] gameFields) {

        String cassette = gameFields[gameFields.length - 2];
        String type = "";
        String name = "";
        int register = Integer.parseInt(gameFields[gameFields.length - 1]);

        switch (gameFields[gameFields.length - 3]) {
            case "NESA":
                type = "JUEGO DE MESA";
                name = String.join(" ", Arrays.copyOfRange(gameFields, 1, gameFields.length - 5));
                break;
            case "DEPORTIVO":
                type = "S. DEPORTIVO";
                name = String.join(" ", Arrays.copyOfRange(gameFields, 1, gameFields.length - 4));
                break;
            default:
                type = calssifyType(gameFields[gameFields.length -3]);
                name = String.join(" ", Arrays.copyOfRange(gameFields, 1, gameFields.length - 3));
        }

        System.out.println("Name: " + name + " Type: " + type + " Cassette: " + cassette);
        return new Game(name, type, cassette, register);
    }

    private static String calssifyType(String type) {
        switch (type) {
            case "OTIEIDAD": return "UTILIDAD";
            case "SIMIEADOR": case "SIMJLADOR": return "SIMULADOR";
            case "CONVERSACIONAE": return "CONVERSACIONAL";
            case "ESTRATECIA": return "ESTRATECIA";
            default: return type;
        }
    }

    private static String doScreensCapture() throws TesseractException {
        BufferedImage capture = robot.createScreenCapture(new Rectangle(0, 0, 1366, 728));
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
            sendKeyEvent(keyCode);
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
