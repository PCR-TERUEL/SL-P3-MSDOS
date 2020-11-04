package dao;

import com.google.gson.JsonObject;
import controller.DOSWrapper;
import model.Game;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NextEntrySearch extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        try {

            Game game = DOSWrapper.nextEntrySearch();

            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().println(game.toJson());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

    }
}