/*
 * Copyright (C) 2019 Ryan Castelli
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ntropy.offline.mousetracking;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Testing mouse tracking in an application.
 *
 * @author NTropy
 * @version 4.19.2019
 * @since 4.19.2019
 */
public final class MouseTracker {

    /**
     * Window dimensions.
     */
    private static final int WIDTH = 1500, HEIGHT = 900;

    /**
     * Panel to track mouse with image.
     */
    private static CatPanel trackingPanel;

    /**
     * Mouse position.
     */
    private static int mouseX, mouseY;

    /**
     * Default constructor.
     */
    private MouseTracker() {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (InstantiationException | ClassNotFoundException
                | IllegalAccessException
                | UnsupportedLookAndFeelException exe) {
            System.err.println("Nimbus unavailable: " + exe);
        }

        JFrame mainFrame = new JFrame("Click To Meow!");
        mainFrame.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        mainFrame.setResizable(false);

        trackingPanel = new CatPanel();
        final int cursorSize = 16;
        BufferedImage blankCursorImg = new BufferedImage(cursorSize, cursorSize,
                BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                blankCursorImg, new Point(0, 0), "blank cursor");
        trackingPanel.setCursor(blankCursor);
        trackingPanel.addMouseMotionListener(new MouseHandler());
        trackingPanel.addMouseListener(new MouseHandler());

        mainFrame.add(trackingPanel);
        mainFrame.pack();
        mainFrame.setLocationByPlatform(true);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }

    /**
     * Create application thread.
     *
     * @param args
     *             command-line arguments; unused here
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
           MouseTracker frame = new MouseTracker();
        });
    }

    /**
     * Panel for image to follow mouse.
     */
    private final class CatPanel extends JPanel {

        /**
         * Cat image.
         */
        private BufferedImage cat;

        /**
         * Populate cat image on creation.
         */
        private CatPanel() {
            try {
                cat = ImageIO.read(new File("images\\cat.png"));
            } catch (IOException e) {
                System.err.println("Error reading from image file: " + e);
            }
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);
            doDrawing(g);
        }

        /**
         * Draw in cat image.
         * @param g
         *          Graphics of frame
         */
        private void doDrawing(final Graphics g) {
            g.drawImage(cat, mouseX, mouseY, null);
        }
    }

    /**
     * Handles all mouse events for the frame.
     */
    private final class MouseHandler implements MouseListener,
            MouseMotionListener {

        @Override
        public void mouseClicked(final MouseEvent me) {
            try {
                File soundFile = new File("sounds\\cat.wav");
                Clip clip = AudioSystem.getClip();
                AudioInputStream audioIn =
                        AudioSystem.getAudioInputStream(soundFile);
                clip.open(audioIn);
                clip.start();
            } catch (IOException e) {
                System.err.println("Error reading sound file: " + e);
            } catch (LineUnavailableException e) {
                System.err.println("Couldn't get audio line: " + e);
            } catch (UnsupportedAudioFileException e) {
                System.err.println("Unsupported audio format: " + e);
            }
        }

        @Override
        public void mousePressed(final MouseEvent me) {
        }

        @Override
        public void mouseReleased(final MouseEvent me) {
        }

        @Override
        public void mouseEntered(final MouseEvent me) {
        }

        @Override
        public void mouseExited(final MouseEvent me) {
        }

        @Override
        public void mouseDragged(final MouseEvent me) {
        }

        @Override
        public void mouseMoved(final MouseEvent me) {
            mouseX = me.getX();
            mouseY = me.getY();
            trackingPanel.repaint();
        }
    }
}
