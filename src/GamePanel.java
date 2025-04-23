import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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


    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MykeyAdapter());
        startGame();
    }
    public void startGame(){
        newAplle();
        running = true;
        timer = new Timer(DELAY,this);
        timer.start();

    }
    public void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        draw(graphics);
    }
    public void draw(Graphics graphics){

       if (running){

           /*
           for (int i = 0; i < SCREEN_HEIGHT/UNIT_SIZE; i++){
               graphics.drawLine(i*UNIT_SIZE,0,i*UNIT_SIZE,SCREEN_HEIGHT);
               graphics.drawLine(0,i*UNIT_SIZE,SCREEN_WIDTH,i*UNIT_SIZE);
           }
            */

           graphics.setColor(Color.red);
           graphics.fillOval(appleX, appleY,UNIT_SIZE,UNIT_SIZE);

           for (int i = 0; i < bodyParts; i++){
               if (i == 0){
                   graphics.setColor(Color.GREEN);
                   graphics.fillRect(x[i],y[i],UNIT_SIZE,UNIT_SIZE);
               }
               else {
                   graphics.setColor(new Color(45,180,0));
                   graphics.setColor(new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
                   graphics.fillRect(x[i],y[i],UNIT_SIZE,UNIT_SIZE);
               }
           }
           graphics.setColor(Color.red);
           graphics.setFont(new Font("Monospaced",Font.BOLD,40));
           FontMetrics metrics = getFontMetrics(graphics.getFont());
           graphics.drawString("Score: " + applesEaten,(SCREEN_WIDTH -metrics.stringWidth("Score: " + applesEaten))/2,graphics.getFont().getSize());
       }
       else {
           gameOver(graphics);
       }
    }
    public void newAplle(){
        appleX = random.nextInt((int) (SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
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
            newAplle();
        }
    }
    public void checkCollisions(){

        //This checks if head collides with body
        for (int i = bodyParts; i > 0;i--){
            if ((x[0] == x[i] && y[0] == y[i])){
                running = false;
            }
        }
        //Checks if head touches left border
        if (x[0] < 0){
            running =false;
        }
        //Checks if head touches right border
        if (x[0] > SCREEN_WIDTH){
            running = false;
        }
        //Checks if head touches top border
        if (y[0] < 0){
            running = false;
        }
        //Checks if head touches bottom border
        if (y[0] > SCREEN_HEIGHT){
            running = false;
        }
        if (!running){
            timer.stop();
        }
    }
    public void gameOver(Graphics graphics){

        //Score
        graphics.setColor(Color.red);
        graphics.setFont(new Font("Lucida Handwriting",Font.BOLD,40));
        FontMetrics metrics1 = getFontMetrics(graphics.getFont());
        graphics.drawString("Score: " + applesEaten,(SCREEN_WIDTH -metrics1.stringWidth("Score: " + applesEaten))/2,graphics.getFont().getSize());
        //GameOver text
        graphics.setColor(Color.red);
        graphics.setFont(new Font("Bradley Hand",Font.BOLD,75));
        FontMetrics metrics2 = getFontMetrics(graphics.getFont());
        graphics.drawString("Game Over",(SCREEN_WIDTH -metrics2.stringWidth("Game over"))/2,SCREEN_HEIGHT/2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (running){
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }
    public class MykeyAdapter extends KeyAdapter{

        @Override
        public void keyPressed(KeyEvent event){
            switch (event.getKeyCode()){
                case KeyEvent.VK_LEFT -> {
                    if (direction != 'R'){
                        direction = 'L';
                    }
                }
                case KeyEvent.VK_RIGHT -> {
                    if (direction != 'L'){
                        direction = 'R';
                    }
                }
                case KeyEvent.VK_UP -> {
                    if (direction != 'D'){
                        direction = 'U';
                    }
                }
                case KeyEvent.VK_DOWN -> {
                    if (direction != 'U'){
                        direction = 'D';
                    }
                }
            }
        }
    }
}
