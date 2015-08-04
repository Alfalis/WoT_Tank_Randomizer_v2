import java.awt.Toolkit;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.ImageIcon;

public class wottrv2_randomizer {

	static ArrayList <String> al_all_tank_id = new ArrayList <String>();
	static ArrayList <String> al_relevant_tank_id = new ArrayList <String>();
	static ArrayList <String> al_randomize_tank_id = new ArrayList <String>();

	public static void randomize() throws ClassNotFoundException, SQLException, MalformedURLException{
		if (wottrv2_main.debug){System.out.println("-- Randomizer Start --");}
		//Check that we actually have tanks to randomize
		if (al_randomize_tank_id.size() > 0){
			Random randomGenerator = new Random(System.currentTimeMillis());
			int randomint = randomGenerator.nextInt(al_randomize_tank_id.size());
			String randomized_tank_id = al_randomize_tank_id.get(randomint);
			if (al_randomize_tank_id.size() > 1){
				if (wottrv2_main.last_randomized_tank_id.equals(randomized_tank_id)){
					if (randomint == al_randomize_tank_id.size() - 1){
						randomint -= 1;
					}
					else{
						randomint += 1;
					}
					randomized_tank_id = al_randomize_tank_id.get(randomint);
				}				
			}
			wottrv2_main.last_randomized_tank_id = al_randomize_tank_id.get(randomint);
			if (wottrv2_main.debug){System.out.println("Randomized tank id: " + randomized_tank_id);}
			String randomized_nation = null;
			String randomized_name = null;
			String randomized_image = null;
			String randomized_type = null;
			int randomized_level = 0;
			int randomized_moe = 0;
			String sql = "SELECT * FROM api_data WHERE tank_id=" + randomized_tank_id;
			ResultSet rs = wottrv2_sql.selectFromDB(sql);
			while (rs.next()){
				randomized_nation = rs.getString("nation_i18n");
				randomized_name = rs.getString("name_i18n");
				randomized_image = rs.getString("image");
				randomized_type = rs.getString("type_i18n");
				randomized_level = rs.getInt("level");
			}
			if (wottrv2_main.debug){System.out.println("Getting MoE for tank_id " + randomized_tank_id);}
			sql = "SELECT mark_of_mastery FROM playedtanks_moe WHERE tank_id=" + randomized_tank_id;
			rs = wottrv2_sql.selectFromDB(sql);
			while(rs.next()){
				randomized_moe = rs.getInt("mark_of_mastery");
			}
			if (wottrv2_main.debug){System.out.println("Tank information " + randomized_nation + "," + randomized_name + "," + randomized_image + "," + randomized_type + "," + randomized_level + "," + randomized_moe);}
			wottrv2_ui_main.tank_pic_label.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(new URL(randomized_image))));
			wottrv2_ui_main.tank_level_label.setIcon(new ImageIcon(wottrv2_main.class.getResource("/img/tier/" + randomized_level + ".png")));
			wottrv2_ui_main.tank_moe_label.setIcon(new ImageIcon(wottrv2_main.class.getResource("/img/moe/mastery_badge_" + randomized_moe + ".png")));
			wottrv2_ui_main.tank_nation_label.setIcon(new ImageIcon(wottrv2_main.class.getResource("/img/nation/" + randomized_nation + ".png")));
			wottrv2_ui_main.tank_name_label.setText(randomized_name);
			wottrv2_ui_main.tank_type_label.setText(randomized_type);
		}
		else{
			//ToDo: Tell the user that he doesn't have tanks for set filters
			if (wottrv2_main.debug){System.out.println("No tanks found to randomize");}
		}
		if (wottrv2_main.debug){System.out.println("-- Randomizer Stop --");}
	}
	
	public static void getAmountRandomize(String nation, String type, String tier) throws ClassNotFoundException, SQLException{
		if (wottrv2_main.debug){System.out.println("-- Getting amount of tanks to randomize --");}
		if (wottrv2_main.debug){System.out.println("Checking for number of tanks with filters Nation=" + nation + ", Type=" + type + " and Tier=" + tier);}
		wottrv2_ui_main.randomize_button.setEnabled(false);
		al_all_tank_id.clear();
		al_relevant_tank_id.clear();
		al_randomize_tank_id.clear();
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
			al_all_tank_id.add(String.valueOf(rs.getInt("tank_id")));
		}
		if (wottrv2_main.debug){System.out.println("Number of tanks from API that matched the filters: " + al_all_tank_id.size());}
		sql = "SELECT * FROM relevant_tanks";
		rs = wottrv2_sql.selectFromDB(sql);
		while (rs.next()){
			al_relevant_tank_id.add(String.valueOf(rs.getInt("tank_id")));
		}
		for (int i = 0; i < al_all_tank_id.size(); i++){
			for (int j = 0; j < al_relevant_tank_id.size(); j++){
				if (al_all_tank_id.get(i).equals(al_relevant_tank_id.get(j))){
					al_randomize_tank_id.add(al_all_tank_id.get(i));
				}
			}
		}
		if (wottrv2_main.debug){System.out.println("Number of tanks from relevant_tanks that matched the filters: " + al_randomize_tank_id.size());}
		wottrv2_ui_main.randomize_button.setText("Randomize (" + al_randomize_tank_id.size() + ")");
		if (al_randomize_tank_id.size() > 0){
			wottrv2_ui_main.randomize_button.setEnabled(true);
		}
	}
}
