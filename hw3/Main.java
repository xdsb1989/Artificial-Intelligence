/*
 * Author:  I am lingfeng zhang, lingfeng2013@fit.edu
 * Course:  CSE 5290, Fall 2014
 * Project: Home Work 03
 */

/*************read me**************
 * Choose the program as player X or player O at first,input the single uppercase "X" or "O"
 * "X" always starts first.
 * Program will run in its own turn,and output the solution which is the next move.
 * Human follow the indicate and input:"row column" and "new_row new_column"
 * If the human's input is illegal, the program will alert and let you input a legal data.
 * Once X or O reach the other side last two rows,the program will declare the winner.
 ***********************************/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
class Pieces_position{//each piece has current coordinate, the next choosen position,and the pritential moves
	int row;//current coordinate
	int col;
	ArrayList<Pieces_posiable_move> protential_move = new ArrayList<Pieces_posiable_move>();
	int choosen_row;//the next choosen position
	int choosen_col;
	int score;
}

class Pieces_posiable_move {//this class store the possiable moves for each particular piece
	int row;
	int col;
}

public class Main {
	static char board[][] = new char [6][6];
	static int flag;
	static int winner=2;
	static int turn=1;
	public static void main (String []args){
		initial_board();//set the "X" and "O" on the chess board 
		display_board();
		System.out.print("Program to be player O or X:");
		Scanner in = new Scanner(System.in);
		String ch = in.next();
		if (ch.equals("X"))
			flag = 1;
		else if (ch.equals("O"))
			flag = 0;
		char temp_board[][] = new char [6][6];
		temp_board = board;
		Pieces_position [] list_X = new Pieces_position[6];//create list_X to store all the X pieces' position and other info
		Pieces_position [] list_O = new Pieces_position[6];//create list_O to store all the O pieces' position and other info
		
		for (int i=0;i<=5;i++){
			list_X[i] = new Pieces_position();
			list_O[i] = new Pieces_position();
			list_X[i].row = 0;list_X[i].col = i;
			list_O[i].row = 5;list_O[i].col = i;
		}
		
		search_possiable_move(list_X,temp_board);//this function is to search the possiable moves for all the pieces 
		search_possiable_move(list_O,temp_board);
		
		if (flag == 1){//if X is computer's move,then analyze first.
			Minimax(list_X,list_O);
			display_board();
		}
		
		while(true){
			search_possiable_move(list_X,board);
			search_possiable_move(list_O,board);
			
			if (flag == 1)
				human_move_piece(list_O);
			else if (flag == 0)
				human_move_piece(list_X);
			
			display_board();
			check_winner();
			if (winner == 1){
				System.out.println("winner is X!");
				return;
			}
			else if (winner == 0){
				System.out.println("winner is O!");
				return;
			}
			search_possiable_move(list_X,board);
			search_possiable_move(list_O,board);
			Minimax(list_X,list_O);//minimax algorithm to find out the next move
			display_board();
			check_winner();
			if (winner == 1){
				System.out.println("winner is X!");
				return;
			}
			else if (winner == 0){
				System.out.println("winner is O!");
				return;
			}
		}
	}
	
