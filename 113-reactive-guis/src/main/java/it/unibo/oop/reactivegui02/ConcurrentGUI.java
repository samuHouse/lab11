package it.unibo.oop.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Second example of reactive GUI.
 */
@SuppressWarnings("PMD.AvoidPrintStackTrace")
public final class ConcurrentGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton stop = new JButton("stop");
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");

    public ConcurrentGUI(){
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        final UpDownAgent agent = new UpDownAgent();
        new Thread(agent).start();
        stop.addActionListener((e) -> agent.stopCounting());
        up.addActionListener((e)->agent.goUp());
        down.addActionListener((e)->agent.goDown());
    }

    public class UpDownAgent implements Runnable{

        private boolean stop = false;
        private volatile boolean up = true;
        private volatile boolean down = false;
        private int counter = 0;

        @Override
        public void run() {

            while(!stop){
                if (up && !down) {
                    try {
                        final var nextValue = Integer.toString(counter);
                        SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(nextValue));
                        counter++;
                        Thread.sleep(100);
                    } catch (InvocationTargetException | InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                else if (!up && down) {
                    try {
                        final var nextValue = Integer.toString(counter);
                        SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(nextValue));
                        counter--;
                        Thread.sleep(100);
                    } catch (InvocationTargetException | InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                else {
                    throw new IllegalStateException("Counter cannot decrease and increase at the same time!");
                }
            }

            
        }

        public void stopCounting() {
            this.stop = true;
        }

        public void goUp() {
            this.up = true;
            this.down = false;
        }

        public void goDown() {
            this.up = false;
            this.down = true;
        }
    }
}
