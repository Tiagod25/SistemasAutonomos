package AdvancedRobot;

import java.awt.geom.*;
import robocode.*;
import robocode.util.Utils;
import standardOdometer.Odometer;

public class CircumNavigator1 extends AdvancedRobot {

    //Variáveis de classe
    private double distanciaPercorrida=0;
    private double x_partida = 18;
    private double y_partida = 18;
    private double x_atual;
    private double y_atual;
    private double x_ultimo= 18;
    private double y_ultimo=18;
    private Odometer odometer = new Odometer("isRacing",this);
    private int seen=0;
    private boolean emMovimento;
    private Point2D target;


    public  void run(){
        //Mover para a posicao de partida.
        move(x_partida,y_partida);
        turnLeft(this.getHeading());

        // Inicializacao dos Odometros
        addCustomEvent(odometer);
        addCustomEvent(new Condition("emMovimento") {
            @Override
            public boolean test() {
                return (getTime()!=0);
            }
        });


        while(true){
            turnRight(2);
            if(this.emMovimento) {
                if (this.seen == 3) {
                    move(18, 18);
                    this.emMovimento=false;
                    turnLeft(this.getHeading());
                }
                if (this.getX() <= 18 && this.getY() <= 18 && this.seen == 3) {
                    System.out.println("Distancia total percorrida: " + this.distanciaPercorrida);
                    System.out.println("Distancia exata " + this.odometer.getRaceDistance());
                    this.emMovimento = false;
                } else if (this.seen > 3) {
                    System.out.println("MInha posicao: " + this.getX() + " , " + this.getY());
                }
            }
        }

    }

    public void onScannedRobot(ScannedRobotEvent e) {
        if(emMovimento){
            double angle = Math.toDegrees(Math.asin((30/e.getDistance()))) + 2;
            turnRight(e.getBearing() - angle);
            ahead(e.getDistance() + 16);
            setTurnRight(60);
            // sqrt(20^2 + 30^2)
            ahead(35);

            System.out.println("Vi mais um tanque");
            this.seen++;
        }
    }

    public void onCustomEvent(CustomEvent ev) {
        Condition cd = ev.getCondition();
        if(cd.getName().equals("emMovimento")){
            this.x_atual = getX();
            this.y_atual = getY();
            if(this.x_atual==this.x_partida && this.y_atual==this.y_partida && this.seen<3)
                emMovimento=true;
            if(emMovimento){
                double dist = Math.sqrt(Math.pow((this.x_atual -this.x_ultimo),2) + Math.pow((this.y_atual - this.y_ultimo), 2));
                this.distanciaPercorrida += dist;
            }
            this.x_ultimo = getX();
            this.y_ultimo = getY();
        }

        if(cd.getName().equals("IsRacing"))
            this.odometer.getRaceDistance();
    }

    public void onHitRobot(HitRobotEvent e){
        // apenas fazer algo se o robot for parado, o que apenas acontece quando o sistema determina que a colisão foi a culpa dele
        if (e.isMyFault() == true) {
            setTurnRight(45);
            back(100);
        }
        // mover para target
        move(this.target.getX(), this.target.getY());
    }

    public void move(double x,double y){
        this.target = new Point2D.Double(x, y);
        if(this.seen == 3){
            System.out.println("########## PRIMEIRO ######## \nDistancia total percorrida: " + this.distanciaPercorrida);
            System.out.println("Distancia exata " + this.odometer.getRaceDistance());
        }

        double angle = Math.toDegrees(Math.atan2(this.getX()-x, this.getY()-y));
        this.turnRight(angle - this.getHeading());
        back(Math.sqrt(Math.pow(this.getX()-x, 2) + Math.pow(this.getY()-y, 2))-1);

        if(this.seen == 3){
            System.out.println("########## SEGUNDO  ######## \nDistancia total percorrida: " + this.distanciaPercorrida);
            System.out.println("Distancia exata " + this.odometer.getRaceDistance());
        }

        angle = Math.toDegrees(Math.atan2(this.getX()-x, this.getY()-y));
        this.turnRight(angle - this.getHeading());
        back(Math.sqrt(Math.pow(this.getX()-x, 2) + Math.pow(this.getY()-y, 2)));
    }
}