	private static void Minimax(Pieces_position[] list_X,
			Pieces_position[] list_O) {
		
		if (flag == 1){
			for (int i=0;i<=5;i++){//scan each pieces
				int purning_value = -1000;
				int one_move_max_score [] = new int [list_X[i].protential_move.size()];
				
				for (int j=0;j < list_X[i].protential_move.size();j++){//check each move,return a score for each protential move
					int row = list_X[i].row;
					int col = list_X[i].col;
					int new_row = list_X[i].protential_move.get(j).row;
					int new_col = list_X[i].protential_move.get(j).col;
					
					char temp_map[][] = new char [6][6];
					copy_map(temp_map,board,row,col,new_row,new_col,'X');//the copy_map is changed for the next level
					
					Pieces_position [] copy_X_list = new Pieces_position[6];
					Pieces_position [] copy_O_list = new Pieces_position[6];
					for (int k=0;k<=5;k++){
						copy_X_list[k] = new Pieces_position();
						copy_O_list[k] = new Pieces_position();
					}
					copy_list(copy_X_list,list_X);//copy_list just copy the coordinates of each piece to the list
					copy_list(copy_O_list,list_O);
					copy_X_list[i].row = new_row;
					copy_X_list[i].col = new_col;
					
					one_move_max_score[j] = min_value(temp_map,copy_X_list,copy_O_list,purning_value);
					//get the minimal value for each branch
					if (one_move_max_score[j] > purning_value){
						//as long as there is a better value larger than the previous pruning value,we will reset the pruning value
						purning_value = one_move_max_score[j];
					}
				}
				
				int max_score = one_move_max_score[0], index = 0;
				for (int j=0;j < list_X[i].protential_move.size();j++){//find the max score for current piece,and its next move
					if (max_score<=one_move_max_score[j]){
						max_score = one_move_max_score[j];
						index = j;
					}
				}
				list_X[i].score = max_score;//get the best move for every single piece
				list_X[i].choosen_row = list_X[i].protential_move.get(index).row;
				list_X[i].choosen_col = list_X[i].protential_move.get(index).col;
			}//after this loop, the list_X have each piece's high score and their own next move
			
			int best_score = list_X[0].score;
			int index = 0;
			for (int i = 0;i<=5; i++){//now compare the six pieces together and choose the best one
				if (best_score < list_X[i].score){
					best_score = list_X[i].score;
					index = i;
				}
			}
			
			System.out.println("the program move is:"+"("+list_X[index].row+","+list_X[index].col+")"+" to "+
					"("+list_X[index].choosen_row+","+list_X[index].choosen_col+")");
			program_change_board(list_X[index],'X');
			reset_list(list_X,list_O);//clear all the information,need to re-analyze the protential_move and "score"
		}
		
		else if (flag == 0){
			for (int i=0;i<=5;i++){//scan each pieces
				int purning_value = -1000;
				int one_move_max_score [] = new int [list_O[i].protential_move.size()];
				
				for (int j=0;j < list_O[i].protential_move.size();j++){//check each move,return a score for each protential move
					int row = list_O[i].row;
					int col = list_O[i].col;
					int new_row = list_O[i].protential_move.get(j).row;
					int new_col = list_O[i].protential_move.get(j).col;
					
					char temp_map[][] = new char [6][6];
					copy_map(temp_map,board,row,col,new_row,new_col,'O');//the copy_map is changed for the next level
					
					Pieces_position [] copy_X_list = new Pieces_position[6];
					Pieces_position [] copy_O_list = new Pieces_position[6];
					for (int k=0;k<=5;k++){
						copy_X_list[k] = new Pieces_position();
						copy_O_list[k] = new Pieces_position();
					}
					copy_list(copy_X_list,list_X);//copy_list just copy the coordinates of each piece to the list
					copy_list(copy_O_list,list_O);
					copy_O_list[i].row = new_row;
					copy_O_list[i].col = new_col;
					
					one_move_max_score[j] = min_value(temp_map,copy_X_list,copy_O_list,purning_value);
					//get the minimal value for each branch
					if (one_move_max_score[j] > purning_value){
						//as long as there is a better value larger than the previous pruning value,we will reset the pruning value
						purning_value = one_move_max_score[j];
					}
				}
				
				int max_score = one_move_max_score[0], index = 0;
				for (int j=0;j < list_O[i].protential_move.size();j++){//find the max score for current piece,and its next move
					if (max_score <= one_move_max_score[j]){
						max_score = one_move_max_score[j];
						index = j;
					}
				}
				list_O[i].score = max_score;
				list_O[i].choosen_row = list_O[i].protential_move.get(index).row;
				list_O[i].choosen_col = list_O[i].protential_move.get(index).col;
			}//after this loop, the list_O have each piece's high score and their own next move
			
			int best_score = list_O[0].score;
			int index = 0;
			for (int i = 0;i<=5; i++){//now compare the six pieces together and choose the best one
				if (best_score < list_O[i].score){
					best_score = list_O[i].score;
					index = i;
				}
			}
			System.out.println("the program move is:"+"("+list_O[index].row+","+list_O[index].col+")"+" to "+
						"("+list_O[index].choosen_row+","+list_O[index].choosen_col+")");
			program_change_board(list_O[index],'O');
			reset_list(list_X,list_O);//clear all the information,need to re-analyze the protential_move and "score"
		}
	}

