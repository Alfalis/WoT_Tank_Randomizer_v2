import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class wottrv2_sql{

	public static void createDB() throws ClassNotFoundException, SQLException{
		if (wottrv2_main.debug){System.out.println("-- Database creation --");}
		Statement stmt = wottrv2_main.con.createStatement();
		//Table for API data
		String sql = "CREATE TABLE 'api_data' (" +
				"'nation_i18n' TEXT," +
				"'name_i18n' TEXT," +
				"'level' INTEGER," +
				"'image' TEXT," +
				"'type_i18n' TEXT," +
				"'tank_id' INTEGER)";
		stmt.executeUpdate(sql);
		//Table for played tanks and marks of excellence
		sql = "CREATE TABLE 'playedtanks_moe' (" +
				"'tank_id' INTEGER," +
				"'mark_of_mastery' INTEGER)";
		stmt.executeUpdate(sql);
		//Table for account information and access token
		sql = "CREATE TABLE 'user' (" +
				"'realm' TEXT," +
				"'nickname' TEXT," +
				"'account_id' INTEGER," +
				"'access_token' TEXT," +
				"'expires_at' INTEGER)";
		stmt.executeUpdate(sql);
		//Table for relevant tanks
		sql = "CREATE TABLE 'relevant_tanks' ('tank_id' INTEGER)";
		stmt.executeUpdate(sql);
		sql = "INSERT INTO relevant_tanks VALUES (529)";
		stmt.executeUpdate(sql);
		
		stmt.close();
		
		if (wottrv2_main.debug){System.out.println("Database \"WoTTRv2_database.db\" successfully created");}
	}
	
	public static ResultSet selectFromDB(String sql){
		if (wottrv2_main.debug){System.out.println("Trying to read from DB: " + sql);}
		ResultSet rs = null;
		try {
			Statement stmt = wottrv2_main.con.createStatement();
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			if (wottrv2_main.debug){System.out.println("Error while reading from database, stopping WoT Tank Randomizer");}
			System.exit(0);
		}
		return(rs);
	}
	
	public static void writeToDB(String sql){
		if (wottrv2_main.debug){System.out.println("Trying to write to DB: " + sql);}
		try {
			Statement stmt = wottrv2_main.con.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			if (wottrv2_main.debug){System.out.println("Error while writing to database, stopping WoT Tank Randomizer");}
			System.exit(0);
		}
	}
	
	public static void clearDBTable(String tablename){
		try {
			Statement stmt = wottrv2_main.con.createStatement();
			String sql = "DELETE FROM " + tablename;
			stmt.executeUpdate(sql);
			stmt.close();
			if (wottrv2_main.debug){System.out.println("Table " + tablename + " cleared!");}
		} catch (SQLException e) {
			e.printStackTrace();
			if (wottrv2_main.debug){System.out.println("Error while clearing database, stopping WoT Tank Randomizer");}
			System.exit(0);
		}
	}
	
	public static void openDBConnection(){
		try {
			Class.forName("org.sqlite.JDBC");
			wottrv2_main.con = DriverManager.getConnection("jdbc:sqlite:WoTTRv2_database.db");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			if (wottrv2_main.debug){System.out.println("Error while opening database, stopping WoT Tank Randomizer");}
			System.exit(0);
		}		
	}
	
	public static void closeDBconnection() throws SQLException{
		wottrv2_main.con.close();
	}
	
}
