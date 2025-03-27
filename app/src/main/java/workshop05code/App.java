package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());
    
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            logger.info("Wordle created and connected.");
        } else {
            logger.warning("Not able to connect. Sorry!");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            logger.info("Wordle structures in place.");
        } else {
            logger.warning("Not able to launch. Sorry!");
            return;
        }

        // Adding words from data.txt
        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                if (line.length() != 4) { 
                    logger.severe("Invalid word in data.txt: " + line);  // Log invalid words
                } else {
                    logger.info("Adding valid word: " + line);  // Log valid words
                    wordleDatabaseConnection.addValidWord(i, line);
                    i++;
                }
            }
        } catch (IOException e) {
            logger.severe("Error reading from data.txt: " + e.getMessage());
            return;
        }

        // Prompt user for guess input
        try (Scanner scanner = new Scanner(System.in)) {
            String guess;
            while (true) {
                System.out.print("Enter a 4 letter word for a guess or q to quit: ");
                guess = scanner.nextLine();
                
                if (guess.equals("q")) {
                    break;
                }

                logger.info("User guessed: " + guess);
                if (wordleDatabaseConnection.isValidWord(guess)) {
                    logger.info("Valid guess: " + guess); 
                    System.out.println("Success! It is in the the list.\n");
                } else {
                    logger.warning("Invalid guess: " + guess);  // Log invalid guesses
                    System.out.println("Sorry. This word is NOT in the the list.\n");
                }
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            logger.log(Level.WARNING, "Error in user input.", e);
        }
    }
}
