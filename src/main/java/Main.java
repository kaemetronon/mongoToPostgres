import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {

        Service s = new Service();
        Map<Integer, String> tables = s.getTables();

        for (Map.Entry<Integer, String> entry : tables.entrySet()) {
            List<Ent> ents = s.doMongo(entry.getKey());
            String codifName = entry.getValue();
            String cardTypeId = "a238a7d4-c778-4fc2-9a60-b0ae84fb8dff";
            String version = "1";

            s.doPostgres(ents, codifName, cardTypeId, version);
        }
    }
}
