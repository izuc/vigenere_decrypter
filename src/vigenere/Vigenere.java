//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Student name: Lance Baker
// Course: INFT3940 (IT Applications)
// Student number: c3128034
// Assignment title: Assignment 2 - Cryptography
// File name: Vigenere.java
// Created: 19-10-2010
// Last Change: 05-10-2010
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package vigenere;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
The Vigenere class contains the core functionality of the application.
 * The class has methods used for loading the three text files (letter
 * frequency, keywords, cipher text) and converts them into usable data
 * structures. The class was designed to keep expansion in mind, and not only
 * comprises the functionality to decrypt files - but to also encrypt them.
 * 
 * The class also contains methods (getSubsets(), getCipherKey(),
 * commonDivisor(), and getCipherKey()) which attempt to find the keyword used
 * in the loaded Ciphertext (without being given the keyword file); which is
 * done by using the Kasiski method to find the key length - dividing the
 * Ciphertext into repeating subsets of 3 characters. It stores the data, and
 * for each subset (that matches the highest amount of repeating groups) it
 * finds the greatest common divider for the distance between repeating subsets
 * (which is the most likely the keyword's length). Once the keyword length has
 * been found, the length of the keyword is iterated; with each character in the
 * Ciphertext being counted (incrementing on the keyword length), and through
 * Interwoven Caesar Shifts it finds the letter with the highest calculated
 * frequency analysis value. The letter found is then appended to a character
 * array - with the next iteration in the keyword length being performed
 * (eventually leading to a fully formed keyword).
 *
 * The algorithm and formulas used for the Kasiski method and Interwoven Caesar
 * Shifts was adapted into this application; which was explained in the book
 * "Cryptography – Theory and Practice (3rd edition)" written by Douglas R.
 * Stinson and published in 2006 (on page 32-36). The code was not copied,
 * but however was written based on his explanation.
 * @author Lance Baker (c3128034)
 */
public class Vigenere {

    private static final String SPACE = " ";
    private static final String EMPTY_STRING = "";
    private String ciphertext;
    private ArrayList<String> keywords;
    private ArrayList<Letter> letters;

    /**
     * The default constructor; which instantiates the instance structures.
     */
    public Vigenere() {
        this.ciphertext = EMPTY_STRING;
        this.keywords = new ArrayList<String>();
        this.letters = new ArrayList<Letter>();
    }

    /**
     * The cipher method is used to encrypt, and decrypt messages (the received
     * Boolean indicates the formula which will be used). The Ciphertext String
     * (which was loaded via a file) is iterated for each character value. A
     * variable is used to keep track of the index position for the keyword;
     * which is incremented, and then reset back to zero once the keyword length
     * has been reached. This allows for the keyword to be repetitively cycled
     * whilst iterating through each character in the Ciphertext, therefore each
     * loop will be specifying a letter character, and the corresponding keyword
     * character. Both character values are then found in the Letter structure,
     * which grabs the position index - assigning it to their own local variables.
     * The formula used to calculate the letter depends on the received Boolean.
     * If the Boolean is true (being you are encrypting) then the letter position
     * and key position is added together, otherwise the letter position is
     * subtracted from the key position.
     *
     * The modulus of the resulting value is then calculated based on the Letter
     * structure size, which is then used as the index position to fetch a
     * Letter object (from the Letter structure). If the method is decrypting,
     * then the Letter object invokes a method which increments a counter tally.
     * The Letter object then fetches the inner character value, and appends it
     * to a StringBuilder object (being essentially a wrapped character array).
     * The resulting message is then returned as a String.
     * @param key String - The Keyword used for the encryption/ decryption.
     * @param encrypt Boolean - The boolean value indicating whether the method
     * is encrypting/ or decrypting.
     * @return message String (encrypted or decrypted)
     */
    private String cipher(String key, boolean encrypt) {
        // StringBuilder object - which is a nice way for appending characters.
        StringBuilder message = new StringBuilder();
        int index = 0; // The index used for the keyword position.
        // The ciphertext is converted to uppercase, and then to a character array.
        // Iterates for each char value contained in the ciphertext.
        for (char letter : this.ciphertext.toUpperCase().toCharArray()) {
            // The primitive letter char is added to a newly instantiated Letter object
            // which enables for the Letter to be searched (using the indexOf method).
            // Returns the index position of the letter.
            int posLetter = this.letters.indexOf(new Letter(letter));
            // The received key is converted to uppercase, and the keyword index counter is used to grab
            // the character contained in the String at that element position. The fetched character is then
            // instantiated into a Letter, which is used to find the index position in the Letter Structure.
            int posKey = this.letters.indexOf(new Letter(key.toUpperCase().charAt(index)));
            // Once both positions are found, then the index can be caculated. If the method is encrypting, then
            // the Letter position and Key position are added. Otherwise, if its decrypting then its subtracted. The value
            // is then modded against the letters size (which is the amount of characters in the alphabet), 
            // and then the resulting index position is used to get the Letter in the structure.
            Letter result = this.letters.get(modulus(((encrypt) ? (posLetter + posKey) : (posLetter - posKey)), this.letters.size()));
            if (!encrypt) {
                // If the method is decrypting, then the letter counter is incremented.
                // This enables for the system to keep track letter occurrences in the decrypted message.
                result.incrementCount();
            }
            // Appends the letter to the StringBuilder object.
            message.append(result.getLetter());
            // Increments/ or resets the counter back to zero if the key length has been reached.
            index = ((index < (key.length() - 1)) ? (index + 1) : 0);
        }
        // Returns the decrypted/ or encrypted message.
        return message.toString();
    }

    /**
     * The getSubsets method is used in conjunction with the getKeyLength()
     * method in order to determine the possible key lengths used to encrypt the
     * Ciphertext. A HashMap<String, Integer> is created (which is used to store
     * the 3 character subsets and a counter value representing the occurrence).
     * The method iterates an index value for the Ciphertext String length, and
     * substrings the Ciphertext string based on the index position (plus 3
     * characters). The substring is then added to the HashMap (as a key) if it
     * doesn't already exist, with starting counter of one. Otherwise if the
     * substring is found, it increments the existing integer counter.
     * @return subsets HashMap<String, Integer> - The subsets of the ciphertext,
     * with a counter representing the frequency it occurred.
     */
    private HashMap<String, Integer> getSubsets() {
        // Instantiates a new HashMap which is used to store the subsets (as a key), and the frequency it occurred.
        HashMap<String, Integer> subsets = new HashMap<String, Integer>();
        // Iterates a index position for the length of the ciphertext String.
        for (int i = 0; i < (this.ciphertext.length() - 2); i++) {
            // Substrings the ciphertext with character sets of 3.
            String subset = this.ciphertext.substring(i, (i + 3));
            // If the subset doesn't already exist, it adds the substring to
            // the HashMap (as a key) with a starting counter of one.
            if (!subsets.containsKey(subset)) {
                subsets.put(subset, 1);
            } else {
                // Otherwise the subset already exists,
                // therefore the previous counter is grabbed and incremented by one.
                // The record is then modified by placing a new HashMap entry with the same key.
                subsets.put(subset, (subsets.get(subset) + 1));
            }
        }
        // Returns the subsets of the ciphertext, with a count based on the occurrence.
        return subsets;
    }

    /**
     * The getKeyLength method uses the Kasiski approach in order to find the
     * possible key lengths based on a encrypted Ciphertext message. This method
     * partners with the getSubsets method (which finds repeating cipher
     * subsets), and based on the max repeat count, the subsets which match are
     * candidates to calculate the key length; since if there are more than one
     * subset being repeated the same amount of times - the key length is less
     * obvious. Therefore with each subset iteration (and if the count is equal
     * to the max count) the Ciphertext will be iterated for each occurrence of
     * the repeated group - the distance between the subset is then calculated,
     * and the greatest common denominator for all subset distances is calculated
     * (which should be the key length). The key length is then added to a collection,
     * which at the end of the iterations is sorted (based on lowest to highest) and returned.
     * @return ArrayList<Integer> - Sorted collection of possible key lengths.
     */
    private ArrayList<Integer> getKeyLength() {
        ArrayList<Integer> possible_lengths = new ArrayList<Integer>();
        HashMap<String, Integer> subsets = this.getSubsets();
        // Finds the max value in the HashMap based on the values.
        int max_value = Collections.max(subsets.values());
        // If the max value is greater than one (meaning the subset occured more than once).
        // Otherwise, the key wouldn't be able to get derived.
        if (max_value > 1) {
            // Iterates through the keys contained in the subset HashMap.
            Iterator iterator = subsets.keySet().iterator();
            // Whilst there is a next element.
            while (iterator.hasNext()) {
                // Grabs the subset String.
                String subset = (String) iterator.next();
                // If the subset count equals the max value.
                // A subset could possibly have multiple subsets with the same occurrence; in this case
                // the key length is less obvious, and is therefore added to a structure for further analysis later on.
                if (subsets.get(subset) == max_value) {
                    // Declares variables used throughout the iteration. The index variable is the position of the
                    // repeated subset, and the increment variable is the previous index value (which is incremented). The
                    // key_length variable is defaultly set to -1; which is used to skip the common divisor for the first
                    // iteration in order to find some starting values - allowing for the previous index value to be found.
                    int index = 0, p_index = 0, key_length = -1;
                    // Iterates whilst the index is within range of the ciphertext length.
                    while (((index >= 0) && (index <= this.ciphertext.length()))) {
                        // Finds the index of the subset in the String (starting the search at the previous index).
                        index = this.ciphertext.indexOf(subset, p_index);
                        // The distance is calculated by subtracting the previous (but incremented) index value with
                        // the index position found. It increments one (since indicies start at zero).
                        int distance = ((index - p_index) + 1);
                        // If the key_length is greater than zero,
                        // then the CommonDivisor can be calculated based on the distance and key_length.
                        if (key_length > 0) {
                            // Assigns the CommonDivisor to the key_length variable.
                            key_length = commonDivisor(distance, key_length);
                        } else {
                            // If the key_length is equal to -1, then the method is starting - which
                            // means the variable should be set to zero. If the value is zero (which will
                            // be for the next increment) then the distance should be assigned.
                            key_length = ((key_length == -1) ? 0 : distance);
                        }
                        // The incremented index value is assigned to a variable, so with the next iteration
                        // it can be used to calculate the distance gap (based on the previous index).
                        p_index = (index + 1);
                    }
                    // If the key_length is greater than 3, and the possible_lengths structure doesn't 
                    // already contain the value - then add the length to the structure.
                    if ((key_length > 3) && (!possible_lengths.contains(key_length))) {
                        possible_lengths.add(key_length);
                    }
                }
            }
            // Sorts the possible_lengths structure from lowest to highest in size.
            Collections.sort((List<Integer>) possible_lengths);
        }
        // Returns the possible lengths found.
        return possible_lengths;
    }

    /**
     * The getCipherKey method is the public interface which uses the
     * getKeyLength method in order to decipher the Ciphertext in order to
     * determine the keyword used. The method's purpose is to iterate the derived
     * possible key lengths (using the Kasiski approach), and determine the
     * keyword characters which were used to encrypt the Ciphertext through
     * Interwoven Caesar Shifts; which uses letter frequency analysis to determine
     * the most probable letter. It then populates the internal keyword structure
     * with the (possible) derived keywords.
     */
    public void getCipherKey() {
        // Structure used to store possible keywords used for the encrypted text.
        ArrayList<String> cipher_keys = new ArrayList<String>();
        // Iterates the key lengths derived using the Kasiski method.
        for (int key_length : this.getKeyLength()) {
            // The amount of repeating values based on the ciphertext size, and key_length.
            double repeats = (this.ciphertext.length() / key_length);
            // The StringBuilder is used to append calculated characters (used to build the key).
            StringBuilder cipher_key = new StringBuilder();
            // Iterates for each key index until the key_length is reached.
            for (int key = 0; key < key_length; key++) {
                // The char_value position - which is used to store the letter corresponding to the max frequency analysis calculation.
                int char_value = 0;
                // Resets the counters for each Letter to zero.
                for (Letter letter : this.letters) {
                    letter.resetCount();
                }
                // Starting at the key index, iterate incrementing the key_length to the
                // counter each time (without exceeding the ciphertext length); it then
                // finds the character within the ciphertext and increments the occurrence of that letter.
                for (int i = key; i < ciphertext.length(); i += key_length) {
                    // Finds the character in the ciphertext at this position.
                    char value = this.ciphertext.charAt(i);
                    // Gets the Letter object in the letters structure.
                    Letter result = this.letters.get(this.letters.indexOf(new Letter(value)));
                    // Increments the letter count.
                    result.incrementCount();
                }
                // The double max is used to store the maximum frequency analysis value found.
                double max = 0;
                // Through a interwoven loop (both iterating the alphabet) - it can find the
                // key character using the regular caesar shift cipher.

                for (int letter_y = 0; letter_y < this.letters.size(); letter_y++) {
                    double sum = 0;
                    for (int letter_x = 0; letter_x < this.letters.size(); letter_x++) {
                        // Finds the Letter language frequency (for the x letter).
                        double frequency = this.letters.get(letter_x).getFrequency();
                        // Calculates the possible key Letter position by adding the two letters together
                        // The result is then modded with the size of the letters structure. The Letter object
                        // is fetched based on the calculated index position, which then grabs the counter and
                        // assigns it to the local count variable.
                        int count = this.letters.get(modulus((letter_x + letter_y), this.letters.size())).getCount();
                        // The frequency letter analysis. The languages letter frequency for the x letter, is
                        // multiplied by the counter fetched. The result is then divided by the amount of repeating
                        // key_lengths, and the output is added to the sum.
                        sum += (((frequency) * count) / repeats);
                        // If the sum is greater than the maxmimum amount stored, then the next max value is added and
                        // the y letter is assigned to the char_value variable (which is the next best key character).
                        if (sum > max) {
                            max = sum; // Assigns the next best sum as the max value.
                            char_value = letter_y; // The next best key character.
                        }
                    }
                }
                // Appends the char_value derived from the interwoven caesar shifts.
                cipher_key.append(this.letters.get(char_value).getLetter());
            }
            // Appends the found key to the possible keywords structure.
            cipher_keys.add(cipher_key.toString().toLowerCase());
        }
        // Sets the keywords structure with the resulting keys found.
        this.setKeywords(cipher_keys);
    }

    /**
     * The loadLetters method is used to fetch the contents of a text file,
     * interpret it, and add it to the internal data structures. First, the
     * existing letters structure is cleared (which enables for letter frequency
     * files to be reloaded) then the contents of the file is fetched based on
     * the carriage returns using the static method loadContent() - with each
     * line being a String element in the ArrayList. The content ArrayList is then
     * iterated, and the data line is split into an array (based on the separating
     * space); the first element being the character value, with the second being
     * the frequency average. The char letter is checked with the static Character
     * internal method to determine whether it’s a letter, and if so - a new
     * instantiation of the Letter class is performed; passing the letter character
     * and the converted frequency double in the constructor. The new object instance
     * (with each iteration) is then added into the letters structure.
     * @param file File - The letter frequency File object.
     * @throws IOException - Thrown in the event that the file could not be found.
     */
    public void loadLetters(File file) throws IOException {
        this.letters.clear(); // Clears the Letter Structure (to ensure that its ready to be added).
        // Fetches the contents of a text File object, and retrieves each line as a new ArrayList element.
        ArrayList<String> content = loadContent(file);
        // Iterates through the contents.
        for (String line : content) {
            try {
                // Splits the data on a space; which is the contents separator used in the text-file.
                String[] data = line.split(SPACE);
                // Grabs the char letter from the data split.
                char letter = data[0].toCharArray()[0];
                // If the character is a valid uni-code letter (and being non-numeric).
                if (Character.isLetter(letter)) {
                    // Instantiates a new Letter object, passing the character letter and frequency double as arguments.
                    // Adds the Letter to the letters structure.
                    this.letters.add(new Letter(letter, Double.parseDouble(data[1])));
                }
                // Catches any exceptions (mainly from invalid array positions, or casting errors).
            } catch (Exception exception) {
            }
        }
    }

    /**
     * The loadKeywords method fetches the contents of the keywords text-based
     * File (separated on the carriage return),which is then added to a emptied
     * keywords ArrayList<String>.
     * @param file File - The keywords File object.
     * @throws IOException - Thrown in the event that the file could not be found.
     */
    public void loadKeywords(File file) throws IOException {
        // Since there is no-additional passing/ formatting required the retrieved contents can be placed
        // directly into the keywords ArrayList.
        this.setKeywords(loadContent(file));
    }

    /**
     * The loadCiphertext method receives the File object - which fetches the
     * File content using the loadContent static method which returns a
     * ArrayList<String> (each element being a line separated on the carriage
     * return). A StringBuilder is instantiated, and the String lines in the content
     * are iterated with each line being transformed into uppercased characters,
     * and also being stripped of any white spaces. The resulting line with the
     * iteration is appended to the StringBuilder object, and at the end of the
     * loop the converted String value is assigned to the Ciphertext instance field.
     * @param file File - The ciphertext File object.
     * @throws IOException - Thrown in the event that the file could not be found.
     */
    public void loadCiphertext(File file) throws IOException {
        // The contents of the File object is loaded into a ArrayList<String> which is
        // separated on the carriage returns.
        ArrayList<String> content = loadContent(file);
        // A new StringBuilder - which is used to append the resulting line String.
        StringBuilder characters = new StringBuilder();
        // Iterates for each String element in the content structure.
        for (String line : content) {
            // The line is converted to uppercase, and the spaces are replaced with a empty string.
            // The line is then appended to the character StringBuilder.
            characters.append(line.toUpperCase().replaceAll(SPACE, EMPTY_STRING));
        }
        // The resulting String is then assigned to the ciphertext instance field.
        this.ciphertext = characters.toString();
    }

    /**
     * The decrypt method is used to decrypt the Ciphertext for each keyword.
     * First, a collection of Message objects is instantiated, and then the
     * keywords structure is iterated (if it’s not empty), with each iteration
     * decrypting the message (based on the iterated key) by invoking the cipher
     * method. The decrypted message is then added to a new Message instance -
     * passing the letters structure (being an object reference), the decrypted
     * message, and the key used to decrypt the message as arguments in the
     * constructor. The new Message object is then added into the collection of
     * messages, and the collection is sorted once the keywords have been fully
     * iterated. The sort then orders the collection based on the messages closest
     * letter frequency matches (compared with the frequency average).
     * @return LinkedList<Message> - Ordered collection of decrypted Message objects.
     */
    public LinkedList<Message> decrypt() {
        // The Message collection is instantiated.
        LinkedList<Message> messages = new LinkedList<Message>();
        // If the keywords structure is not empty
        if (this.keywords.size() > 0) {
            // Iterates for each keyword String.
            for (String key : this.keywords) {
                // Decrypts the loaded ciphertext with the iterated key using the cipher method.
                // Instantiates a new Message object; which receives the letters structure (object reference),
                // the decrypted message, and the key used.
                // Adds the Message object to the messages collection.
                messages.add(new Message(this.letters, this.cipher(key, false), key));
            }
            // Sorts the Message collection - which uses the Message's implemented compareTo method.
            // Orders the collection based on lowest to most closest match (based on letter frequencies).
            Collections.sort((List<Message>) messages);
        }
        // Returns an ordered collection of decrypted Message objects.
        return messages;
    }

    /**
     * The encrypt method uses the loaded plaintext (which is inputted using
     * the regular loadCiphertext method) and encrypts the text using the
     * cipher method. The encrypted form is then iterated, with a white space
     * character being appended once a counter has reached 5 (resetting the counter
     * also back to zero). Therefore, the outputted String will be the encrypted
     * Ciphertext formatted to have a space with every 5th character.
     * @param key String - Keyword that will be used to encrypt the message.
     * @return String - The formatted encrypted message.
     */
    public String encrypt(String key) {
        int counter = 1; // Defines and sets the counter used to regulate gaps to zero.
        // A StringBuilder object is used to append characters together.
        StringBuilder characters = new StringBuilder();
        // Encrypts the plaintext message using the cipher method (based on the received key).
        // The encrypted message is then converted to an character array, with each char value
        // being iterated.
        for (char c : this.cipher(key, true).toCharArray()) {
            characters.append(c); // Appends the char to the StringBuilder object.
            if (counter == 5) { // If the counter reaches 5.
                characters.append(SPACE); // Append the space.
            }
            // Increments, or resets the counter once it has reached 5.
            counter = ((counter < 5) ? (counter + 1) : 1);
        }
        // Returns a formatted encrypted message.
        return characters.toString();
    }

    /**
     * Getter for the ArrayList<Letter> letters.
     * @return letters ArrayList<Letter>
     */
    public ArrayList<Letter> getLetters() {
        return this.letters;
    }

    /**
     * Setter for the ArrayList<String> keywords. It first clears the keywords collection,
     * and adds the received ArrayList elements into the collection.
     * @param keywords ArrayList<String>
     */
    public void setKeywords(ArrayList<String> keywords) {
        this.keywords.clear();
        this.keywords.addAll(keywords);
    }

    /**
     * Getter for the ArrayList<String> keywords.
     * @return keywords ArrayList<String>
     */
    public ArrayList<String> getKeywords() {
        return this.keywords;
    }

    /**
     * Setter used for the ciphertext. Receives a String - which is assigned
     * to the instance ciphertext field. Enables for the ciphertext to be changed
     * without having to load a file.
     * @param ciphertext String
     */
    public void setCiphertext(String ciphertext) {
        this.ciphertext = ciphertext.toUpperCase().replaceAll(SPACE, EMPTY_STRING);
    }

    /**
     * Getter for the ciphertext.
     * @return ciphertext String.
     */
    public String getCiphertext() {
        return this.ciphertext;
    }

    /**
     * The modulus method is needed in order to perform a true mod operation.
     * In the event that a negative number is encountered the % operator doesn't
     * perform the mod, and the resulting number still remains negative. This is
     * because Java’s internal % operator only finds the remainder.
     * @param value int - The value for the modulus operation.
     * @param exponent int - The modulus exponent.
     * @return modulus int - The resulting modulus.
     */
    private static int modulus(int value, int exponent) {
        return ((value % exponent) + exponent) % exponent;
    }

    /**
     * The commonDivisor method is used recursively, and in conjunction with the
     * getKeyLength method which is used to find the greatest common devisor
     * between the subset character distance and the key length.
     * @param distance int
     * @param key_length int
     * @return int - The greatest common divisor
     */
    private static int commonDivisor(int distance, int key_length) {
        return ((key_length > 0) ? commonDivisor(key_length, distance % key_length) : distance);
    }

    /**
     * The loadContent method is used by the three load file methods. It enables
     * for the contents of a File to be retrieved without having to do the file
     * handling in each method occurrence. The File object is placed in a FileReader
     * object, which is then placed into a BufferedReader - which enables for the
     * contents of the file to be iterated as its being read.  The lines throughout
     * the iteration are also placed as elements in an ArrayList<String> and returned
     * once the end of the file has been reached.
     * @param file File - The File object that needs to be loaded.
     * @return ArrayList<String> - The structure of String lines.
     * @throws IOException - Thrown in the event that the file could not be found.
     */
    private static ArrayList<String> loadContent(File file) throws IOException {
        // Loads the File object into a FileReader, which is passed into a newly Instantiated BufferedReader.
        // Enabling for each line of the file to be read.
        BufferedReader br = new BufferedReader(new FileReader(file));
        // Creates a structure, which is used to store the file lines.
        ArrayList<String> content = new ArrayList<String>();
        // Iterates throughout the file until the end has been reached.
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            // If the line value exists.
            if (line != null) {
                // Adds the line String to the content structure.
                content.add(line);
            }
        }
        // Closes the BufferedReader file connection.
        br.close();
        // Returns the structure of String lines.
        return content;
    }
}
