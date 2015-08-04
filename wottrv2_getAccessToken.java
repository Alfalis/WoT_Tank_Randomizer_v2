import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.json.JSONException;

public class wottrv2_getAccessToken {

	public static void getAccessToken(String realm, String applicationID, int account_id){
		if (wottrv2_main.debug){System.out.println("-- Get Access Token frame creation --");}
		JFrame getTokenFrame = new JFrame("Get Access Token");		
		getTokenFrame.setSize(250,380);
		getTokenFrame.setLocation(100,100);
		getTokenFrame.setLayout(new FlowLayout());
		getTokenFrame.setResizable(false);
		getTokenFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		getTokenFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				getTokenFrame.setVisible(false);
				wottrv2_ui_main.mainFrame.requestFocus();
			}
		});
		
		JLabel label_get_token_explanation = new JLabel("<html>" +
				"Explanation:<br />" +
				"- Click the \"Get Access Token\" button and you will be redirected to the Wargaming OpenID login page<br />" +
				"- Login and copy & paste your Access Token into the appropriate field in WoT Tank Randomizer<br />" +
				"- Finally click \"OK\" and all the tanks you currently have in your garage will be added to the database meaning all of them are considered during randomization<br />" +
				"- Of course you can still make changes as usual via the settings screen</html>");

		label_get_token_explanation.setPreferredSize(new Dimension(230,250));
		JLabel label_access_token = new JLabel("<html><div style=\"text-align: center;\">Access Token:</div></html>");
		label_access_token.setPreferredSize(new Dimension(90,30));
		JTextField textfield_access_token = new JTextField("",12);
		JButton button_get_token = new JButton("Get Access Token");
		JButton button_ok = new JButton("OK");

		ActionListener al_get_id_token = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (wottrv2_main.debug){System.out.println("Opening authentication site in default browser");}
	    		String url = "https://api.worldoftanks." + realm + "/wot/auth/login/?application_id=" + applicationID + "&redirect_uri=https://" + realm + ".wargaming.net/developers/api_explorer/wot/auth/login/complete/";
				try {
					Desktop.getDesktop().browse(new URL(url).toURI());
				} catch (IOException | URISyntaxException e1) {}
				textfield_access_token.requestFocus();
	    	}
		};
	    button_get_token.addActionListener(al_get_id_token);
	    button_get_token.setPreferredSize(new Dimension (150,30));
	    
	    button_ok.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
	    		try {
					if (wottrv2_main.debug){System.out.println("Writing access_token to database");}
		    		String access_token = textfield_access_token.getText();
		    		String sql = "SELECT * FROM user";
		    		String realm = null;
		    		String nickname = null;
		    		Integer account_id = 0;
		    		ResultSet rs = wottrv2_sql.selectFromDB(sql);
		    		while (rs.next()){
		    			realm = rs.getString("realm");
		    			nickname = rs.getString("nickname");
		    			account_id = rs.getInt("account_id");
		    		}
		    		wottrv2_sql.clearDBTable("user");
		    		sql = "INSERT INTO user (realm,nickname,account_id,access_token) VALUES ('" + realm + "','" + nickname + "'," + account_id + ",'" + access_token + "')";
		    		wottrv2_sql.writeToDB(sql);
					wottrv2_getTanksAndMoE.getTanksAndMoE();
		    		getTokenFrame.setVisible(false);
		    		wottrv2_ui_main.mainFrame.requestFocus();
					if (wottrv2_main.debug){System.out.println("Returning to mainframe");}
				} catch (ClassNotFoundException | SQLException | IOException | JSONException e1) {
					e1.printStackTrace();
				}
	    	}
	    });

		getTokenFrame.add(button_get_token);
		getTokenFrame.add(label_access_token);
		getTokenFrame.add(textfield_access_token);
		getTokenFrame.add(button_ok);
		getTokenFrame.add(label_get_token_explanation);
		getTokenFrame.setVisible(true);
		textfield_access_token.requestFocus();
	}
}
