package scrabble;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.SwingUtilities;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import gameLogic.Game;
import gameView.ConsoleView;
import gameView.GUIView;
import storage.GameLoader;

public class Main {
	
	private static boolean gui = true;
	private static String inFile;
	private static String outFile;
	
	private static Options buildOptions() {
		
		Options cmdLineOptions = new Options();

		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("Fichero de entrada, de donde se pueden leer instrucciones.").build());
		cmdLineOptions.addOption(Option.builder("o").longOpt("output").hasArg().desc("Fichero de salida, donde se puede ver el resultado de ejecución.").build());
		cmdLineOptions.addOption(Option.builder("a").longOpt("ayuda").desc("Imprime este mensaje").build());
		cmdLineOptions.addOption(Option.builder("m").longOpt("mode").hasArg().desc("Modo de visualización de la aplicación").build());
		
		return cmdLineOptions;
	}
	
	private static void parseModeOption(CommandLine line) throws ParseException {
		
		if(line.hasOption("m")) {
			if(!line.getOptionValue("m").equals("gui") && !line.getOptionValue("m").equals("console")) {
				throw new ParseException("La visualización debe ser a través de la interfaz gráfica o de la consola.");
			}
			else if("console".equals(line.getOptionValue("m"))) {
				gui = false;
			}
		}
		
	}

	private static void parseHelpOption(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}
	
	private static void parseInFileOption(CommandLine line) throws ParseException {
		
		inFile = line.getOptionValue("i");
	}

	private static void parseOutFileOption(CommandLine line) throws ParseException {
		outFile = line.getOptionValue("o");
	}

	private static void parseArgs(String[] args) {

		// Definimos las lineas de comando válidas
		Options cmdLineOptions = buildOptions();

		// Parseamos la línea de comandos introducida.
		CommandLineParser parser = new DefaultParser();
		
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);
			
			parseHelpOption(line, cmdLineOptions);
			
			parseModeOption(line); // Primero para que en parseInFileOption ya se sepa si tenemos GUI.
			
			parseInFileOption(line);
			
			if(!gui) 
				parseOutFileOption(line);
			
			// Si todavia queda algo por procesar, significa que la línea de argumentos no tenía el formato correcto.
			
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}

		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

	}
	

	private static void startBatchMode(Controller controller) throws IOException {
		
		InputStream in = (inFile == null ? System.in : new FileInputStream(new File(inFile)));
		OutputStream out = (outFile == null ? System.out : new FileOutputStream(new File(outFile)));
		
		new ConsoleView(controller, in, out);
	}
	
	private static void startGUIMode(Controller controller) throws IOException{
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new GUIView(controller);
			}
			
		});
	}

	private static void start(String[] args) throws IOException {

		GameLoader.initBuilders();
		Game.initWordList();
		
		parseArgs(args);
		
		Controller controller = new Controller();
		
		if(gui) startGUIMode(controller);
		else startBatchMode(controller);
	}

	public static void main(String[] args) {
		try {
			start(args);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
