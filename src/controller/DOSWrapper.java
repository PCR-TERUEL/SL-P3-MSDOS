package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DOSWrapper {

    public DOSWrapper() throws IOException {
        launchDOSBox();
    }

    private void launchDOSBox() throws IOException {
        printResults(Runtime.getRuntime().exec("cmd /c cd Database-MSDOS & start database.bat"));

    }


    public static void main(String[] args) throws IOException {
        new DOSWrapper();
    }


    public void printResults(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }


}
