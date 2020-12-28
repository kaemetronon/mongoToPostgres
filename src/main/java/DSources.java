import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DSources {

    public MongoCollection<Document> mc(String collectionName) {
        MongoClient client = new MongoClient("192.168.11.131", 27017);
        MongoDatabase database = client.getDatabase("testdb");

        MongoCollection<Document> collection = database.getCollection(collectionName);
        return collection;
    }

    public Connection pc() {
        String url = "jdbc:postgresql://an-01-pg.test.telda:5432/dds01";
        String name = "postgres";
        String pwd = "admin";

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, name, pwd);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }
}
