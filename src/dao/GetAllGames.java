package dao;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import controller.DOSWrapper;
import model.Game;
import net.sourceforge.tess4j.TesseractException;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GetAllGames extends javax.servlet.http.HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");

            DOSWrapper dosWrapper = new DOSWrapper(false);
            List<Game> games  = dosWrapper.getGames();

            request.setAttribute("games", games);
            request.setAttribute("total_num_games", dosWrapper.getFilesNumber());
            request.getRequestDispatcher("/index.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        doPost(request, response);
    }
}
