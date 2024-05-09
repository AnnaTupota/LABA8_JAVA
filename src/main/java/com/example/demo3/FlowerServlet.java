package com.example.demo3;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import org.json.*;


@WebServlet(name = "FlowerServlet", value = "/FlowerServlet")
public class FlowerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

// Use the servlet context's real path to get the correct file path
        String filePath = getServletContext().getRealPath("/WEB-INF/data/flowers.json");

// Read the file content and write it to the response
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            StringBuilder jsonContent = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            response.getWriter().write(jsonContent.toString());
        } catch (FileNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\":\"File not found\"}");
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error reading file\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

// Parse the request to JSON
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error reading input\"}");
            return;
        }

// Convert the request to a JSON object
        JSONObject flowerData;
        try {
            flowerData = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid JSON data\"}");
            return;
        }

// Get the path to the JSON file
        String filePath = getServletContext().getRealPath("/WEB-INF/data/flowers.json");

// Read the existing content from the file
        File file = new File(filePath);
        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\":\"File not found\"}");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder jsonContent = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

// Convert the content to JSON
            JSONObject jsonObject = new JSONObject(jsonContent.toString());
            JSONArray flowersArray = jsonObject.getJSONArray("flowers");

// Add the new flower data to the array
            flowersArray.put(flowerData);

// Write the updated JSON array back to the file
            try (FileWriter fileWriter = new FileWriter(filePath)) {
                fileWriter.write(jsonObject.toString());
            } catch (IOException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\":\"Error writing to file\"}");
                return;
            }

// Set the response status to CREATED and write the new flower data
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(flowerData.toString());
        } catch (JSONException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error processing JSON\"}");
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error reading file\"}");
        }
    }
}
