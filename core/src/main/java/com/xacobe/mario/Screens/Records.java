package com.xacobe.mario.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Records {
    private Preferences prefs;

    // Constructor: se crea o se carga la preferencia con el nombre "MarioBrosRecords"
    public Records() {
        prefs = Gdx.app.getPreferences("MarioBrosRecords");
    }

    /**
     * Actualiza el registro del nivel indicado.
     * Solo se guarda si:
     * - El nuevo tiempo es menor que el registrado, o
     * - El tiempo es igual y el número de vidas es mayor.
     *
     * @param level Número del nivel.
     * @param time  Tiempo (por ejemplo, en segundos) con el que se terminó el nivel.
     * @param lives Número de vidas restantes.
     */
    public void updateRecord(int level, int time, int lives) {
        // Claves únicas para cada nivel
        String keyTime = "level" + level + "_time";
        String keyLives = "level" + level + "_lives";

        int recordedTime = prefs.getInteger(keyTime, Integer.MAX_VALUE);
        int recordedLives = prefs.getInteger(keyLives, 0);

        // Actualiza solo si el nuevo resultado es mejor
        if (time < recordedTime || (time == recordedTime && lives > recordedLives)) {
            prefs.putInteger(keyTime, time);
            prefs.putInteger(keyLives, lives);
            prefs.flush(); // Guarda los cambios en disco
        }
    }

    /**
     * Obtiene el registro almacenado para un nivel.
     */
    public String getRecord(int level) {
        String keyTime = "level" + level + "_time";
        String keyLives = "level" + level + "_lives";

        if (!prefs.contains(keyTime) || !prefs.contains(keyLives)) {
            return "Level " + level + ": No record";
        }
        int time = prefs.getInteger(keyTime);
        int lives = prefs.getInteger(keyLives);
        return "Level " + level + ": Time: " + time + " Lives: " + lives;
    }

    /**
     * Devuelve todos los registros en líneas separadas.
     */
    public String getAllRecords(int maxLevel) {
        StringBuilder sb = new StringBuilder();
        for (int level = 1; level <= maxLevel; level++) {
            sb.append(getRecord(level));
            if (level < maxLevel) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
