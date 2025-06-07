// JavaScript tests for dashboard game history functionality
// These would typically run with Jest or similar testing framework

describe('ChessDashboard Game History', () => {
    let dashboard;
    let mockData;

    beforeEach(() => {
        // Setup DOM
        document.body.innerHTML = `
            <div id="gameHistory"></div>
        `;
        
        // Create dashboard instance
        dashboard = new ChessDashboard();
        
        // Mock data with multiple games
        mockData = {
            timestamp: '2024-01-15T10:30:00',
            currentScoreboard: [
                { name: 'Player1', score: 100, rank: 1 },
                { name: 'Player2', score: 50, rank: 2 }
            ],
            totalScores: [],
            previousScoreboards: [],
            gameStats: { totalGames: 0, totalPlayers: 2 }
        };
    });

    describe('Game History Display Logic', () => {
        test('should show last 5 games when more than 5 games exist', () => {
            // Given - 8 previous games
            mockData.previousScoreboards = createMockGames(8);
            
            // When
            dashboard.updateGameHistory(mockData.previousScoreboards);
            
            // Then
            const historyContainer = document.getElementById('gameHistory');
            const gameCards = historyContainer.querySelectorAll('.card');
            
            expect(gameCards).toHaveLength(5);
            
            // Verify it shows the LAST 5 games (games 7,6,5,4,3 - most recent first)
            expect(gameCards[0].textContent).toContain('Game 8'); // Most recent
            expect(gameCards[4].textContent).toContain('Game 4'); // 5th most recent
        });

        test('should show all games when less than 5 games exist', () => {
            // Given - 3 previous games
            mockData.previousScoreboards = createMockGames(3);
            
            // When
            dashboard.updateGameHistory(mockData.previousScoreboards);
            
            // Then
            const historyContainer = document.getElementById('gameHistory');
            const gameCards = historyContainer.querySelectorAll('.card');
            
            expect(gameCards).toHaveLength(3);
        });

        test('should show "No previous games" when no games exist', () => {
            // Given - no previous games
            mockData.previousScoreboards = [];
            
            // When
            dashboard.updateGameHistory(mockData.previousScoreboards);
            
            // Then
            const historyContainer = document.getElementById('gameHistory');
            expect(historyContainer.textContent).toContain('No previous games');
        });

        test('should display games in reverse chronological order (most recent first)', () => {
            // Given - 6 previous games
            mockData.previousScoreboards = createMockGames(6);
            
            // When
            dashboard.updateGameHistory(mockData.previousScoreboards);
            
            // Then
            const historyContainer = document.getElementById('gameHistory');
            const gameCards = historyContainer.querySelectorAll('.card');
            
            // Should show games 6,5,4,3,2 (last 5, most recent first)
            expect(gameCards[0].textContent).toContain('Game 6');
            expect(gameCards[1].textContent).toContain('Game 5');
            expect(gameCards[2].textContent).toContain('Game 4');
            expect(gameCards[3].textContent).toContain('Game 3');
            expect(gameCards[4].textContent).toContain('Game 2');
        });
    });

    describe('Edge Cases', () => {
        test('should handle exactly 5 previous games', () => {
            // Given - exactly 5 previous games
            mockData.previousScoreboards = createMockGames(5);
            
            // When
            dashboard.updateGameHistory(mockData.previousScoreboards);
            
            // Then
            const historyContainer = document.getElementById('gameHistory');
            const gameCards = historyContainer.querySelectorAll('.card');
            
            expect(gameCards).toHaveLength(5);
        });

        test('should handle null or undefined previousScoreboards', () => {
            // Given - null data
            mockData.previousScoreboards = null;
            
            // When
            dashboard.updateGameHistory(mockData.previousScoreboards);
            
            // Then
            const historyContainer = document.getElementById('gameHistory');
            expect(historyContainer.textContent).toContain('No previous games');
        });
    });

    // Helper function to create mock game data
    function createMockGames(count) {
        const games = [];
        for (let i = 1; i <= count; i++) {
            games.push([
                { 
                    name: `Player1_Game${i}`, 
                    score: 100 + i, 
                    rank: 1,
                    playerType: 'TestPlayer'
                },
                { 
                    name: `Player2_Game${i}`, 
                    score: 50 + i, 
                    rank: 2,
                    playerType: 'SimplePlayer'
                }
            ]);
        }
        return games;
    }
});

// Integration test for the complete flow
describe('Game History Integration', () => {
    test('should correctly process API response and display last 5 games', () => {
        // This would test the complete flow from API response to DOM update
        // Including the service layer logic and frontend display
    });
});
