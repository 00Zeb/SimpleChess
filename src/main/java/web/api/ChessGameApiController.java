package web.api;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import web.dto.GameRequest;
import web.dto.ScoreboardResponse;
import web.service.ChessGameService;

@RestController
@RequestMapping("/api/v1/chess")
@Tag(name = "Chess Game API", description = "REST API for chess tournament management")
public class ChessGameApiController {

    private final ChessGameService chessGameService;

    @Autowired
    public ChessGameApiController(ChessGameService chessGameService) {
        this.chessGameService = chessGameService;
    }

    @GetMapping("/scoreboard")
    @Operation(summary = "Get current scoreboard", 
               description = "Returns the current game results and statistics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved scoreboard"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ScoreboardResponse> getScoreboard() {
        ScoreboardResponse response = chessGameService.getCurrentScoreboard();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/scoreboard/cc")
    @Operation(summary = "Get CC scoreboard", 
               description = "Returns the scoreboard excluding Cybercom players")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved CC scoreboard"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ScoreboardResponse> getCCScoreboard() {
        ScoreboardResponse response = chessGameService.getCCScoreboard();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/game/run")
    @Operation(summary = "Run a new game tournament", 
               description = "Starts a new tournament with all available players")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Game completed successfully"),
        @ApiResponse(responseCode = "500", description = "Error running game")
    })
    public CompletableFuture<ResponseEntity<ScoreboardResponse>> runNewGame() {
        return chessGameService.runNewGame()
            .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/game/custom")
    @Operation(summary = "Run a custom game tournament", 
               description = "Starts a tournament with specified players and settings")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Custom game completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid game request"),
        @ApiResponse(responseCode = "500", description = "Error running custom game")
    })
    public CompletableFuture<ResponseEntity<ScoreboardResponse>> runCustomGame(
            @Valid @RequestBody GameRequest request) {
        return chessGameService.runCustomGame(request)
            .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/players")
    @Operation(summary = "Get available player types", 
               description = "Returns a list of all available player implementations")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved player types")
    })
    public ResponseEntity<List<String>> getAvailablePlayerTypes() {
        List<String> playerTypes = chessGameService.getAvailablePlayerTypes();
        return ResponseEntity.ok(playerTypes);
    }
}
