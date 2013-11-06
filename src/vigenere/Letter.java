//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Student name: Lance Baker
// Course: INFT3940 (IT Applications)
// Student number: c3128034
// Assignment title: Assignment 2 - Cryptography
// File name: Letter.java
// Created: 21-10-2010
// Last Change: 05-10-2010
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package vigenere;

/**
 * The letter class is used to store a languages alphabet character, and the
 * frequency occurrence average in that language. The class is used in conjunction
 * with a structure, and the ordering of the letters in the language is determined
 * based on the element position. The class also contains a counter instance field,
 * which is used to count the occurrence of a letter once a message has been decrypted.
 * @author Lance Baker (c3128034)
 */
public class Letter {

    private static final String SEPARATOR = " - ";
    private char letter;
    private double frequency;
    private int count;

    /**
     * Constructor which accepts just a char letter (without the frequency).
     * This is used in cases where you want to compare a primitive char with a
     * Letter, which is used in conjunction with the overridden equals method.
     * @param letter char
     */
    public Letter(char letter) {
        this(letter, 0);
    }

    /**
     * Constructor which accepts a char letter, and the average frequency occurrence.
     * @param letter char
     * @param frequency double
     */
    public Letter(char letter, double frequency) {
        this.setLetter(letter);
        this.setFrequency(frequency);
    }

    /**
     * Setter for the Letter, which accepts a primitive char. The received character
     * is then transformed to uppercase, and set to the instance field.
     * @param letter char
     */
    private void setLetter(char letter) {
        this.letter = Character.toUpperCase(letter);
    }

    /**
     * Setter for the languages letter Frequency.
     * @param frequency double
     */
    private void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    /**
     * Getter for the letter character.
     * @return letter char
     */
    public char getLetter() {
        return this.letter;
    }

    /**
     * Getter for the languages letter frequency.
     * @return frequency double.
     */
    public double getFrequency() {
        return this.frequency;
    }

    /**
     * Increments the counter by one. The method is used to count the occurrence of
     * letters in a decrypted message.
     */
    public void incrementCount() {
        this.count++;
    }

    /**
     * Getter for the letter count.
     * @return count int
     */
    public int getCount() {
        return this.count;
    }

    /**
     * Resets the counter to zero.
     */
    public void resetCount() {
        this.count = 0;
    }

    /**
     * The overridden ‘equals’ method is used to compare Letter objects. The method
     * is changed to only compare the inner letter character - which is used to
     * quickly find the corresponding Letter in the main structure.
     * @param object Letter.
     * @return boolean value indicating whether the character matches.
     */
    @Override
    public boolean equals(Object object) {
        // Checks whether the received object is a Letter.
        if (object instanceof Letter) {
            Letter l = (Letter) object;
            // Compares the received Letter character, with the instances character.
            return (this.getLetter() == l.getLetter());
        }
        return false;
    }

    /**
     * The toString method is overridden to allow for a Letter to be represented
     * in the form of a String.
     * @return String
     */
    @Override
    public String toString() {
        return this.getLetter() + SEPARATOR + this.getFrequency();
    }
}
