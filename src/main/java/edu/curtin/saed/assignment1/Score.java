package edu.curtin.saed.assignment1;

/* *******************************************************************
* File:       Score.java
* Author:     G.G.T.Shashen
* Created:    23/08/2023
* Modified:   09/09/2022
* Desc:       Score class to control the score
***********************************************************************/
public class Score {
    private int score = 0;

    public int getScore() {
        return score;
    }

    public void addScore() {
        score += 10;
    }

    public void addDestroyBonus() {
        score += 100;
    }

}
