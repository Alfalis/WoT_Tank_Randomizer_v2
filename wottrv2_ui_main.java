import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.json.JSONException;

@SuppressWarnings({"rawtypes", "unchecked"})
public class wottrv2_ui_main {
	
	public static JFrame mainFrame;
	public static String [] nation_array = {"Nation","China","France","Germany","Japan","U.K.","U.S.A.","U.S.S.R."};
	public static String [] type_array = {"Type","Heavy Tank","Medium Tank","Light Tank","Tank Destroyer","SPG"};
	public static String [] tier_array = {"Tier","1","2","3","4","5","6","7","8","9","10"};
	public static JLabel tank_pic_label = new JLabel();
	public static JLabel tank_level_label = new JLabel();
	public static JLabel tank_moe_label = new JLabel();
	public static JLabel tank_nation_label = new JLabel();
	public static JLabel tank_name_label = new JLabel();
	public static JLabel tank_type_label = new JLabel();
	public static JButton randomize_button = new JButton("Randomize");
	public static JComboBox filter_nation = new JComboBox(nation_array);
	public static JComboBox filter_type = new JComboBox(type_array);
	public static JComboBox filter_tier = new JComboBox(tier_array);
	public static JPanel middle_panel = new JPanel();
	public static JLabel middle_panel_spacer = new JLabel();
	public static JPanel right_panel = new JPanel();
	
	public static void openMainFrame(){
		if (wottrv2_main.debug){System.out.println("-- Main frame creation --");}
		mainFrame = new JFrame("WoT Tank Randomizer v" + wottrv2_main.version);
		if (wottrv2_main.update_available == true){
			wottrv2_ui_main.mainFrame.setTitle("WoT Tank Randomizer v" + wottrv2_main.version + " - New Version available!");
		}
		mainFrame.setLayout(new FlowLayout());
		mainFrame.setResizable(false);
		mainFrame.setSize(505,198);
		mainFrame.setLocation(250,250);
		mainFrame.getContentPane().setBackground(Color.WHITE);
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainFrame.addWindowListener(new WindowAdapter() {
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
		
		//Menubar stuff start
		JMenuBar menubar = new JMenuBar();
		JMenu jm_update = new JMenu("Update");
		jm_update.setForeground(Color.BLACK);
		jm_update.setToolTipText("Update");
		menubar.add(jm_update);
		
		JMenuItem jmi_updateapi = new JMenuItem("Data from API");
		jmi_updateapi.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				try {
					wottrv2_updateapi.updateAPI(0);
				} catch (ClassNotFoundException | SQLException | IOException | JSONException e1) {
					if (wottrv2_main.debug){e1.printStackTrace();}
				}
			}
		});
		jmi_updateapi.setToolTipText("Update data from Wargaming API");
		jm_update.add(jmi_updateapi);
		
