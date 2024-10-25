from copy import deepcopy
from MCTS import *


class Board():
    def __init__(self, board=None):
        self.player_1, self.player_2, self.empty_square = 'x', 'o', '.'
        self.position = {}
        self.init_board()
        if board is not None:
            self.__dict__ = deepcopy(board.__dict__)

    # init (reset) board
    def init_board(self):
        for length in range(9):
            self.position[length] = self.empty_square

    # make move
    def make_move(self, index):
        _board = Board(self)
        _board.position[index] = self.player_1
        (_board.player_1, _board.player_2) = (_board.player_2, _board.player_1)
        return _board

    # get whether the game is drawn
    def is_draw(self):
        for values in self.position.values():
            if values == self.empty_square:
                return False
        return True

    def is_win(self):
        if ((self.position[0] == self.position[1] and self.position[2] == self.position[1] and self.position[0] !=
             self.empty_square) or
                (self.position[3] == self.position[4] and self.position[5] == self.position[4] and self.position[5] !=
                 self.empty_square) or
                (self.position[6] == self.position[7] and self.position[8] == self.position[7] and self.position[8] !=
                 self.empty_square) or
                (self.position[0] == self.position[3] and self.position[6] == self.position[3] and self.position[6] !=
                 self.empty_square) or
                (self.position[1] == self.position[4] and self.position[7] == self.position[4] and self.position[7] !=
                 self.empty_square) or
                (self.position[2] == self.position[5] and self.position[8] == self.position[5] and self.position[8] !=
                 self.empty_square) or
                (self.position[0] == self.position[4] and self.position[8] == self.position[4] and self.position[8] !=
                 self.empty_square) or
                (self.position[2] == self.position[4] and self.position[6] == self.position[4] and self.position[6] !=
                 self.empty_square)):
            return True
        return False

    def generate_states(self):
        actions = []
        [actions.append(self.make_move(i)) for i in range(9) if self.position[i] == self.empty_square]
        return actions

    # main game loop
    def game_loop(self):
        print('  Type "exit" to quit the game')
        print('  Move format index: "1"')

        print(self)

        mcts = MCTS()

        while True:
            user_input = input('> ')
            if user_input == 'exit': break
            if user_input == '': continue
            
            index = int(user_input)
            if self.position[index] != self.empty_square:
                print(' Illegal move!')
                continue
            self = self.make_move(index)
            print(self)
            best_move = mcts.search(self, None)
            
            try:
                self = best_move.board
            except:
                pass
            print(self)
            if self.is_win():
                print('player "%s" has won the game!\n' % self.player_2)
                break
            elif self.is_draw():
                print(self)
                print('Game is drawn!\n')
                break

    def __str__(self):
        print(f"{self.position[0]} {self.position[1]} {self.position[2]}\n"
              f"{self.position[3]} {self.position[4]} {self.position[5]}\n"
              f"{self.position[6]} {self.position[7]} {self.position[8]}")
        board_string = ''
        if self.player_1 == 'x':
            board_string = '\n--------------\n "x" to move:\n--------------\n\n' + board_string
        elif self.player_1 == 'o':
            board_string = '\n--------------\n "o" to move:\n--------------\n\n' + board_string
        return board_string


if __name__ == '__main__':
    board = Board()
    board.game_loop()
