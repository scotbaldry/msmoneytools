package com.scotbaldry.msmoneytools.gui;

import com.jhlabs.awt.ParagraphLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class OFXBuilderGUI extends JFrame {
    JTextArea m_outputArea = new JTextArea(20, 100);

    public OFXBuilderGUI(String title) throws HeadlessException {
        super(title);

        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        buildDirPanel();
        buildOutputPanel();
        buildButtonPanel();

        pack();
        setVisible(true);
    }

    private void buildOutputPanel() {
        JPanel outputPanel = new JPanel();

        outputPanel.setBorder(BorderFactory.createTitledBorder("Output"));
        outputPanel.setLayout(new BorderLayout());
        JScrollPane areaScrollPane = new JScrollPane(m_outputArea);
        outputPanel.add(areaScrollPane, BorderLayout.CENTER);
        this.getContentPane().add(outputPanel, BorderLayout.CENTER);
    }

    private void buildDirPanel() {
        JPanel dirPanel = new JPanel();
        dirPanel.setLayout(new ParagraphLayout());
        JLabel zenLabel = new JLabel("Zen Media Directory : ");
        JLabel iphoneLabel = new JLabel("iPhone Media Directory : ");
        JLabel mediaLabel = new JLabel("Media Directory : ");
        JTextField zenDir = new JTextField(30);
        JTextField iphoneDir = new JTextField(30);
        JTextField mediaDir = new JTextField(30);

        zenDir.setEditable(false);
        iphoneDir.setEditable(false);
        mediaDir.setEditable(false);

        dirPanel.add(zenLabel, ParagraphLayout.NEW_PARAGRAPH);
        dirPanel.add(zenDir);
        dirPanel.add(iphoneLabel, ParagraphLayout.NEW_PARAGRAPH);
        dirPanel.add(iphoneDir);
        dirPanel.add(mediaLabel, ParagraphLayout.NEW_PARAGRAPH);
        dirPanel.add(mediaDir);
        this.getContentPane().add(dirPanel, BorderLayout.NORTH);
    }

    private void buildButtonPanel() {
        JPanel buttonPanel = new JPanel();
        JButton exitButton = new JButton();
        JButton runButton = new JButton();

        runButton.setAction(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                startBusySafely();
                clear();
                //Thread t = new Thread(new CleanupRunnable(OFXBuilderGUI.this));
                //t.setDaemon(true);
                //t.run();
            }
        });

        exitButton.setAction(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        runButton.setText("Cleanup");
        exitButton.setText("Exit");

        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(exitButton);
        buttonPanel.add(runButton);
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    public void output(final String s) {
        runSafelyAsync(new Runnable() {
            public void run() {
                m_outputArea.append(s + "\n");
                invalidate();
            }
        });
    }

    public void clear() {
        runSafelyAsync(new Runnable() {
            public void run() {
                m_outputArea.setText("");
                invalidate();
            }
        });
    }

    private void runSafelyAsync(Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }

    private void startBusySafely() {
        Runnable r = new Runnable() {
            public void run() {
                getGlassPane().setVisible(true);
                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                //The requestFocus() call must be executed after all pending focus requests have been consumed
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        getGlassPane().requestFocus();
                    }
                });
            }
        };

        runSafelyAsync(r);
    }

    private void endBusySafely(final JComponent focusComponent) {
        Runnable r = new Runnable() {
            public void run() {
                getGlassPane().setVisible(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                if (focusComponent != null) {
                    //The requestFocus() call must be executed after all pending focus requests have been consumed
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            focusComponent.requestFocus();
                        }
                    });
                }
            }
        };

        runSafelyAsync(r);
    }

    /**
     * Runnable to handle asych execution of CleanupController.
     */
    private class CleanupRunnable implements Runnable {
        public void run() {
//                TVCleanup cleanupController = new TVCleanup();
//                try {
//                    cleanupController.run(m_outputConsole);
//                    endBusySafely(null);
//                }
//                catch (IOException e) {
//                    e.printStackTrace();
//                }
        }
    }

    public static void main(String[] args) {
        OFXBuilderGUI gui = new OFXBuilderGUI("MSMoney OFX Builder");
    }
}
