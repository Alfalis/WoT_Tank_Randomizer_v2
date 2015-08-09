import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

@SuppressWarnings({"rawtypes", "unchecked"})
public class wottrv2_ui_settings {

	public static String [] nation_array = {"Nation","China","France","Germany","Japan","U.K.","U.S.A.","U.S.S.R."};
	public static String [] type_array = {"Type","Heavy Tank","Medium Tank","Light Tank","Tank Destroyer","SPG"};
	public static String [] tier_array = {"Tier","1","2","3","4","5","6","7","8","9","10"};
	public static JFrame settingsframe = new JFrame("WoT Tank Randomizer v" + wottrv2_main.version + " - Settings");
	public static JComboBox filter_nation = new JComboBox(nation_array);
	public static JComboBox filter_type = new JComboBox(type_array);
	public static JComboBox filter_tier = new JComboBox(tier_array);
	public static JLabel label_selected = new JLabel("Selected", SwingConstants.CENTER);
	public static JLabel label_notselected = new JLabel("Not Selected", SwingConstants.CENTER);
	public static DefaultListModel left_tanklist_model = new DefaultListModel();
	public static DefaultListModel right_tanklist_model = new DefaultListModel();
	public static JList left_tanklist = new JList(left_tanklist_model);
	public static JList right_tanklist = new JList(right_tanklist_model);
	public static JScrollPane left_tanklist_scrollbar = new JScrollPane(left_tanklist);
	public static JScrollPane right_tanklist_scrollbar = new JScrollPane(right_tanklist);
	public static JButton left_button = new JButton("Remove");
	public static JButton right_button = new JButton("Add");
	public static JPanel top_panel = new JPanel();
	public static JPanel top_left_panel = new JPanel();
	public static JPanel top_middle_panel = new JPanel();
	public static JPanel top_right_panel = new JPanel();
	public static JPanel left_panel = new JPanel();
	public static JPanel right_panel = new JPanel();
	public static ActionListener alistener_left_button;
	public static ActionListener alistener_right_button;
	public static ActionListener alistener_filter_nation;
	public static ActionListener alistener_filter_type;
	public static ActionListener alistener_filter_tier;

