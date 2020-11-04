package dao;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import controller.DOSWrapper;
import model.Game;
import net.sourceforge.tess4j.TesseractException;

import java.awt.*;
import java.util.List;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GetAllGames extends javax.servlet.http.HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");

            DOSWrapper dosWrapper = new DOSWrapper();
            getServletContext().log("Wrapper on");
            List<Game> games  = dosWrapper.getGames();

            JsonArray gamesJson = new JsonArray();
            for (Game game : games) {
                gamesJson.add(game.toJson());
            }

            PrintWriter out = response.getWriter();
            out.println(gamesJson);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

    }
}
