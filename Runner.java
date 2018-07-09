package oneil;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class Runner {
	public static void main(String[] args) throws IOException, URISyntaxException {
		String libs = new File(Runner.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent() + "\\EmotivLibs";
		Runtime.getRuntime().exec("java -Djava.library.path=" + libs + " -jar test.jar");
	}
}
