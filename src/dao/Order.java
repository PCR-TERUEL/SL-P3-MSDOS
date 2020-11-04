package dao;

import com.google.gson.JsonObject;
import controller.DOSWrapper;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Order extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        try {
            String order = request.getParameter("order");
            DOSWrapper.order(order);

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
