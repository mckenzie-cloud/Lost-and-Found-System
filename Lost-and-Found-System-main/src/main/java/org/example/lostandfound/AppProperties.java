package org.example.lostandfound;

import java.util.Properties;

public class AppProperties {
    Properties prop;

    public AppProperties(String target_prop)
    {
        prop = new Properties();
        if (target_prop.equals("database"))
        {
            prop.setProperty("url", "jdbc:postgresql://localhost:5433/");
            prop.setProperty("db_name", "lost_and_found_system");
            prop.setProperty("user", "postgres");
            prop.setProperty("password", "pass1234");
        }

        if (target_prop.equals("filestorage"))
        {
            prop.setProperty("url", "http://127.0.0.1:9000/");
            prop.setProperty("bucket_name", "bsu-system");
            prop.setProperty("access_key", "minioadmin");
            prop.setProperty("secret_key", "minioadmin");
        }
    }
}