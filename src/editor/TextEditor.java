package editor;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditor extends JFrame{
//    static JTextField filenameField = new JTextField();
    static JTextArea textArea = new JTextArea(20,20);

   static JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

    public TextEditor() throws IOException, ClassNotFoundException {

    setTitle("Text editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        addComponents(this);
        addMenu(this);
        setVisible(true);
        setLocationRelativeTo(null);

    }

    private static void addMenu(JFrame frame) {

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menu.setName("MenuFile");

        JMenuItem loadItem = new JMenuItem("Load");
        loadItem.setName("MenuLoad");
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.setName("MenuSave");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setName("MenuExit");

        loadItem.addActionListener(event -> {
            loadAction();
        });

        saveItem.addActionListener(event -> {
            saveAction();
        });

        exitItem.addActionListener(event -> {
            frame.dispose();
        });

        menu.add(loadItem);
        menu.add(saveItem);
        menu.add(exitItem);

        menuBar.add(menu);
        frame.setJMenuBar(menuBar);
    }

    public static void addComponents(JFrame pane) throws IOException, ClassNotFoundException {
        JPanel textPanel = new JPanel();

        textArea.setName("TextArea");
        JScrollPane scrollableTextArea = new JScrollPane(textArea);
        scrollableTextArea.setName("ScrollPane");

        scrollableTextArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollableTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        textPanel.add(scrollableTextArea);

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new FlowLayout());
        addSaveButton(northPanel);
        addLoadButton(northPanel);
        addSearch(northPanel);
//        addFieldFileName(northPanel);

        pane.add(northPanel, BorderLayout.PAGE_START);
        pane.add(textPanel, BorderLayout.CENTER);
    }

//    public static void addFieldFileName(JPanel northPanel) {
//       filenameField.setName("FilenameField");
//       filenameField.setPreferredSize(new Dimension(80, 20));
//       filenameField.getDocument().putProperty("filterNewlines", Boolean.TRUE);
//       filenameField.setBounds(60, 2, 170, 20);
//
//       northPanel.add(filenameField);
//    }

    static void addSearch(JPanel northPanel) throws IOException {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setName("UseRegExCheckbox");

        JTextField searchField = new JTextField();
        searchField.setName("SearchField");
        searchField.setName("FilenameField");
        searchField.setPreferredSize(new Dimension(80, 20));

        Image img = ImageIO.read(new File("./searchicon.jpg"));
        Image resizedImage = img.getScaledInstance(25, 25, 0);
        JButton buttonSearch = new JButton(new ImageIcon(resizedImage));
        buttonSearch.setName("StartSearchButton");

        img = ImageIO.read(new File("./nexticon.png"));
        resizedImage = img.getScaledInstance(25, 25, 0);
        JButton buttonNext = new JButton(new ImageIcon(resizedImage));
        buttonSearch.setName("NextMatchButton");

        img = ImageIO.read(new File("./previcon.png"));
        resizedImage = img.getScaledInstance(25, 25, 0);
        JButton buttonPrev = new JButton(new ImageIcon(resizedImage));
        buttonSearch.setName("PreviousMatchButton");

        int[] currIndex = {0};
        AtomicReference<String> textState = new AtomicReference<>();
        AtomicReference<Matcher> matcher = new AtomicReference<>();
        ArrayList<Integer> indexes = new ArrayList<>();

        buttonSearch.addActionListener(actionEvent -> {
            String searchValue = searchField.getText();
            Pattern pattern = Pattern.compile(searchValue);
            textState.set(textArea.getText());
            matcher.set(pattern.matcher(textArea.getText()));
            while (matcher.get().find()) {
                indexes.add(matcher.get().start());
            }
            if (!(indexes.isEmpty())) {
                matcher.get().reset();
                matcher.get().find();
                String foundText = matcher.get().group();
                textArea.setCaretPosition(indexes.get(0) + foundText.length());
                textArea.select(indexes.get(0), indexes.get(0) + foundText.length());
                textArea.grabFocus();
                currIndex[0] = 0;
            }
        }

        buttonPrev.addActionListener(actionEventPrev -> {
            matcher.get().reset();
            int targetIndex;
            if (currIndex[0] == 0) {
                targetIndex = indexes.size() - 1;
            } else {
                targetIndex = currIndex[0] - 1;
            }
            while (matcher.get().find()) {
                if (matcher.get().start() == indexes.get(targetIndex)) {
                    String foundPrevText = matcher.get().group();
                    textArea.setCaretPosition(indexes.get(targetIndex));
                    textArea.select(indexes.get(targetIndex), indexes.get(targetIndex) + foundPrevText.length());
                    textArea.grabFocus();
                    currIndex[0] = targetIndex;
                    break;
                }
            }
        });

        buttonNext.addActionListener(actionEventNext -> {
            matcher.get().reset();
            int targetIndex;
            if (currIndex[0] == indexes.size() - 1) {
                targetIndex = 0;
            } else {
                targetIndex = currIndex[0] + 1;
            }
            while (matcher.get().find()) {
                if (matcher.get().start() == indexes.get(targetIndex)) {
                    String foundNextText = matcher.get().group();
                    textArea.setCaretPosition(indexes.get(targetIndex));
                    textArea.select(indexes.get(targetIndex), indexes.get(targetIndex) + foundNextText.length());
                    textArea.grabFocus();
                    currIndex[0] = targetIndex;
                    break;
                }
            }
        });


        northPanel.add(searchField);
        northPanel.add(buttonSearch);
        northPanel.add(buttonPrev);
        northPanel.add(buttonNext);
        northPanel.add(checkBox);
    }

    static void addSaveButton(JPanel northPanel) throws IOException, ClassNotFoundException {
        Image img = ImageIO.read(new File("./saveicon.png"));
        Image resizedImage = img.getScaledInstance(25, 25, 0);
        JButton button = new JButton(new ImageIcon(resizedImage));
        button.setName("SaveButton");
        button.addActionListener(actionEvent -> {
            saveAction();
        });

        northPanel.add(button);
    }

    public static void addLoadButton(JPanel northPanel) throws IOException, ClassNotFoundException{
        Image img = ImageIO.read(new File("./openicon.png"));
        Image resizedImage = img.getScaledInstance(25, 25, 0);
        JButton button = new JButton(new ImageIcon(resizedImage));
        button.setName("OpenButton");
        button.addActionListener(actionEvent -> {
            loadAction();
        });

        northPanel.add(button);
    }

    static void loadAction(){
        jfc.showOpenDialog(null);

        File file = jfc.getSelectedFile();
//            File file = new File(filename);
        try {
            if (file.exists()) {
                FileReader fileReader = new FileReader(file.getName());
                textArea.read(fileReader, file.getName());
            } else {
                textArea.setText("");
                return;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void saveAction(){
        jfc.showSaveDialog(null);

        File file = jfc.getSelectedFile();
        if (!(file.exists())) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            serialize(file.getName(), textArea.getText());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void serialize(String fileName, Object obj) throws IOException, ClassNotFoundException {
        FileOutputStream fos = new FileOutputStream(fileName);
        OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
        Writer out = new BufferedWriter(osw);
        out.write(textArea.getText());
        out.close();
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new TextEditor();
    }
}
