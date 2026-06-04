package com.tn.softsys.blocoperatoire.domain;

public enum StatutSalle {
    DISPONIBLE,
    PLANIFIEE,
    EN_INTERVENTION,
    MAINTENANCE,
    NETTOYAGE,
    FERMEE;

    public boolean isOperational() {
        return switch (this) {
            case DISPONIBLE, PLANIFIEE, EN_INTERVENTION -> true;
            case MAINTENANCE, NETTOYAGE, FERMEE -> false;
        };
    }

    public boolean isUnavailable() {
        return !isOperational();
    }
}
