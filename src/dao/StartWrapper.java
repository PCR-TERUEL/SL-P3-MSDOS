package dao;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import controller.DOSWrapper;
import model.Game;
import java.util.List;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StartWrapper extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        try {
            //new DOSWrapper();

            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");

            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("result", "ok");
            response.getWriter().println(jsonResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

    }
}
