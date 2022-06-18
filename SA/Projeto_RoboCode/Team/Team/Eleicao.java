package Team;

import java.io.Serializable;

public class Eleicao implements Serializable {


    //Este sinal qd for -1 vai significar que o lider atual morreu
    // De seguida, envia uma mensagem em broadcast a aviasr q morreu
    //OS team8s qd recebem Eleicao.getSignal == -1 , vao avisar os outros da sua energia para ser eleito novo lider
    private double signal;


    public Eleicao(){
        this.signal=-1;
    }

    public Eleicao(double sig){
        this.signal=sig;
    }

    public double getSignal() {
        return this.signal;
    }


    public void setSignal(double signal) {
        this.signal = signal;
    }


}
