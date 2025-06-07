package web.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Scoreboard response containing current and historical game results")
public record ScoreboardResponse(
    @Schema(description = "Timestamp when this scoreboard was generated", example = "2024-01-15T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime timestamp,
    
    @Schema(description = "Current game results")
    List<PlayerScoreDto> currentScoreboard,
    
    @Schema(description = "Total accumulated scores across all games")
    List<PlayerScoreDto> totalScores,
    
    @Schema(description = "Previous game results")
    List<List<PlayerScoreDto>> previousScoreboards,
    
    @Schema(description = "Game statistics")
    GameStatsDto gameStats
) {
}
