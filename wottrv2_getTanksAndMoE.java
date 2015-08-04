import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class wottrv2_getTanksAndMoE {

	public static void getTanksAndMoE() throws IOException, JSONException, ClassNotFoundException, SQLException{
		if (wottrv2_main.debug){System.out.println("-- Getting tanks and MoE --");}
		wottrv2_sql.clearDBTable("relevant_tanks");
		wottrv2_sql.clearDBTable("playedtanks_moe");
		String applicationID = wottrv2_main.applicationID_EU;
		String sql = "SELECT * FROM user";
		String realm = null;
		String access_token = null;
		int account_id = 0;
		try {
			ResultSet rs = wottrv2_sql.selectFromDB(sql);
			while (rs.next()){
				realm = rs.getString("realm");
				account_id = rs.getInt("account_id");
				access_token = rs.getString("access_token");
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		switch (realm){
			case "eu": applicationID = wottrv2_main.applicationID_EU; break;
			case "com": applicationID = wottrv2_main.applicationID_NA; break;
			case "ru": applicationID = wottrv2_main.applicationID_RU; break;
			case "asia": applicationID = wottrv2_main.applicationID_SEA; break;
		}
		//Get Tanks in garage
		URL url = new URL("https://api.worldoftanks." + realm + "/wot/tanks/stats/?application_id=" + applicationID + "&fields=tank_id&access_token=" + access_token + "&account_id=" + account_id + "&in_garage=1");
		if (wottrv2_main.debug){System.out.println("Get my tanks: https://api.worldoftanks." + realm + "/wot/tanks/stats/?application_id=" + applicationID + "&fields=tank_id&access_token=" + access_token + "&account_id=" + account_id + "&in_garage=1");}
		InputStream input = url.openStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		String jsonText = reader.readLine();
		JSONObject jsonObject = new JSONObject(jsonText);
		jsonObject = jsonObject.getJSONObject("data");
		JSONArray jsonIDArray = jsonObject.getJSONArray(String.valueOf(account_id));
		String [] ingaragearray = new String[jsonIDArray.length()];
		for (int i = 0; i < jsonIDArray.length(); i++){
			JSONObject jsonNameObject = jsonIDArray.getJSONObject(i);
			ingaragearray[i] = String.valueOf(jsonNameObject.getInt("tank_id"));
		}
	    //Write to relevant_tanks table
	    if (wottrv2_main.debug){System.out.println("Writing tank_ids into relevant_tanks table");}
		sql = "INSERT INTO relevant_tanks VALUES ";
		int k = 0;
		for (int i = 0; i < jsonIDArray.length(); i++) {
		    sql += "(" + ingaragearray[i] + "),";
		    if (k == 250){
		    	sql = sql.substring(0, sql.length()-1);
		    	wottrv2_sql.writeToDB(sql);
		    	k = 0;
		    	sql = "INSERT INTO relevant_tanks VALUES ";
		    }
		}
	    sql = sql.substring(0, sql.length()-1);
	    wottrv2_sql.writeToDB(sql);
		//Get all played tanks and their MoE
		url = new URL("https://api.worldoftanks." + realm + "/wot/account/tanks/?application_id=" + applicationID + "&fields=mark_of_mastery%2Ctank_id&account_id=" + account_id);
		if (wottrv2_main.debug){System.out.println("Get played tanks and MoE: https://api.worldoftanks." + realm + "/wot/account/tanks/?application_id=" + applicationID + "&fields=mark_of_mastery%2Ctank_id&account_id=" + account_id);}
		input = url.openStream();
		reader = new BufferedReader(new InputStreamReader(input));
		jsonText = reader.readLine();
		jsonObject = new JSONObject(jsonText);
		jsonObject = jsonObject.getJSONObject("data");
		JSONArray jsonMoEArray = jsonObject.getJSONArray(String.valueOf(account_id));
		JSONObject jsonMoEObject;
		String [][] playedmoearray = new String[2][jsonMoEArray.length()];
		for (int i = 0; i < jsonMoEArray.length(); i++){
			jsonMoEObject = jsonMoEArray.getJSONObject(i);
			playedmoearray[0][i] = String.valueOf(jsonMoEObject.getInt("tank_id"));
			playedmoearray[1][i] = String.valueOf(jsonMoEObject.getInt("mark_of_mastery"));
		}
		//Write to playedtanks_moe table
		if (wottrv2_main.debug){System.out.println("Writing tank_ids and mark_of_mastery into playedtanks_moe table");}
		sql = "INSERT INTO playedtanks_moe VALUES ";
		k = 0;
		for (int i = 0; i < jsonMoEArray.length(); i++) {
		    sql += "(" + playedmoearray[0][i] + "," +
		    		playedmoearray[1][i] + "),";
		    if (k == 250){
		    	sql = sql.substring(0, sql.length()-1);
		    	wottrv2_sql.writeToDB(sql);
		    	k = 0;
		    	sql = "INSERT INTO playedtanks_moe VALUES ";
		    }
		}
	    sql = sql.substring(0, sql.length()-1);
	    wottrv2_sql.writeToDB(sql);
	    wottrv2_randomizer.getAmountRandomize(wottrv2_ui_main.nation_array[0], wottrv2_ui_main.type_array[0], wottrv2_ui_main.tier_array[0]);
	}
}
