/**
 * Node.js script to run JavaScript tests as part of Gradle build
 * This script imports the test modules and runs them programmatically
 */

// Import required modules
const gameHistoryTests = require('./game-history.test.js');
const fs = require('fs');
const path = require('path');

// Parse command line arguments
const args = process.argv.slice(2);
const jsonOutput = args.includes('--json');
const outputFile = args.find(arg => arg.startsWith('--output='))?.split('=')[1];

// Configure console output formatting
const COLORS = {
  RESET: '\x1b[0m',
  RED: '\x1b[31m',
  GREEN: '\x1b[32m',
  YELLOW: '\x1b[33m',
  BLUE: '\x1b[34m'
};

console.log(`${COLORS.BLUE}=== Running JavaScript Tests ===${COLORS.RESET}\n`);

// Run the game history tests
console.log(`${COLORS.YELLOW}Running Game History Tests:${COLORS.RESET}`);
const gameHistoryResults = gameHistoryTests.runTests();

// Calculate overall results
const totalTests = gameHistoryResults.length;
const passedTests = gameHistoryResults.filter(result => result.pass).length;
const failedTests = totalTests - passedTests;

// Create detailed test report
const testReport = {
  summary: {
    total: totalTests,
    passed: passedTests,
    failed: failedTests,
    success: failedTests === 0
  },
  tests: gameHistoryResults.map(result => ({
    name: result.name,
    pass: result.pass,
    expected: result.expected,
    actual: result.actual,
    details: result.details || ''
  }))
};

// Print summary to console
console.log(`\n${COLORS.BLUE}=== JavaScript Test Summary ===${COLORS.RESET}`);
console.log(`Total Tests: ${totalTests}`);
console.log(`Passed: ${COLORS.GREEN}${passedTests}${COLORS.RESET}`);
console.log(`Failed: ${failedTests > 0 ? COLORS.RED + failedTests + COLORS.RESET : failedTests}`);

// Output JSON if requested
if (jsonOutput) {
  const jsonString = JSON.stringify(testReport, null, 2);
  if (outputFile) {
    const outputDir = path.dirname(outputFile);
    if (!fs.existsSync(outputDir)) {
      fs.mkdirSync(outputDir, { recursive: true });
    }
    fs.writeFileSync(outputFile, jsonString);
    console.log(`\nTest results written to: ${outputFile}`);
  } else {
    console.log(`\n${jsonString}`);
  }
}

// Exit with appropriate code (0 for success, 1 for failure)
if (failedTests > 0) {
  console.log(`\n${COLORS.RED}JavaScript tests failed!${COLORS.RESET}`);
  process.exit(1);
} else {
  console.log(`\n${COLORS.GREEN}All JavaScript tests passed!${COLORS.RESET}`);
  process.exit(0);
}