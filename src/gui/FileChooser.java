//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Student name: Lance Baker
// Course: INFT3940 (IT Applications)
// Student number: c3128034
// Assignment title: Assignment 2 - Cryptography
// File name: FileChooser.java
// Created: 25-10-2010
// Last Change: 05-10-2010
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

/**
 * The file chooser class is used to create an object instance for each file
 * chooser; which contains a JTextField, a 'Browse' JButton, and a 'Load' JButton
 * that also receives the desired action to be performed. The class also extends
 * JPanel, therefore each FileChooser instance is a JPanel with the components
 * added to it. The Browse button uses the JFileChooser class, essentially being
 * a dialog box that allows for directory file browsing, and once a file is
 * selected - the JTextField is set to contain the selected file path.
 * @author Lance Baker (c3128034)
 */
class FileChooser extends JPanel {

    private static final String BROWSE = "...";
    private static final String LOAD = "Load";
    private static final String DOT = ".";
    private static final String TEXT_FILE_EXT = ".txt";
    private static final String TEXT_FILE_DESC = "Text Files (*.txt)";
    private JTextField fileLocation;
    private JButton load;
    private String title;

    /**
     * The Constructor receives the title of the FileChooser (which is used
     * in a TitledBorder), and the ActionListener for the Load JButton (which is
     * the desired action to performed once the button is pressed).
     * @param title String
     * @param action ActionListener
     */
    public FileChooser(String title, ActionListener action) {
        // Sets the JPanel Layout to BorderLayout - with a padding of 5.
        this.setLayout(new BorderLayout(5, 5));
        // Instantiates the JTextField, which is used to contain the selected file location.
        this.fileLocation = new JTextField();
        // Disallows users to change the contents in the JTextField.
        this.fileLocation.setEditable(false);
        // Assigns the received title parameter to the instance title variable.
        this.title = title;
        // Creates a JPanel to contain the buttons.
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        // Instantiates a JButton to use for the browsing functionality with the JFileChooser.
        // The variable will only need a local scope due to the ActionListener being assigned; which
        // invokes the openFileChooser method.
        JButton browse = new JButton(BROWSE);
        browse.addActionListener(new ActionListener() { // Adds the ActionListener
            // The action performed method is triggered once the button is pressed.

            public void actionPerformed(ActionEvent e) {
                openFileChooser(); // Invokes the openFileChooser method.
            }
        });
        // Instantiates a JButton, assigning it to the instance variable load.
        this.load = new JButton(LOAD);
        // Disables the Button until a file has been selected.
        this.load.setEnabled(false);
        // Adds the received ActionListener to the load button.
        this.load.addActionListener(action);
        // Adds the browse and load button to the buttons JPanel Container.
        buttons.add(browse);
        buttons.add(this.load);
        // Adds the JTextField to the centered position of this instance (which is
        // a inherited JPanel).
        this.add(BorderLayout.CENTER, this.fileLocation);
        // Adds the Buttons JPanel to the East position, therefore the buttons would
        // be located to the right.
        this.add(BorderLayout.EAST, buttons);
        // Adds a TitledBorder with the received String title.
        this.setBorder(BorderFactory.createTitledBorder(this.title));
    }

    /**
     * The openFileChooser method uses the JFileChooser to prompt a dialog which
     * enables for the user to browse file directories; which is filtered to only
     * display folders, and text file documents. Once a user has selected a file -
     * the file path is then added to the JTextField file location, and the load
     * button is enabled.
     */
    private void openFileChooser() {
        // Instantiates a JFileChooser.
        JFileChooser chooser = new JFileChooser();
        // Sets the file choosers current directory to the directory of the application.
        chooser.setCurrentDirectory(new java.io.File(DOT));
        // Sets the file choosers title.
        chooser.setDialogTitle(this.title);
        // Sets the selection mode to only allow browsing of Files and Directories.
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        // Disables the default file filter (disallowing any file to be chosen).
        chooser.setAcceptAllFileFilterUsed(false);
        // Creates a new FileFilter, with the accept criteria only allowing
        // text files and directories to be displayed.
        chooser.setFileFilter(new FileFilter() {
            // A file is passed through this method in order to be determined whether
            // it will be shown to the user.

            public boolean accept(File file) {
                // Returns boolean true if the file is a text document, or a directory.
                return (file.getName().toLowerCase().endsWith(TEXT_FILE_EXT) || file.isDirectory());
            }

            // The description which will be shown in the 'File Type' section
            // of the chooser dialog box.
            public String getDescription() {
                return TEXT_FILE_DESC;
            }
        });

        // Once the user has selected a file.
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            // The JTextField file location will be set with the selected file path.
            this.fileLocation.setText(chooser.getSelectedFile().toString());
            // Enables the load button.
            this.load.setEnabled(true);
        }
    }

    /**
     * A getter that returns the file location (being the text within the JTextField).
     * @return String file location.
     */
    public String getSelectedPath() {
        return this.fileLocation.getText();
    }
}