	private static int min_value(char[][] map,
			Pieces_position[] X_list, Pieces_position[] O_list,int purning_value) {
		if (flag == 1){
			search_possiable_move(O_list,map);
			int min_value=10000;
			for (int i=0;i<=5;i++){//after get all the protential move,begin to simulate each move
				for (int j=0;j < O_list[i].protential_move.size();j++){
					int row = O_list[i].row;
					int col = O_list[i].col;
					int new_row = O_list[i].protential_move.get(j).row;
					int new_col = O_list[i].protential_move.get(j).col;
					
					char temp_map[][] = new char [6][6];
					copy_map(temp_map,map,row,col,new_row,new_col,'O');//the copy_map is changed for the next level
					
					Pieces_position [] copy_X_list = new Pieces_position[6];
					Pieces_position [] copy_O_list = new Pieces_position[6];
					for (int k=0;k<=5;k++){
						copy_X_list[k] = new Pieces_position();
						copy_O_list[k] = new Pieces_position();
					}
					
					copy_list(copy_X_list,X_list);//copy_list just copy the coordinates of each piece to the list
					copy_list(copy_O_list,O_list);
					copy_O_list[i].row = new_row;//change the O_list
					copy_O_list[i].col = new_col;
					
					int value = evaluation_function(copy_X_list,copy_O_list,temp_map);
					if (value < purning_value){
						/*if we get some value less than the pruning value,which means no need to 
						 * search the rest of the branches.Then return the min_value.
						 */
						min_value = value;
						return min_value;//exit the loop, beta-alpha pruning
					}
					if (value < min_value)//update the min_value if we find some smaller
						min_value = value;
				}
			}
			return min_value;
		}
		
		else{
			search_possiable_move(X_list,map);
			int min_value=10000;
			for (int i=0;i<=5;i++){//after get all the protential move,begin to simulate each move
				for (int j=0;j < X_list[i].protential_move.size();j++){
					int row = X_list[i].row;
					int col = X_list[i].col;
					int new_row = X_list[i].protential_move.get(j).row;
					int new_col = X_list[i].protential_move.get(j).col;
					
					char temp_map[][] = new char [6][6];
					copy_map(temp_map,map,row,col,new_row,new_col,'O');//the copy_map is changed for the next level
					
					Pieces_position [] copy_X_list = new Pieces_position[6];
					Pieces_position [] copy_O_list = new Pieces_position[6];
					for (int k=0;k<=5;k++){
						copy_X_list[k] = new Pieces_position();
						copy_O_list[k] = new Pieces_position();
					}
					
					copy_list(copy_X_list,X_list);//copy_list just copy the coordinates of each piece to the list
					copy_list(copy_O_list,O_list);
					copy_X_list[i].row = new_row;//change the O_list
					copy_X_list[i].col = new_col;
					
					int value = evaluation_function(copy_X_list,copy_O_list,temp_map);
					if (value < purning_value){
						min_value = value;
						return min_value;//exit the loop, beta-alpha purning
					}
					if (value < min_value)
						min_value = value;
				}
			}
			return min_value;
		}
	}

