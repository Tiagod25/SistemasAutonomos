package Team;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

public class InfoRobot {

    final String nome;
    private double x;
    private double y;
    private double last_x;
    private double last_y;
    private double velocidade;
    private double bearing;
    private double heading;
    private double energy;
    private boolean isTeammate;

    public InfoRobot(String nome, double x, double y, int vel, double bearing, double head, double last_x,double last_y, double energy, boolean isTeammate) {
        this.nome = nome;
        this.x=x;
        this.y=y;
        this.velocidade=vel;
        this.bearing=bearing;
        this.heading=head;
        this.last_x=last_x;
        this.last_y=last_y;
        this.energy=energy;
        this.isTeammate = isTeammate;
    }

    public InfoRobot(String nome){
        this.nome = nome;
        this.x=0;
        this.y=0;
        this.velocidade=0;
        this.bearing=0;
        this.heading=0;
        this.last_x=0;
        this.last_y=0;
        this.energy=0;
        this.isTeammate = false;
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

    public double getLast_x(){
        return this.last_x;
    }

    public double getLast_y(){
        return this.last_y;
    }

    public double getEnergy(){
        return this.energy;
    }

    public boolean isTeammate() {
        return this.isTeammate;
    }

    public InfoRobot(TeamRobot r,ScannedRobotEvent e){

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
        this.energy=e.getEnergy();
        this.isTeammate = r.isTeammate(this.nome);

    }

    public void updateInfoRobot(Point2D ponto, ScannedRobotEvent e){

        this.last_x=x;
        this.last_y=y;

        //Atualziar as Coordenadas
        this.x=ponto.getX();
        this.y=ponto.getY();

        //Update velocidade
        this.velocidade=e.getVelocity();
        //Update Bearing
        this.bearing=e.getBearing();
        this.heading=e.getHeading();
        this.energy=e.getEnergy();
    }
}
