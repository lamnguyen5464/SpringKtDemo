package com.server.onlineup.common.utils;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Scanner;

public class JsonUtils {
    private static String extractPostRequestBody(HttpServletRequest request) {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            Scanner s = null;
            try {
                s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return s.hasNext() ? s.next() : "";
        }
        return "";
    }

    public static String GetAttrBodyFromRequest(HttpServletRequest req, String key) throws IOException, ParseException {
        String params = extractPostRequestBody(req);
        if(params.equals("")) return null;
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(params);
        return json.getAsString(key);
    }
}
