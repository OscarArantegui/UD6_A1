import java.awt.*;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

public class MasterMindUI {

    // ----- Constants -----
    private static final int PIN_SIZE = 10;
    private static final int GUESS_SIZE = 35;
    private static final int BUTTON_SIZE = 50;
    // Colors
    private static final Color BG_COLOR = new Color(141, 69, 220);
    private static final Color BASE_COLOR = new Color(187, 183, 172);
    private Color selectedColor = null;
    // Game logic
    private final MasterMindLogic LOGIC;

    // Stores each row of guessed colors (4 circles per row)
    private ArrayList<Circle[]> guessRows = new ArrayList<>();

    // Stores each row of feedback pins (black/white pins for each guess)
    private ArrayList<Circle[]> pinRows = new ArrayList<>();

    private int currentRow = 0;

    // Circle class for rounded objects
    private static class Circle extends JButton {
        private Color color;
        private final int diameter;

        public Circle(Color color, int diameter, boolean clickable) {
            this.color = color;
            this.diameter = diameter;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setEnabled(clickable);
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


    // ----- Helper functions -----
    // Create styled JButton
    private JButton createStyledButton(String text, Color bg, int size) {
        JButton b = new JButton(text);
        b.setPreferredSize(new Dimension(size, size));
        b.setBackground(bg);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        return b;
    }

    // Pin panel for feedback
    private JPanel createPinPanel() {
        JPanel pinPanel = new JPanel();
        Circle[] pinArray = new Circle[4];

        pinPanel.setBackground(BG_COLOR);
        pinPanel.setLayout(new GridLayout(2, 2, 5, 5));

        for (int j = 0; j < 4; j++) {
            Circle pin = new Circle(BASE_COLOR, PIN_SIZE, false);
            pinArray[j] = pin;
            pinPanel.add(pin);
        }

        pinRows.add(pinArray);

        return pinPanel;
    }

    // Guess slots panel
    private JPanel createGuessPanel() {
        JPanel guessPanel = new JPanel();
        Circle[] guessArray = new Circle[4];
        guessPanel.setBackground(BG_COLOR);
        guessPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        for (int j = 0; j < 4; j++) {
            Circle slot = new Circle(BASE_COLOR, GUESS_SIZE, true);
            guessArray[j] = slot;
            int rowIndex = guessRows.size();  // save the row index before adding
            slot.addActionListener(e -> {
                // Only allow coloring the CURRENT row
                if (rowIndex == currentRow && selectedColor != null) {
                    slot.setCircleColor(selectedColor);
                }
            });

            guessPanel.add(slot);
        }
        guessRows.add(guessArray);
        return guessPanel;
    }

    // Round row,
    private JPanel createRoundRow() {
        JPanel roundPanel = new JPanel();
        roundPanel.setBackground(BG_COLOR);
        roundPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 0));

        roundPanel.add(createPinPanel());
        roundPanel.add(createGuessPanel());

        return roundPanel;
    }

    // Label showing selected color
    private JLabel createColorDisplayLabel(Color bg, int size) {
        JLabel label = new JLabel("Selected", SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(bg);
        label.setPreferredSize(new Dimension(size, size));
        return label;
    }

    private void colorPins(Circle[] pins, MasterMindLogic.Result result) {
        int blacks = result.blacks;
        int whites = result.whites;

        if (blacks == 4) {
            JOptionPane.showMessageDialog(null, "You guessed it!");
            return;
        } else {
            JOptionPane.showMessageDialog(null, "Black: " + blacks + ". White: " + whites);
        }
        if (currentRow == 9) {
            JOptionPane.showMessageDialog(null, "You lost, the answer was: " + LOGIC.showSecret());
        }
        for (int i = 0; i < 4; i++) {
            if (blacks > 0) {
                pins[i].setCircleColor(Color.BLACK);
                blacks--;
            } else if (whites > 0) {
                pins[i].setCircleColor(Color.WHITE);
                whites--;
            }
        }
    }

    // Bottom panel with colors & button
    private JPanel createBottomPanel(Color[] colors, String[] labels) {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(BG_COLOR);
        bottomPanel.setLayout(new BorderLayout(10, 0));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(BG_COLOR);
        controlPanel.setLayout(new GridLayout(1, 2, 10, 0));
        controlPanel.setPreferredSize(new Dimension(200, 50));

        JButton checkBtn = createStyledButton("Check", BASE_COLOR, BUTTON_SIZE);
        checkBtn.addActionListener(e -> {
            Circle[] currentGuess = guessRows.get(currentRow);

            // Check if any slot is still BASE_COLOR
            boolean allFilled = true;
            for (Circle slot : currentGuess) {
                if (slot.getColor().equals(BASE_COLOR)) {
                    allFilled = false;
                    break;
                }
            }

            if (allFilled) {
                Circle[] current = guessRows.get(currentRow);
                Color[] colorsRound = new Color[current.length];

                for (int i = 0; i < current.length; i++) {
                    colorsRound[i] = current[i].color;
                }

                colorPins(pinRows.get(currentRow), LOGIC.checkGuess(colorsRound));
                // Move on to the next row
                currentRow++;
            } else {
                // Show warning that row is incomplete
                JOptionPane.showMessageDialog(null, "Please fill all slots before checking!");
            }
        });

        JLabel selected = createColorDisplayLabel(BASE_COLOR, BUTTON_SIZE);

        controlPanel.add(checkBtn);
        controlPanel.add(selected);

        JPanel colorPanel = new JPanel();
        colorPanel.setBackground(BG_COLOR);
        colorPanel.setLayout(new GridLayout(1, colors.length, 0, 0));

        for (int i = 0; i < colors.length; i++) {
            JButton btn = createStyledButton(labels[i], colors[i], BUTTON_SIZE);
            final Color chosen = colors[i];
            // Change the color of selected label
            btn.addActionListener(e -> {
                selectedColor = chosen;
                selected.setBackground(selectedColor);
            });

            colorPanel.add(btn);
        }

        bottomPanel.add(colorPanel, BorderLayout.WEST);
        bottomPanel.add(controlPanel, BorderLayout.EAST);

        return bottomPanel;
    }

    // Main UI
    public MasterMindUI(Color[] colors, String[] labels, int rounds, MasterMindLogic logic) {
        this.LOGIC = logic;

        // Main frame
        JFrame frame = new JFrame("MasterMind");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        // Frame with rounds
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(BG_COLOR);
        centerPanel.setLayout(new GridLayout(rounds, 1, 0, 0));
        // Creating rounds
        for (int i = 0; i < rounds; i++) {
            centerPanel.add(createRoundRow());
        }
        frame.add(centerPanel, BorderLayout.CENTER);
        // Creating panel with buttons
        frame.add(createBottomPanel(colors, labels), BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }
}
