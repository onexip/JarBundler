/*
 * Copyright (c) 2015, UltraMixer Digital Audio Solutions <info@ultramixer.com>, Seth J. Morabito <sethm@loomcom.com>
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.ultramixer.jarbundler.example;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;


public class HelloWorld {

    public static void main(String[] args) {
        JFrame frame = new JFrame("JarBuilder Test Application");

        JMenuBar menubar = new JMenuBar();

        menubar.add(new JMenu("File"));
        menubar.add(new JMenu("Edit"));

        JMenu helpMenu = new JMenu("Help");
        JMenuItem helpItem = new JMenuItem("Hello World Help");
        helpMenu.add(helpItem);
        helpItem
            .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SLASH,
                KeyEvent.SHIFT_MASK
                    | Toolkit.getDefaultToolkit()
                    .getMenuShortcutKeyMask()));

        // Attach simple anonymous listener
        helpItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                HelpBook.launchHelpViewer();
            }
        });

        menubar.add(helpMenu);
        frame.setJMenuBar(menubar);

        final long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);

        String[] cp = System.getProperty("java.class.path").split(":");
        String classpathList = "";
        for (int i = 0; i < cp.length; i++) {
            classpathList += "<li>" + cp[i] + "</li>";
        }

        String argsList = "";
        for (int i = 0; i < args.length; i++) {
            argsList += "<li>" + args[i] + "</li>";
        }

        JLabel label = new JLabel(
            "<html>" +
                "Hello, world!<br/><br/>" +

                "Java version: " + System.getProperty("java.version") + "<br/>" +
                "Java vendor: " + System.getProperty("java.vendor") + "<br/>" +
                "Java home: " + System.getProperty("java.home") + "<br/><br/>" +

                "Classpath: <ul>" + classpathList + "</ul><br/>" +

                "Max Memory: " + maxMemory + "MB<br/><br/>" +

                "Args: <ul>" + argsList + "</ul><br/>" +
                "</html>", SwingConstants.LEFT);
        frame.getContentPane().add(label, BorderLayout.CENTER);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        placeWindow(frame, 400, 300);
        frame.setVisible(true);
    }

    public static void placeWindow(Window window, int width, int height) {

        // New size for this window
        Dimension windowSize = new Dimension(width, height);
        window.setSize(windowSize);

        // Place in the 'dialog' position, centered aligned 1/3 from top
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        Point windowLocation = new Point(0, 0);
        windowLocation.x = (screenSize.width - windowSize.width) / 2;
        windowLocation.y = (screenSize.height / 3) - (windowSize.height / 2);

        // Set final size and location
        window.setLocation(windowLocation);
    }

}