	private static int evaluation_function(Pieces_position[] X_list,Pieces_position[] O_list, char[][] map) {
		/* the evaluation function base on 4 parts: player's current states,player's forward best states,opponent current and
		 * opponent forward best states.Using the first two value minus the last two value,then return the result
		 */
		search_possiable_move(X_list,map);
		search_possiable_move(O_list,map);
		if(flag == 1){
			int current_states = 0;
			int future_state = 0;
			int opponent_state = 0;
			int opponent_future = 0;
			for (int i = 0; i<=5; i++){
				if (X_list[i].row>4)
					current_states = current_states + 4;
				else
					current_states = current_states + X_list[i].row;//the "current_states" is the score for the current board
				
				if (X_list[i].row == 4 || X_list[i].row == 5)
					current_states = current_states + 3;
				
				int best_future = 0;//"best_future" is the best protential move for each piece
				for (int j = 0; j < X_list[i].protential_move.size(); j++){
					if (best_future < X_list[i].protential_move.get(j).row){
						best_future = X_list[i].protential_move.get(j).row;
					}
				}
				future_state = future_state + best_future;//"future_state" indicate the protential best score in the next step
				
				opponent_state = opponent_state + (O_list[i].row - 5);
				int worst_futrue = 0;
				for (int j = 0; j < O_list[i].protential_move.size(); j++){
					if (worst_futrue > O_list[i].protential_move.get(j).row){
						worst_futrue = O_list[i].protential_move.get(j).row;
					}
				}
				opponent_future = opponent_future + (worst_futrue - 5);
			}
			return (current_states + future_state + opponent_state + opponent_future);
			//the opponent_state and opponent_future are negative number,so don't need to minus them
		}
		
		else{
			int current_states = 0;
			int future_state = 0;
			int opponent_state = 0;
			int opponent_future = 0;
			for (int i = 0; i<=5; i++){
				int distance = 5 - O_list[i].row;
				
				if (distance>4)
					current_states = current_states + 4;
				else
					current_states = current_states + distance;//the "current_states" is the score for the current board
				
				if (O_list[i].row == 1 || O_list[i].row == 0)
					current_states = current_states + 3;
				
				int best_future = 6;//"best_future" is the best protential move for each piece
				for (int j = 0; j < O_list[i].protential_move.size(); j++){
					if (best_future > O_list[i].protential_move.get(j).row){
						best_future = O_list[i].protential_move.get(j).row;
					}
				}
				future_state = future_state + (5 - best_future);//"future_state" indicate the protential best score in the next step
				
				opponent_state = opponent_state + X_list[i].row;
				int worst_futrue = 0;
				for (int j = 0; j < X_list[i].protential_move.size(); j++){
					if (worst_futrue < X_list[i].protential_move.get(j).row){
						worst_futrue = X_list[i].protential_move.get(j).row;
					}
				}
				opponent_future = opponent_future + worst_futrue;
			}
			return (current_states + future_state - opponent_state - opponent_future);
		}
	}

	private static void search_possiable_move(Pieces_position[] list,char[][] map) {
		//calculate all the "X" pieces potential moves,this is important to evaluate the current chess board
		for (int i=0;i<=5;i++){
			list[i].protential_move.clear();//clear the previous info in the list,because the board may be changed
			HashMap<Integer,Boolean> visited_position = new HashMap<Integer,Boolean>();
			//hashmap to avoid the repeat position
			visited_position.put(list[i].row*10 + list[i].col, true);
			char ch = map[list[i].row][list[i].col];
			map[list[i].row][list[i].col] = '-';//clear the occupy position before searching the move
			search_adjacent_move(list[i],visited_position,map);//search the adjacent move
			search_hop_move(list[i],list[i].row,list[i].col,visited_position,map);//search the hop move 
			map[list[i].row][list[i].col] = ch;//put the char back,after the searching move
		}
	}
	
