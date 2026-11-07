/**
 * Entry point of program
 * battleProbability: must be between 0 and 1, the higher the value, the more likely battles occur
 * play() starts the game
 */

void main(String[] args) {
    MonstersandHeroes game = new MonstersandHeroes(0.7);
    game.play();
}