		JMenuItem jmi_updategarage = new JMenuItem("Tanks in garage and MoE");
		jmi_updategarage.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				wottrv2_utilities.tanksmoewarnframe();
			}
		});
		jmi_updategarage.setToolTipText("Get Marks of Excellence and the tanks you currently have in your garage");
		jm_update.add(jmi_updategarage);
		
		JMenu jm_settings = new JMenu("Settings");
		jm_settings.setForeground(Color.BLACK);
		jm_settings.setToolTipText("Settings");
		menubar.add(jm_settings);
		
		JMenuItem jmi_settingstanks = new JMenuItem("Edit your tanks");
		jmi_settingstanks.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				try {
					mainFrame.setVisible(false);
					wottrv2_ui_settings.openSettingsFrame();
				} catch (ClassNotFoundException | SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		jmi_settingstanks.setToolTipText("Edit the tanks considered during randomization");
		jm_settings.add(jmi_settingstanks);
		
		JMenu jm_about = new JMenu("About");
		jm_about.setForeground(Color.BLACK);
		jm_about.setToolTipText("About");
		menubar.add(jm_about);
		
		JMenuItem jmi_aboutupdatewottr = new JMenuItem("Update WoT Tank Randomizer");
		jmi_aboutupdatewottr.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				try {
					Desktop.getDesktop().browse(new URL("http://alfalis.de/WoT_Tank_Randomizer_v2").toURI());
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});
		jm_about.add(jmi_aboutupdatewottr);
		jmi_aboutupdatewottr.setToolTipText("Update WoT Tank Randomizer (only if there is a new version available)");
		jmi_aboutupdatewottr.setEnabled(wottrv2_main.update_available);
		
		JMenuItem jmi_aboutchangelog = new JMenuItem("Online Changelog");
		jmi_aboutchangelog.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				try {
					Desktop.getDesktop().browse(new URL("http://alfalis.de/WoT_Tank_Randomizer_v2/changelog.html").toURI());
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});
		jm_about.add(jmi_aboutchangelog);
		jmi_aboutchangelog.setToolTipText("Online Changelog");
		
		JMenuItem jmi_abouthelp = new JMenuItem("Online Help");
		jmi_abouthelp.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				try {
					Desktop.getDesktop().browse(new URL("http://alfalis.de/WoT_Tank_Randomizer_v2/help.html").toURI());
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});
		jm_about.add(jmi_abouthelp);
		jmi_abouthelp.setToolTipText("Online Help");
		
		JLabel menubar_separation_01 = new JLabel("|", JLabel.CENTER);
		JLabel menubar_separation_02 = new JLabel("|", JLabel.CENTER);
		JLabel menubar_separation_03 = new JLabel("|", JLabel.CENTER);
		menubar_separation_01.setForeground(Color.BLACK);
		menubar_separation_01.setFont(new Font("Arial", Font.BOLD, 16)); 
		menubar_separation_02.setForeground(Color.BLACK);
		menubar_separation_02.setFont(new Font("Arial", Font.BOLD, 16)); 
		menubar_separation_03.setForeground(Color.BLACK);
		menubar_separation_03.setFont(new Font("Arial", Font.BOLD, 16)); 
		
		menubar.add(jm_update);
		menubar.add(menubar_separation_01);
		menubar.add(jm_settings);
		menubar.add(menubar_separation_02);
		menubar.add(jm_about);
		menubar.add(Box.createGlue());
		//Menubar stuff stop
		
		//size and stuff definition start
		tank_pic_label.setPreferredSize(new Dimension(160,100));
		tank_pic_label.setLayout(new FlowLayout(FlowLayout.LEFT));
		tank_pic_label.setVerticalAlignment(JLabel.TOP);
		tank_pic_label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		middle_panel.setPreferredSize(new Dimension(160,100));
		middle_panel.setBackground(Color.WHITE);
		middle_panel_spacer.setPreferredSize(new Dimension(155,1));
		tank_name_label.setPreferredSize(new Dimension(155,20));
		tank_type_label.setPreferredSize(new Dimension(155,20));
		right_panel.setPreferredSize(new Dimension(160,100));
		right_panel.setBackground(Color.WHITE);
		tank_moe_label.setPreferredSize(new Dimension(150,67));
		tank_moe_label.setHorizontalAlignment(JLabel.RIGHT);
		tank_moe_label.setVerticalAlignment(JLabel.BOTTOM);
		tank_nation_label.setHorizontalAlignment(JLabel.CENTER);
		tank_name_label.setHorizontalAlignment(JLabel.CENTER);
		tank_type_label.setHorizontalAlignment(JLabel.CENTER);
		tank_type_label.setVerticalAlignment(JLabel.TOP);
		filter_nation.setPreferredSize(new Dimension(155,25));
		filter_type.setPreferredSize(new Dimension(155,25));
		filter_tier.setPreferredSize(new Dimension(155,25));
		filter_tier.setMaximumRowCount(11);
		randomize_button.setPreferredSize(new Dimension(150,30));
		//size and stuff definition stop
		
		//add stuff to panels start
		middle_panel.add(middle_panel_spacer);
		middle_panel.add(tank_nation_label);
		middle_panel.add(tank_name_label);
		middle_panel.add(tank_type_label);
		right_panel.add(filter_nation);
		right_panel.add(filter_type);
		right_panel.add(filter_tier);
		filter_nation.setToolTipText("Nation");
		filter_type.setToolTipText("Type");
		filter_tier.setToolTipText("Tier");
		//add stuff to panels stop
		
		//add stuff to mainframe start
		mainFrame.add(tank_pic_label);
		tank_pic_label.add(tank_level_label);
		tank_pic_label.add(tank_moe_label);
		mainFrame.add(middle_panel);
		mainFrame.add(right_panel);
		mainFrame.add(randomize_button);
		//add stuff to mainframe stop
		
		//ActionListener start
		ActionListener randomize_al = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					wottrv2_randomizer.randomize();
				} catch (ClassNotFoundException | MalformedURLException | SQLException e1) {
					e1.printStackTrace();
				}
				mainFrame.requestFocus();
			}
		};
		randomize_button.addActionListener(randomize_al);
		ActionListener filter_nation_al = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int nation = filter_nation.getSelectedIndex();
				int type = filter_type.getSelectedIndex();
				int tier = filter_tier.getSelectedIndex();
				try {
					wottrv2_randomizer.getAmountRandomize(nation_array[nation], type_array[type], tier_array[tier]);
				} catch (ClassNotFoundException | SQLException e1) {
					e1.printStackTrace();
				}
				mainFrame.requestFocus();			
			}
		};
		filter_nation.addActionListener(filter_nation_al);
		ActionListener filter_type_al = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int nation = filter_nation.getSelectedIndex();
				int type = filter_type.getSelectedIndex();
				int tier = filter_tier.getSelectedIndex();
				try {
					wottrv2_randomizer.getAmountRandomize(nation_array[nation], type_array[type], tier_array[tier]);
				} catch (ClassNotFoundException | SQLException e1) {
					e1.printStackTrace();
				}
				mainFrame.requestFocus();		
			}
		};
		filter_type.addActionListener(filter_type_al);
		ActionListener filter_tier_al = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int nation = filter_nation.getSelectedIndex();
				int type = filter_type.getSelectedIndex();
				int tier = filter_tier.getSelectedIndex();
				try {
					wottrv2_randomizer.getAmountRandomize(nation_array[nation], type_array[type], tier_array[tier]);
				} catch (ClassNotFoundException | SQLException e1) {
					e1.printStackTrace();
				}
				mainFrame.requestFocus();
			}
		};
		filter_tier.addActionListener(filter_tier_al);
		//ActionListener stop
		
		mainFrame.setJMenuBar(menubar);
		mainFrame.setVisible(true);
		mainFrame.requestFocus();
	}
}
