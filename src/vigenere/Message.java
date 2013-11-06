//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Student name: Lance Baker
// Course: INFT3940 (IT Applications)
// Student number: c3128034
// Assignment title: Assignment 2 - Cryptography
// File name: Message.java
// Created: 24-10-2010
// Last Change: 05-10-2010
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package vigenere;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * The Message class is used to contain data based on a decryption, which includes
 * the keyword used, and the decrypted message based on that keyword. The class
 * is also responsible for comparing the decrypted Messages based on the closest
 * match to the languages frequency average. The closest match is determined based
 * on a calculated rounded difference with each letter in the decryption, being
 * compared to the languages average percentage; if the difference is zero, then
 * a counter is incremented. The structure of messages is then sorted based on
 * the counter.
 * @author Lance Baker (c3128034)
 */
public class Message implements Comparable<Message> {

    private static final DecimalFormat decimalFormat = new DecimalFormat("#.###");
    private ArrayList<Letter> letters; // Object reference to the Letters (not a copy).
    private HashMap<Character, Double> frequencies = new HashMap<Character, Double>();
    private String message;
    private String key;

    /**
     * The constructor receives the languages letter structure (which is a object 
     * memory reference),the decrypted message attempt, and the keyword used to
     * decrypt the message.
     * @param letters ArrayList<Letter>
     * @param message String
     * @param key String
     */
    public Message(ArrayList<Letter> letters, String message, String key) {
        this.setLetters(letters);
        this.setMessage(message);
        this.setKey(key);
        this.setFrequencies();
    }

    /**
     * Setter used for the ArrayList<Letter> letters.
     * @param letters ArrayList<Letter>
     */
    private void setLetters(ArrayList<Letter> letters) {
        this.letters = letters;
    }

    /**
     * Setter for the decrypted message.
     * @param message String
     */
    private void setMessage(String message) {
        this.message = message;
    }

    /**
     * Setter for the keyword
     * @param key String
     */
    private void setKey(String key) {
        this.key = key;
    }

    /**
     * The setFrequency method iterates throughout the Letters structure, and
     * calculates the percentage occurrence based on the Letter counter and message
     * length. Stores the percentage in the frequencies HashMap with the Letter
     * character used as the key index, and resets the Letter counter.
     */
    private void setFrequencies() {
        // Iterates for each Letter contained within the languages alphabet.
        for (Letter letter : this.letters) {
            // Adds the calculated percentage to the frequencies HashMap (with the character used as a key).
            this.frequencies.put(new Character(letter.getLetter()), (((double) letter.getCount() / this.getMessage().length()) * 100));
            // Resets the letter counter.
            letter.resetCount();
        }
    }

    /**
     * The countMatches method iterates throughout the Letter Structure, and
     * calculates a rounded difference between the messages Letter frequency and
     * the languages average frequency. If there is no difference between the
     * two values, then the counter is incremented.
     * @return count int - counter value of the letter matches (to the languages
     * average) in the decryption.
     */
    private int countMatches() {
        int count = 0;
        // Iterates through each Letter in the language.
        for (Letter letter : this.letters) {
            // Grabs the message frequency from the HashMap based on the character. Then subtracts the
            // languages frequency average, rounding the result. If the result is zero, then there is a match.
            if (Math.round(this.getFrequencies().get(letter.getLetter()) - letter.getFrequency()) == 0) {
                count++; // Match has occurred, increments count.
            }
        }
        return count;
    }

    /**
     * Getter for the ArrayList<Letter> letters.
     * @return letters ArrayList<Letter>
     */
    public ArrayList<Letter> getLetters() {
        return this.letters;
    }

    /**
     * Getter for the decrypted message.
     * @return message String
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Getter for the keyword
     * @return key String
     */
    public String getKey() {
        return this.key;
    }

    /**
     * Getter used to fetch the frequencies HashMap.
     * @return frequencies HashMap
     */
    public HashMap<Character, Double> getFrequencies() {
        return this.frequencies;
    }

    /**
     * The getComparison method iterates the Letter structure; grabs the
     * corresponding frequency for the decrypted messages, and calculates the
     * difference between the two. It then adds the compared values to a Vector
     * record, which is added to the row Vector. The method returns a Vector
     * (containing the rows), with each row containing data for the comparison.
     * @return frequency comparison data.
     */
    public Vector<Vector<Object>> getComparison() {
        // The main data structure, which is used to contain the Vector grid.
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        // Iterates for each letter in the languages alphabet.
        for (Letter letter : this.letters) {
            // Grabs the letter frequency percentage for the decrypted message.
            double frequency = this.frequencies.get(letter.getLetter());
            // Calculates the rounded difference between the messages letter frequency,
            // and the average letter frequency for that language.
            double difference = Math.round(frequency - letter.getFrequency());
            // Creates a Vector structure for the records data.
            Vector<Object> record = new Vector<Object>();
            // Adds the Letter character.
            record.add(letter.getLetter());
            // Adds the Letter Language Frequency Average
            record.add(letter.getFrequency());
            // Adds the Message Letter Frequency (formatted to two decimal places).
            record.add(decimalFormat.format(frequency));
            // Adds the rounded difference between the two.
            record.add(difference);
            // Adds the record to the main structure of rows.
            data.add(record);
        }
        // Returns the Vector data grid.
        return data;
    }

    @Override
    /**
     * The compareTo method is a implemented method from the Comparable
     * interface - which enables for the object to be sorted in a Collection.
     * The method receives a Message object, which is then compared against the
     * instance object. The comparison between both objects involves invoking the
     * instance method countMatches() and comparing the results. If the instance
     * Message is greater an positive integer is returned; otherwise if the
     * received Message is greater an negative integer is returned. In the event
     * that both objects match a zero is returned. The value returned then determines
     * the direction the element is shuffled in the sorting process.
     */
    public int compareTo(Message other) {
        // Fetches the matches for both objects.
        int value1 = this.countMatches();
        int value2 = other.countMatches();
        // Compares the amount of matches, and returns a integer value to indicate the sorting action.
        return ((value1 > value2) ? 1 : ((value1 < value2) ? -1 : 0));
    }

    /**
     * The String representation of the Message object.
     * @return String.
     */
    @Override
    public String toString() {
        return this.getKey();
    }
}
