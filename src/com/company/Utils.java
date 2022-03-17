package com.company;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.Random;
import java.util.function.Consumer;

public class Utils {
    public static String randomString() {
        int n = new Random().nextInt(20) + 10;
        String AlphaNumericString = "YosefJOO"
                + "01032296137"
                + "abc";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }
    public static void addATextWatcher(JTextComponent jTextComponent, Consumer<String> onTextChanged) {
        jTextComponent.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changed();
            }

            private void changed() {
                if (jTextComponent.isFocusOwner())
                    onTextChanged.accept(jTextComponent.getText());
            }
        });
    }
    public static void addComponentToGBL(JPanel panel, JComponent comp, int xPos, int yPos, int xWeight, int yWeight, int compWidth, int compHeight, int place, int stretch) {

        GridBagConstraints gridConstraints = new GridBagConstraints();

        gridConstraints.gridx = xPos;
        gridConstraints.gridy = yPos;
        gridConstraints.gridwidth = compWidth;
        gridConstraints.gridheight = compHeight;
        gridConstraints.weightx = xWeight;
        gridConstraints.weighty = yWeight;
        gridConstraints.insets = new Insets(5, 5, 5, 5);
        gridConstraints.anchor = place;
        gridConstraints.fill = stretch;

        panel.add(comp, gridConstraints);

    }


}
