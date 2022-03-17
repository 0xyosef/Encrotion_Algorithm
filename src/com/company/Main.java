package com.company;

import UI.AppGUI;
import encryptions.Base64;
import encryptions.IDEA;
import encryptions.PlayFair;
import encryptions.XOR;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        AppGUI appGUI =new AppGUI(List.of(
            new IDEA(),
            new PlayFair(),
            new XOR(),
            new Base64()

        ));
    }

}

