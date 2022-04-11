package scrabble;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.NoSuchElementException;

import javax.swing.SwingUtilities;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import logic.Game;
import storage.GameLoader;
import utils.StringUtils;
import view.ConsoleView;
import view.GUIView;

/* APUNTES GENERALES:
   
   La clase Main es la clase donde comienza la ejecución de la aplicación.
   En los argumentos del método main, se pueden usar las siguientes opciones:
   
   -a,--ayuda: muestra ayuda sobre los posibles comandos.
   -i,--input <arg>: especifica el fichero de entrada de instrucciones "arg".
   -o,--output <arg>: especifica el fichero de salida "arg".
   -m,--modo <arg>: interfaz gráfica ("gui") o consola ("console").
   
 */
public class Main {
	
	//private static final int defaultSeed = 100;
	private static boolean gui = true;
	
	private static String inFile;
	private static String outFile;
	private static int seed;
	
	private static Options buildOptions() {
		
		Options cmdLineOptions = new Options();

		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("Fichero de entrada, de donde se pueden leer instrucciones.").build());
		cmdLineOptions.addOption(Option.builder("o").longOpt("output").hasArg().desc("Fichero de salida, donde se puede ver el resultado de ejecución.").build());
		cmdLineOptions.addOption(Option.builder("a").longOpt("ayuda").desc("Imprime esta ayuda").build());
		cmdLineOptions.addOption(Option.builder("m").longOpt("modo").hasArg().desc("Modo de visualización de la aplicación").build());
		cmdLineOptions.addOption(Option.builder("s").longOpt("semilla").hasArg().desc("Semilla para la generación de partidas").build());

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
		if (line.hasOption("a")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}
	
	private static void parseInFileOption(CommandLine line) {
		
		inFile = line.getOptionValue("i");
	}

	private static void parseOutFileOption(CommandLine line) {
		outFile = line.getOptionValue("o");
	}

	
	private static void parseSeedOption(CommandLine line) throws ParseException {
		
		if(line.hasOption('s')) {
			
			try {
				seed = Integer.parseInt(line.getOptionValue('s'));
			} catch (NumberFormatException e) {
				throw new ParseException("La semilla debe ser un número.");
			}
		}
		
		else seed = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
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
			if(!gui) parseOutFileOption(line);
			
			parseSeedOption(line);
			
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
		
		try {
			
			InputStream in = (inFile == null ? System.in : new FileInputStream(new File(inFile)));
			OutputStream out = (outFile == null ? System.out : new FileOutputStream(new File(outFile)));
			
			if(inFile == null && outFile != null) {
				in = System.in;
				out = System.out;
			}
					
			new ConsoleView(controller, in, out);
		}
		
		catch(FileNotFoundException fnfe) {
			throw new FileNotFoundException("El fichero de entrada \"" + inFile + "\" no existe." + StringUtils.LINE_SEPARATOR + "Ejecución finalizada.");
		}
		
		catch(NoSuchElementException nsee) {
			throw new NoSuchElementException(StringUtils.DOUBLE_LINE_SEPARATOR + "El fichero de entrada \"" + inFile + "\" no tiene el formato correcto." + StringUtils.LINE_SEPARATOR);
			
		}
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
		
		Game.setSeed(seed);
		
		Controller controller = new Controller();
		
		if(gui) startGUIMode(controller);
		else startBatchMode(controller);
	}

	public static void main(String[] args) {
		try {
			start(args);
		} catch (Exception e) {
			System.err.print(e.getMessage());
		}

	}
}
