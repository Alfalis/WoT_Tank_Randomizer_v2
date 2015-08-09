import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class wottrv2_main{
	
	public static boolean debug;
	public static boolean update_available = false;
	public static float version;
	public static float newversion = 0;
	public static String sql;
	public static String applicationID_EU = "a0ea76acb92e0f9a7e1b0394362e20ae";
	public static String applicationID_NA = "94efb54da9e538ec40bdac37835d7686";
	public static String applicationID_RU = "af8866d771ea71494c3c00b637a779c0";
	public static String applicationID_SEA = "5e77a8ed384a0612b9fdd66e49246717";
	public static Connection con;
	public static String last_randomized_tank_id = "1";
	
	public static void main (String[]args) throws ClassNotFoundException, SQLException, IOException{
		//Admin things
		//For logging use: java -jar WoT_Tank_Randomizer_v2.jar debug > WoT_Tank_Randomizer_v2_log.txt
		debug = false;
		version = 2.05f;
		
		//User debug option
		if (args.length > 0){
			if (args[0].equals("debug")){
				debug = true;
			}
		}

		if (wottrv2_main.debug){System.out.println("-- Startup --");}
		
		//Check for newer version
		wottrv2_utilities.versioncheck();
		
		//Check if debug batch file exists, if not create it
		File f = new File("WoT_Tank_Randomizer_v2_debug.bat");
		if (!f.exists()){
			wottrv2_utilities.createdebugbatch();
		}
		
		f = new File("Start_Tank_Randomizer.bat");
		if (!f.exists()){
			wottrv2_utilities.createstartbatch();
		}
		
		//Check if database exists, if not create it
		f = new File("WoTTRv2_database.db");
		if (!f.exists()){
			if (wottrv2_main.debug){System.out.println("Database \"WoTTRv2_database.db\" not found - trying to create it");}
			wottrv2_sql.openDBConnection();
			wottrv2_sql.createDB();
		}
		else{
			wottrv2_sql.openDBConnection();			
		}
		
		//Check if we know the user
		sql = "SELECT * FROM user";
		ResultSet rs = wottrv2_sql.selectFromDB(sql);
		if (!rs.next()){
			if (wottrv2_main.debug){System.out.println("No user found in DB table \"user\", opening search frame");}
			wottrv2_ui_search.openSearchUserFrame();
		}
		else{
			if (wottrv2_main.debug){System.out.println("Found a user, no need to open search frame");}
			//We call checkAccessToken with parameter "1" to make sure the getAccessToken window doesn't show up on every startup
			//if the user doesn't want to use this feature
			wottrv2_checkAccessToken.checkAccessToken(1);
			wottrv2_ui_main.openMainFrame();
			wottrv2_randomizer.getAmountRandomize(wottrv2_ui_main.nation_array[0], wottrv2_ui_main.type_array[0], wottrv2_ui_main.tier_array[0]);
			wottrv2_randomizer.randomize();
		}
	}	
}
