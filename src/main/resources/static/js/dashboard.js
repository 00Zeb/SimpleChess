// Chess Tournament Dashboard JavaScript

class ChessDashboard {
    constructor() {
        this.apiBase = '/api/v1/chess';
        this.stompClient = null;
        this.isConnected = false;
        this.gameRunningModal = null;
        this.gameTimeout = null;

        this.init();
    }

    init() {
        this.setupEventListeners();
        this.setupWebSocket();
        this.loadInitialData();
        this.gameRunningModal = new bootstrap.Modal(document.getElementById('gameRunningModal'));
    }

    setupEventListeners() {
        document.getElementById('runGameBtn').addEventListener('click', () => this.runNewGame());
        document.getElementById('refreshBtn').addEventListener('click', () => this.refreshData());
    }

    setupWebSocket() {
        const socket = new SockJS('/ws');
        this.stompClient = Stomp.over(socket);
        
        this.stompClient.connect({}, 
            (frame) => {
                console.log('Connected: ' + frame);
                this.isConnected = true;
                this.updateConnectionStatus(true);
                
                // Subscribe to scoreboard updates
                this.stompClient.subscribe('/topic/scoreboard', (message) => {
                    const scoreboard = JSON.parse(message.body);
                    this.updateScoreboard(scoreboard);

                    // Hide modal if it's showing (game completed via WebSocket)
                    if (this.gameRunningModal) {
                        const modalElement = document.getElementById('gameRunningModal');
                        if (modalElement && modalElement.classList.contains('show')) {
                            this.hideGameModal();
                            this.showSuccess('Game completed successfully!');
                        }
                    }
                });
            },
            (error) => {
                console.error('WebSocket connection error:', error);
                this.isConnected = false;
                this.updateConnectionStatus(false);
                
                // Retry connection after 5 seconds
                setTimeout(() => this.setupWebSocket(), 5000);
            }
        );
    }

    updateConnectionStatus(connected) {
        const statusElement = document.getElementById('connectionStatus');
        if (connected) {
            statusElement.textContent = 'Connected';
            statusElement.className = 'badge bg-success connection-status';
        } else {
            statusElement.textContent = 'Disconnected';
            statusElement.className = 'badge bg-danger connection-status';
        }
    }

    async loadInitialData() {
        try {
            const response = await fetch(`${this.apiBase}/scoreboard`);
            if (response.ok) {
                const data = await response.json();
                this.updateScoreboard(data);
            } else {
                this.showError('Failed to load initial data');
            }
        } catch (error) {
            console.error('Error loading initial data:', error);
            this.showError('Error connecting to server');
        }
    }

    async runNewGame() {
        try {
            // Show the modal with spinner
            this.gameRunningModal.show();

            // Disable the run game button to prevent multiple clicks
            const runGameBtn = document.getElementById('runGameBtn');
            runGameBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>Running...';
            runGameBtn.disabled = true;

            // Set a timeout to hide modal after 30 seconds if no response
            this.gameTimeout = setTimeout(() => {
                this.hideGameModal();
                this.showError('Game is taking longer than expected. Please refresh to see results.');
            }, 30000);

            const response = await fetch(`${this.apiBase}/game/run`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                const data = await response.json();
                this.updateScoreboard(data);
                this.hideGameModal();
                this.showSuccess('Game completed successfully!');
            } else {
                this.hideGameModal();
                this.showError('Failed to run game');
            }
        } catch (error) {
            console.error('Error running game:', error);
            this.hideGameModal();
            this.showError('Error running game');
        }
    }

    hideGameModal() {
        // Clear timeout if it exists
        if (this.gameTimeout) {
            clearTimeout(this.gameTimeout);
            this.gameTimeout = null;
        }

        // Hide modal using manual class removal with delay
        // The 100ms timeout is necessary for Bootstrap modal state management
        const modalElement = document.getElementById('gameRunningModal');
        if (modalElement) {
            setTimeout(() => {
                modalElement.classList.remove('show');
                modalElement.style.display = 'none';
                modalElement.setAttribute('aria-hidden', 'true');
                modalElement.removeAttribute('aria-modal');

                // Remove backdrop if it exists
                const backdrop = document.querySelector('.modal-backdrop');
                if (backdrop) {
                    backdrop.remove();
                }

                // Remove modal-open class from body
                document.body.classList.remove('modal-open');
                document.body.style.overflow = '';
                document.body.style.paddingRight = '';
            }, 100);
        }

        // Restore button state
        const runGameBtn = document.getElementById('runGameBtn');
        if (runGameBtn) {
            runGameBtn.innerHTML = '<i class="fas fa-play me-1"></i>Run New Game';
            runGameBtn.disabled = false;
        }
    }

    async refreshData() {
        const refreshBtn = document.getElementById('refreshBtn');
        const originalContent = refreshBtn.innerHTML;
        
        refreshBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>Refreshing...';
        refreshBtn.disabled = true;
        
        try {
            await this.loadInitialData();
        } finally {
            refreshBtn.innerHTML = originalContent;
            refreshBtn.disabled = false;
        }
    }

    updateScoreboard(data) {
        this.updateLastUpdated(data.timestamp);
        this.updateGameStats(data.gameStats);
        this.updateCurrentScoreboard(data.currentScoreboard);
        this.updateTotalScoreboard(data.totalScores);
        this.updateGameHistory(data.previousScoreboards);
    }

