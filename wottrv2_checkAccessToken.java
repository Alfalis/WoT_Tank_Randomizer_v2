import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONException;

public class wottrv2_checkAccessToken {

	public static void checkAccessToken(int startup){
		if (wottrv2_main.debug){System.out.println("-- Checking Access Token --");}
		//Check for valid access token, if expired prolong it
		String realm = null;
		String access_token = null;
		int account_id = 0;
		int expires_at = 0;
		ResultSet rs;
		String sql = "SELECT * FROM user";
		try {
			rs = wottrv2_sql.selectFromDB(sql);
			while (rs.next()){
				realm = rs.getString("realm");
				access_token = rs.getString("access_token");
				expires_at = rs.getInt("expires_at");
			}
		} catch (SQLException e2) {
			e2.printStackTrace();
		}
		int current_time = (int)(System.currentTimeMillis() / 1000);
		if (wottrv2_main.debug){System.out.println("Access token information: Current Unix time=" + current_time + " - expires_at=" + expires_at);}
		String applicationID = null;
		switch (realm){
			case "eu": applicationID = wottrv2_main.applicationID_EU; break;
			case "com": applicationID = wottrv2_main.applicationID_NA; break;
			case "ru": applicationID = wottrv2_main.applicationID_RU; break;
			case "asia": applicationID = wottrv2_main.applicationID_SEA; break;
		}
		if (((current_time > expires_at && expires_at != 0) || (access_token == null)) && (startup != 1)){
			//Access token expired or no access_token found
			if (wottrv2_main.debug){System.out.println("Access token expired or no access_token found");}
			wottrv2_getAccessToken.getAccessToken(realm, applicationID, account_id);
		}
		else if ((access_token != null) && (startup == 1)){
			//Prolong access token
			if (wottrv2_main.debug){System.out.println("Access token fine, let's prolong it");}
			try {
				wottrv2_prolongAccessToken.prolongAccessToken(realm, access_token);
				wottrv2_updateapi.updateAPI();
			} catch (IOException | ClassNotFoundException | JSONException | SQLException e1) {
				e1.printStackTrace();
			}
		}
		else if ((access_token != null && (startup != 1))){
			//Prolong access token and get tank in garage and MoE
			if (wottrv2_main.debug){System.out.println("Access token fine, let's prolong it and get tank in garage and MoE");}
			try {
				wottrv2_prolongAccessToken.prolongAccessToken(realm, access_token);
				wottrv2_updateapi.updateAPI();
				wottrv2_getTanksAndMoE.getTanksAndMoE();
			} catch (IOException | ClassNotFoundException | JSONException | SQLException e1) {
				e1.printStackTrace();
			}
		}
		else{
			if (wottrv2_main.debug){System.out.println("Seems that there is no access token, nothing for me to do here");}			
		}
	}
}
