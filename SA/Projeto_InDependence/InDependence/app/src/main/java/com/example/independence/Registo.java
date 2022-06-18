package com.example.independence;

import java.io.Serializable;

public class Registo implements Serializable {

    private int nNotifSociais;
    private int nNotifChatting;
    private int nNotifOutrasApps;
    private int nNotificaces;
    private int nAtivacoesEcra;
    private int nChamadasEfet;
    private int nChamadasRecebidas;
    private int nSMSEnviadas;
    private int nSMSRecebidas;
    private int nClicksHome;
    private int nClicksRecentes;
    private int wifi;
    private int dadosMoveis;
    private double tempo_ecra;
    private String resp_isDependent;

    public int getnNotificaces() {
        return nNotificaces;
    }

    public void setnNotificaces(int nNotificaces) {
        this.nNotificaces = nNotificaces;
    }

    public int getnAtivacoesEcra() {
        return nAtivacoesEcra;
    }

    public void setnAtivacoesEcra(int nAtivacoesEcra) {
        this.nAtivacoesEcra = nAtivacoesEcra;
    }

    public int getnChamadasEfet() {
        return nChamadasEfet;
    }

    public void setnChamadasEfet(int nChamadasFeitas) {
        this.nChamadasEfet = nChamadasFeitas;
    }

    public int getnChamadasRecebidas() {
        return nChamadasRecebidas;
    }

    public void setnChamadasRecebidas(int nChamadasRecebidas) {
        this.nChamadasRecebidas = nChamadasRecebidas;
    }


    public int getnSMSEnviadas() {
        return nSMSEnviadas;
    }

    public void setnSMSEnviadas(int nSMSEnviadas) {
        this.nSMSEnviadas = nSMSEnviadas;
    }

    public int getnSMSRecebidas() {
        return nSMSRecebidas;
    }

    public void setnSMSRecebidas(int nSMSRecebidas) {
        this.nSMSRecebidas = nSMSRecebidas;
    }

    public int getnClicksHome() {
        return nClicksHome;
    }

    public void setnClicksHome(int nClicksHome) {
        this.nClicksHome = nClicksHome;
    }

    public int getnClicksRecentes() {
        return nClicksRecentes;
    }

    public void setnClicksRecentes(int nClicksRecentes) {
        this.nClicksRecentes = nClicksRecentes;
    }


    public int getWifi() {
        return wifi;
    }

    public void setWifi(int wifi) {
        this.wifi = wifi;
    }

    public int getDadosMoveis() {
        return dadosMoveis;
    }

    public void setDadosMoveis(int dadosMoveis) {
        this.dadosMoveis = dadosMoveis;
    }

    public String getResp_isDependent() {
        return resp_isDependent;
    }

    public void setResp_isDependent(String resp_isDependent) {
        this.resp_isDependent = resp_isDependent;
    }



    public Registo() {
        this.nClicksRecentes=0;
        this.nClicksHome=0;
        this.nAtivacoesEcra=0;
        this.nSMSRecebidas=0;
        this.nSMSEnviadas=0;
        this.nChamadasEfet=0;
        this.nChamadasRecebidas=0;
        this.nNotificaces=0;
        this.tempo_ecra=0;
        this.nNotifSociais=0;
        this.nNotifChatting=0;
        this.nNotifOutrasApps=0;
        this.nNotificaces=0;
    }

    public void incNotifSocias(){
        this.nNotifSociais++;
    }

    public void incNotifChatting(){
        this.nNotifChatting++;
    }

    public void incNotifOutrasApps(){
        this.nNotifOutrasApps++;
    }

    public void incNNotif(){
        this.nNotificaces++;
    }

    public void incAtivEcra(){
        this.nAtivacoesEcra++;
    }

    public void incNChamReceb(){
        this.nChamadasRecebidas++;
    }

    public void incNChamEfet(){
        this.nChamadasEfet++;
    }

    public void incNSMSEnv(){
        this.nSMSEnviadas++;
    }

    public void incNSMSReceb(){
        this.nSMSRecebidas++;
    }

    public void incNClicksHome(){
        this.nClicksHome++;
    }


    public void incNClicksRecent(){
        this.nClicksRecentes++;
    }

    public Registo(int nNotifSociais, int nNotifChatting, int nNotifOutrasApps, int nNotificaces, int nAtivacoesEcra, int nChamadasEfet, int nChamadasRecebidas, int nSMSEnviadas, int nSMSRecebidas, int nClicksHome, int nClicksRecentes, int wifi, int dadosMoveis, long tempo_utili, String resp_isDependent) {
        this.nNotifSociais = nNotifSociais;
        this.nNotifChatting = nNotifChatting;
        this.nNotifOutrasApps = nNotifOutrasApps;
        this.nNotificaces = nNotificaces;
        this.nAtivacoesEcra = nAtivacoesEcra;
        this.nChamadasEfet = nChamadasEfet;
        this.nChamadasRecebidas = nChamadasRecebidas;
        this.nSMSEnviadas = nSMSEnviadas;
        this.nSMSRecebidas = nSMSRecebidas;
        this.nClicksHome = nClicksHome;
        this.nClicksRecentes = nClicksRecentes;
        this.wifi = wifi;
        this.dadosMoveis = dadosMoveis;
        this.tempo_ecra = tempo_utili;
        this.resp_isDependent = resp_isDependent;
    }

    public int getnNotifSociais() {
        return nNotifSociais;
    }

    public void setnNotifSociais(int nNotifSociais) {
        this.nNotifSociais = nNotifSociais;
    }

    public int getnNotifChatting() {
        return nNotifChatting;
    }

    public void setnNotifChatting(int nNotifChatting) {
        this.nNotifChatting = nNotifChatting;
    }

    public int getnNotifOutrasApps() {
        return nNotifOutrasApps;
    }

    public void setnNotifOutrasApps(int nNotifOutrasApps) {
        this.nNotifOutrasApps = nNotifOutrasApps;
    }

    public double getTempo_ecra() {
        return tempo_ecra;
    }

    public void setTempo_ecra(double tempo_utili) {
        this.tempo_ecra = tempo_utili;
    }

    public void adicionaTempoEcra(double tempo) {
        this.tempo_ecra += tempo;
    }
}


