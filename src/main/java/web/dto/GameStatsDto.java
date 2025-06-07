package web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Game statistics and metadata")
public record GameStatsDto(
    @Schema(description = "Total number of games played", example = "42")
    int totalGames,
    
    @Schema(description = "Number of registered players", example = "3")
    int totalPlayers,
    
    @Schema(description = "Average game duration in milliseconds", example = "1250")
    long averageGameDuration,
    
    @Schema(description = "Last game duration in milliseconds", example = "1100")
    long lastGameDuration,
    
    @Schema(description = "Application uptime in milliseconds", example = "3600000")
    long uptime
) {
}
