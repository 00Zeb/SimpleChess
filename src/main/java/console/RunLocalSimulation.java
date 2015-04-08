package console;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import controller.ChessGame;
import controller.Player;
import controller.Score;
import controller.ScoreFormatter;

public class RunLocalSimulation {

/*	public static void main(String[] args) throws Exception {
		List<Player> players = scanPlayerImplementations();
		ScoreFormatter scoreFormatter = new ScoreFormatter();
		ChessGame chessGame = new ChessGame(players);
		List<Score> scoreboard = scoreFormatter.score(chessGame.runGame());
		for (Score score : scoreboard) {
			System.out.println(score);
		}
	}
*/
	private static List<Player> scanPlayerImplementations() throws Exception {
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
				true);
		provider.addIncludeFilter(new AssignableTypeFilter(Player.class));
		Set<BeanDefinition> components = provider.findCandidateComponents("player");
		List<Player> players = new ArrayList<Player>();
		for (BeanDefinition component : components) {
			@SuppressWarnings("rawtypes")
			Class cls = Class.forName(component.getBeanClassName());
			players.add((Player) cls.newInstance());
		}
		return players;
	}
}
