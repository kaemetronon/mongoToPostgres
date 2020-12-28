import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class Service {

    public static final Connection pc;
    public static final MongoCollection<Document> mcNames;
    public static final MongoCollection<Document> mcFields;

    static {
        final DSources ds = new DSources();
        pc = ds.pc();
        mcNames = ds.mc("F6NamesHelper");
        mcFields = ds.mc("F6FieldHelper");
    }

    public Map<Integer, String> getTables() {

        var i = mcNames.find().iterator();
        Map<Integer, String> idList = new LinkedHashMap<>();
        while (i.hasNext()) {
            var x = i.next();
            var tableNum = (Integer) x.get("table");
            if (tableNum == 0) continue;
            String jo = new JSONObject(((List) x.get("name")).get(0).toString())
                    .get("tableName").toString();
            idList.put(tableNum, jo);
        }
        idList.remove(0);
        return idList;
    }

    public List<Ent> doMongo(Integer table) {

        MongoCursor<Document> i = mcFields.find(Filters.eq("table", table)).iterator();

        List<Ent> entList = new LinkedList<>();

        while (i.hasNext()) {
            var temp = i.next();
            var x = new Ent();
            x.setCode((Integer) temp.get("code"));
            x.setText((String) temp.get("text"));
            entList.add(x);
        }
        return entList;
    }

    public void doPostgres(List<Ent> ents, String codifName, String cardTypeId, String version) throws SQLException {
        pc.setAutoCommit(false);

        var x = UUID.randomUUID().toString();
        PreparedStatement codifier = pc.prepareStatement("insert into nsi.codifier (id, name, \"cardTypeId\", version) " +
                "values(?::uuid ,?,?::uuid,?)");
        codifier.setString(1, x);
        codifier.setString(2, codifName);
        codifier.setString(3, cardTypeId);
        codifier.setString(4, version);
        try {
            codifier.executeQuery();
        } catch (SQLException throwables) {
        }

        PreparedStatement codifItems = pc.prepareStatement("insert into nsi.\"codifierItems\" (code, value, \"codifierId\") values (?,?,?::uuid)");
        for (Ent ent : ents) {
            codifItems.setInt(1, ent.getCode());
            codifItems.setString(2, ent.getText());
            codifItems.setString(3, x);
            try {
                codifItems.executeQuery();
            } catch (SQLException throwables) {
            }
        }
        pc.commit();
    }
}
