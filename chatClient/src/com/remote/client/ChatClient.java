package com.remote.client;

import com.remote.server.InterfaceServer;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javazoom.jl.player.Player;
import javazoom.jl.decoder.JavaLayerException;

public class ChatClient extends UnicastRemoteObject implements InterfaceClient {
    private final InterfaceServer server;
    private final String name;
    private final JTextArea input;
    private final JTextArea output;
    private final JPanel jpanel;

    public ChatClient(String name, InterfaceServer server, JTextArea jtext1, JTextArea jtext2, JPanel jpanel)
            throws RemoteException {
        this.name = name;
        this.server = server;
        this.input = jtext1;
        this.output = jtext2;
        this.jpanel = jpanel;
        server.addClient(this);
    }

    @Override
    public void retrieveMessage(String message) throws RemoteException {
        output.setForeground(Color.BLUE);
        output.setText(output.getText() + "\n" + message);
    }

    @Override
    public void retrieveMessageAdd(String message) {
        // Font myFont1 = new Font("Serif", Font.ITALIC, 12);
        output.setForeground(Color.BLUE);
        output.setText(output.getText() + "\n" + message);
    }

    @Override
    public void retrieveMessageRemove(String message) {
        output.setForeground(Color.BLUE);
        output.setText(output.getText() + "\n" + message);
        // outputRemove.setText(outputRemove.getText() + "\n" + message);
    }

