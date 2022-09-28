/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.rphstudio.codingdojo.students;


import fr.rphstudio.codingdojo.game.Pod;
import fr.rphstudio.codingdojo.game.PodPlugIn;

/**
 * @author Romuald GRIGNON
 */
public class Student35 extends PodPlugIn {
    public Student35(Pod p) {
        super(p);
    }

    //-------------------------------------------------------
    // DECLARE YOUR OWN VARIABLES AND FUNCTIONS HERE


    // Vaisseau besion de Charge ?
    boolean inCharge = false;

    float Seuil = 30;

    // Fonction pour le mouvement et le rechargement du vaisseau
    void moveAndRechargeV2(float speed, float minBatLevel, float maxBatLevel) {

        // Récupere le niveau de baterie du vaisseau
        float batLevel = getShipBatteryLevel();

        // Détermine si le vaisseau doit se recharger
        if (batLevel < minBatLevel) {
            inCharge = true;
        }

        // Détermine si le vaisseau peut repatir du CCP
        if (inCharge && batLevel >= maxBatLevel) {
            inCharge = false;
        }


        // Initialise les co et récupere les co du vaisseau
        float x0 = getShipPositionX();
        float y0 = getShipPositionY();
        float x1;
        float y1;
        float x2;
        float y2;
        float x_1;
        float y_1;

        // Détermine les co du prochain CCP
        int NbCheckPoint = getNbRaceCheckPoints();
        if (inCharge) {
            int CCP = 1;
            float distVaiPoint = 100000;
            for (int i = 0; i < NbCheckPoint; i = i + 1) {
                if (isCheckPointCharging(i)) {
                    float a = getCheckPointX(i);
                    float b = getCheckPointY(i);
                    if (sqrt((a - x0) * (a - x0) + (b - y0) * (b - y0)) < distVaiPoint) {
                        CCP = i;
                        distVaiPoint = sqrt((a - x0) * (a - x0) + (b - y0) * (b - y0));
                    }
                }
            }
            x_1 = getCheckPointX((CCP + NbCheckPoint - 1) % NbCheckPoint);
            y_1 = getCheckPointY((CCP + NbCheckPoint - 1) % NbCheckPoint);
            x1 = getCheckPointX(CCP);
            y1 = getCheckPointY(CCP);
            x2 = getCheckPointX((CCP + 1) % NbCheckPoint);
            y2 = getCheckPointY((CCP + 1) % NbCheckPoint);
        }
        // Détermine les co du prchain CP
        else {
            int a = getNextCheckPointIndex();
            x_1 = getCheckPointX((a + NbCheckPoint - 1) % NbCheckPoint);
            y_1 = getCheckPointY((a + NbCheckPoint - 1) % NbCheckPoint);
            x1 = getCheckPointX(a);
            y1 = getCheckPointY(a);
            x2 = getCheckPointX((a + 1) % NbCheckPoint);
            y2 = getCheckPointY((a + 1) % NbCheckPoint);
        }

//        if (isCheckPointCharging(CCP)) {
//            float BatLevel = getShipBatteryLevel();
//            if (BatLevel <= 40) {
//                inCharge = true;
//            }
//        }

        // Calculer la longueur de X et Y
        float X_1Lenght = x0 - x_1;
        float Y_1Lenght = y0 - y_1;
        float Xlenght = x1 - x0;
        float Ylenght = y1 - y0;
        float X2lenght = x2 - x1;
        float Y2lenght = y2 - y1;

        // Orient le vaisseau vers le prochain P
        float angleCheckPoint = atan2(Ylenght, Xlenght) - getShipAngle();
        float NextAngleCP = atan2(Y2lenght, X2lenght) - getShipAngle();
        float compangle = atan2(getShipSpeedY(), getShipSpeedX()) - getShipAngle();

        if (angleCheckPoint > 180) {
            angleCheckPoint = angleCheckPoint - 360;
        } else if (angleCheckPoint < -180) {
            angleCheckPoint = angleCheckPoint + 360;
        }

        if (NextAngleCP > 180) {
            NextAngleCP = NextAngleCP - 360;
        } else if (NextAngleCP < -180) {
            NextAngleCP = NextAngleCP + 360;
        }
        if (compangle > 180) {
            compangle = compangle - 360;
        } else if (compangle < -180) {
            compangle = compangle + 360;
        }

        float Opticompangle = (angleCheckPoint - compangle * 0.4f);
        if (Opticompangle > 180) {
            Opticompangle = Opticompangle - 360;
        } else if (Opticompangle < -180) {
            Opticompangle = Opticompangle + 360;
        }


        // Calculer la distance des prochain P
        float PrevdistanceP = sqrt((X_1Lenght) * (X_1Lenght) + (Y_1Lenght) * (Y_1Lenght));
        float distanceP = sqrt((Xlenght) * (Xlenght) + (Ylenght) * (Ylenght));
        float Nexdist = sqrt((X2lenght) * (X2lenght) + (Y2lenght) * (Y2lenght));

        if (inCharge) {
            if (distanceP < 0.5f) {
                speed = -1;
            } else {
                speed = 0.3f;
                turn(angleCheckPoint);
            }
        } else if (getShipBatteryLevel() <= Seuil) {
            int cb = getNextCheckPointIndex();
            if (isCheckPointCharging(cb)) {
                if (distanceP < 1) {
                    inCharge = true;
                }
                turn(angleCheckPoint);
                speed = 0.3f;
            } else {
                turn(angleCheckPoint);
                speed = 1;
            }
        } else if (distanceP <= 3) {
            if (abs(getShipSpeedX()) > 4.5 || abs(getShipSpeedY()) > 4.5) {
                turn(NextAngleCP);
                speed = 0f;
            } else {
                turn(angleCheckPoint);
                speed = 1;
            }
        } /*else if (PrevdistanceP <= 2.5) {
            if (abs(getShipSpeedX()) > 2.5 || abs(getShipSpeedY()) > 2.5) {
                turn(Opticompangle);
                speed = 1;
            } else {
                turn(angleCheckPoint);
                speed = 1;
            }
        } else {
            turn(angleCheckPoint);
            speed = 1;
        }*/ else {
            // compensation par défaut
            turn(Opticompangle);
            speed = 1;
        }

        // Utilisation du Boost
        if (getShipBoostLevel() == 100) {
            if (distanceP >= 10 && abs(angleCheckPoint) <= 5.5f) {
                useBoost();
            } else if (Nexdist >= 10 && abs(NextAngleCP) <= 5.5f) {
                useBoost();
            }
        }


        // Permet au vaisseau d'avancer jusqu'au prochain P
        //turn(angleCheckPoint);

        accelerateOrBrake(speed);
    }

    // END OF VARIABLES/FUNCTIONS AREA
    //-------------------------------------------------------

    @Override
    public void process(int delta) {
        //-------------------------------------------------------
        // WRITE YOUR OWN CODE HERE

        setPlayerName("Ronflex");
        selectShip(35);
        setPlayerColor(5, 71, 106, 200);

        moveAndRechargeV2(1, 15, 85);

        // END OF CODE AREA
        //-------------------------------------------------------
    }

}