	private static void copy_list(Pieces_position[] copy_list,Pieces_position[] list) {
		//the copy_list function just copy the coordinate to the target list
		for (int i=0;i<=5;i++){
			copy_list[i].row = list[i].row;
			copy_list[i].col = list[i].col;
		}
	}

	private static void copy_map(char[][] temp_map, char [][] sample,int row,
			int col, int new_row, int new_col,char ch) {
		//copy the sample map to the temp_map
		for (int i=0;i<6;i++)
			for (int j=0;j<6;j++){
				temp_map[i][j] = sample[i][j];
			}
		temp_map[row][col] = '-';
		temp_map[new_row][new_col] = ch;
	}

	private static void reset_list(Pieces_position[] list_X,Pieces_position[] list_O) {
		for (int i = 0;i<=5; i++){
			list_X[i].protential_move.clear();
			list_X[i].score = -1000;
			list_O[i].protential_move.clear();
			list_O[i].score = -1000;
		}
	}

	private static void program_change_board(Pieces_position pieces_position,char c) {
		int new_row,new_col;
		new_row = pieces_position.choosen_row;
		new_col = pieces_position.choosen_col;
		board[pieces_position.row][pieces_position.col] = '-';
		board[new_row][new_col] = c;
		pieces_position.row = new_row;
		pieces_position.col = new_col;
	}
	
