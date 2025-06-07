package web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Player score information")
public record PlayerScoreDto(
    @Schema(description = "Player name", example = "SimplePlayer")
    String name,
    
    @Schema(description = "Player score", example = "150")
    int score,
    
    @Schema(description = "Player ranking position", example = "1")
    int rank,
    
    @Schema(description = "Player type/class", example = "player.SimplePlayer")
    String playerType,
    
    @Schema(description = "Additional reflection information", example = "Wins: 5, Losses: 2")
    String reflection,
    
    @Schema(description = "Win rate percentage", example = "71.4")
    double winRate,
    
    @Schema(description = "Total games played", example = "7")
    int gamesPlayed
) {
}
