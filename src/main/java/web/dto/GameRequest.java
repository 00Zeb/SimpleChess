package web.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to start a new game tournament")
public record GameRequest(
    @Schema(description = "List of player types to include in the tournament", 
            example = "[\"player.SimplePlayer\", \"player.TestPlayer\"]")
    @NotEmpty(message = "At least one player must be specified")
    @Size(min = 1, max = 10, message = "Tournament must have between 1 and 10 players")
    List<String> playerTypes,
    
    @Schema(description = "Number of games to play between each pair of players", 
            example = "5", minimum = "1", maximum = "100")
    @Min(value = 1, message = "Must play at least 1 game")
    int gamesPerPair
) {
}
