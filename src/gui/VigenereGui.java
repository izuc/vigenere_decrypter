//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Student name: Lance Baker
// Course: INFT3940 (IT Applications)
// Student number: c3128034
// Assignment title: Assignment 2 - Cryptography
// File name: VigenereGui.java
// Created: 25-10-2010
// Last Change: 05-10-2010
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import vigenere.Message;
import vigenere.Vigenere;

/**
 * The Splash class is essentially a JDialog which is displayed for a short
 * duration. The JDialog has an image added, non re-sizable, and the system
 * buttons (close, maximise, minimise) are set to undecorated - therefore the
 * whole window title bar disappears, only leaving the contents to be displayed.
 * @author Lance Baker (c3128034)
 */
class Splash extends JDialog {

    private static final String SPLASH_IMAGE = "/resources/splash.png";
    private static final int TIME_DELAY = 5000;

    /**
     * The Splash constructor receives the VigenereGui parent object (which is a JFrame)
     * and is used to set the JDialog to the relative centred position of the parent JFrame.
     * @param parent VigenereGui
     */
    public Splash(VigenereGui parent) {
        super(parent); // Adds the parent JFrame to the super constructor.
        this.setResizable(false); // Disables the ability for the JDialog to be resized.
        // Sets the JDialog to use a BorderLayout.
        this.getContentPane().setLayout(new BorderLayout());
        // Creates a new ImageIcon based on the resource location of the splash image.
        // Adds the ImageIcon into a new JLabel - which then adds the JLabel into the centred
        // region of the JDialog content pane.
        this.getContentPane().add(BorderLayout.CENTER, new JLabel(new ImageIcon(getClass().getResource(SPLASH_IMAGE))));
        // The JDialog is set to Undecorated, this mode destroys the window title bar - only showing the contents within.
        this.setUndecorated(true);
        // Resizes the JDialog to the size of the contents. Therefore the JDialog will only be around the containing image.
        this.pack();
        // Sets the location of the JDialog to the centred position of the parent.
        this.setLocationRelativeTo(parent);
        // Displays the JDialog box.
        this.setVisible(true);
        // The following places the JDialog on a delay; which then will execute the proceeding code after the delay has finished.
        // It is placed within a Try catch block because Thread.sleep might throw an exception if its iterrupted (unlikely though).
        try {
            Thread.sleep(TIME_DELAY); // Delays execution
            // After the delay has finished, the JDialog's visibility is set to false. Java doesn't have the option to destroy objects,
            // and any object without a reference gets collected by the garbage collector.
            this.setVisible(false);
        } catch (InterruptedException e) {
        }
    }
}

/**
 * The VigenereGui class is the main class within the graphical user interface.
 * It extends JFrame, and contains all the graphical components used by the
 * application. Upon instantiation, it creates two sections - the first section
 * is used to display the three FileChooser objects (for the letter frequencies,
 * the keyword file, and the cipher text file). The second section has two JList
 * components (for the letter frequencies, and the keywords) and one JTextArea
 * which is used to display the cipher text message. The keyword JList and the
 * letter frequencies JList is populated once the load button is pressed on the
 * corresponding FileChooser. The JTextArea is set to contain the contents of
 * the encrypted message once it’s loaded, however when the 'Decrypt' button is
 * pressed the keywords JList is re-populated with Message objects (that
 * corresponds to each keyword, hence still displaying the keyword) and a message
 * once selected displays the decrypted message into the JTextArea.
 * @author Lance Baker (c3128034)
 */
public class VigenereGui extends JFrame {

