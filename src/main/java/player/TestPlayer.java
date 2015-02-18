package player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import controller.Board;
import controller.Move;
import controller.Piece;
import controller.Player;
import controller.Point;

@Service
public class TestPlayer extends Player {

	@Override
	public Move getMove(Board board, Player enemy) {
		List<Move> possibleMoves = new ArrayList<>();
		List<Piece> pieces = this.getPieces(board);
		
		for (Piece piece : pieces) {
			for (Point p : piece.getValidDestinationSet(board)) {
				possibleMoves.add(new Move(piece, p));
			}
		}
		//Adding a commment.
		//another comment.
		int rnd = new Random().nextInt(possibleMoves.size());
		return possibleMoves.get(rnd);
	}

}
