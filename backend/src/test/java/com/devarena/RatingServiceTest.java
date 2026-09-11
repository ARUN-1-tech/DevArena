package com.devarena;

import com.devarena.battle.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RatingServiceTest {

    private RatingService ratingService;

    @BeforeEach
    void setUp() {
        ratingService = new RatingService();
    }

    @Test
    @DisplayName("Equal rating match: Winner gets +16, Loser gets -16")
    void testEqualRatingMatch() {
        RatingService.RatingResult result = ratingService.calculateElo(1000, 1000, 1.0);

        assertThat(result.player1Delta()).isEqualTo(16);
        assertThat(result.player2Delta()).isEqualTo(-16);
        assertThat(result.player1NewRating()).isEqualTo(1016);
        assertThat(result.player2NewRating()).isEqualTo(984);
    }

    @Test
    @DisplayName("Higher rated player beating lower rated player gains fewer points")
    void testHigherBeatingLower() {
        RatingService.RatingResult result = ratingService.calculateElo(1200, 1000, 1.0);

        assertThat(result.player1Delta()).isLessThan(16);
        assertThat(result.player1Delta()).isGreaterThan(0);
        assertThat(result.player2Delta()).isEqualTo(-result.player1Delta());
    }

    @Test
    @DisplayName("Lower rated player beating higher rated player gains larger points (upset victory)")
    void testLowerBeatingHigher() {
        RatingService.RatingResult result = ratingService.calculateElo(1000, 1200, 1.0);

        assertThat(result.player1Delta()).isGreaterThan(16);
        assertThat(result.player2Delta()).isLessThan(-16);
    }

    @Test
    @DisplayName("Draw between equal ratings results in 0 delta")
    void testDrawEqualRatings() {
        RatingService.RatingResult result = ratingService.calculateElo(1000, 1000, 0.5);

        assertThat(result.player1Delta()).isZero();
        assertThat(result.player2Delta()).isZero();
        assertThat(result.player1NewRating()).isEqualTo(1000);
        assertThat(result.player2NewRating()).isEqualTo(1000);
    }

    @Test
    @DisplayName("Rating never falls below floor of 100")
    void testFloorRating() {
        RatingService.RatingResult result = ratingService.calculateElo(100, 1500, 0.0);

        assertThat(result.player1NewRating()).isGreaterThanOrEqualTo(100);
    }
}
