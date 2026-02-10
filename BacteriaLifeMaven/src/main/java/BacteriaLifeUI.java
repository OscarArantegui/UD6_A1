import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class BacteriaLifeUI {
    // Constants
    private final BacteriaLifeLogic LOGIC;
    private static final int BACTERIA_SIZE = 10;
    private static final Color BG_COLOR = new Color(141, 69, 220);
    private static final int DIMENSION = 30;
    private final JPanel genPanel;
    // Components promoted for testing
    private JFrame frame;
    private JButton startButton;
    private JLabel roundLabel;
    private Timer timer; // Reference to timer to stop it if needed
    // Current active gen
    private int[][] bacteriaGen;

    // Circle class for rounded objects (bacteria)
    static class Circle extends JButton {
        private Color color;
        private final int diameter;

        public Circle(Color color) {
            this.color = color;
            this.diameter = BACTERIA_SIZE;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setEnabled(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(color);
            g.fillOval(0, 0, diameter, diameter); // use diameter instead of getWidth()/getHeight() if you want consistent circles
            super.paintComponent(g);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(diameter, diameter);
        }

        public void setCircleColor(Color c) {
            this.color = c;
            repaint();
        }

        public Color getColor() {
            return color;
        }
    }

    // Generate a generation
    private JPanel generateGen() {
        JPanel gen = new JPanel();
        gen.setLayout(new GridLayout(DIMENSION, DIMENSION, 3, 3));
        gen.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        gen.setBackground(BG_COLOR);
        // Row
        for (int i = 0; i < DIMENSION; i++) {
            // Column
            for (int j = 0; j < DIMENSION; j++) {
                Color color = Color.WHITE;
                if (bacteriaGen[i][j] == 1) {
                    color = Color.BLACK;
                }
                Circle bacteria = new Circle(color);
                gen.add(bacteria);
            }
        }
        return gen;
    }

    // Refresh the grid after generating a new round
    private void refreshGenPanel() {
        genPanel.removeAll();
        genPanel.setLayout(new GridLayout(DIMENSION, DIMENSION, 3, 3));

        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                Color color = (bacteriaGen[i][j] == 1) ? Color.BLACK : Color.WHITE;
                genPanel.add(new Circle(color));
            }
        }

        genPanel.revalidate(); // To avoid bugs
        genPanel.repaint();
    }

    // A bottom panel with a round label and a start button
    private JPanel bottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(BG_COLOR);

        this.roundLabel = new JLabel();
        roundLabel.setText("Round: " + LOGIC.getRound());

        this.startButton = getStartButton(roundLabel);

        startButton.setPreferredSize(new Dimension(70, 50));
        startButton.setBackground(Color.WHITE);
        startButton.setContentAreaFilled(true);
        startButton.setBorderPainted(false);
        startButton.setFocusPainted(false);

        bottomPanel.add(roundLabel, BorderLayout.WEST);   // Left side
        bottomPanel.add(startButton, BorderLayout.EAST);  // Right side

        return bottomPanel;
    }
    // Extracted logic for testing. This represents ONE tick of the timer.
    void performEvolutionStep() {
        int[][] oldGen = deepCopy(bacteriaGen);
        int[][] newGen = LOGIC.generateNewGen(oldGen);

        if (BacteriaLifeLogic.checkStableGen(oldGen, newGen)) {
            if (timer != null) timer.stop();
            return;
        }

        // Move forward
        bacteriaGen = newGen;
        refreshGenPanel();
        roundLabel.setText("Round: " + LOGIC.getRound());
    }
    // Start button
    private JButton getStartButton(JLabel roundLabel) {
        JButton startButton = new JButton("Start");

        startButton.addActionListener(e -> {
            // If timer is already running, do nothing (or restart, depending on reqs)
            if (timer != null && timer.isRunning()) return;

            this.timer = new Timer(100, ev -> performEvolutionStep());
            timer.start();
        });
        return startButton;
    }

    // To copy the gen
    private int[][] deepCopy(int[][] bacteriaGen) {
        if (bacteriaGen == null) return null;
        int[][] copy = new int[bacteriaGen.length][];
        for (int i = 0; i < bacteriaGen.length; i++) {
            copy[i] = Arrays.copyOf(bacteriaGen[i], bacteriaGen[i].length);
        }
        return copy;
    }

    // Main
    public BacteriaLifeUI(BacteriaLifeLogic logic) {
        this.LOGIC = logic;
        this.bacteriaGen = LOGIC.generateInitialGen();

        // Main frame
        this.frame = new JFrame("BacteriaLife");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Add the gen
        this.genPanel = generateGen();
        frame.add(genPanel, BorderLayout.CENTER);

        // Add the bottom label
        frame.add(bottomPanel(), BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }
    // --- Getters for Testing ---
    public JFrame getFrame() { return frame; }
    public JButton getStartButton() { return startButton; }
    public JLabel getRoundLabel() { return roundLabel; }
    public JPanel getGenPanel() { return genPanel; }
    public int[][] getBacteriaGen() { return bacteriaGen; }
}
