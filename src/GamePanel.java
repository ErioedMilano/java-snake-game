import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
    static final int DELAY = 75;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 1;
    int applesEaten = 0;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;

    // Game states
    enum GameState { MENU, RUNNING, GAME_OVER }
    private GameState state = GameState.MENU;

    // Menu items
    private final String[] menuItems = {"START GAME", "EXIT"};
    private int currentMenuItem = 0;

    // Highscore
    private int highScore = 0;
    private static final String HIGHSCORE_FILE = "highscore.dat";

    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(new Color(20, 20, 30));
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        timer = new Timer(DELAY, this);

        // Load highscore
        loadHighScore();
    }

    public void startGame(){
        resetGame();
        state = GameState.RUNNING;
        running = true;
        timer.start();
    }

    public void resetGame() {
        bodyParts = 1;
        applesEaten = 0;
        direction = 'R';
        running = true;

        // Start in the center
        x[0] = SCREEN_WIDTH / 2;
        y[0] = SCREEN_HEIGHT / 2;

        newApple();
    }

    public void newApple(){
        appleX = random.nextInt((int) (SCREEN_WIDTH/UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT/UNIT_SIZE)) * UNIT_SIZE;
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        switch(state) {
            case MENU:
                drawMenu(g);
                break;
            case RUNNING:
                drawGame(g);
                break;
            case GAME_OVER:
                drawGameOver(g);
                break;
        }
    }

    private void drawMenu(Graphics g) {
        // Title
        g.setColor(new Color(50, 200, 100));
        g.setFont(new Font("Monospaced", Font.BOLD, 70));
        FontMetrics titleMetrics = g.getFontMetrics();
        String title = "SNAKE GAME";
        g.drawString(title, (SCREEN_WIDTH - titleMetrics.stringWidth(title)) / 2, 150);

        // Menu items
        g.setFont(new Font("Monospaced", Font.BOLD, 40));
        FontMetrics menuMetrics = g.getFontMetrics();

        for (int i = 0; i < menuItems.length; i++) {
            if (i == currentMenuItem) {
                g.setColor(new Color(255, 215, 0)); // Gold for selected
            } else {
                g.setColor(new Color(180, 180, 180)); // Gray for others
            }
            String item = menuItems[i];
            g.drawString(item, (SCREEN_WIDTH - menuMetrics.stringWidth(item)) / 2, 300 + i * 60);
        }

        // Highscore
        g.setColor(new Color(100, 200, 255));
        g.setFont(new Font("Monospaced", Font.BOLD, 30));
        String highScoreText = "HIGHSCORE: " + highScore;
        FontMetrics hsMetrics = g.getFontMetrics();
        g.drawString(highScoreText, (SCREEN_WIDTH - hsMetrics.stringWidth(highScoreText)) / 2, 500);

        // Instructions
        g.setColor(new Color(150, 150, 150));
        g.setFont(new Font("Monospaced", Font.PLAIN, 18));
        String instructions = "Use UP/DOWN arrows to navigate, ENTER to select";
        FontMetrics instrMetrics = g.getFontMetrics();
        g.drawString(instructions, (SCREEN_WIDTH - instrMetrics.stringWidth(instructions)) / 2, 550);
    }

    private void drawGame(Graphics g) {
        // Background grid
        g.setColor(new Color(40, 40, 50));
        for (int i = 0; i < SCREEN_HEIGHT/UNIT_SIZE; i++){
            g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
            g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
        }

        // Apple
        g.setColor(new Color(255, 50, 50));
        g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

        // Snake
        for (int i = 0; i < bodyParts; i++){
            if (i == 0){
                g.setColor(new Color(50, 200, 100)); // Head
            } else {
                g.setColor(new Color(45, 180, 0)); // Body
            }
            g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);

            // Snake border
            g.setColor(new Color(30, 30, 40));
            g.drawRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
        }

        // Score
        g.setColor(new Color(255, 215, 0));
        g.setFont(new Font("Monospaced", Font.BOLD, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, 40);
    }

    private void drawGameOver(Graphics g) {
        // Game Over text
        g.setColor(new Color(200, 50, 50));
        g.setFont(new Font("Monospaced", Font.BOLD, 75));
        FontMetrics gameOverMetrics = g.getFontMetrics();
        String gameOverText = "GAME OVER";
        g.drawString(gameOverText, (SCREEN_WIDTH - gameOverMetrics.stringWidth(gameOverText)) / 2, SCREEN_HEIGHT / 2 - 50);

        // Score
        g.setColor(new Color(255, 215, 0));
        g.setFont(new Font("Monospaced", Font.BOLD, 50));
        FontMetrics scoreMetrics = g.getFontMetrics();
        String scoreText = "Score: " + applesEaten;
        g.drawString(scoreText, (SCREEN_WIDTH - scoreMetrics.stringWidth(scoreText)) / 2, SCREEN_HEIGHT / 2 + 50);

        // Highscore
        g.setColor(new Color(100, 200, 255));
        g.setFont(new Font("Monospaced", Font.BOLD, 40));
        String highScoreText = "Highscore: " + highScore;
        FontMetrics hsMetrics = g.getFontMetrics();
        g.drawString(highScoreText, (SCREEN_WIDTH - hsMetrics.stringWidth(highScoreText)) / 2, SCREEN_HEIGHT / 2 + 120);

        // Restart instruction
        g.setColor(new Color(180, 180, 180));
        g.setFont(new Font("Monospaced", Font.PLAIN, 25));
        String restartText = "Press SPACE to return to menu";
        FontMetrics restartMetrics = g.getFontMetrics();
        g.drawString(restartText, (SCREEN_WIDTH - restartMetrics.stringWidth(restartText)) / 2, SCREEN_HEIGHT / 2 + 200);
    }

    public void move(){
        for (int i = bodyParts; i > 0; i--){
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        switch (direction){
            case 'U' -> y[0] = y[0] - UNIT_SIZE;
            case 'D' -> y[0] = y[0] + UNIT_SIZE;
            case 'L' -> x[0] = x[0] - UNIT_SIZE;
            case 'R' -> x[0] = x[0] + UNIT_SIZE;
        }
    }

    public void checkApple(){
        if ((x[0] == appleX && y[0] == appleY)){
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions(){
        // Check if head collides with body
        for (int i = bodyParts; i > 0; i--){
            if ((x[0] == x[i] && y[0] == y[i])){
                running = false;
            }
        }

        // Check border collisions
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT){
            running = false;
        }

        if (!running){
            timer.stop();
            state = GameState.GAME_OVER;

            // Update highscore
            if (applesEaten > highScore) {
                highScore = applesEaten;
                saveHighScore();
            }
        }
    }

    private void loadHighScore() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(HIGHSCORE_FILE))) {
            highScore = (int) ois.readObject();
        } catch (FileNotFoundException e) {
            // First run, no highscore yet
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveHighScore() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HIGHSCORE_FILE))) {
            oos.writeObject(highScore);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (state) {
                case MENU:
                    handleMenuInput(e);
                    break;
                case RUNNING:
                    handleGameInput(e);
                    break;
                case GAME_OVER:
                    handleGameOverInput(e);
                    break;
            }
        }

        private void handleMenuInput(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_UP) {
                currentMenuItem = (currentMenuItem - 1 + menuItems.length) % menuItems.length;
                repaint();
            } else if (key == KeyEvent.VK_DOWN) {
                currentMenuItem = (currentMenuItem + 1) % menuItems.length;
                repaint();
            } else if (key == KeyEvent.VK_ENTER) {
                if (currentMenuItem == 0) {
                    startGame();
                } else if (currentMenuItem == 1) {
                    System.exit(0);
                }
            }
        }

        private void handleGameInput(KeyEvent e) {
            int key = e.getKeyCode();

            switch (key) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') direction = 'L';
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') direction = 'R';
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') direction = 'U';
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') direction = 'D';
                    break;
                case KeyEvent.VK_ESCAPE:
                    state = GameState.MENU;
                    timer.stop();
                    repaint();
                    break;
            }
        }

        private void handleGameOverInput(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                state = GameState.MENU;
                repaint();
            }
        }
    }
}