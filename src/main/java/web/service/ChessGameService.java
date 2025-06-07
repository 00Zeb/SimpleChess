package web.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import controller.ChessGame;
import controller.FileHandler;
import controller.HistoryData;
import controller.Player;
import controller.Score;
import controller.ScoreFormatter;
import web.dto.GameRequest;
import web.dto.GameStatsDto;
import web.dto.PlayerScoreDto;
import web.dto.ScoreboardResponse;

@Service
public class ChessGameService {

    private final List<Player> availablePlayers;
    private final ScoreFormatter scoreFormatter;
    private HistoryData historyData;
    private LocalDateTime lastGameTime;
    private long lastGameDuration;
    private final long startTime;

    @Autowired
    public ChessGameService(List<Player> players) {
        this.availablePlayers = players;
        this.scoreFormatter = new ScoreFormatter();
        this.startTime = System.currentTimeMillis();
        loadHistoryData();
    }

    private void loadHistoryData() {
        historyData = (HistoryData) FileHandler.get("HistoryData");
        if (historyData == null) {
            historyData = new HistoryData();
        }
    }

    public ScoreboardResponse getCurrentScoreboard() {
        return new ScoreboardResponse(
            lastGameTime != null ? lastGameTime : LocalDateTime.now(),
            convertToPlayerScoreDtos(historyData.getScore().isEmpty() ?
                List.of() : historyData.getScore().get(0)),
            convertEntriesToPlayerScoreDtos(historyData.getTotalScore()),
            historyData.getPreviousScoreboards().stream()
                .map(this::convertToPlayerScoreDtos)
                .collect(Collectors.toList()),
            createGameStats()
        );
    }

    public ScoreboardResponse getCCScoreboard() {
        return new ScoreboardResponse(
            lastGameTime != null ? lastGameTime : LocalDateTime.now(),
            convertToPlayerScoreDtos(historyData.getScore().isEmpty() ?
                List.of() : historyData.getScore().get(0)),
            convertEntriesToPlayerScoreDtos(historyData.getTotalScoreWithoutCybercomPlayers()),
            historyData.getPreviousScoreboards().stream()
                .map(this::convertToPlayerScoreDtos)
                .collect(Collectors.toList()),
            createGameStats()
        );
    }

    public CompletableFuture<ScoreboardResponse> runNewGame() {
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            
            ChessGame chessGame = new ChessGame(availablePlayers);
            Map<Class<? extends Player>, Integer> results = chessGame.runGame();
            List<Score> scoreboard = scoreFormatter.score(results);
            
            lastGameTime = LocalDateTime.now();
            lastGameDuration = System.currentTimeMillis() - startTime;
            
            historyData.addScoreboard(scoreboard);
            historyData.addTimestamp(lastGameTime.toString());
            FileHandler.set(historyData, "HistoryData");
            
            return getCurrentScoreboard();
        });
    }

    public CompletableFuture<ScoreboardResponse> runCustomGame(GameRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            // Implementation for custom games with specific players
            // This would require extending ChessGame to accept specific player types
            return runNewGame().join(); // For now, run standard game
        });
    }

    public List<String> getAvailablePlayerTypes() {
        return availablePlayers.stream()
            .map(player -> player.getClass().getName())
            .collect(Collectors.toList());
    }

    private List<PlayerScoreDto> convertToPlayerScoreDtos(List<Score> scores) {
        return scores.stream()
            .map(this::convertToPlayerScoreDto)
            .collect(Collectors.toList());
    }

    private List<PlayerScoreDto> convertEntriesToPlayerScoreDtos(List<Entry<String, Integer>> entries) {
        return entries.stream()
            .map(this::convertEntryToPlayerScoreDto)
            .collect(Collectors.toList());
    }

    private PlayerScoreDto convertToPlayerScoreDto(Score score) {
        return new PlayerScoreDto(
            score.getName(),
            score.getScore(),
            score.getPlace(), // rank from Score object
            score.getPlayer().getName(), // playerType - full class name
            score.getReflection(),
            calculateWinRate(score), // would need to implement
            calculateGamesPlayed(score) // would need to implement
        );
    }

    private PlayerScoreDto convertEntryToPlayerScoreDto(Entry<String, Integer> entry) {
        return new PlayerScoreDto(
            entry.getKey(),
            entry.getValue(),
            1, // rank - would need to calculate properly based on position in list
            entry.getKey(), // playerType - same as name for now
            "", // no reflection data for total scores
            0.0, // no win rate data for total scores
            0 // no games played data for total scores
        );
    }

    private GameStatsDto createGameStats() {
        return new GameStatsDto(
            historyData.getScore().size(),
            availablePlayers.size(),
            calculateAverageGameDuration(),
            lastGameDuration,
            System.currentTimeMillis() - startTime
        );
    }

    private double calculateWinRate(Score score) {
        // Placeholder - would need to track wins/losses
        return 0.0;
    }

    private int calculateGamesPlayed(Score score) {
        // Placeholder - would need to track individual games
        return 0;
    }

    private long calculateAverageGameDuration() {
        // Placeholder - would need to track game durations
        return lastGameDuration;
    }
}
