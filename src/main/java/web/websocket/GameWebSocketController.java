package web.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import web.dto.ScoreboardResponse;
import web.service.ChessGameService;

@Controller
public class GameWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChessGameService chessGameService;

    @Autowired
    public GameWebSocketController(SimpMessagingTemplate messagingTemplate, 
                                   ChessGameService chessGameService) {
        this.messagingTemplate = messagingTemplate;
        this.chessGameService = chessGameService;
    }

    @MessageMapping("/game/start")
    @SendTo("/topic/scoreboard")
    public ScoreboardResponse startGame() {
        // Run game asynchronously and broadcast result
        chessGameService.runNewGame().thenAccept(result -> {
            messagingTemplate.convertAndSend("/topic/scoreboard", result);
        });
        
        return chessGameService.getCurrentScoreboard();
    }

    @MessageMapping("/scoreboard/subscribe")
    @SendTo("/topic/scoreboard")
    public ScoreboardResponse subscribeToScoreboard() {
        return chessGameService.getCurrentScoreboard();
    }

    public void broadcastScoreboardUpdate(ScoreboardResponse scoreboard) {
        messagingTemplate.convertAndSend("/topic/scoreboard", scoreboard);
    }
}
