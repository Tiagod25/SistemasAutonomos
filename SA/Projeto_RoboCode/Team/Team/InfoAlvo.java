package Team;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class InfoAlvo implements Serializable {

    private String nome_alvo;
    private  double pos_x_alvo;
    private double pos_y_alvo;



    public InfoAlvo(){
        this.nome_alvo="";
        this.pos_x_alvo=0;
        this.pos_y_alvo=0;
    }



    public InfoAlvo(String nome, double pos_x, double pos_y){
        this.nome_alvo=nome;
        this.pos_x_alvo=pos_x;
        this.pos_y_alvo=pos_y;
    }



    public String getNome_alvo(){
        return this.nome_alvo;
    }


    public double getPos_x_alvo(){
        return this.pos_x_alvo;
    }

    public double getPos_y_alvo(){
        return this.pos_y_alvo;
    }


    public void setNome_alvo(String nome_alvo){
        this.nome_alvo=nome_alvo;
    }

    public void setPos_x_alvo(double pos_x_alvo){
        this.pos_x_alvo=pos_x_alvo;
    }


    public void setPos_y_alvo(double pos_y_alvo){
        this.pos_y_alvo=pos_y_alvo;
    }




}
