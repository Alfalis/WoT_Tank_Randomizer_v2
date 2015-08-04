import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

public class wottrv2_settings_util {

	public static ArrayList <String> al_relevant_tanks = new ArrayList <String>();
	public static ArrayList <String> al_available_tanks = new ArrayList <String>();
	
	public static void settings_util_initialize() throws ClassNotFoundException, SQLException{
		//This method generates the initial arraylists with relevant_tanks and all tanks
		if (wottrv2_main.debug){System.out.println("Initializing settings arraylists");}
		al_relevant_tanks.clear();
		al_available_tanks.clear();
		//get initial "full" lists
		String sql = "SELECT * FROM relevant_tanks";
		ResultSet rs = wottrv2_sql.selectFromDB(sql);
		while (rs.next()){
			al_relevant_tanks.add(rs.getString("tank_id"));
		}
		sql = "SELECT name_i18n,tank_id FROM api_data";
		rs = wottrv2_sql.selectFromDB(sql);
		while (rs.next()){
			al_available_tanks.add(rs.getString("name_i18n") + " : " + String.valueOf(rs.getInt("tank_id")));
		}
		//minimize lists to show only valid options
		String [] splitted = new String[2];
		int reduce_one = 0;
		for (int i = 0; i < al_relevant_tanks.size(); i++){
			if (reduce_one == 1){
				i--;
				reduce_one = 0;
			}
			for (int j = 0; j < al_available_tanks.size(); j++){
				splitted = al_available_tanks.get(j).split(" : ");
				if (splitted[1].equals(al_relevant_tanks.get(i))){
					al_relevant_tanks.remove(i);
					al_relevant_tanks.add(al_available_tanks.get(j));
					al_available_tanks.remove(j);
					reduce_one = 1;
				}
			}
		}
		
		settings_util_putintoUI(al_relevant_tanks, al_available_tanks);
	}
	
	public static void settings_util_add_remove(String tank_id, String add_remove){
		//This method will update both arraylists
		if (wottrv2_main.debug){System.out.println("Updating settings arraylists with tank_id=" + tank_id + " and add_remove=" + add_remove);}
		if (add_remove.equals("add")){
			for (int i = 0; i < al_available_tanks.size(); i++){
				String [] splitted = al_available_tanks.get(i).split(" : ");
				if (splitted[1].equals(tank_id)){
					al_relevant_tanks.add(al_available_tanks.get(i));
					al_available_tanks.remove(i);
				}
			}		
		}
		if (add_remove.equals("remove")){
			for (int i = 0; i < al_relevant_tanks.size(); i++){
				String [] splitted = al_relevant_tanks.get(i).split(" : ");
				if (splitted[1].equals(tank_id)){
					al_available_tanks.add(al_relevant_tanks.get(i));
					al_relevant_tanks.remove(i);
				}
			}	
		}

		settings_util_putintoUI(al_relevant_tanks, al_available_tanks);
	}
	
	public static void settings_util_updatebyfilter(String nation, String type, String tier) throws ClassNotFoundException, SQLException{
		if (wottrv2_main.debug){System.out.println("Updating arraylists with filters Nation=" + nation + ", Type=" + type + " and Tier=" + tier);}
		ArrayList <String> al_relevant_tanks_UI = new ArrayList <String>();
		ArrayList <String> al_available_tanks_UI = new ArrayList <String>();
		ArrayList <String> al_tank_id = new ArrayList <String>();
		String nation_sql = nation;
		String type_sql = type;
		String tier_sql = tier;
		if (nation.equals("Nation")){
			nation_sql = "%"; 
		}
		if (type.equals("Type")){
			type_sql = "%"; 
		}
		if (tier.equals("Tier")){
			tier_sql = "%"; 
		}
		String sql = "SELECT tank_id FROM api_data WHERE nation_i18n LIKE '" + nation_sql + "' AND type_i18n LIKE '" + type_sql + "' AND level LIKE '" + tier_sql + "'";
		ResultSet rs = wottrv2_sql.selectFromDB(sql);
		while (rs.next()){
			al_tank_id.add(String.valueOf(rs.getInt("tank_id")));
		}
		if (wottrv2_main.debug){System.out.println("Settings arraylist update: Number of tanks from API that matched the filters: " + al_tank_id.size());}
		//Update al_relevant_tanks_UI
		for (int i = 0; i < al_relevant_tanks.size(); i++){
			String [] splitted = al_relevant_tanks.get(i).split(" : ");
			for (int j = 0; j < al_tank_id.size(); j++){
				if (splitted[1].equals(al_tank_id.get(j))){
					al_relevant_tanks_UI.add(al_relevant_tanks.get(i));
				}
			}
		}
		//Update al_available_tanks_UI
		for (int i = 0; i < al_available_tanks.size(); i++){
			String [] splitted = al_available_tanks.get(i).split(" : ");
			for (int j = 0; j < al_tank_id.size(); j++){
				if (splitted[1].equals(al_tank_id.get(j))){
					al_available_tanks_UI.add(al_available_tanks.get(i));
				}
			}
		}
		
		settings_util_putintoUI(al_relevant_tanks_UI, al_available_tanks_UI);
	}
	
	@SuppressWarnings("unchecked")
	public static void settings_util_putintoUI(ArrayList<String> relevant_tanks, ArrayList<String> available_tanks){
		//Put the new arraylist into our UI
		Collections.sort(relevant_tanks);
		Collections.sort(available_tanks);
		wottrv2_ui_settings.left_tanklist_model.clear();
		wottrv2_ui_settings.right_tanklist_model.clear();
		for (int i = 0; i < relevant_tanks.size(); i++){
			wottrv2_ui_settings.left_tanklist_model.addElement(relevant_tanks.get(i));
		}
		wottrv2_ui_settings.left_tanklist.setSelectedIndex(0);
		wottrv2_ui_settings.label_selected.setText("Selected (" + relevant_tanks.size() + ")");
		for (int i = 0; i < available_tanks.size(); i++){
			wottrv2_ui_settings.right_tanklist_model.addElement(available_tanks.get(i));
		}
		wottrv2_ui_settings.right_tanklist.setSelectedIndex(0);
		wottrv2_ui_settings.label_notselected.setText("Not Selected (" + available_tanks.size() + ")");
	}
	
	public static void settings_util_saveSettings() throws ClassNotFoundException, SQLException{
		if (wottrv2_main.debug){System.out.println("Saving settings to relevant_tanks table in DB");}
		wottrv2_sql.clearDBTable("relevant_tanks");
		String sql = "INSERT INTO relevant_tanks VALUES ";
		int k = 0;
		for (int i = 0; i < al_relevant_tanks.size(); i++) {
			String [] splitted = al_relevant_tanks.get(i).split(" : ");
		    sql += "(" + splitted[1] + "),";
		    if (k == 250){
		    	sql = sql.substring(0, sql.length()-1);
		    	wottrv2_sql.writeToDB(sql);
		    	k = 0;
		    	sql = "INSERT INTO relevant_tanks VALUES ";
		    }
		}
	    sql = sql.substring(0, sql.length()-1);
	    wottrv2_sql.writeToDB(sql);
	}
}
