/*import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Sweeper extends JFrame {
    private JButton button = new JButton("Press");
    private JTextField input = new JTextField("", 5);
    private JLabel label = new JLabel("Input:");
    private JRadioButton radio1 = new JRadioButton("Select this");
    private JRadioButton radio2 = new JRadioButton("Select that");
    private JCheckBox check = new JCheckBox("Check", false);
    private JMenu jmenu = new JMenu();


    public Sweeper() {
        super("Simple Example");
        this.setBounds(100,100,550,300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container container = this.getContentPane();
        container.setLayout(new GridLayout(3,2,2,2));
        container.add(label);
        container.add(input);

        ButtonGroup group = new ButtonGroup();
        group.add(radio1);
        group.add(radio2);
        container.add(radio1);

        radio1.setSelected(true);
        container.add(radio2);
        container.add(check);
        button.addActionListener(new ButtonEventListener());
        container.add(button);
    }

    class ButtonEventListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String message = "";
            message += "Button was pressed\n";
            message += "Text is " + input.getText() + "\n";
            message += (radio1.isSelected()?"Radio #1":"Radio #2")
                    + " is selected\n";
            message += "CheckBox is " + ((check.isSelected())
                    ?"checked":"unchecked");
            JOptionPane.showMessageDialog(null,
                    message,
                    "Output",
                    JOptionPane.PLAIN_MESSAGE);
        }
    }

    public static void main(String[] args) {
        Sweeper app = new Sweeper();
        app.setVisible(true);
    }
}*/

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sweeper.Box;
import sweeper.Coord;
import sweeper.Game;
import sweeper.Ranges;
import java.sql.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Sweeper extends JFrame {
    private JPanel panel;
    private JLabel label;
    private JButton button;

    //Game game;
    private Game game;
    private int IMAGE_SIZE = 50;
    private int ROWS = 10;
    private int COLS = 10;
    private int BOMBS = 15;
    private static final Logger log = LogManager.getLogger(Sweeper.class);
    private Font font = new Font("Verdana", Font.PLAIN, 20);

    public static void main(String[] args) {
        log.info("Apllication start");
        new Sweeper();
    }

    private Sweeper() {
        game = new Game(COLS, ROWS, BOMBS);
        game.start();
        setImages();
        initLabel();
        initPanel();
        initButton();
        initMainFrame();
    }

    private void initLabel() {
        label = new JLabel(getMessage());
        //Font font = new Font("Verdana", Font.PLAIN, 20);
        label.setFont(font);
        label.setHorizontalAlignment(JLabel.CENTER);
        add(label, BorderLayout.SOUTH);
    }

    public void initPanel() {
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (Coord coord : Ranges.getAllCoords())
                    g.drawImage((Image) game.getBox(coord).image,
                            coord.x * IMAGE_SIZE,
                            coord.y * IMAGE_SIZE,
                            Color.WHITE, this);

            }
        };
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX() / IMAGE_SIZE;
                int y = e.getY() / IMAGE_SIZE;
                Coord coord = new Coord(x, y);
                if (e.getButton() == MouseEvent.BUTTON1)
                    game.pressLeftButton(coord);
                if (e.getButton() == MouseEvent.BUTTON2)
                    game.start();
                if (e.getButton() == MouseEvent.BUTTON3)
                    game.pressRightButton(coord);
                label.setText(getMessage());
                panel.repaint();
            }
        });
        panel.setPreferredSize(new Dimension(Ranges.getSize().x * IMAGE_SIZE, Ranges.getSize().y * IMAGE_SIZE));

        add(panel);
    }

    private void initMainFrame() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sweeper");
        setResizable(false);
        setVisible(true);
        pack();
        setLocationRelativeTo(null);
    }

    private void initButton() {
        button = new JButton("Settings");
        button.addActionListener(new ButtonActionListener());
        add(button, BorderLayout.NORTH);
    }

    private Image getImage(String name) {
        String filename = "img/" + name + ".png";
        ImageIcon icon = new ImageIcon(getClass().getResource(filename));
        return icon.getImage();
    }

    private void setImages() {
        for (Box box : Box.values())
            box.image = getImage(box.name().toLowerCase());
        setIconImage(getImage("icon"));
    }

    private String getMessage() {
        switch (game.getState()) {
            case BOMBED:
                return "You lose!";
            case WINNER:
                return "You won!";
            default:
                if (game.getTotalFlaged() == 0)
                    return "Welcome!";
                return "Enjoy! Flaged " + game.getTotalFlaged() + " of " + game.getTotalBomb() + " bombs.";
        }
    }

    void setROWS(int ROWS) {
        this.ROWS = ROWS;
    }

    public void setCOLS(int COLS) {
        this.COLS = COLS;
    }

    public void setBOMBS(int BOMBS) {
        this.BOMBS = BOMBS;
    }

}

