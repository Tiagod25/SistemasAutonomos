package Team;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;

import java.awt.*;
import java.awt.geom.Point2D;

public class InfoRobot {

    final String nome;
    private double x;
    private double y;
    private double velocidade;
    private double bearing;
    private double heading;


    public InfoRobot(String nome, double x, double y, int vel, double bearing, double head) {
        this.nome = nome;
        this.x=x;
        this.y=y;
        this.velocidade=vel;
        this.bearing=bearing;
        this.heading=head;
    }

    public InfoRobot(String nome){
        this.nome = nome;
        this.x=0;
        this.y=0;
        this.velocidade=0;
        this.bearing=0;
        this.heading=0;
    }



    public String getNome(){
        return this.nome;
    }

    public double getX(){
        return this.x;
    }


    public double getY(){
        return this.y;
    }


    public double getVelocidade(){
        return this.velocidade;
    }


    public double getBearing(){
        return this.bearing;
    }

    public double getHeading(){
        return heading;
    }



    public InfoRobot(AdvancedRobot r,ScannedRobotEvent e){

        //Criar com o nome do Robot dentro do RoboCode
        this.nome = e.getName();

        //Atualziar as Coordenadas
        Point2D.Double point = Util.findCoords(r,e.getBearing(),e.getDistance());
        this.x=point.x;
        this.y=point.y;


        //Update velocidade
        this.velocidade=e.getVelocity();
        //Update Bearing
        this.bearing=e.getBearing();
        this.heading=e.getHeading();



    }

    public void updateInfoRobot(Point2D ponto, ScannedRobotEvent e){

        //Atualziar as Coordenadas
        this.x=ponto.getX();
        this.y=ponto.getY();

        //Update velocidade
        this.velocidade=e.getVelocity();
        //Update Bearing
        this.bearing=e.getBearing();
        this.heading=e.getHeading();

    }


}
