package dao;

import controller.DOSWrapper;
import model.Game;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchGames extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)  {
        try {
            System.out.println("Hello world xd que sea todo una linea enrome de menos---------------------------------------------------");
            DOSWrapper dosWrapper = new DOSWrapper(false);
            List<Game> gameResult;

            String searchInput = request.getParameter("search");
            System.out.println(searchInput);
            String searchSelector = request.getParameter("searchSelector");
            System.out.println(searchSelector);
            if (searchSelector.equals("name")) {
                gameResult = dosWrapper.searchByName(searchInput);
            } else {
                gameResult = dosWrapper.searchByCassette(searchInput);
            }

            request.setAttribute("games", gameResult);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            request.getRequestDispatcher("/index.jsp").forward(request, response);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

    }
}
