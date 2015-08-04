import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

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
		writer.println("java -jar WoT_Tank_Randomizer_v2.jar debug > WoT_Tank_Randomizer_v2_log.txt");
		writer.close();
	}
}