    public void openImageFile(String filename, ArrayList<Integer> content) throws RemoteException {
        try {
            String separator = System.getProperty("os.name").startsWith("Linux")
                    || System.getProperty("os.name").startsWith("MacOS") ? "/" : "\\";
            String filePath = System.getProperty("user.home") + separator + filename;
            FileOutputStream out = new FileOutputStream(filePath);
            for (int i : content) {
                out.write((byte) i);
            }
            out.flush();
            out.close();

            // Display the image in a JFrame
            JFrame frame = new JFrame("Image Viewer");
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            JLabel label = new JLabel();
            label.setIcon(new ImageIcon(filePath));
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setVerticalAlignment(JLabel.CENTER);
            frame.add(label);
            frame.setVisible(true);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void openAudioFile(String filename, ArrayList<Integer> content) throws RemoteException {
        try {
            String separator = System.getProperty("os.name").startsWith("Linux")
                    || System.getProperty("os.name").startsWith("MacOS") ? "/" : "\\";
            String filePath = System.getProperty("user.home") + separator + filename;
            FileOutputStream out = new FileOutputStream(filePath);
            for (int i : content) {
                out.write((byte) i);
            }
            out.flush();
            out.close();

            // Play the audio file using JLayer
            JFrame frame = new JFrame("Audio Player");
            frame.setSize(300, 100);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            JLabel label = new JLabel("Playing: " + filename);
            frame.add(label);
            frame.setVisible(true);

            // Center the frame on the screen
            frame.setLocationRelativeTo(null);

            // Add play/pause button
            JButton playPauseButton = new JButton("Pause");
            frame.add(playPauseButton, BorderLayout.SOUTH);

            Player player;
            FileInputStream fis = new FileInputStream(filePath);
            try {
                player = new Player(fis);
            } catch (JavaLayerException e) {
                System.out.println("Error: " + e.getMessage());
                return;
            }

            final boolean[] isPlaying = {true};
            final Object lock = new Object();

            Thread playerThread = new Thread(() -> {
                try {
                    synchronized (lock) {
                        while (true) {
                            if (!isPlaying[0]) {
                                lock.wait();
                            }
                            if (!player.play(1)) {
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            });

            final boolean[] isPlay = {true};
            playPauseButton.addActionListener(e -> {
                if (isPlay[0]) {
                    playerThread.suspend();
                    playPauseButton.setText("Play");
                } else {
                    playerThread.resume();
                    playPauseButton.setText("Pause");
                }
                isPlay[0] = !isPlay[0];
            });

            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    player.close();
                    try {
                        fis.close();
                    } catch (IOException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
            });

            playerThread.start();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void openTextFile(String filename, ArrayList<Integer> content) throws RemoteException {
        try {
            StringBuilder fileContent = new StringBuilder();
            for (int i : content) {
                fileContent.append((char) i);
            }
            String[] extension = filename.split("\\.");
            String filePath = System.getProperty("user.home") + "\\" + filename;
            switch (extension[extension.length - 1]) {
                case "txt":
                case "java":
                case "php":
                case "c":
                case "cpp":
                case "xml":
                    // Open with Notepad
                    Runtime.getRuntime().exec(new String[] { "notepad", filePath });
                    break;
                case "docx":
                    // Open with Word
                    Runtime.getRuntime().exec(new String[] { "cmd", "/c", "start", "winword", filePath });
                    break;
                case "pdf":
                case "pptx":
                    // Open with PowerPoint
                    Runtime.getRuntime().exec(new String[] { "cmd", "/c", "start", "powerpnt", filePath });
                    break;
                default:
                    // Open with the default application
                    Runtime.getRuntime().exec(new String[] { "cmd", "/c", "start", filePath });
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void retrieveMessage(String filename, ArrayList<Integer> inc) throws RemoteException {
        JLabel label = new JLabel("<HTML><U><font size=\"4\" color=\"#365899\">" + filename + "</font></U></HTML>");
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    FileOutputStream out;
                    String separator;
                    if (System.getProperty("os.name").startsWith("Linux")
                            || System.getProperty("os.name").startsWith("MacOS"))
                        separator = "/";
                    else
                        separator = "\\";
                    out = new FileOutputStream(System.getProperty("user.home") + separator + filename);
                    String[] extension = filename.split("\\.");
                    for (int i = 0; i < inc.size(); i++) {
                        int cc = inc.get(i);
                        if (extension[extension.length - 1].equals("txt") ||
                                extension[extension.length - 1].equals("java") ||
                                extension[extension.length - 1].equals("php") ||
                                extension[extension.length - 1].equals("c") ||
                                extension[extension.length - 1].equals("cpp") ||
                                extension[extension.length - 1].equals("xml") ||
                                extension[extension.length - 1].equals("mp3") ||
                                extension[extension.length - 1].equals("mp4") ||
                                extension[extension.length - 1].equals("pdf") ||
                                extension[extension.length - 1].equals("ppt") ||
                                extension[extension.length - 1].equals("pptx") ||
                                extension[extension.length - 1].equals("docx") ||
                                extension[extension.length - 1].equals("xlsx"))
                            out.write((char) cc);
                        else {
                            out.write((byte) cc);
                        }
                    }
                    out.flush();
                    out.close();

                    // Automatically open the file if it is a text file
                    if (extension[extension.length - 1].equals("txt") ||
                            extension[extension.length - 1].equals("java") ||
                            extension[extension.length - 1].equals("php") ||
                            extension[extension.length - 1].equals("c") ||
                            extension[extension.length - 1].equals("cpp") ||
                            extension[extension.length - 1].equals("xml")) {
                        openTextFile(filename, inc);
                    } else if (extension[extension.length - 1].equals("jpg") ||
                            extension[extension.length - 1].equals("jpeg") ||
                            extension[extension.length - 1].equals("png") ||
                            extension[extension.length - 1].equals("gif")) {
                        openImageFile(filename, inc);
                    } else if (extension[extension.length - 1].equals("mp3")) {
                        openAudioFile(filename, inc);
                    }
                } catch (FileNotFoundException ex) {
                    System.out.println("Error: " + ex.getMessage());
                } catch (IOException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        jpanel.add(label);
        jpanel.repaint();
        jpanel.revalidate();
    }

    public void sendMessage(List<String> list) {
        try {
            server.broadcastMessage(name + " : " + input.getText(), list);

        } catch (RemoteException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public void sendRemoveMessage() {
        try {
            server.broadcastMessage(name + " : " + "has left the conversation", new ArrayList<>());
        } catch (RemoteException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void closeChat(String message) throws RemoteException {
        input.setEditable(false);
        input.setEnabled(false);
        JOptionPane.showMessageDialog(new JFrame(), message, "Alert", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void openChat() throws RemoteException {
        input.setEditable(true);
        input.setEnabled(true);
    }

}
