import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main {
    static ArrayList<Person> people = new ArrayList<>();

    public static void main(String[] args) {
        Spark.init();
        try {
            readFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Spark.get("/",
                (request, response) -> {
                    String offset = request.queryParams("offset");//has to match the query operator in the href mustache
                    int offsetNum = 0;
                    if (offset != null) {
                        offsetNum = Integer.valueOf(offset);
                    }
                    //create a temporary list to hold the next 20 people
                    ArrayList<Person> pList = new ArrayList<Person>();
                    //get  a subset of 20 people in the whole list of people
                    for (int x = offsetNum; x < (offsetNum + 20); x = x + 1) {
                        pList.add(people.get(x));
                    }
                    HashMap m = new HashMap();

                    int nextOffset = offsetNum + 20;
                    int previousOffset;
                    if ((offsetNum >= 20)) {
                        previousOffset = offsetNum - 20;
                        m.put("previousOffset", previousOffset);
                    }
                    if(offsetNum <= 979){
                        m.put("nextOffset",nextOffset);
                    }
                    //add nextOffset to the map

                    m.put("people", pList);
                    return new ModelAndView(m, "index.html");
                },
                new MustacheTemplateEngine()
        );

        Spark.get("/person",//this is the route
                (request, response) -> {
                    String id = request.queryParams("id");
                    HashMap m = new HashMap();
                    Person p = null;
                    for (Person person : people) {
                        if (person.id.equals(id)) {
                            p = person;
                        }
                    }
                    m.put("person", p);
                    return new ModelAndView(m, "personal.html");// this matches the html name of file
                },
                new MustacheTemplateEngine()
        );
    }

    public static void readFile() throws Exception {
        //read each line into an object arraylist
        File f = new File("People.txt");
        Scanner fileScanner = new Scanner(f);
        while (fileScanner.hasNext()) {
            String line = fileScanner.nextLine();
            String[] columns = line.split(",");
            Person p = new Person(columns[0], columns[1], columns[2], columns[3], columns[4], columns[5]);
            people.add(p);//loads into the arraylist
            //System.out.println(p);
        }
        fileScanner.close();
    }
}



