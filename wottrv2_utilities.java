import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

@SuppressWarnings({"unused", "resource"})
public class wottrv2_utilities {
	
	public static void versioncheck(){
		try {
			//optional for testing version
			//Scanner versionscanner = new Scanner(new File("Webservices/version.txt"));
			Scanner versionscanner = new Scanner(new URL("http://www.alfalis.de/WoT_Tank_Randomizer_v2/version.txt").openStream());
			while (versionscanner.hasNextLine() == true){
				String reading = versionscanner.nextLine();
				wottrv2_main.newversion = Float.parseFloat(reading);
			}
			if (wottrv2_main.debug){System.out.println("Version check - Client version: " + wottrv2_main.version);}
			if (wottrv2_main.debug){System.out.println("Version check - Newest version: " + wottrv2_main.newversion);}
			if (wottrv2_main.newversion > wottrv2_main.version){
				wottrv2_main.update_available = true;
			}
		} catch (IOException e) {
			if (wottrv2_main.debug){System.out.println("Error while checking version");}
			e.printStackTrace();
		}
	}
	
	public static void createdebugbatch() throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter("WoT_Tank_Randomizer_v2_debug.bat", "UTF-8");
		writer.println("java -Dfile.encoding=UTF-8 -jar WoT_Tank_Randomizer_v2.jar debug > WoT_Tank_Randomizer_v2_log.txt");
		writer.close();
	}
	
	public static void createstartbatch() throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter("Start_Tank_Randomizer.bat", "UTF-8");
		writer.println("java -Dfile.encoding=UTF-8 -jar WoT_Tank_Randomizer_v2.jar");
		writer.close();
	}
	
	public static void tanksmoewarnframe(){
		JFrame tanksmoewarnframe = new JFrame ("Warning");
		tanksmoewarnframe.setLayout(new FlowLayout());
		tanksmoewarnframe.setResizable(false);
		tanksmoewarnframe.setSize(300,120);
		tanksmoewarnframe.setLocationRelativeTo(wottrv2_ui_main.mainFrame);
		tanksmoewarnframe.getContentPane().setBackground(Color.WHITE);
		tanksmoewarnframe.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		tanksmoewarnframe.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				tanksmoewarnframe.dispose();
				wottrv2_ui_main.mainFrame.requestFocus();
			}
		});
		
		JLabel tanksmoelabel = new JLabel();
		tanksmoelabel.setText("<html><div style=\"text-align: center;\">This will overwrite any manual changes you have<br />"
				+ "made to your relevant tanks with the tanks<br />"
				+ "you currently have in your garage!</div></html>");
		JButton tanksmoebutton = new JButton("Proceed");
		tanksmoebutton.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				tanksmoewarnframe.dispose();
				wottrv2_ui_main.mainFrame.requestFocus();
				wottrv2_checkAccessToken.checkAccessToken(0);
			}
		});
		tanksmoewarnframe.add(tanksmoelabel);
		tanksmoewarnframe.add(tanksmoebutton);
		tanksmoewarnframe.setVisible(true);
		tanksmoewarnframe.requestFocus();
	}
}