	private static void search_hop_move(Pieces_position pieces_position,int row,int col,
			HashMap<Integer, Boolean> visited_position,char temp_board[][]) {
		/*using DFS search algorithm to find out all the hop moves from the particular piece,
		 *there are 8 directions for every piece,so the function is in 8 sections
		 */
		int distance=0;
		int new_row, new_col;
		int jump = 0;
		for (int i=row-1, j=col-1; i>=0 && j>=0; i--, j--){
			distance++;
			if (temp_board[i][j] == 'X' || temp_board[i][j] == 'O'){
				new_row = row - distance*2;
				new_col = col - distance*2;
				for (i=i-1,j=j-1; i>new_row && j>new_col; i--,j--)
					if (!(check_boundry(i,j) && (temp_board[i][j] == '-'))){//make sure there is no other piece block the way
						jump = 1;
						break;
					}
				if (jump == 0){
					if (check_hop_position(new_row,new_col,visited_position,pieces_position,temp_board))
						search_hop_move(pieces_position,new_row,new_col,visited_position,temp_board);
				}
				break;
			}
		}
		
		distance=0;
		jump = 0;
		for (int i=row-1, j=col; i>=0; i--){
			distance++;
			if (temp_board[i][j] == 'X' || temp_board[i][j] == 'O'){
				new_row = row - distance*2;
				new_col = col;
				for (i=i-1; i>new_row; i--)
					if (!(check_boundry(i,j) && (temp_board[i][j] == '-'))){//make sure there is no other piece block the way
						jump = 1;
						break;
					}
				if (jump == 0){
					if (check_hop_position(new_row,new_col,visited_position,pieces_position,temp_board))
						search_hop_move(pieces_position,new_row,new_col,visited_position,temp_board);
				}
				break;
			}
		}
		
		jump = 0;
		distance=0;
		for (int i=row-1, j=col+1; i>=0 && j<=5; i--, j++){
			distance++;
			if (temp_board[i][j] == 'X' || temp_board[i][j] == 'O'){
				new_row = row - distance*2;
				new_col = col + distance*2;
				for (i=i-1,j=j+1; i>new_row && j<new_col; i--,j++)
					if (!(check_boundry(i,j) && (temp_board[i][j] == '-'))){//make sure there is no other piece block the way
						jump = 1;
						break;
					}
				if (jump == 0){
					if (check_hop_position(new_row,new_col,visited_position,pieces_position,temp_board))
						search_hop_move(pieces_position,new_row,new_col,visited_position,temp_board);
				}
				break;
			}
		}
		
		distance=0;
		jump = 0;
		for (int i=row, j=col-1; j>=0; j--){
			distance++;
			if (temp_board[i][j] == 'X' || temp_board[i][j] == 'O'){
				new_row = row;
				new_col = col - distance*2;
				for (j=j-1; j>new_col; j--)
					if (!(check_boundry(i,j) && (temp_board[i][j] == '-'))){//make sure there is no other piece block the way
						jump = 1;
						break;
					}
				if (jump == 0){
					if (check_hop_position(new_row,new_col,visited_position,pieces_position,temp_board))
						search_hop_move(pieces_position,new_row,new_col,visited_position,temp_board);
				}
				break;
			}
		}
		
		distance=0;
		jump = 0;
		for (int i=row, j=col+1; j<=5; j++){
			distance++;
			if (temp_board[i][j] == 'X' || temp_board[i][j] == 'O'){
				new_row = row;
				new_col = col + distance*2;
				for (j=j+1; j<new_col; j++)
					if (!(check_boundry(i,j) && (temp_board[i][j] == '-'))){//make sure there is no other piece block the way
						jump = 1;
						break;
					}
				if (jump == 0){
					if (check_hop_position(new_row,new_col,visited_position,pieces_position,temp_board))
						search_hop_move(pieces_position,new_row,new_col,visited_position,temp_board);
				}
				break;
			}
		}
		
		distance=0;
		jump=0;
		for (int i=row+1, j=col-1; i<=5 && j>=0; i++, j--){
			distance++;
			if (temp_board[i][j] == 'X' || temp_board[i][j] == 'O'){
				new_row = row + distance*2;
				new_col = col - distance*2;
				for (i=i+1,j=j-1; i<new_row && j>new_col; i++,j--)
					if (!(check_boundry(i,j) && (temp_board[i][j] == '-'))){//make sure there is no other piece block the way
						jump = 1;
						break;
					}
				if (jump == 0){
					if (check_hop_position(new_row,new_col,visited_position,pieces_position,temp_board))
						search_hop_move(pieces_position,new_row,new_col,visited_position,temp_board);
				}
				break;
			}
		}
		
		distance=0;
		jump=0;
		for (int i=row+1, j=col; i<=5; i++){
			distance++;
			if (temp_board[i][j] == 'X' || temp_board[i][j] == 'O'){
				new_row = row + distance*2;
				new_col = col;
				for (i=i+1; i<new_row; i++)
					if (!(check_boundry(i,j) && (temp_board[i][j] == '-'))){//make sure there is no other piece block the way
						jump = 1;
						break;
					}
				if (jump ==0){
					if (check_hop_position(new_row,new_col,visited_position,pieces_position,temp_board))
						search_hop_move(pieces_position,new_row,new_col,visited_position,temp_board);
				}
				break;
			}
		}
		
		distance=0;
		jump = 0;
		for (int i=row+1, j=col+1; i<=5 && j<=5; i++,j++){
			distance++;
			if (temp_board[i][j] == 'X' || temp_board[i][j] == 'O'){
				new_row = row + distance*2;
				new_col = col + distance*2;
				for (i=i+1,j=j+1; i<new_row && j<new_col; i++,j++)
					if (!(check_boundry(i,j) && (temp_board[i][j] == '-'))){//make sure there is no other piece block the way
						jump = 1;
						break;
					}
				if (jump == 0){
					if (check_hop_position(new_row,new_col,visited_position,pieces_position,temp_board))
						search_hop_move(pieces_position,new_row,new_col,visited_position,temp_board);
				}
				break;
			}
		}
	}

