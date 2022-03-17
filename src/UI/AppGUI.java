package UI;

import com.company.EncryptionAlgorithm;
import com.company.Utils;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.LayoutStyle;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.List;


public class AppGUI extends JFrame{
    private final java.util.List<EncryptionAlgorithm> algorithmList;
    private EncryptionAlgorithm selectedAlgorithm;
    public AppGUI(List<EncryptionAlgorithm> algorithmList) {
        this.algorithmList = algorithmList;
        selectedAlgorithm = algorithmList.get(0);
        setUpUI();
    }

    private String[] getAlgorithmNameArr() {
        return algorithmList.stream().map(EncryptionAlgorithm::name).toArray(String[]::new);
    }



    private void setUpUI() {
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Encryption Algorithms | Yosef Mahmoud | Project GUI");
        setBackground(Color.LIGHT_GRAY);

        JPanel mainPanel = new JPanel(new MigLayout("", "[grow][grow]", "[]"));
        mainPanel.setBackground(Color.getHSBColor(105,100,58));
        JTextArea encTxt = new JTextArea();
        JTextArea decTxt = new JTextArea();
        JTextField keyTxt = new JTextField();
        JButton encBtn = new JButton("Encrypt ->");
        JButton decBtn = new JButton("<- Decrypt");
        JComboBox<String> algorithmJComboBox = new JComboBox<>(getAlgorithmNameArr());

        JPanel comboboxLayout = new JPanel(new BorderLayout());
        comboboxLayout.setBackground(mainPanel.getBackground());
        comboboxLayout.setBorder(BorderFactory.createTitledBorder("Selected Algorithm:"));
        //comboboxLayout.add(new JLabel("Selected Algorithm:"));
        comboboxLayout.add(algorithmJComboBox, BorderLayout.CENTER);


        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setSize(1000, 100);
        infoPanel.setBackground(mainPanel.getBackground());
        Border border = BorderFactory.createTitledBorder("Description:");
        infoPanel.setBorder(border);
        JTextArea desConLabel = new JTextArea(selectedAlgorithm.description());
        desConLabel.setLineWrap(true);
        desConLabel.setEditable(false);
        desConLabel.setBackground(mainPanel.getBackground());
        JScrollPane scrollPane = new JScrollPane(desConLabel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        infoPanel.add(scrollPane);

        CardLayout keyCardLayout = new CardLayout();
        JPanel keyPanel = new JPanel(keyCardLayout);
        keyPanel.setBackground(mainPanel.getBackground());
        keyPanel.setBorder(BorderFactory.createTitledBorder("Key:"));
        Box keyLayout = Box.createHorizontalBox();
        keyLayout.add(keyTxt);
        JButton genKeyBtn = new JButton("Random Key");
        keyLayout.add(genKeyBtn);


        String KEY = "KEY";
        String NO_KEY = "NO_KEY";

        keyPanel.add(keyLayout, KEY);
        JLabel noKeyLabel = new JLabel("This Algorithm Doesn't Need A Key");
        noKeyLabel.setHorizontalAlignment(JLabel.CENTER);
        keyPanel.add(noKeyLabel, NO_KEY);

        if (selectedAlgorithm.requireKey()) {
            keyCardLayout.show(keyPanel, KEY);
        } else {
            keyCardLayout.show(keyPanel, NO_KEY);
        }


        encTxt.setLineWrap(true);
        decTxt.setLineWrap(true);

        JLabel tl1 = new JLabel("Created By Yosef ");
        tl1.setHorizontalAlignment(JLabel.CENTER);
        JLabel tl2 = new JLabel("GitHub.com/YosefJoo");
        tl2.setHorizontalAlignment(JLabel.CENTER);


        mainPanel.add(comboboxLayout, "span 2, center, growx, wrap");
        mainPanel.add(infoPanel, "span 2, center, growx, wrap");
        mainPanel.add(keyPanel, "span 2, center, growx, wrap");
        mainPanel.add(new JLabel("Decrypted Text:"));
        mainPanel.add(new JLabel("Encrypted Text:"), "wrap");
        mainPanel.add(new JScrollPane(decTxt), "pushy,hmin 100,grow 1");
        mainPanel.add(new JScrollPane(encTxt), "pushy,hmin 100,grow 1, wrap");
        mainPanel.add(encBtn, "grow 50");
        mainPanel.add(decBtn, "grow 50, wrap");
        mainPanel.add(tl1, "align center,span 2,wrap");
        mainPanel.add(tl2, "align center,span 2,wrap");




        algorithmJComboBox.addItemListener(event -> {
            int selectedIndex = algorithmJComboBox.getSelectedIndex();
            selectedAlgorithm = algorithmList.get(selectedIndex);
            enc(encTxt, decTxt, keyTxt);


            desConLabel.setText(selectedAlgorithm.description());

            if (selectedAlgorithm.requireKey()) {
                keyCardLayout.show(keyPanel, KEY);
            } else {
                keyCardLayout.show(keyPanel, NO_KEY);
            }
        });

        genKeyBtn.addActionListener(e -> {
            keyTxt.setText(selectedAlgorithm.generateKey());
            enc(encTxt, decTxt, keyTxt);
        });
        Utils.addATextWatcher(keyTxt, s -> enc(encTxt, decTxt, keyTxt));
        Utils.addATextWatcher(decTxt, s -> enc(encTxt, decTxt, keyTxt));
        Utils.addATextWatcher(encTxt, s -> dec(encTxt, decTxt, keyTxt));
        encBtn.addActionListener(e -> enc(encTxt, decTxt, keyTxt));
        decBtn.addActionListener(e -> dec(encTxt, decTxt, keyTxt));
        add(mainPanel);
        setSize(1000, 1000);
        setVisible(true);
    }


    private void enc(JTextComponent encTxt, JTextComponent decTxt, JTextComponent keyTxt) {
        String plain = decTxt.getText();
        String key = keyTxt.getText();
        if (invalidKey(key)) {
            encTxt.setText("=== ERROR ===\nInvalid Key\n===\n");
            //JOptionPane.showMessageDialog(null,"=== ERROR ===\nInvalid Key\n===\n");
        } else {
            try {
                encTxt.setText(selectedAlgorithm.encrypt(plain, key));
            } catch (Exception e) {
                e.printStackTrace();
                encTxt.setText("=== ERROR ===\n" + e.toString() + "\n===\n");
                //JOptionPane.showMessageDialog(null,"=== ERROR ===\\n\" + e.toString() + \"\\n===\\n");

            }
        }
    }

    private void dec(JTextComponent encTxt, JTextComponent decTxt, JTextComponent keyTxt) {
        String plain = encTxt.getText();
        String key = keyTxt.getText();
        if (invalidKey(key)) {
            decTxt.setText("=== ERROR ===\nInvalid Key\n===\n");
        } else {
            try {
                decTxt.setText(selectedAlgorithm.decrypt(plain, key));
            } catch (Exception e) {
                e.printStackTrace();
                decTxt.setText("=== ERROR ===\n" + e.toString() + "\n===\n");
            }
        }
    }
    private boolean invalidKey(String key) {
        return selectedAlgorithm.requireKey() && !selectedAlgorithm.isValidKey(key);
    }

}
