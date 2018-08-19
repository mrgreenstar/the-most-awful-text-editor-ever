package com.company.awful;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

// Добавить переключатель тем с темной на белую
public class Main extends JFrame {
    private JFrame frame;
    private NoTabTextArea text;
    private JScrollPane scrollText;
    // Menu for file options
    private JMenuBar mainMenuBar;
    private JMenu mainMenu;
    private JMenuItem newFileItem;
    private JMenuItem openFileItem;
    private JMenuItem saveFileItem;
    // Menu for view settings
    private JMenu viewMenu;
    private JMenuItem changeFont;
    private JMenuItem changeTheme;
    private JComboBox<String> themesList;
    private JLabel themeLabel;
    // Items for VIEW dialog window
    private JDialog settings;
    private JPanel firstLayer;
    private JPanel secondLayer;
    private JComboBox<String> listOfFonts;
    private JComboBox<Integer> listOfFontSizes;
    private JTextField fontExample;
    private JButton setButton;
    // Popup menu
    private JPopupMenu jpu;
    private JMenuItem jmiCut;
    private JMenuItem jmiCopy;
    private JMenuItem jmiPaste;
    // Bottom panel with information about columns and rows
    private JPanel bottomPanel;
    private JLabel rowsAndColumns;
    // For file opening
    private JFileChooser fileOpen;
    private FileNameExtensionFilter filter;

    private JPanel inputPanel;
    private JPanel secondInputPanel;
    private JTextField inputName;
    private JComboBox<String> chooseFormat;
    private JLabel inputtedFileName;
    private File file;
    private String fileName;

    private String themeName;
    private Config cfg;
    // Font variables
    private int TabSize = 2;
    private Font[] awailableFonts =
            GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    private String defaultFont = "Andale Mono";
    private Integer[] awailableFontSizes = {13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30};
    private int defaultFontSize = 15;
    private Font font = new Font(defaultFont, Font.PLAIN, defaultFontSize);

    private boolean wasSaved = false;
    private boolean isOpened = false;

    private String[] availableFormats = {"txt", "cpp", "c", "py", "java"};

