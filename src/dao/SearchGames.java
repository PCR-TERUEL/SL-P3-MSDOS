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
            List<Game> gameResult;

            DOSWrapper dosWrapper = new DOSWrapper(false);

            String searchInput = request.getParameter("search");
            String searchSelector = request.getParameter("searchSelector");

            if (searchSelector.equals("name")) {
                gameResult = dosWrapper.searchByName(searchInput);
            } else {
                gameResult = dosWrapper.searchByCassette(searchInput);
            }

            dosWrapper.killDOSBox();

            request.setAttribute("games", gameResult);
            request.setAttribute("total_num_games", dosWrapper.getFilesNumber());
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