    private static final String TITLE = "Xtreme Vigenere Decrypter";
    private static final String TITLE_LETTER_DEFINITIONS = "Letter Definitions";
    private static final String TITLE_KEYWORDS = "Keywords";
    private static final String TITLE_CIPHER_TEXT = "Cipher Text";
    private static final String TITLE_LOAD_FILES = "Load Files";
    private static final String TITLE_MANAGE_DECRYPTION = "Manage Decryption";
    private static final String BUTTON_DECRYPT = "Decrypt";
    private static final String BUTTON_CLEAR = "Clear";
    private static final String BUTTON_VIEW_DETAILS = "View Details";
    private static final String ERROR_TITLE = "An Error Occurred";
    private static final String ERROR_PLEASE_SELECT = "Make sure a file is selected.";
    private static final String ERROR_LETTERS = "Must load letter definitions.";
    private static final String ERROR_CIPHERTEXT = "Must load ciphertext file";
    private static final String ERROR_KEYWORDS = "The keyword could not be derived; please load the keywords file.";
    private static final Font FONT_HEADING = new Font("SansSerif", Font.BOLD, 15);
    // The Vigenere cipher reference, which contains the functionality required in order to decrypt a message.
    private Vigenere vigenere;
    // The FileChooser instance variables.
    // Each FileChooser is essentially a JPanel object which pertains further functionality.
    private FileChooser fcLetters;
    private FileChooser fcKeywords;
    private FileChooser fcCipher;
    // The letters JList is populated once the letter frequencies file loaded.
    private JList lstLetters;
    // The keywords JList is populated first when the keywords are loaded (which is just String objects), the
    // second time is when a message is decrypted - which is then populated with Message Objects (that outputs
    // the corresponding keyword via the toString method).
    private JList lstKeywords;
    // The ciphertext JTextArea is set once the encrypted message is loaded, and is also reset with the decrypted message
    // when a Message object is selected from the keywords JList.
    private JTextArea txtCipher;
    // The decrypt button is initally disabled, and re-enabled once the appropriate files have been loaded.
    private JButton btnDecrypt;
    // The ViewDetails button is only enabled once a message has been decrypted.
    private JButton btnViewDetails;
    // The Clear Keywords button is only enabled once keywords have been loaded.
    private JButton btnClearKeywords;

    /**
     * The constructor instantiates a new Vigenere object (which contains the
     * core functionality of the application) and added to a instance variable.
     * The JFrame is set with a BorderLayout, and the components are added to the
     * JFrame's content pane.
     */
    public VigenereGui() {
        this.vigenere = new Vigenere(); // Instantiates the Vigenere cipher object.
        // Sets the layout of the JFrame to BorderLayout.
        this.getContentPane().setLayout(new BorderLayout());
        // Initialises the three FileChoosers - which gets added to the north region.
        this.initFileChoosers();
        // The Initialises components in the main section - including the letters JList, the keywords JList, and the ciphertext JTextArea.
        // The JButtons (Decrypt, Clear, and ViewDetails) are also created and controlled in this method.
        this.initMain();
        // The application is terminated once the JFrame is closed.
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Sets the title of the application.
        this.setTitle(TITLE);
        // Sets the window size. Supports 1024x768 resolution.
        this.setSize(900, 500);
        // Disallows window resizing. The current window size is good enough.
        this.setResizable(false);
        // If the setLocationRelativeTo receives a null argument, the JFrame is set centred to the screen.
        this.setLocationRelativeTo(null);
        // Instantiates a Splash object before this JFrame is displayed (without being assigned a reference).
        // Therefore, once this Splash JDialog has finished being shown (the visibility is set to false)
        // the object will fall into empty-space and collected by the garbage collector.
        new Splash(this);
        // Sets the visibility of the JFrame to true, therefore its shown after the splash screen has been displayed.
        this.setVisible(true);
    }

