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
    private final Robot robot;
    private Tesseract1 ocr;
    private static final List<String> dataTypes = List.of("UTILIDAD", "ARCADE", "CONVERSACIONAL", "VIDEOAVENTRA",
                                                          "SIMULADOR", "JUEGO DE MESA", "S. DEPORTIVO", "ESTRATEGIA");
    private static final List<String> ORDER_TYPES = List.of("NOMBRE", "TIPO", "CINTA", "ANTIGUEDAD", "-------");
    private static final String CMD_LAUNCH_DOSBOX = "cmd /c cd Database-MSDOS & start /max database.bat";
    private static final int DOSBOX_EXEC_TIME = 5000;
    private static final int ORDER_TIME = 30000;
    private static final int ROBOT_DELAY = 100;

    public DOSWrapper() throws IOException, InterruptedException, AWTException, TesseractException {
        launchDOSBox();
        robot = new Robot();
        initializeOCR();

        getGames();

        //insertData("Prueba", "SIMULADOR", "1");
        //ordenar("NOMBRE");
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

    public void insertData(String name, String type, String cassette) {
        robot.keyPress(KeyEvent.VK_1);
        robot.keyRelease(KeyEvent.VK_1);
        sendKeys(name, true);
        sendKeys(type, true);
        sendKeys(cassette, true);
        sendKeyEvent(KeyEvent.VK_ENTER);
        sendKeyEvent(KeyEvent.VK_ENTER);
    }

    public void ordenar(String orden) throws InterruptedException {
        sendKeys("3", false);
        sendKeys(String.valueOf(ORDER_TYPES.indexOf(orden) + 1),true);
        Thread.sleep(ORDER_TIME);
        sendKeyEvent(KeyEvent.VK_ENTER);
    }

    public List<String> getDatabaseInfo() throws TesseractException {
        sendKeys("4", false);
        String infoDb = doScreensCapture();

        String nFiles = infoDb.split("\n")[3].split(" ")[1];
        String availableMem = infoDb.split("\n")[5].split(" ")[3];

        return List.of(nFiles, availableMem);
    }

    public List<Game> getGames() throws TesseractException {
        sendKeys("6", true);

        List<Game> games = new ArrayList<>();
        String[] linesPage;
        int indice = 0;
        int lineaComienzo = 4;
        String page = doScreensCapture();
        linesPage = page.split("\n");

        do {
            for (int i = lineaComienzo; i < linesPage.length - 1; i++) {
                String[] gameFields = linesPage[i].split(" ");
                if(!gameFields[0].isBlank()) { // Última página, líneas blancas
                    Game game = getGameInfo(gameFields);
                    game.setId(indice);
                    games.add(game);
                    indice++;
                }
            }

            sendKeyEvent(KeyEvent.VK_SPACE);
            lineaComienzo = 5; // Primera página comenzamos en línea 4, el resto en línea 5
            page = doScreensCapture();
            linesPage = page.split("\n");

        } while (!linesPage[2].trim().equals("M E M U"));

        return games;
    }

    private Game getGameInfo(String[] gameFields) {

        String cassette = gameFields[gameFields.length - 2];
        String type = "";
        String name = "";

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
        return new Game(name, type, cassette);
    }

    private String calssifyType(String type) {
        switch (type) {
            case "OTIEIDAD": return "UTILIDAD";
            case "SIMIEADOR": return "SIMULADOR";
            case "CONVERSACIONAE": return "CONVERSACIONAL";
            case "ESTRATECIA": return "ESTRATECIA";
            default: return type;
        }
    }

    private String doScreensCapture() throws TesseractException {
        BufferedImage capture = robot.createScreenCapture(new Rectangle(0, 0, 1340, 700));
        return ocr.doOCR(capture);
    }

    private void sendKeys(String keys, boolean enter) {
        for (char c : keys.toCharArray()) {
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
            if (KeyEvent.CHAR_UNDEFINED == keyCode) {
                throw new RuntimeException("Key code not found for character " + c);
            }
            robot.keyPress(keyCode);
            robot.delay(ROBOT_DELAY);
            robot.keyRelease(keyCode);
            robot.delay(ROBOT_DELAY);
        }
        if (enter) {
            sendKeyEvent(KeyEvent.VK_ENTER);
        }
    }

    private void sendKeyEvent(int keyEvent) {
        robot.keyPress(keyEvent);
        robot.delay(100);
        robot.keyRelease(keyEvent);
        robot.delay(100);
    }


}