    updateLastUpdated(timestamp) {
        const element = document.getElementById('lastUpdated');
        const date = new Date(timestamp);
        element.textContent = date.toLocaleString();
    }

    updateGameStats(stats) {
        document.getElementById('totalGames').textContent = stats.totalGames;
        document.getElementById('totalPlayers').textContent = stats.totalPlayers;
    }

    updateCurrentScoreboard(scoreboard) {
        const tbody = document.getElementById('currentScoreboard');
        tbody.innerHTML = '';

        if (!scoreboard || scoreboard.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="4" class="text-center text-muted">
                        No games played yet
                    </td>
                </tr>
            `;
            return;
        }

        scoreboard.forEach((player, index) => {
            const row = this.createPlayerRow(player, index + 1, true);
            tbody.appendChild(row);
        });
    }

    updateTotalScoreboard(scoreboard) {
        const tbody = document.getElementById('totalScoreboard');
        tbody.innerHTML = '';

        if (!scoreboard || scoreboard.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="4" class="text-center text-muted">
                        No total scores available
                    </td>
                </tr>
            `;
            return;
        }

        scoreboard.forEach((player, index) => {
            const row = this.createPlayerRow(player, index + 1, false);
            tbody.appendChild(row);
        });
    }

    createPlayerRow(player, rank, showWinRate) {
        const row = document.createElement('tr');
        
        const rankBadgeClass = this.getRankBadgeClass(rank);
        const winRateHtml = showWinRate ? 
            `<div class="win-rate-bar">
                <div class="win-rate-fill ${this.getWinRateClass(player.winRate)}" 
                     style="width: ${player.winRate}%"></div>
             </div>
             <small class="text-muted">${player.winRate.toFixed(1)}%</small>` :
            player.gamesPlayed;

        row.innerHTML = `
            <td>
                <span class="rank-badge ${rankBadgeClass}">${rank}</span>
            </td>
            <td>
                <div>
                    <strong>${this.getPlayerDisplayName(player.name)}</strong>
                    <br>
                    <small class="player-type">${player.playerType}</small>
                </div>
            </td>
            <td>
                <strong>${player.score}</strong>
                ${player.reflection ? `<br><small class="text-muted">${player.reflection}</small>` : ''}
            </td>
            <td>${winRateHtml}</td>
        `;

        return row;
    }

    getRankBadgeClass(rank) {
        switch (rank) {
            case 1: return 'rank-1';
            case 2: return 'rank-2';
            case 3: return 'rank-3';
            default: return 'rank-other';
        }
    }

    getWinRateClass(winRate) {
        if (winRate >= 75) return 'win-rate-excellent';
        if (winRate >= 60) return 'win-rate-good';
        if (winRate >= 40) return 'win-rate-average';
        return 'win-rate-poor';
    }

    getPlayerDisplayName(name) {
        // Extract class name from full package name
        return name.split('.').pop();
    }

    updateGameHistory(previousScoreboards) {
        const container = document.getElementById('gameHistory');

        if (!previousScoreboards || previousScoreboards.length === 0) {
            container.innerHTML = '<p class="text-center text-muted">No previous games</p>';
            return;
        }

        // Clear container first to ensure clean update
        container.innerHTML = '';

        // Get the first 5 games (newest games are at the start of the array)
        // If we have [newest, newer, new, old, older, oldest] we want [newest, newer, new, old, older] (first 5)
        const last5Games = previousScoreboards.slice(0, 5);
        const totalGames = previousScoreboards.length;

        let html = '<div class="row">';

        last5Games.forEach((scoreboard, index) => {
            // Calculate the actual game number (newest games are at start of array)
            // If we have 46 total games, index 0 = game 46, index 1 = game 45, etc.
            const gameNumber = totalGames - index;

            html += `
                <div class="col-md-6 col-lg-4 mb-3">
                    <div class="card">
                        <div class="card-header bg-light">
                            <h6 class="mb-0">Game ${gameNumber}</h6>
                        </div>
                        <div class="card-body p-2">
                            <div class="table-responsive">
                                <table class="table table-sm mb-0">
                                    <tbody>
                                        ${scoreboard.slice(0, 3).map((player, rank) => `
                                            <tr>
                                                <td class="text-center" style="width: 30px;">
                                                    <span class="badge bg-secondary">${rank + 1}</span>
                                                </td>
                                                <td>${this.getPlayerDisplayName(player.name)}</td>
                                                <td class="text-end"><strong>${player.score}</strong></td>
                                            </tr>
                                        `).join('')}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            `;
        });

        html += '</div>';

        // Force DOM update
        container.innerHTML = html;

        // Force a reflow to ensure the update is visible
        container.offsetHeight;
    }

    showSuccess(message) {
        this.showToast(message, 'success');
    }

    showError(message) {
        this.showToast(message, 'danger');
    }

    showToast(message, type) {
        // Create a simple toast notification
        const toast = document.createElement('div');
        toast.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
        toast.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
        toast.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        
        document.body.appendChild(toast);
        
        // Auto-remove after 5 seconds
        setTimeout(() => {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, 5000);
    }
}

// Initialize dashboard when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new ChessDashboard();
});
