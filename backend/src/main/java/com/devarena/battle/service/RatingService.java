package com.devarena.battle.service;

import org.springframework.stereotype.Service;

@Service
public class RatingService {

    public static final int K_FACTOR = 32;
    public static final int MIN_RATING = 100;

    public record RatingResult(
            int player1Delta,
            int player2Delta,
            int player1NewRating,
            int player2NewRating
    ) {}

    /**
     * Calculate Elo rating changes.
     * @param rating1 Current rating of player 1
     * @param rating2 Current rating of player 2
     * @param score1 Outcome for player 1: 1.0 (win), 0.5 (draw), 0.0 (loss)
     * @return Calculated delta and new ratings for both players
     */
    public RatingResult calculateElo(int rating1, int rating2, double score1) {
        double score2 = 1.0 - score1;

        double expected1 = 1.0 / (1.0 + Math.pow(10.0, (rating2 - rating1) / 400.0));
        double expected2 = 1.0 / (1.0 + Math.pow(10.0, (rating1 - rating2) / 400.0));

        int delta1 = (int) Math.round(K_FACTOR * (score1 - expected1));
        int delta2 = (int) Math.round(K_FACTOR * (score2 - expected2));

        // If win was achieved, guarantee at least +1 MMR gain
        if (score1 == 1.0 && delta1 <= 0) delta1 = 1;
        if (score2 == 1.0 && delta2 <= 0) delta2 = 1;

        int newRating1 = Math.max(MIN_RATING, rating1 + delta1);
        int newRating2 = Math.max(MIN_RATING, rating2 + delta2);

        return new RatingResult(delta1, delta2, newRating1, newRating2);
    }
}
