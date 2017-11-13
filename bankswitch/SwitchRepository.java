package bankswitch;

import java.sql.*;

/**
 * @author Mohammad Oloomi
 */
public class SwitchRepository {

    private static SwitchRepository theSwitchRepository = new SwitchRepository();
    private String username;
    private String password;
    static final String CONN_STR = "jdbc:mysql://localhost:3306/switch";

    private SwitchRepository() {
        username = "root";
        password = "";
    }
    
    public static SwitchRepository getSwitchRepository() {
        return theSwitchRepository;
    }

    public void insertReq(PurchaseRequest req) throws SQLException {
        Connection con = DriverManager.getConnection(CONN_STR, username, password);
	Statement st = con.createStatement();

	st.executeUpdate("insert into trx values('" + req.getTrxId() + "', '" + req.getCardId()
                + "','" + req.getPrice() + "','"+ req.getDateTime() + "','" + req.getPOSid() + 
                "', '', '', 'MessageReciever')" );
        con.close();
    }

    public void updateStatus(PurchaseRequest req, String status) throws SQLException {
        Connection con = DriverManager.getConnection(CONN_STR, username, password);
	Statement st = con.createStatement();
        String query = "update trx set trx.status = '" + status + "' where trx.trxId = '" + req.getTrxId() + "'";
	st.executeUpdate(query);
        con.close();
    }

    public void updateRefId(PurchaseRequest req, String refId) throws SQLException {
        Connection con = DriverManager.getConnection(CONN_STR, username, password);
	Statement st = con.createStatement();
        String query = "update trx set trx.refId = '" + refId + "' where trx.trxId = '" + req.getRefId() + "'";
	st.executeUpdate(query);
        con.close();
    }

    public void updateResponse(PurchaseRequest req, String resp) throws SQLException {
        Connection con = DriverManager.getConnection(CONN_STR, username, password);
	Statement st = con.createStatement();
        String query = "update trx set trx.response = '" + resp + "' where trx.trxId = '" + req.getResponse() + "'";
	st.executeUpdate(query);
        con.close();
    }

}
