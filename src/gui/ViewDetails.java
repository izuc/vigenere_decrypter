//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Student name: Lance Baker
// Course: INFT3940 (IT Applications)
// Student number: c3128034
// Assignment title: Assignment 2 - Cryptography
// File name: ViewDetails.java
// Created: 27-10-2010
// Last Change: 05-10-2010
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import vigenere.Letter;
import vigenere.Message;

/**
 * The ViewDetails class is used to show further details based on the message 
 * decryption; showing comparison between the decrypted message letter frequencies 
 * and the language average. The comparison between the letter frequencies are 
 * displayed in two approaches: The first being a bar graph overview - which 
 * shows the comparison for each letter, and the second being a more in-depth view
 * showing the rounded differences (within a JTable). The class extends JDialog,
 * therefore the instance itself is a dialog box. The user can also navigate between
 * decrypted messages, since the constructor must receive the parent VigenereGui -
 * it allows for the retrieval of the selected decrypted message, with also the ability to
 * navigate throughout the elements.
 * @author Lance Baker (c3128034)
 */
class ViewDetails extends JDialog {

    private static final Dimension WINDOW_SIZE = new Dimension(500, 550);
    private static final Dimension CHART_SIZE = new Dimension(500, 400);
    private static final String TITLE = "View Frequency Details: ";
    private static final String PREVIOUS = " < Previous";
    private static final String NEXT = "Next > ";
    private static final String COLUMN_LETTER = "Letter";
    private static final String COLUMN_LANGUAGE_FREQUENCY = "Language Freq.";
    private static final String COLUMN_DECRYPTION_FREQUENCY = "Decryption Freq.";
    private static final String COLUMN_DIFFERENCE = "Difference";
    private static final String FREQUENCY = "Frequency";
    private static final String TAB_CHART = "Chart";
    private static final String TAB_MORE_DETAILS = "More Details";
    private VigenereGui parent; // A reference to the parent.
    private DefaultTableModel dataModel;
    private JPanel chart;
    private JButton btnPrevious;
    private JButton btnNext;

    /***
     * The constructor receives the parent VigenereGui object which is a JFrame.
     * The parent is added to the super constructor (for the dialog box) enabling
     * for the window to be focused. The parent is then stored as an instance
     * variable for further use (which will allow for browsing between the
     * decrypted messages).
     * @param parent
     */
    public ViewDetails(VigenereGui parent) {
        // Sets the parent dialog with the parent JFrame; allowing for the dialog
        // to be focused once its shown.
        super(parent);
        // Sets the parent to be stored in the instance variable (for future reference).
        this.setParent(parent);
        // Disposes the dialog once the cross is pressed.
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        // Sets the layout to be a BorderLayout.
        this.getContentPane().setLayout(new BorderLayout());
        // Disables the ability to resize the dialog window.
        this.setResizable(false);
        // Invokes the initialise method; which adds components to the dialog.
        this.initialise();
        // Sets the window size.
        this.setSize(WINDOW_SIZE);
        // Shows the dialog box relative to the parent JFrame location.
        this.setLocationRelativeTo(parent);
        // Shows the dialog window.
        this.setVisible(true);
    }

    /**
     * The setter for the Parent Object.
     * @param parent VigenereGui.
     */
    private void setParent(VigenereGui parent) {
        this.parent = parent;
    }

