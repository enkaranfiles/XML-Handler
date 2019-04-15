package com.mycompany.app;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

public class App {
    //search methods 
    /*
    give the name and that returns an list of names whose names 
    */
    public static String search(String input1, String input2) {
        String result = "";
        try {
            File inputFile = new File("EEAS.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("NAME");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if((!input1.equals("")) && input2.equals("")) {
                        if(eElement.getElementsByTagName("FIRSTNAME").item(0).getTextContent().equals(input1)) {
                            result += eElement.getAttribute("Entity_id") + "\n";
                            result += eElement.getElementsByTagName("FIRSTNAME").item(0).getTextContent() + "\n";
                            result += eElement.getElementsByTagName("LASTNAME").item(0).getTextContent() + "\n";
                            result += "---------------\n";
                        }
                    }else if(input1.equals("") && !(input2.equals(""))) {
                        if(eElement.getElementsByTagName("LASTNAME").item(0).getTextContent().equals(input2)) {
                            result += eElement.getAttribute("Entity_id") + "\n";
                            result += eElement.getElementsByTagName("FIRSTNAME").item(0).getTextContent() + "\n";
                            result += eElement.getElementsByTagName("LASTNAME").item(0).getTextContent() + "\n";
                            result += "---------------\n";
                        }
                    }else if(input1.equals("") && !(input2.equals(""))) {
                        if(eElement.getElementsByTagName("FIRSTNAME").item(0).getTextContent().equals(input1) && eElement.getElementsByTagName("LASTNAME").item(0).getTextContent().equals(input2)) {
                            result += eElement.getAttribute("Entity_id") + "\n";
                            result += eElement.getElementsByTagName("FIRSTNAME").item(0).getTextContent() + "\n";
                            result += eElement.getElementsByTagName("LASTNAME").item(0).getTextContent() + "\n";
                            result += "---------------\n";
                        }
                    }else if(input1.equals("") && !(input2.equals(""))) {
                        result += "No Input!!!" + "\n";
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        port(getHerokuAssignedPort());

        get("/", (req, res) -> "Hello, World");

        post("/compute", (req, res) -> {
            // System.out.println(req.queryParams("input1"));
            // System.out.println(req.queryParams("input2"));

            String input1 = req.queryParams("input1").replaceAll("\\s", "");
            System.out.println("input1: " + input1);

            String input2 = req.queryParams("input2").replaceAll("\\s", "");
            System.out.println("input2: " + input2);

            String result = App.search(input1, input2);

            Map map = new HashMap();
            map.put("result", result);
            return new ModelAndView(map, "compute.mustache");
        }, new MustacheTemplateEngine());

        get("/compute", (rq, rs) -> {
            Map map = new HashMap();
            map.put("result", "not computed yet!");
            return new ModelAndView(map, "compute.mustache");
        }, new MustacheTemplateEngine());
    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; // return default port if heroku-port isn't set (i.e. on localhost)
    }
}