/**
 * Unit tests for Game History functionality
 * Tests the logic for showing the last 5 games from the dashboard
 *
 * @jest-environment jsdom
 */

// Mock the game history logic from dashboard.js
function updateGameHistory(previousScoreboards) {
    if (!previousScoreboards || previousScoreboards.length === 0) {
        return { message: 'No previous games', games: [] };
    }

    // Get the first 5 games (newest games are at the start of the array)
    // Array structure: [newest, newer, new, old, older, oldest...]
    const last5Games = previousScoreboards.slice(0, 5);
    const totalGames = previousScoreboards.length;
    
    const games = last5Games.map((scoreboard, index) => {
        // Calculate the actual game number (newest games are at start of array)
        // index 0 = newest game, index 1 = second newest, etc.
        const gameNumber = totalGames - index;
        return {
            gameNumber: gameNumber,
            scoreboard: scoreboard,
            players: scoreboard.map(player => ({
                name: player.name,
                score: player.score,
                rank: player.rank
            }))
        };
    });
    
    return { games: games };
}

// Helper function to create mock game data
function createMockScoreboards(totalGames) {
    const scoreboards = [];
    
    // Create games in reverse order (newest first in array)
    for (let gameNum = totalGames; gameNum >= 1; gameNum--) {
        const scoreboard = [
            { 
                name: 'CCPlayer', 
                score: 30 + gameNum, 
                rank: 1,
                playerType: 'player.CCPlayer'
            },
            { 
                name: 'TestPlayer', 
                score: 20 + gameNum, 
                rank: 2,
                playerType: 'player.TestPlayer'
            },
            { 
                name: 'SimplePlayer', 
                score: 10 + gameNum, 
                rank: 3,
                playerType: 'player.SimplePlayer'
            }
        ];
        scoreboards.push(scoreboard);
    }
    
    return scoreboards;
}

// Jest Test Suite
describe('Game History Logic', () => {

    test('should show last 5 games when more than 5 games exist', () => {
        // Given: 8 total games
        const mockData = createMockScoreboards(8);

        // When
        const result = updateGameHistory(mockData);

        // Then
        const expectedGameNumbers = [8, 7, 6, 5, 4]; // Last 5 games
        const actualGameNumbers = result.games.map(g => g.gameNumber);

        expect(actualGameNumbers).toEqual(expectedGameNumbers);
        expect(result.games).toHaveLength(5);
    });

    test('should show all games when less than 5 games exist', () => {
        // Given: 3 total games
        const mockData = createMockScoreboards(3);

        // When
        const result = updateGameHistory(mockData);

        // Then
        const expectedGameNumbers = [3, 2, 1]; // All 3 games
        const actualGameNumbers = result.games.map(g => g.gameNumber);

        expect(actualGameNumbers).toEqual(expectedGameNumbers);
        expect(result.games).toHaveLength(3);
    });

    test('should show exactly 5 games when exactly 5 games exist', () => {
        // Given: 5 total games
        const mockData = createMockScoreboards(5);

        // When
        const result = updateGameHistory(mockData);

        // Then
        const expectedGameNumbers = [5, 4, 3, 2, 1]; // All 5 games
        const actualGameNumbers = result.games.map(g => g.gameNumber);

        expect(actualGameNumbers).toEqual(expectedGameNumbers);
        expect(result.games).toHaveLength(5);
    });

    test('should show correct scores for each game', () => {
        // Given: 3 games with different scores
        const mockData = createMockScoreboards(3);

        // When
        const result = updateGameHistory(mockData);

        // Then - verify each game has different scores
        const game3Scores = result.games[0].players.map(p => p.score); // [33, 23, 13]
        const game2Scores = result.games[1].players.map(p => p.score); // [32, 22, 12]
        const game1Scores = result.games[2].players.map(p => p.score); // [31, 21, 11]

        expect(game3Scores).toEqual([33, 23, 13]);
        expect(game2Scores).toEqual([32, 22, 12]);
        expect(game1Scores).toEqual([31, 21, 11]);

        // Verify all games have different scores
        expect(game3Scores).not.toEqual(game2Scores);
        expect(game2Scores).not.toEqual(game1Scores);
        expect(game3Scores).not.toEqual(game1Scores);
    });

    test('should handle empty game list', () => {
        // Given: no games
        const mockData = [];

        // When
        const result = updateGameHistory(mockData);

        // Then
        expect(result.message).toBe('No previous games');
        expect(result.games).toHaveLength(0);
    });

    test('should handle large number of games (show only last 5)', () => {
        // Given: 20 total games
        const mockData = createMockScoreboards(20);

        // When
        const result = updateGameHistory(mockData);

        // Then
        const expectedGameNumbers = [20, 19, 18, 17, 16]; // Last 5 games
        const actualGameNumbers = result.games.map(g => g.gameNumber);

        expect(actualGameNumbers).toEqual(expectedGameNumbers);
        expect(result.games).toHaveLength(5);
    });

});

// Export for testing (Jest will handle test execution)
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { updateGameHistory, createMockScoreboards };
}
