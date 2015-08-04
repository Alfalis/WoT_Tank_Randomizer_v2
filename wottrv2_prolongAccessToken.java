import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

public class wottrv2_prolongAccessToken {
	
	public static void prolongAccessToken(String realm, String access_token) throws IOException, JSONException, ClassNotFoundException, SQLException{
		if (wottrv2_main.debug){System.out.println("-- Prolonging Access Token --");}
		String nickname = null;
		String sql = "SELECT * FROM user";
		ResultSet rs = wottrv2_sql.selectFromDB(sql);
		while (rs.next()){
			realm = rs.getString("realm");
			nickname = rs.getString("nickname");
		}
		wottrv2_sql.clearDBTable("user");
		String applicationID = wottrv2_main.applicationID_EU;
		switch (realm){
			case "eu": applicationID = wottrv2_main.applicationID_EU; break;
			case "com": applicationID = wottrv2_main.applicationID_NA; break;
			case "ru": applicationID = wottrv2_main.applicationID_RU; break;
			case "asia": applicationID = wottrv2_main.applicationID_SEA; break;
		}
		if (wottrv2_main.debug){System.out.println("Prolonging access_token with access_token \"" + access_token + "\", applicationID \"" + applicationID + "\" and realm \"" + realm + "\"");}
		URL url = new URL("https://api.worldoftanks." + realm + "/wot/auth/prolongate/");
	    URLConnection conn = url.openConnection();
	    conn.setDoOutput(true);
	    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
	    writer.write("application_id=" + applicationID + "&access_token=" + access_token);
	    writer.flush();
	    String line;
	    String response = "";
	    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    while ((line = reader.readLine()) != null) {
	      response += line;
	    }
	    writer.close();
	    reader.close();
		if (wottrv2_main.debug){System.out.println("Response from prolong access token: " + response);}
		JSONObject jsonObject = new JSONObject(response);
		jsonObject = jsonObject.getJSONObject("data");
		access_token = jsonObject.getString("access_token");
		int account_id = jsonObject.getInt("account_id");
		int expires_at = jsonObject.getInt("expires_at");
		sql = "INSERT INTO user VALUES ('" + realm + "','" + nickname + "'," + account_id + ",'" + access_token + "'," + expires_at + ")";
		wottrv2_sql.writeToDB(sql);
	}
}
