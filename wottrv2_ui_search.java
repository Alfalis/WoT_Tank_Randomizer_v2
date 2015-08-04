import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings({"rawtypes", "unchecked"})
public class wottrv2_ui_search{
	static DefaultListModel jlm_search_player = new DefaultListModel();
	static JList jl_search_player = new JList(jlm_search_player);
	static JButton jb_search_player = new JButton("OK");
	static JButton jb_help = new JButton("Help");
	static JComboBox search_realmbox;
	static JTextField search_textfield;
	static String realm = "eu";
	static String nickname;
	static int account_id;
	static String applicationID = wottrv2_main.applicationID_EU;
	
	public static void openSearchUserFrame(){
		if (wottrv2_main.debug){System.out.println("-- Search frame creation --");}
		JFrame searchUserFrame = new JFrame("Search User");
		searchUserFrame.setLayout(new FlowLayout());
		searchUserFrame.setLocation(100,100);
		searchUserFrame.setResizable(false);
		searchUserFrame.setSize(220,320);
		searchUserFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		searchUserFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				try {
					wottrv2_sql.closeDBconnection();
					if (wottrv2_main.debug){System.out.println("-- Closing WoT Tank Randomizer v2 --");}
					System.exit(0);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
		
		JLabel search_realm_label = new JLabel("Realm:");
		String[] realm_array = { "EU", "NA", "RU", "SEA"};
		search_realmbox = new JComboBox(realm_array);
		search_realmbox.setPreferredSize(new Dimension(150,20));
		search_realmbox.setSelectedIndex(0);
		search_realmbox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				jlm_search_player.clear();
				search_textfield.setText("");
				search_textfield.requestFocus();
				switch ((String)search_realmbox.getSelectedItem()){
					case "EU": realm = "eu"; applicationID = wottrv2_main.applicationID_EU; break;
					case "NA": realm = "com"; applicationID = wottrv2_main.applicationID_NA; break;
					case "RU": realm = "ru"; applicationID = wottrv2_main.applicationID_RU; break;
					case "SEA": realm = "asia"; applicationID = wottrv2_main.applicationID_SEA; break;
				}
			}
		});
		
		jl_search_player.setPreferredSize(new Dimension(200,200));
		jl_search_player.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JLabel search_name = new JLabel("Name:");
		search_textfield = new JTextField("", 12);
		search_textfield.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent arg0) {}
			public void keyReleased(KeyEvent arg0) {
				jlm_search_player.clear();
				if (search_textfield.getText().length() > 2){
					try {
						InputStream input = new URL("http://api.worldoftanks." + realm + "/wot/account/list/?application_id=" + applicationID + "&search=" + search_textfield.getText()).openStream();
						if (wottrv2_main.debug){System.out.println("Searchframe asking: http://api.worldoftanks." + realm + "/wot/account/list/?application_id=" + applicationID + "&search=" + search_textfield.getText());}
						BufferedReader reader = new BufferedReader(new InputStreamReader((input), "UTF-8"));
						String jsonText = reader.readLine();
						JSONObject jsonObject = new JSONObject(jsonText);
						JSONArray jsonArrayData = jsonObject.getJSONArray("data");
						for (int i = 0; i < jsonArrayData.length(); i++){
							if (i < 11){
								JSONObject jObj = jsonArrayData.getJSONObject(i);
								jlm_search_player.addElement(jObj.getString("nickname") + " - " + String.valueOf(jObj.getInt("account_id")));								
							}
						}
						jl_search_player.setSelectedIndex(0);
					} catch (IOException | JSONException e) {}					
				}
			}
			public void keyTyped(KeyEvent arg0) {}
		});
		
		jb_search_player.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String[] splitted = String.valueOf(jl_search_player.getSelectedValue()).split(" - ");
				nickname = splitted[0];
				account_id = Integer.parseInt(splitted[1]);
				String sql = "INSERT INTO user (realm,nickname,account_id) VALUES ('" + realm + "','" + nickname + "'," + account_id + ")";
				wottrv2_sql.writeToDB(sql);
				searchUserFrame.setVisible(false);
				try {
					wottrv2_updateapi.updateAPI();
				} catch (ClassNotFoundException | SQLException | IOException | JSONException e1) {
					e1.printStackTrace();
				}
				wottrv2_ui_main.openMainFrame();
				try {
					wottrv2_randomizer.getAmountRandomize(wottrv2_ui_main.nation_array[0], wottrv2_ui_main.type_array[0], wottrv2_ui_main.tier_array[0]);
					wottrv2_randomizer.randomize();
				} catch (ClassNotFoundException | MalformedURLException | SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		jb_help.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					Desktop.getDesktop().browse(new URL("http://alfalis.de/WoT_Tank_Randomizer_v2/help.html").toURI());
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		searchUserFrame.add(search_realm_label);
		searchUserFrame.add(search_realmbox);
		searchUserFrame.add(search_name);
		searchUserFrame.add(search_textfield);
		searchUserFrame.add(jl_search_player);
		searchUserFrame.add(jb_search_player);
		searchUserFrame.add(jb_help);
		searchUserFrame.setVisible(true);
		search_textfield.requestFocus();
	}
}
