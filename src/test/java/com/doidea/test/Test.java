package com.doidea.test;

import javax.swing.*;

public class Test {
    public static void main(String[] args) {
        System.out.println(String.class.getName());
        System.out.println(String[].class.getName());
        System.out.println(int.class.getName());

        JDialog dialog = new JDialog();
        dialog.setTitle("Licenses");
    }
}
