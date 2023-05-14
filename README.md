# Thesus Find the Exit

Consider the labyrinth below:

![](images/labyrinth.png)

The goal of the game is to navigate the blue ball to the square marked with the empty circle.

## Rules

- In a move, the ball must be rolled to up, right, down, or left.
- The ball moves until it reaches the wall around the labyrinth or any inner wall.

## Technical Implementation

- Uses MVC pattern.
- The game loads the labyrinth from a `JSON` file, which makes it easily extensible to add more maps.

