package com.tn.softsys.blocoperatoire.factory;

import com.tn.softsys.blocoperatoire.domain.*;

public class ScoreFactory {

    public static Score create(ScoreType type) {

        return switch (type) {
            case ASA -> new ASA();
            case ALDRETE -> new Aldrete();
            case SOFA -> new SOFA();
            case APACHEII -> new APACHEII();
            case GLASGOW -> new Glasgow();
            case EVA -> new EVA();
        };
    }
}