    /**
     * The initialise method is pretty much the main method for the Dialog. It
     * is invoked from the constructor before the dialog is shown (therefore the
     * components are fully added before it’s displayed). The main content pane
     * is added with a JTabbedPane in the centred region, with the navigation
     * buttons (JPanel) added to the south. The JTabbedPane is created with two
     * tabs - the first being a JPanel (used as a container for the bar chart)
     * and the second being a JTable.
     */
    private void initialise() {
        // The JTabbedPane is used to contain the two tabbed panels (the chart, and the JTable).
        JTabbedPane centre = new JTabbedPane();
        // The first tab is created; which contains a JPanel (which is added to a instance variable called chart).
        centre.addTab(TAB_CHART, this.chart = new JPanel());
        // The chart JPanel is added with a white background colour.
        this.chart.setBackground(Color.white);
        // The next table is added; which contains a JTable added to a JScrollPane (allowing for the
        // JTable to have scroll bars if it exceeds the allowed size). The JTable is constructed with a reference
        // to a DefaultTableModel (which is added to the instance variable called dataModel). The table model allows
        // for the contents of the JTable to be modified.
        centre.addTab(TAB_MORE_DETAILS, new JScrollPane(new JTable(this.dataModel = new DefaultTableModel() {
            // The DefaultTableModel is extended anonymously, which consists of one override isCellEditable;
            // which is changed to return false - therefore all cells cannot be edited.

            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        })));
        // The buttons are stored in a JPanel container, with a right flowlayout - hence
        // the buttons will be aligned to the right. The padding will be 10.
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        // Instantiates new JButtons with a corresponding instance reference - which are
        // also added to the buttons JPanel container.
        buttons.add(this.btnPrevious = new JButton(PREVIOUS));
        buttons.add(this.btnNext = new JButton(NEXT));

        // The btnPrevious button is added with an an anonymous ActionListener instantiation,
        // which has the functionality within the inner actionPerformed method (which is invoked
        // once the button is pressed).
        this.btnPrevious.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // The keyword JList is grabbed from the parent.
                JList list = parent.getLstKeywords();
                // The currently selected index is subtracted one (which is the previous index).
                int selected = (list.getSelectedIndex() - 1);
                // If the subtracted index is greater than -1
                if (selected > -1) {
                    // The list is then changed to have that selection.
                    list.setSelectedIndex(selected);
                    // The list is set to ensure that the selected index is shown;
                    // which is used when the scroll bars are hiding previous elements.
                    list.ensureIndexIsVisible(selected);
                    // The displayFrequencies method is invoked, which updates the chart and the JTable
                    // with the current decrypted message comparison.
                    displayFrequencies();
                }
            }
        });

        // The btnNext button is added with an anonymous ActionListener instantiation (which is basically the same
        // as the btnPrevious button). The inner actionPerformed method is only invoked once the button is pressed.
        this.btnNext.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // The keyword JList is grabbed from the parent.
                JList list = parent.getLstKeywords();
                // Adds one to the selected index, which gives the next index.
                int selected = (list.getSelectedIndex() + 1);
                // Grabs the size of the structure within the JList.
                int size = ((DefaultListModel) list.getModel()).size();
                // If the next selection is less than the structure size
                if (selected < size) {
                    // Sets the JList selection with the next index.
                    list.setSelectedIndex(selected);
                    // Makes sure the selection can be seen.
                    list.ensureIndexIsVisible(selected);
                    // Invokes the displayFrequencies method, which updates the views with the
                    // current message decryption.
                    displayFrequencies();
                }
            }
        });
        // Adds the centre JPanel (containing the two tabs) to the centred region of the Dialog window.
        this.getContentPane().add(BorderLayout.CENTER, centre);
        // Adds the navigation buttons to the south region.
        this.getContentPane().add(BorderLayout.SOUTH, buttons);
        // Invokes the displayFrequencies method, which updates the views to the currently selected
        // decrypted message.
        this.displayFrequencies();
    }

    /**
     * This method is used to determine whether the navigation buttons should be
     * enabled or disabled. It checks whether the buttons have enough room -
     * being that the selected index is greater (or lesser) than the structures regions.
     */
    private void changeNavigationBtnStates() {
        // It grabs the JList from the parent object.
        JList list = this.parent.getLstKeywords();
        // Gets the size of the inner JList structure.
        int size = ((DefaultListModel) list.getModel()).size();
        // If the next selected index is less than the structures size - then the
        // condition returns return, and the value is added to the setEnabled method.
        // Hence the button would be enabled/ disabled depending on that condition.
        this.btnNext.setEnabled(((list.getSelectedIndex() + 1) < size));
        // The selected index is required to be greater than the first element in order
        // to be enabled. Otherwise the previous button would be disabled.
        this.btnPrevious.setEnabled((list.getSelectedIndex() > 0));
    }

    /**
     * The displayFrequencies method shows the comparison between the letter
     * frequencies of the language, and the frequencies occurred in the decrypted
     * message. It enables for the user to understand whether the message has been
     * properly decrypted (without being required to know the language). The
     * method grabs the currently selected value from the keywords JList (which
     * in this case is a Message object). The retrieved message object comparison
     * is added to a new Chart (which replaces the previous chart in the Chart
     * JPanel) and the JTable model is populated with the compared data.
     */
    private void displayFrequencies() {
        // Grabs the currently selected message.
        Message message = (Message) this.parent.getLstKeywords().getSelectedValue();
        // Creates a Vector collection of Strings.
        Vector<String> columns = new Vector<String>();
        // Grabs the Vector of Vector objects from the message (which is the frequency comparison).
        // The structure is used to add the data to the data model (which then reflects changes back to the JTable).
        // The structure is essentially Rows of Field Data, the datatype is Vector because the data model accepts that structure.
        Vector<Vector<Object>> data = message.getComparison();
        // Adds the columns to the Column Vector.
        columns.add(COLUMN_LETTER);
        columns.add(COLUMN_LANGUAGE_FREQUENCY);
        columns.add(COLUMN_DECRYPTION_FREQUENCY);
        columns.add(COLUMN_DIFFERENCE);
        // Sets both vector structures to the data model - which updates the JTable.
        this.dataModel.setDataVector(data, columns);
        // Sets the title of the JDialog with the default title, and the decrypted messages key.
        this.setTitle(TITLE + message.getKey().toUpperCase());
        // Removes all components from the chart JPanel (therefore ensuring that the container is clear);
        // this is because the ChartPanel object cannot be modified once created. Java handles all objects without references, and
        // frees them using the internal garbage collector.
        this.chart.removeAll();
        // The ChartPanel is created based on data contained in the Message Object.
        ChartPanel chartPanel = createChart(message);
        // Enables the Chart to have the zoom operation.
        chartPanel.setFillZoomRectangle(true);
        // Sets the preferred size of the chart.
        chartPanel.setPreferredSize(CHART_SIZE);
        // Adds the ChartPanel to the chart JPanel.
        this.chart.add(chartPanel);
        // The validate method refreshes the components in the container; therefore
        // displays the new ChartPanel object.
        this.chart.validate();
        // The changeNavigationBtnStates method is invoked, which either enables or disables the
        // navigation JButtons based on the whereabouts in the JList.
        this.changeNavigationBtnStates();
    }

    /**
     * The createChart method uses the external JFreeChart library in order to
     * have the capability to create the chart. The method receives the Message
     * object which is then used to get the comparison data.
     * @param message Message
     * @return ChartPanel
     */
    private static ChartPanel createChart(Message message) {
        // Instantiates a new DefaultCategoryDataset object, which is used as a structure for adding the chart data.
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        // Iterates for each letter
        for (Letter letter : message.getLetters()) {
            // Adds the languages average letter frequency (which is added as the first series).
            dataset.addValue(letter.getFrequency(), COLUMN_LANGUAGE_FREQUENCY, Character.toString(letter.getLetter()));
            // Adds the message decrypted letter frequency.
            dataset.addValue(message.getFrequencies().get(letter.getLetter()), COLUMN_DECRYPTION_FREQUENCY, Character.toString(letter.getLetter()));
        }
        // Creates a JFreeChart object using the factory method. It adds the messages decryption key as the title, adds the two axis column names along with the dataset.
        // The PlotOrientation is used to make the columns appear in a vertical orientation (the other option being horizontal). The boolean true value is used to enable the lengend (which
        // displays the two series), and the other boolean values disable some non-essential features.
        JFreeChart chart = ChartFactory.createBarChart(message.getKey().toUpperCase(), COLUMN_LETTER, FREQUENCY, dataset, PlotOrientation.VERTICAL, true, false, false);
        // Sets the background of the chart to white.
        chart.setBackgroundPaint(Color.white);
        // Gets the CategoryPlot, which is the x axis (for the letters).
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        // Gets the range axis, which is the y axis (for the numeric data).
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        // Sets the units for the range axis based on the max data contained in the structure.
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // Gets the renderer used by the letter plots (which is for the bars).
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        // Diables the outline view for the bars.
        renderer.setDrawBarOutline(false);
        // Creates two gradients used for the two series (the languages freq, and the decrypted message freq).
        GradientPaint gradient1 = new GradientPaint(0.0f, 0.0f, Color.blue, 0.0f, 0.0f, new Color(0, 0, 64));
        GradientPaint gradient2 = new GradientPaint(0.0f, 0.0f, Color.green, 0.0f, 0.0f, new Color(0, 64, 0));
        // Sets the renderer (for the bars) with the two gradients corresponding to the desired series.
        renderer.setSeriesPaint(0, gradient1);
        renderer.setSeriesPaint(1, gradient2);
        // Adds the JFreeChart to a instantiated ChartPanel. The ChartPanel enables for the chart to be rendered,
        // and added to other graphical components.
        return new ChartPanel(chart);
    }
}
