import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

public class wottrv2_updateapi {

	public static void updateAPI() throws ClassNotFoundException, SQLException, IOException, JSONException{
		if (wottrv2_main.debug){System.out.println("-- Updating API --");}
		wottrv2_sql.clearDBTable("api_data");
		String sql = "SELECT * from user";
		ResultSet rs = wottrv2_sql.selectFromDB(sql);
		String realm = "eu";
		String applicationID = wottrv2_main.applicationID_EU;
		while (rs.next()){
			realm = rs.getString("realm");
		}
		switch (realm){
			case "eu": applicationID = wottrv2_main.applicationID_EU; break;
			case "com": applicationID = wottrv2_main.applicationID_NA; break;
			case "ru": applicationID = wottrv2_main.applicationID_RU; break;
			case "asia": applicationID = wottrv2_main.applicationID_SEA; break;
		}
		URL url = new URL("https://api.worldoftanks." + realm + "/wot/encyclopedia/tanks/?application_id=" + applicationID);
		if (wottrv2_main.debug){System.out.println("Update API: https://api.worldoftanks." + realm + "/wot/encyclopedia/tanks/?application_id=" + applicationID);}
		InputStream input = url.openStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		String jsonText = reader.readLine();
		JSONObject jsonObject = new JSONObject(jsonText);
		JSONObject jsonObjectData = jsonObject.getJSONObject("data");
		String[] array_for_data = JSONObject.getNames(jsonObjectData);
		JSONObject jObj = null;
		String nation_i18n, name_i18n, image, type_i18n;
		Integer level, tank_id;
		
		sql = "INSERT INTO api_data VALUES ";
		int k = 0;
		for (int i=0; i < array_for_data.length; i++){
			k++;
			jObj = jsonObjectData.getJSONObject(array_for_data[i]);
			nation_i18n = jObj.getString("nation_i18n");
			name_i18n = jObj.getString("name_i18n");
			image = jObj.getString("image");
			type_i18n = jObj.getString("type_i18n");
			level = jObj.getInt("level");
			tank_id = jObj.getInt("tank_id");
			
			sql += "('" + nation_i18n + "','" +
					name_i18n + "'," +
					level + ",'" +
					image + "','" +
					type_i18n + "'," +
					tank_id + "),";
			if (k == 250){
				sql = sql.substring(0, sql.length()-1);
				wottrv2_sql.writeToDB(sql);
				k = 0;
				sql = "INSERT INTO api_data VALUES ";
			}
		}
		sql = sql.substring(0, sql.length()-1);
		wottrv2_sql.writeToDB(sql);
	}
}
