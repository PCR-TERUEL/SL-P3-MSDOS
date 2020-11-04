package dao;

import com.google.gson.JsonObject;
import controller.DOSWrapper;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class InsertGame extends javax.servlet.http.HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        try {
            String name = request.getParameter("name");
            String type = request.getParameter("type");
            String cassette = request.getParameter("cassette");

            DOSWrapper.insertData(name, type, cassette);

            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("result", "ok");

            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().println(jsonResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

    }
}