	private static boolean check_hop_position(int new_row, int new_col,
			HashMap<Integer, Boolean> visited_position,
			Pieces_position pieces_position,char temp_board[][]) {
		
		if (check_boundry(new_row,new_col))
			if (temp_board[new_row][new_col] == '-')//check the landing place if it is available
				if (visited_position.get(new_row*10+new_col) == null){
					Pieces_posiable_move node = new Pieces_posiable_move();
					node.row = new_row; node.col = new_col;
					pieces_position.protential_move.add(node);//add the position into the children list
					visited_position.put((new_row*10+new_col), true);
					return true;
				}
		return false;
	}

	private static void search_adjacent_move(Pieces_position pieces_position, 
			HashMap<Integer,Boolean> visited_position,char temp_board[][]) {
		//check the current piece's 8 adjacent move, if they are available
		int row = pieces_position.row;
		int col = pieces_position.col;
		for (int i=row-1;i<=row+1;i++){
			for (int j=col-1;j<=col+1;j++){
				if (check_boundry(i,j)){//if the current position is not out of the boundary
					if (temp_board[i][j] == '-')
						if (visited_position.get(i*10+j) == null){//if the adjacent list is empty
							Pieces_posiable_move node = new Pieces_posiable_move();//get a new node
							node.row = i; node.col = j;
							pieces_position.protential_move.add(node);//add the position into the children list
							visited_position.put((i*10+j), true);
					}
				}
			}
		}
	}

	private static boolean check_boundry(int i, int j) {//check some moves if it is out of boundary
		if (i<=5 && i>=0 && j<=5 && j>= 0)
			return true;
		else
			return false;
	}

	private static void check_winner() {//check the winner.
		int X=0,O=0;
		for (int i=0;i<=1;i++)
			for (int j=0;j<=5;j++)
				if (board[i][j]=='O')
					O++;
		for (int i=4;i<=5;i++)
			for (int j=0;j<=5;j++)
				if (board[i][j]=='X')
					X++;
		if (O == 6)
			winner = 0;
		else if (X == 6)
			winner = 1;
	}

	private static void initial_board() {//set the initial board
		for (int i=0;i<6;i++){
			for (int j=0;j<6;j++){
				if (i == 0)
					board[i][j] = 'X';
				else if (i == 5)
					board[i][j] = 'O';
				else
					board[i][j] = '-';
			}
		}
	}

	private static void human_move_piece(Pieces_position[] list) {
		Scanner in = new Scanner(System.in);
		int row1,col1,row2,col2;
		while (true){
			System.out.print("input the piece you move:");
			row1 = in.nextInt();
			col1 = in.nextInt();
			System.out.print("input the position you move:");
			row2 = in.nextInt();
			col2 = in.nextInt();
			if (check_legal(list,row1,col1,row2,col2))//if the human input is legal,then break the loop,otherwise continue
				break;
			else{
				System.out.println("input is incorrect,retype again!");
			}
		}
		
		for (int i=0;i<=5;i++){
			if (list[i].row == row1 && list[i].col == col1){
				list[i].row = row2;
				list[i].col = col2;
				break;
			}
		}
		board[row1][col1] = '-';
		if (flag == 1)
			board[row2][col2] = 'O';
		else if (flag == 0)
			board[row2][col2] = 'X';
	}
	
	private static boolean check_legal(Pieces_position[] list, int row1, int col1,int row2, int col2) {
		int i;
		for (i=0;i<=5;i++){//find the current piece
			if (list[i].row == row1 && list[i].col == col1)
				break;
		}
		if (i>5)
			return false;
		int j;
		for (j=0;j<list[i].protential_move.size();j++)//check the next move if it is in its own next move list
			if (row2 == list[i].protential_move.get(j).row && col2 == list[i].protential_move.get(j).col)
				return true;
		return false;
	}

	private static void display_board() {
		System.out.print("  ");
		for (int i=0;i<=5;i++)
			System.out.print(i+" ");
		System.out.println();
		for (int i=0;i<6;i++){
			System.out.print(i+" ");
			for (int j=0;j<6;j++)
				System.out.print(board[i][j]+" ");
			System.out.println();
		}
	}
}