    /**
     * The initFileChoosers method creates a JPanel with a single rowed 3
     * column GridLayout, adding three instances of the FileChooser class (which
     * extends JPanel) to each column space. The instances are used by the three
     * different types of files (the letter frequencies, the keywords, and the
     * Ciphertext) that require loading by the application. The instantiations
     * of the FileChooser also need a corresponding Action for the load button
     * operation, which invokes other methods contained within this class. The
     * created JPanel is then added to the north pane of the JFrame.
     */
    private void initFileChoosers() {
        // Creates a container JPanel for the FileChoosers with a defined GridLayout.
        JPanel loadFiles = new JPanel(new GridLayout(1, 3, 10, 10));
        // A titled border is created based on a constant value.
        TitledBorder border = BorderFactory.createTitledBorder(TITLE_LOAD_FILES);
        // The border is assigned a specified font.
        border.setTitleFont(FONT_HEADING);
        // The loadFiles JPanel is assigned the awesomely created TitledBorder; therefore, placing
        // emphasis on the container.
        loadFiles.setBorder(border);
        // The FileChooser for the letter frequencies is instantiated; which receives the titled heading, and a
        // action for the load operation. The ActionListener is created anonymously and within the actionPerformed method - 
        // it invokes the loadLetters method of this class.
        this.fcLetters = new FileChooser(TITLE_LETTER_DEFINITIONS, new ActionListener() {
            // The actionPerformed method is invoked once the button is pressed.

            public void actionPerformed(ActionEvent e) {
                // The loadLetters() instance method is invoked - which loads the letters file based on the selected file path.
                loadLetters();
            }
        });

        // The FileChooser for the keywords is instantiated; which receives the title, and a ActionListener. 
        // The anonymous ActionListener's inner actionPerformed method invokes the loadKeywords() instance method.
        this.fcKeywords = new FileChooser(TITLE_KEYWORDS, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // The loadKeywords instance method is invoked.
                // Loads the keywords file based on the selected file path.
                loadKeywords();
            }
        });

        // The FileChooser for the ciphertext is instantiated; which receives the title, and a ActionListener.
        // The anonymous ActionListener's inner actionPerformed method invokes the loadCipher() instance method.
        this.fcCipher = new FileChooser(TITLE_CIPHER_TEXT, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // The loadCipher instance method is invoked.
                // Loads the ciphertext file based on the selected file path.
                loadCipher();
            }
        });

        // Adds the three FileChoosers to the loadFiles JPanel.
        // The loadFiles panel is set with a GridLayout, and each addition represents a new columnar position.
        loadFiles.add(this.fcLetters);
        loadFiles.add(this.fcKeywords);
        loadFiles.add(this.fcCipher);
        // The loadFiles panel is then added to the North region of the content pane.
        this.getContentPane().add(loadFiles, BorderLayout.NORTH);
    }

    /**
     * The initMain method is used to add the component-guts of the application.
     * The method creates the letter freq. JList, the keywords JList, and the
     * JTextArea for the Ciphertext. The method also creates the JButtons used
     * to control the interface.
     */
    private void initMain() {
        // Creates the body JPanel with a GridLayout - consisting of a single row, and 3 columns.
        JPanel body = new JPanel(new GridLayout(1, 3, 10, 10));
        // Creates a TitledBorder based on a constant value.
        TitledBorder border = BorderFactory.createTitledBorder(TITLE_MANAGE_DECRYPTION);
        // Sets the TitledBorder with a awesomeness font.
        border.setTitleFont(FONT_HEADING);
        // Sets the body JPanel with the TitledBorder.
        body.setBorder(border);
        // Creates a JPanel (with a BorderLayout) for the letters JList.
        JPanel jpLetters = new JPanel(new BorderLayout(10, 10));
        // Adds a TitledBorder to the letters JPanel.
        jpLetters.setBorder(BorderFactory.createTitledBorder(TITLE_LETTER_DEFINITIONS));
        // Instantiates a new JList (which is assigned to the lstLetters instance variable).
        // Adds the JList to a JScrollPane - which allows for the JList to be scrolled.
        // Adds the JScrollPane to the letters JPanel in the Center region. The border layout
        // enables for the JList to be spread across the entire width and height of the divided section.
        jpLetters.add(BorderLayout.CENTER, new JScrollPane(this.lstLetters = new JList()));
        // Sets the selection mode on the letters to be a single section.
        this.lstLetters.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Since the JList doesn't have a method to disallow selection, it must be done manually.
        // Sets the selection background to white, therefore you cannot see the selection made.
        this.lstLetters.setSelectionBackground(Color.white);
        // Sets the focusable on the JList to false, therefore the elements
        // won't have an outline of the selection shown.
        this.lstLetters.setFocusable(false);
        // The JPanel container for the keywords JList, and keyword buttons JPanel.
        JPanel jpKeywords = new JPanel(new BorderLayout(10, 10));
        // Adds the TitledBorder (based on a constant value) to the JPanel.
        jpKeywords.setBorder(BorderFactory.createTitledBorder(TITLE_KEYWORDS));
        // Creates a JPanel container for two JButtons (clear, and view details).
        // The JPanel is added with a Left FlowLayout.
        JPanel jpKeywordButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        // Instantiates a new JButton used for clearing the JList keywords.
        // Assigns the object to a instance variable.
        this.btnClearKeywords = new JButton(BUTTON_CLEAR);
        // Adds an anonymous ActionListener to the Clear JButton. Once the button is pressed,
        // the inner actionPerformed method is triggered which then invokes the clearKeywords() instance method.
        this.btnClearKeywords.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                clearKeywords();
            }
        });
        // Instantiates a new JButton, and assigns it to a instance variable.
        this.btnViewDetails = new JButton(BUTTON_VIEW_DETAILS);
        // Adds an anonymous ActionListener.
        this.btnViewDetails.addActionListener(new ActionListener() {

            // Invokes the showDetailsDialog method once actionPeformed method is triggered.
            public void actionPerformed(ActionEvent e) {
                showDetailsDialog();
            }
        });
        // Disables the JButtons
        this.btnViewDetails.setEnabled(false);
        this.btnClearKeywords.setEnabled(false);
        // Adds the two JButtons to the keyword button JPanel.
        jpKeywordButtons.add(this.btnClearKeywords);
        jpKeywordButtons.add(this.btnViewDetails);
        // Instantiates a new JList(with a DefaultListModel instance added)
        // The JList reference is then assigned to the lstKeywords instance variable.
        // The lstKeywords is then added to a JScrollPane (allowing for the JList to be scrolled) and the JScrollPane
        // is then added to the JPanel centre region.
        jpKeywords.add(BorderLayout.CENTER, new JScrollPane(this.lstKeywords = new JList(new DefaultListModel())));
        // Adds the keyword buttons JPanel to the south region of the keywords JPanel.
        jpKeywords.add(BorderLayout.SOUTH, jpKeywordButtons);
        // The keywords JList is set to only have a single selection.
        this.lstKeywords.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // The JList is added with a new anonymous ListSectionListener, which invokes the 
        // valueChanged method once a selection in the JList has been made.
        this.lstKeywords.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                // The selected value is casted to a Object variable.
                Object selected = lstKeywords.getSelectedValue();
                // If the selected object is a Message object then it
                // sets the textcipher JTextArea to the contents of the decrypted
                // message. The JTextArea scroll bar is then set to the top position.
                if (selected instanceof Message) {
                    // Sets the text of the JTextArea to the decrypted message contents.
                    txtCipher.setText(((Message) selected).getMessage());
                    // Sets the position of the scroll bar to the top.
                    txtCipher.setCaretPosition(0);
                }
                // Only enables the ViewDetails JButton, if the selected object is a Message.
                // Otherwise it will set the JButton to false.
                btnViewDetails.setEnabled((selected instanceof Message));
            }
        });
        // The JPanel container for the cipher JTextArea, and the cipher buttons JPanel.
        JPanel jpCipher = new JPanel(new BorderLayout(10, 10));
        // Sets the container to have a TitledBorder (based on a constant value).
        jpCipher.setBorder(BorderFactory.createTitledBorder(TITLE_CIPHER_TEXT));
        // Instantiates a new JTextArea (which is added to a instance variable).
        // The JTextArea is then added to a JScrollPane, and to the centred position of the cipher JPanel container.
        jpCipher.add(BorderLayout.CENTER, new JScrollPane(this.txtCipher = new JTextArea()));
        // The JTextArea is set to disallow editing.
        this.txtCipher.setEditable(false);
        // Sets the JTextArea to have line wrapping on.
        this.txtCipher.setLineWrap(true);
        // The container for the Cipher Buttons, set with a Left FlowLayout.
        JPanel jpCipherButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        // Instantiates a new JButton (which is used to Decrypt), and adds it to a instance variable.
        this.btnDecrypt = new JButton(BUTTON_DECRYPT);
        // The Decrypt JButton is then added a ActionListener -
        // which then invokes the decryptCipher method once the action is triggered.
        this.btnDecrypt.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                decryptCipher();
            }
        });
        // Sets the Decrypt JButton to be disabled.
        this.btnDecrypt.setEnabled(false);
        // Adds the Decrypt JButton to the Cipher buttons JPanel container.
        jpCipherButtons.add(this.btnDecrypt);
        // Adds the cipher buttons JPanel to the south region of the cipher JPanel.
        jpCipher.add(BorderLayout.SOUTH, jpCipherButtons);
        // Adds the three containers to the divided Body JPanel.
        body.add(jpLetters); // Adds the Letters JPanel to the first column.
        body.add(jpKeywords); // Adds the Keywords JPanel to the second column.
        body.add(jpCipher); // Adds the Cipher JPanel to the third column.
        // Adds the body JPanel container to the centred region of the contentpane.
        this.getContentPane().add(body, BorderLayout.CENTER);
    }

    /**
     * The loadLetters method is used to load the contents of the selected file
     * into the system. The Vigenere object's loadLetters method accepts the File
     * object to be loaded (which interprets the text within the document).
     * The loaded letter structure is then converted to an Array, and loaded into
     * the letters JList. The changeDecryptBtnState method is then invoked,
     * which handles whether the decrypt button should be enabled - since the
     * decryption functionality requires more than one file to be loaded.
     */
    private void loadLetters() {
        try {
            // Adds the file selection path into a new File object and passed as an argument to the vigenere loadLetters system method.
            this.vigenere.loadLetters(new File(this.fcLetters.getSelectedPath()));
            // The letters JList is then set with an Array of Letter objects.
            this.lstLetters.setListData(this.vigenere.getLetters().toArray());
            // The changeDecryptBtnState method handles whether the Decrypt JButton should be enabled.
            this.changeDecryptBtnState();
        } catch (Exception ex) {
            // Shows an Error message in the event that the file could not be found.
            JOptionPane.showMessageDialog(this, ERROR_PLEASE_SELECT, ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * The loadKeywords method is used to load the keywords file. The path is
     * grabbed from the keywords FileChooser, and added to a new File Object
     * instance - which is then passed as an argument to the Vigenere object's
     * loadKeywords system method. Once the keywords file is loaded - the
     * displayKeywords method is then invoked.
     */
    private void loadKeywords() {
        try {
            // Loads the Keywords file.
            this.vigenere.loadKeywords(new File(this.fcKeywords.getSelectedPath()));
            // Displays the loaded keywords in the JList.
            this.displayKeywords();
        } catch (Exception ex) {
            // Prompts an error message in the event that the file could not be found.
            JOptionPane.showMessageDialog(this, ERROR_PLEASE_SELECT, ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * The loadCipher method loads the chosen ciphertext file into the system.
     * The selected file path, is added to a new File object, which is then passed
     * as an argument to the Vigenere loadCiphertext() system method. The JTextArea
     * is then set to display the contents of the encrypted message loaded, and
     * the position of the scroll bar is reset to the top. The displayKeywords()
     * method is then invoked; which ensures that the keywords JList contains just
     * String keywords (and not the decrypted Message objects). This allows for
     * the Ciphertext to be reloaded (without issues) once a decryption has
     * already taken place.

     */
    private void loadCipher() {
        try {
            // The selected file path, is added to a new File object and passed as an argument to the loadCiphertext system method.
            this.vigenere.loadCiphertext(new File(this.fcCipher.getSelectedPath()));
            // The JTextArea is set with the encrypted ciphertext.
            this.txtCipher.setText(this.vigenere.getCiphertext());
            // Realigns the scroll bar to the top position.
            this.txtCipher.setCaretPosition(0);
            // Displays the keywords.
            this.displayKeywords();
            // Changes the Decrypted button state.
            this.changeDecryptBtnState();
        } catch (Exception ex) {
            // Shows a message in case the file path could not be found.
            JOptionPane.showMessageDialog(this, ERROR_PLEASE_SELECT, ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * The clearKeywords method is used to clear both the stored keywords in
     * the Vigenere object, and the keywords DefaultListModel structure. Once
     * both structures are cleared, it disables the Clear JButton (since it
     * cannot be emptied again).
     */
    private void clearKeywords() {
        this.vigenere.getKeywords().clear(); // Clears the loaded keywords.
        ((DefaultListModel) this.lstKeywords.getModel()).clear(); // Clears the keywords JList structure (which also empties the JList).
        this.btnClearKeywords.setEnabled(false); // Disables the clear JButton.
    }

    /**
     * The displayKeywords method grabs the DefaultListModel from the keywords
     * JList, clears it, and re-adds the keywords by iterating throughout the
     * Vigenere system keyword structure - adding each keyword String to the model.
     * The clear JButton is then enabled if the keyword structure size has elements.
     */
    private void displayKeywords() {
        // Grabs the keyword's JList model.
        DefaultListModel model = (DefaultListModel) this.lstKeywords.getModel();
        model.clear(); // Clears the model (therefore ensuring that the JList is empty).
        // Iterates throughout each keyword stored in the keywords structure.
        for (String keyword : this.vigenere.getKeywords()) {
            model.addElement(keyword); // Adds the keyword String to the model.
        }
        // Enables the Clear Button - if the keyword structure is not empty.
        this.btnClearKeywords.setEnabled((!this.vigenere.getKeywords().isEmpty()));
    }

    /**
     * The decryptCipher method is used to decrypt a given encrypted message.
     * If the keywords list isn't loaded (or cleared), it will attempt to derive
     * the keyword and populate the keyword structure with the results found.
     * Once the system has keywords, the decryption process takes place; it
     * decrypts the message for every given keyword, and orders the results based
     * on the closest letter frequency matches (outputting it as a structure
     * of Message(s)). The decrypted Message objects are then populated into an
     * already cleared keywords JList model (which updates the GUI component).
     * The clear button is then enabled, and the last index of the JList is
     * selected (which should be the decrypted message) in doing so triggering
     * the selected index changed event (which outputs the message to the user).
     */
    private void decryptCipher() {
        if (!this.vigenere.getCiphertext().isEmpty()) {
            if (this.vigenere.getLetters().size() > 0) {
                if (this.vigenere.getKeywords().isEmpty()) {
                    // Attempts to derive the used key - using the Kasiski method (which will find the keyword length), one the keyword length
                    // is known; the key can be found by treating it as a interwoven caesar cipher (please see method for further explanation).
                    this.vigenere.getCipherKey();
                }
                // Ensures the keyword structure is not empty
                if (this.vigenere.getKeywords().size() > 0) {
                    // Grabs the model used to mutate the keyword JList contents.
                    DefaultListModel model = (DefaultListModel) this.lstKeywords.getModel();
                    // Decrypts the message, and outputs the results in an ordered collection of Message objects
                    // based on letter frequency comparison.
                    LinkedList<Message> messages = this.vigenere.decrypt();
                    model.clear(); // Clears the JList
                    // Iterates throughout the decryped messages (each message corresponding to a used keyword)
                    // adding each message to the keywords JList.
                    for (Message message : messages) {
                        model.addElement(message);
                    }
                    // Enables the clear button (only if the keywords JList was populated).
                    this.btnClearKeywords.setEnabled((model.getSize() > 0));
                    // Selects the last index (which should be the decrypted message).
                    this.lstKeywords.setSelectedIndex((model.getSize() - 1));
                    // Ensures the user can see the selected keyword.
                    this.lstKeywords.ensureIndexIsVisible((model.getSize() - 1));
                } else {
                    // The keyword could not be derived from the encrypted message.
                    JOptionPane.showMessageDialog(this, ERROR_KEYWORDS, ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // The letters definition file wasn't loaded.
                JOptionPane.showMessageDialog(this, ERROR_LETTERS, ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // The ciphertext is empty.
            JOptionPane.showMessageDialog(this, ERROR_CIPHERTEXT, ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * The showDetailsDialog method prompts the ViewDetails JDialog - Only if
     * the keywords JList has a selection, and the selected element in the JList 
     * is a Message object.
     */
    private void showDetailsDialog() {
        // If the keywords JList has a selection.
        if (!this.lstKeywords.isSelectionEmpty()) {
            // Checks whether the selected JList value is a Message object.
            if (this.lstKeywords.getSelectedValue() instanceof Message) {
                new ViewDetails(this); // Instantiates the ViewDetails JDialog. 
            }
        }
    }

    /**
     * Enables the Decrypt Button - if the Letters and Ciphertext file is loaded.
     */
    private void changeDecryptBtnState() {
        this.btnDecrypt.setEnabled(((this.vigenere.getLetters().size() > 0) && (this.vigenere.getCiphertext().length() > 0)));
    }

    /**
     * Getter for the Vigenere instance.
     * @return Vigenere.
     */
    public Vigenere getVigenere() {
        return this.vigenere;
    }

    /**
     * Getter for the keywords JList.
     * @return JList keywords.
     */
    public JList getLstKeywords() {
        return this.lstKeywords;
    }

    /**
     * The main method of the application; which instantiates a VigenereGui instance.
     * @param args
     */
    public static void main(String[] args) {
        new VigenereGui();
    }
}