    private Main() {
        createFrameSettings();
        jpu = new JPopupMenu();
        jmiCut = new JMenuItem("Cut");
        jmiCopy = new JMenuItem("Copy");
        jmiPaste = new JMenuItem("Paste");
        jpu.add(jmiCut);

        jpu.add(jmiCopy);
        jpu.add(jmiPaste);
        // Shit for bottom panel where i wanna show information about column and row
        rowsAndColumns = new JLabel();
        bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.add(rowsAndColumns, BorderLayout.EAST);

        inputtedFileName = new JLabel("Enter the file name and choose file format");
        inputName = new JTextField();
        inputName.setColumns(10);
        chooseFormat = new JComboBox<>(availableFormats);

        inputPanel = new JPanel(new BorderLayout());
        secondInputPanel = new JPanel();
        secondInputPanel.add(inputName);
        secondInputPanel.add(chooseFormat);
        inputPanel.add(inputtedFileName, BorderLayout.NORTH);
        inputPanel.add(secondInputPanel, BorderLayout.SOUTH);

        readConfig();
        text = new NoTabTextArea();
        text.setFont(font);
        text.setColumns(45);
        text.setRows(100);
        if (themeName.equals("Dark")) {
            text.setForeground(Color.WHITE);
            text.setBackground(Color.DARK_GRAY);
            text.setCaretColor(Color.GREEN);
        }
        text.setEnabled(false);
        text.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S) {
                    saveFileFunc();
                    JOptionPane.showMessageDialog(frame, "File has been saved",
                            "Message", JOptionPane.PLAIN_MESSAGE);
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if (!wasSaved){
                    frame.setTitle("* " + file.getName());
                }
                if (e.getKeyChar() == '(') {
                    text.insert(")", text.getCaretPosition());
                    text.setCaretPosition(text.getCaretPosition() - 1);
                }
                if (e.getKeyChar() == '{') {
                    text.insert("\n}", text.getCaretPosition());
                    text.setCaretPosition(text.getCaretPosition() - 1);
                }
                if (e.getKeyChar() == '[') {
                    text.insert("]", text.getCaretPosition());
                    text.setCaretPosition(text.getCaretPosition() - 1);
                }
                if (e.getKeyChar() == '\"') {
                    text.insert("\"", text.getCaretPosition());
                    text.setCaretPosition(text.getCaretPosition() - 1);
                }
                if (e.getKeyChar() == '\'') {
                    text.insert("\'", text.getCaretPosition());
                    text.setCaretPosition(text.getCaretPosition() - 1);
                }
                wasSaved = false;
            }
        });
        text.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    jpu.show(frame, e.getX(), e.getY());
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    jpu.show(frame, e.getX(), e.getY());
                }
            }
        });
        // new thread for update information about rows and columns
        Thread t = new Thread(){
            @Override
            public void run() {
                while (true){
                    try {
                        int offset = text.getCaretPosition();
                        int line = text.getLineOfOffset(offset);
                        int column = offset - text.getLineStartOffset(line);
                        rowsAndColumns.setText(Integer.toString(line + 1) + ":" + Integer.toString(column + 1) + " ");
                    }
                    catch (BadLocationException exc) {}
                }
            }
        };
        t.start();

        scrollText = new JScrollPane(text, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        fileOpen = new JFileChooser();
        filter = new FileNameExtensionFilter("Text and code", availableFormats[0],
                availableFormats[1], availableFormats[2], availableFormats[3], availableFormats[4]);
        fileOpen.setFileFilter(filter);
        // FILE MENU
        mainMenuBar = new JMenuBar();
        mainMenu = new JMenu("File");
        mainMenu.setMnemonic(KeyEvent.VK_F);

        newFileItem = new JMenuItem("Create file", KeyEvent.VK_C);
        newFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        openFileItem = new JMenuItem("Open file", KeyEvent.VK_O);
        openFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        saveFileItem = new JMenuItem("Save file", KeyEvent.VK_S);
        saveFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));

        newFileItem.addActionListener((ae) -> createFileFunc());
        openFileItem.addActionListener((ae) -> openFileFunc());
        saveFileItem.addActionListener((ae) -> saveFileFunc());

        mainMenu.add(newFileItem);
        mainMenu.add(openFileItem);
        mainMenu.add(saveFileItem);

        // VIEW MENU
        fontExample = new JTextField("Font example");
        listOfFonts = new JComboBox<>();
        for (Font f : awailableFonts) {
            listOfFonts.addItem(f.getFontName());
        }
        listOfFonts.setSelectedItem(cfg.getFontName());
        listOfFonts.addActionListener((ae) -> {
            font = new Font(listOfFonts.getSelectedItem().toString(), Font.PLAIN,
                    Integer.parseInt(listOfFontSizes.getSelectedItem().toString()));
            fontExample.setFont(font);
        });
        listOfFontSizes = new JComboBox<>(awailableFontSizes);
        listOfFontSizes.setSelectedItem(cfg.getFontSize());
        listOfFontSizes.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                font = new Font(listOfFonts.getSelectedItem().toString(), Font.PLAIN,
                        Integer.parseInt(listOfFontSizes.getSelectedItem().toString()));
                fontExample.setFont(font);
            }
        });
        setButton = new JButton("Apply");
        firstLayer = new JPanel(new BorderLayout());
        secondLayer = new JPanel(new BorderLayout());
        firstLayer.add(listOfFonts, BorderLayout.WEST);
        firstLayer.add(listOfFontSizes, BorderLayout.EAST);
        secondLayer.add(firstLayer, BorderLayout.NORTH);
        secondLayer.add(fontExample, BorderLayout.CENTER);
        secondLayer.add(setButton, BorderLayout.SOUTH);
        setButton.addActionListener((ae) -> {
            text.setFont(font);
            settings.setVisible(false);
            cfg.setFontName(font.getFontName());
            cfg.setFontSize(font.getSize());
            cfg.saveConfig();
        });
        settings = new JDialog(frame, "Font settings", true);
        settings.setLayout(new FlowLayout());
        settings.setSize(450, 150);
        settings.add(secondLayer);
        viewMenu = new JMenu("View");
        changeFont = new JMenuItem("Font settings");
        changeFont.addActionListener((ae) -> {
            settings.setVisible(true);
        });
        changeTheme = new JMenuItem("Theme settings");
        themeLabel = new JLabel("Choose theme");
        String[] themesL = {"Light", "Dark"};
        themesList = new JComboBox<>(themesL);
        JPanel themePanel = new JPanel(new BorderLayout());
        themePanel.add(themeLabel, BorderLayout.NORTH);
        themePanel.add(themesList, BorderLayout.SOUTH);

        changeTheme.addActionListener((ae) -> {
            int res = JOptionPane.showConfirmDialog(frame,
                    themePanel, "Theme settings", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                String choice = themesList.getSelectedItem().toString();
                Theme theme = Themes.getLightTheme();
                if (choice.equals("Light")) {
                    theme = Themes.getLightTheme();
                }
                else if (choice.equals("Dark")) {
                    theme = Themes.getDarkTheme();
                }
                text.setBackground(theme.getBackgroundColor());
                text.setForeground(theme.getFontColor());
                cfg.setThemeName(choice);
                cfg.saveConfig();
            }
        });
        viewMenu.add(changeFont);
        viewMenu.add(changeTheme);

        mainMenuBar.add(mainMenu);
        mainMenuBar.add(viewMenu);
        frame.setJMenuBar(mainMenuBar);
        frame.add(scrollText);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
        frame.setExtendedState(MAXIMIZED_BOTH);
    }
    // Class with changed method.
    // Method changes all tabulations to spaces
    private class NoTabTextArea extends JTextArea {
        @Override
        protected void processComponentKeyEvent(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_TAB) {
                text.insert(spacesInsteadTab().toString(), text.getCaretPosition());
                e.consume();
            }
        }
    }
    // Read config from file
    private void readConfig() {
        cfg = new Config();
        if (cfg.readConfig()) {
            font = cfg.getFont();
            themeName = cfg.getThemeName();
        }
    }

    // Create settings for frame
    private void createFrameSettings() {
        frame = new JFrame();
        frame.setTitle("Test Window");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screenSize.width, screenSize.height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Func for create file
    private void createFileFunc() {
        String fileName;
        text.setEnabled(true);
        int result = JOptionPane.showConfirmDialog(frame, inputPanel,"Enter file name and file format",
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            fileName = inputName.getText() + "." + chooseFormat.getSelectedItem();
            file = new File(fileName);
            frame.setTitle(fileName);
        }
    }

    // Func for open file
    private void openFileFunc() {
        int returnValue = fileOpen.showDialog(null, "Open");
        fileOpen.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            file = fileOpen.getSelectedFile();
            fileName = file.getName();
            String[] parts = fileName.split("[.]");
            boolean rightFormat = false;
            for (String format : availableFormats) {
                if (format.equals(parts[1])) {
                    rightFormat = true;
                }
            }
            if (rightFormat) {
                text.setText("");
                text.setEnabled(true);
                try (FileReader fReader = new FileReader(file)) {
                    frame.setTitle(fileName);
                    BufferedReader fileScanner = new BufferedReader(fReader);
                    String line;
                    while ((line = fileScanner.readLine()) != null) {
                        text.append(line + "\n");
                    }
                    isOpened = true;
                }
                catch (IOException exc) {
                    JOptionPane.showMessageDialog(frame, "Problems with file opening",
                            "Open file error", JOptionPane.ERROR_MESSAGE);
                }
            }
            else {
                JOptionPane.showMessageDialog(frame, "Wrong file format",
                        "File format error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Func for save file
    private void saveFileFunc() {
        if (isOpened){
            try (FileWriter fWriter = new FileWriter(file)) {
                wasSaved = true;
                fWriter.write(text.getText());
                frame.setTitle(file.getName());
            }
            catch (IOException exc) {
                JOptionPane.showMessageDialog(frame, "Problems with file saving",
                        "Save file error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    // Func for change all tabs to spaces
    private StringBuffer spacesInsteadTab() {
        StringBuffer line = new StringBuffer();
        while (line.length() < TabSize) {
            line.append(" ");
        }
        return line;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main();
            }
        });
    }
}