	public static void openSettingsFrame() throws ClassNotFoundException, SQLException{
		if (wottrv2_main.debug){System.out.println("-- Settings frame creation --");}
		left_button.removeActionListener(alistener_left_button);
		right_button.removeActionListener(alistener_right_button);
		filter_nation.removeActionListener(alistener_filter_nation);
		filter_type.removeActionListener(alistener_filter_type);
		filter_tier.removeActionListener(alistener_filter_tier);
		settingsframe.setIconImage(new ImageIcon(wottrv2_main.class.getResource("/img/icon.png")).getImage());
		settingsframe.setLayout(new FlowLayout());
		settingsframe.setResizable(false);
		settingsframe.setSize(500,355);
		settingsframe.setLocationRelativeTo(wottrv2_ui_main.mainFrame);
		settingsframe.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		wottrv2_ui_main.mainFrame.setVisible(false);
		settingsframe.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				try {
					wottrv2_settings_util.settings_util_saveSettings();
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
				}
				settingsframe.setVisible(false);
				wottrv2_ui_main.mainFrame.setVisible(true);
				wottrv2_ui_main.mainFrame.requestFocus();
				if (wottrv2_main.debug){System.out.println("-- Settings frame closed --");}
				int nation = wottrv2_ui_main.filter_nation.getSelectedIndex();
				int type = wottrv2_ui_main.filter_type.getSelectedIndex();
				int tier = wottrv2_ui_main.filter_tier.getSelectedIndex();
				try {
					wottrv2_randomizer.getAmountRandomize(wottrv2_ui_main.nation_array[nation], wottrv2_ui_main.type_array[type], wottrv2_ui_main.tier_array[tier]);
				} catch (ClassNotFoundException | SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		//Color definition
		settingsframe.getContentPane().setBackground(Color.WHITE);
		top_panel.setBackground(Color.WHITE);
		top_left_panel.setBackground(Color.WHITE);
		top_middle_panel.setBackground(Color.WHITE);
		top_right_panel.setBackground(Color.WHITE);
		left_panel.setBackground(Color.WHITE);
		right_panel.setBackground(Color.WHITE);
		
		//Size definition
		top_panel.setPreferredSize(new Dimension(485,40));
		top_left_panel.setPreferredSize(new Dimension(90,60));
		top_middle_panel.setPreferredSize(new Dimension(120,60));
		top_right_panel.setPreferredSize(new Dimension(90,60));
		left_panel.setPreferredSize(new Dimension(240,280));
		right_panel.setPreferredSize(new Dimension(240,280));
		filter_nation.setPreferredSize(new Dimension(80,25));
		filter_type.setPreferredSize(new Dimension(115,25));
		filter_tier.setPreferredSize(new Dimension(80,25));
		label_selected.setPreferredSize(new Dimension(220,25));
		label_notselected.setPreferredSize(new Dimension(220,25));

		filter_tier.setMaximumRowCount(11);
		settingsframe.add(top_panel);
		settingsframe.add(left_panel);
		settingsframe.add(right_panel);
		left_tanklist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		right_tanklist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		left_tanklist_scrollbar.setPreferredSize(new Dimension(230,200));
		right_tanklist_scrollbar.setPreferredSize(new Dimension(230,200));
		
		//Adding stuff to the top panel
		top_panel.add(top_left_panel);
		top_panel.add(top_middle_panel);
		top_panel.add(top_right_panel);
		top_left_panel.add(filter_nation);
		top_middle_panel.add(filter_type);
		top_right_panel.add(filter_tier);
		filter_nation.setSelectedIndex(0);
		filter_type.setSelectedIndex(0);
		filter_tier.setSelectedIndex(0);
		filter_nation.setToolTipText("Nation");
		filter_type.setToolTipText("Type");
		filter_tier.setToolTipText("Tier");
		
		//Adding stuff to the left panel
		left_panel.add(label_selected);
		left_panel.add(left_tanklist_scrollbar);
		left_panel.add(left_button);
		
		//Adding stuff to the right panel
		right_panel.add(label_notselected);
		right_panel.add(right_tanklist_scrollbar);
		right_panel.add(right_button);
		
		//ActionListener
		alistener_left_button = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String [] splitted = String.valueOf(left_tanklist.getSelectedValue()).split(" : ");
				wottrv2_settings_util.settings_util_add_remove(splitted[1], "remove");
				int nation = filter_nation.getSelectedIndex();
				int type = filter_type.getSelectedIndex();
				int tier = filter_tier.getSelectedIndex();
				try {
					wottrv2_settings_util.settings_util_updatebyfilter(nation_array[nation], type_array[type], tier_array[tier]);
				} catch (ClassNotFoundException | SQLException e1) {
					e1.printStackTrace();
				}
				settingsframe.requestFocus();
			}
		};
		left_button.addActionListener(alistener_left_button);
		alistener_right_button = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String [] splitted = String.valueOf(right_tanklist.getSelectedValue()).split(" : ");
				wottrv2_settings_util.settings_util_add_remove(splitted[1], "add");
				int nation = filter_nation.getSelectedIndex();
				int type = filter_type.getSelectedIndex();
				int tier = filter_tier.getSelectedIndex();
				try {
					wottrv2_settings_util.settings_util_updatebyfilter(nation_array[nation], type_array[type], tier_array[tier]);
				} catch (ClassNotFoundException | SQLException e1) {
					e1.printStackTrace();
				}
				settingsframe.requestFocus();
			}
		};
		right_button.addActionListener(alistener_right_button);
		alistener_filter_nation = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int nation = filter_nation.getSelectedIndex();
				int type = filter_type.getSelectedIndex();
				int tier = filter_tier.getSelectedIndex();
				try {
					wottrv2_settings_util.settings_util_updatebyfilter(nation_array[nation], type_array[type], tier_array[tier]);
				} catch (ClassNotFoundException | SQLException e1) {
					e1.printStackTrace();
				}
				settingsframe.requestFocus();
			}
		};
		filter_nation.addActionListener(alistener_filter_nation);
		alistener_filter_type = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int nation = filter_nation.getSelectedIndex();
				int type = filter_type.getSelectedIndex();
				int tier = filter_tier.getSelectedIndex();
				try {
					wottrv2_settings_util.settings_util_updatebyfilter(nation_array[nation], type_array[type], tier_array[tier]);
				} catch (ClassNotFoundException | SQLException e1) {
					e1.printStackTrace();
				}
				settingsframe.requestFocus();
			}
		};
		filter_type.addActionListener(alistener_filter_type);
		alistener_filter_tier = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int nation = filter_nation.getSelectedIndex();
				int type = filter_type.getSelectedIndex();
				int tier = filter_tier.getSelectedIndex();
				try {
					wottrv2_settings_util.settings_util_updatebyfilter(nation_array[nation], type_array[type], tier_array[tier]);
				} catch (ClassNotFoundException | SQLException e1) {
					e1.printStackTrace();
				}
				settingsframe.requestFocus();
			}
		};
		filter_tier.addActionListener(alistener_filter_tier);
		
		settingsframe.setVisible(true);
		settingsframe.requestFocus();
		wottrv2_settings_util.settings_util_initialize();
	}
}
