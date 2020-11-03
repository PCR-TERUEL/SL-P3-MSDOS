package controller;

import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.awt.event.KeyEvent;
import java.io.IOException;


public class DOSWrapper {
    private final Robot robot;
    private Tesseract1 ocr;
    private static final List<String> dataTypes = List.of("UTILIDAD", "ARCADE", "CONVERSACIONAL", "VIDEOAVENTRA",
                                                          "SIMULADOR", "JUEGO DE MESA", "S. DEPORTIVO", "ESTRATEGIA");
    private static final List<String> ORDER_TYPES = List.of("NOMBRE", "TIPO", "CINTA", "ANTIGUEDAD");
    private static final String CMD_LAUNCH_DOSBOX = "cmd /c cd Database-MSDOS & start database.bat";
    private static final int DOSBOX_EXEC_TIME = 5000;
    private static final int ORDER_TIME = 30000;
    private static final int ROBOT_DELAY = 100;

    public DOSWrapper() throws IOException, InterruptedException, AWTException, TesseractException {
        launchDOSBox();
        robot = new Robot();
        initializeOCR();
        getDatabaseInfo();

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
        sendKeys(name);
        sendKeys(type);
        sendKeys(cassette);
        sendEnterKey();
        sendEnterKey();
    }

    public void ordenar(String orden) throws InterruptedException {
        sendKeys("3");
        sendKeys(String.valueOf(ORDER_TYPES.indexOf(orden) + 1));
        Thread.sleep(ORDER_TIME);
        sendEnterKey();
    }

    public void getDatabaseInfo() throws TesseractException {
        BufferedImage capture = robot.createScreenCapture(new Rectangle(80, 100, 720, 500));
        System.out.println(ocr.doOCR(capture));
    }

    private void sendKeys(String keys) {
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
        sendEnterKey();
    }

    private void sendEnterKey() {
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.delay(100);
        robot.keyRelease(KeyEvent.VK_ENTER);
        robot.delay(100);
    }


}
