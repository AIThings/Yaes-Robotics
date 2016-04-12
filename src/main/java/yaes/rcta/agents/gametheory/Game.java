package yaes.rcta.agents.gametheory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import weka.core.matrix.Matrix;
import yaes.rcta.constRCTA;
import yaes.ui.format.Formatter;
import yaes.ui.text.TextUiHelper;

/**
 * Represents a game played between multiple abstract agents.
 * 
 * As a note, this model assumes that all the agents have the same set of moves.
 * However, the payoff matrix is not necessarily symmetrical, so this is just a
 * naming problem.
 * 
 * @author Lotzi Boloni
 * 
 */
public class Game implements Serializable, constRCTA {

	private static final long serialVersionUID = 8365259061422248731L;

	private List<String> moves = new ArrayList<>();
	private List<String> players = new ArrayList<>();
	private double values[];
	private String doneMoves[];

	/**
	 * Creates a game by setting the players and the moves
	 * 
	 * @param moves
	 * @param players
	 */
	public Game() {
	}

	/**
	 * Sets the set of players playing the game
	 * 
	 * @param players
	 */
	public void setPlayers(String... players) {
		this.players.clear();
		for (String player : players) {
			this.players.add(player);
		}
		init();
	}

	/**
	 * Sets the set of moves playable in the game
	 * 
	 * @param moves
	 */
	public void setMoves(String... moves) {
		this.moves.clear();
		for (String move : moves) {
			this.moves.add(move);
		}
		init();
	}

	/**
	 * Initializes the value matrix
	 */
	private void init() {
		int size = (int) (this.players.size() * Math.pow(this.moves.size(), this.players.size()));
		this.values = new double[size];
		this.doneMoves = new String[this.players.size()];
	}

	/**
	 * Sets the payoff for a certain set of moves
	 * 
	 * @param apa
	 * @param value
	 */
	public void setPayoff(String beneficiary, double value, String... moves) {
		int address = getAddress(beneficiary, moves);
		this.values[address] = value;
	}

	/**
	 * Get the payoff for a certain set of moves
	 * 
	 * @param apa
	 * @param value
	 */
	public double getPayoff(String beneficiary, String... moves) {
		int address = getAddress(beneficiary, moves);
		return this.values[address];
	}

	/**
	 * Gets the address for a certain beneficiary and a set of moves FIXME:
	 * implement me
	 * 
	 * @param moves
	 * @return
	 */
	private int getAddress(String beneficiary, String... playedMoves) {
		int tmp = 0;
		for (String move : playedMoves) {
			tmp = tmp * this.moves.size();
			tmp = tmp + getMoveIndex(move);
			// tmp = (tmp + getMoveIndex(move)) * moves.size();
		}
		tmp = tmp * this.players.size();
		tmp = tmp + getPlayerIndex(beneficiary);
		return tmp;
	}

	/**
	 * Returns the index of a player
	 * 
	 * @param player
	 * @return
	 */
	private int getPlayerIndex(String player) {
		int retval = this.players.indexOf(player);
		if (retval == -1) {
			throw new Error("Cannot find player " + player); //$NON-NLS-1$
		}
		return retval;
	}

	/**
	 * Returns the index of a move
	 * 
	 * @param move
	 * @return
	 */
	private int getMoveIndex(String move) {
		int retval = this.moves.indexOf(move);
		if (retval == -1) {
			throw new Error("Cannot find move " + move); //$NON-NLS-1$
		}
		return retval;
	}

	/**
	 * Sets the move done by a given player
	 * 
	 * @param player
	 * @param move
	 */
	public void setDoneMove(String player, String move) {
		int index = getPlayerIndex(player);
		this.doneMoves[index] = move;
	}

	/**
	 * Returns the move done by a given player
	 * 
	 * @param player
	 * @return
	 */
	public String getDoneMove(String player) {
		int index = getPlayerIndex(player);
		return this.doneMoves[index];
	}

	/**
	 * @return the moves
	 */
	public List<String> getMoves() {
		return this.moves;
	}

	/**
	 * @return the players
	 */
	public List<String> getPlayers() {
		return this.players;
	}

	/**
	 * Formatting the game
	 */
	@Override
	public String toString() {
		Formatter fmt = new Formatter();
		fmt.add("Game"); //$NON-NLS-1$
		fmt.indent();
		String tmp = "Players (" + getPlayers().size() + "): "; //$NON-NLS-1$ //$NON-NLS-2$
		for (String player : getPlayers()) {
			tmp += player + " "; //$NON-NLS-1$
		}
		fmt.add(tmp);
		tmp = "Moves (" + getMoves().size() + "): ";  //$NON-NLS-1$//$NON-NLS-2$
		for (String move : getMoves()) {
			tmp += move + " "; //$NON-NLS-1$
		}
		fmt.add(tmp);
		if (getPlayers().size() == 2 && getMoves().size() == 2) {
			toStringMatrix22(fmt);
		} else {
			toStringMatrixNM(fmt);
		}
		fmt.add("Plays:"); //$NON-NLS-1$
		fmt.indent();
		// done moves:
		for (int i = 0; i != getPlayers().size(); i++) {
			String player = getPlayers().get(i);
			if (getDoneMove(player) == null) {
				fmt.add(player + " did not play yet."); //$NON-NLS-1$
			} else {
				fmt.add(player + " played " + getDoneMove(player)); //$NON-NLS-1$
			}
		}
		fmt.deindent();
		return fmt.toString();
	}

	/**
	 * Formats an n x m payoff matrix
	 * 
	 * @param fmt
	 */
	private void toStringMatrixNM(Formatter fmt) {
		// TODO: Fix me for better solution of N x M matrix
		String player1 = getPlayers().get(0);
		String player2 = getPlayers().get(1);
		String[] mixedStrategyMoves = mixedGameMoves.keySet().toArray(new String[mixedGameMoves.size()]);
		double[][] player1vals = new double[mixedGameMoves.size()][mixedGameMoves.size()];
		double[][] player2vals = new double[mixedGameMoves.size()][mixedGameMoves.size()];

		int i = 0;
		int j = 0;
		for (String mv1 : mixedStrategyMoves) {
			j = 0;
			for (String mv2 : mixedStrategyMoves) {
				player1vals[i][j] = getPayoff(player1, mv1, mv2);
				player2vals[i][j] = getPayoff(player2, mv1, mv2);
				j++;
			}
			i++;
		}

		Matrix A = new Matrix(player1vals, mixedGameMoves.size(), mixedGameMoves.size());
		Matrix B = new Matrix(player2vals, mixedGameMoves.size(), mixedGameMoves.size());

		fmt.add("Player1 Payoff"); //$NON-NLS-1$
		fmt.add(A.toString());
		fmt.add("Player2 Payoff"); //$NON-NLS-1$
		fmt.add(B.toString());
	}

	/**
	 * Formats the 2 x 2 payoff matrix
	 * 
	 * @param fmt
	 * @return
	 */
	private void toStringMatrix22(Formatter fmt) {
		StringBuffer buf = new StringBuffer();
		int cellLength = 15;
		int subCellLength = 10;
		// horizontal line
		String hline = "";
		for (int i = 0; i != (3 * cellLength + 4); i++) {
			hline = hline + "-";
		}
		// empty cell
		String emptyCell = "";
		for (int i = 0; i != cellLength; i++) {
			emptyCell = emptyCell + " ";
		}
		// shift cell for diagonal writing
		String shiftCell = "";
		for (int i = 0; i != cellLength / 3; i++) {
			shiftCell = shiftCell + " ";
		}
		String player1 = getPlayers().get(0);
		String player2 = getPlayers().get(1);
		String moveC = getMoves().get(0);
		String moveD = getMoves().get(1);
		//
		// The header
		//
		buf.append(hline + "\n");
		String tmp = "|" + TextUiHelper.padTo(player1, subCellLength) + shiftCell + "|"
				+ TextUiHelper.padTo(moveC, subCellLength) + shiftCell + "|" + TextUiHelper.padTo(moveD, subCellLength)
				+ shiftCell + "|";
		buf.append(tmp + "\n");
		tmp = "|" + shiftCell + TextUiHelper.padTo(player2, subCellLength) + "|" + emptyCell + "|" + emptyCell + "|";
		buf.append(tmp + "\n");
		buf.append(hline + "\n");
		//
		// The body, first line
		//
		tmp = "|" + TextUiHelper.padTo(moveC, subCellLength) + shiftCell + "|"
				+ TextUiHelper.padTo(Formatter.fmt(getPayoff(player1, moveC, moveC)), subCellLength) + shiftCell + "|"
				+ TextUiHelper.padTo(Formatter.fmt(getPayoff(player1, moveC, moveD)), subCellLength) + shiftCell + "|";
		buf.append(tmp + "\n");
		tmp = "|" + emptyCell + "|" + shiftCell
				+ TextUiHelper.padTo(Formatter.fmt(getPayoff(player2, moveC, moveC)), subCellLength) + "|" + shiftCell
				+ TextUiHelper.padTo(Formatter.fmt(getPayoff(player2, moveC, moveD)), subCellLength) + "|";
		buf.append(tmp + "\n");
		buf.append(hline + "\n");
		//
		// The body, second line
		//
		tmp = "|" + TextUiHelper.padTo(moveD, subCellLength) + shiftCell + "|"
				+ TextUiHelper.padTo(Formatter.fmt(getPayoff(player1, moveD, moveC)), subCellLength) + shiftCell + "|"
				+ TextUiHelper.padTo(Formatter.fmt(getPayoff(player1, moveD, moveD)), subCellLength) + shiftCell + "|";
		buf.append(tmp + "\n");
		tmp = "|" + emptyCell + "|" + shiftCell
				+ TextUiHelper.padTo(Formatter.fmt(getPayoff(player2, moveD, moveC)), subCellLength) + "|" + shiftCell
				+ TextUiHelper.padTo(Formatter.fmt(getPayoff(player2, moveD, moveD)), subCellLength) + "|";
		buf.append(tmp + "\n");
		buf.append(hline + "\n");
		fmt.add("Payoff matrix");
		fmt.indent();
		fmt.add(buf.toString());
		fmt.deindent();
	}

	/**
	 * Formats the 2 x 2 payoff matrix for printing in Latex
	 * 
	 * @param fmt
	 * @return
	 */
	public void toLatexMatrix22(Formatter fmt) {
		StringBuffer buf = new StringBuffer();
		// horizontal line
		String hline = "\\hline";
		// empty cell
		String emptyCell = " ";
		String eol = "\\\\";
		// shift cell for diagonal writing
		// String shiftCell = " \\quad\\quad ";
		String shiftCell = " ~~~~~~ ";
		String player1 = getPlayers().get(0);
		String player2 = getPlayers().get(1);
		String moveC = getMoves().get(0);
		String moveD = getMoves().get(1);
		//
		// The header
		//
		buf.append("\\begin{tabular}{|p{0.8cm}|p{0.8cm}|p{0.8cm}|}\n");
		buf.append(hline + "\n");
		String tmp = player1 + " & " + moveC + " & " + moveD + eol;
		buf.append(tmp + "\n");
		tmp = shiftCell + player2 + " & " + emptyCell + " & " + emptyCell + eol;
		buf.append(tmp + "\n");
		buf.append(hline + "\n");
		//
		// The body, first line
		//
		tmp = moveC + " & " + Formatter.fmt(getPayoff(player1, moveC, moveC)) + " & "
				+ Formatter.fmt(getPayoff(player1, moveC, moveD)) + eol;
		buf.append(tmp + "\n");
		tmp = emptyCell + " & " + shiftCell + Formatter.fmt(getPayoff(player2, moveC, moveC)) + " & " + shiftCell
				+ Formatter.fmt(getPayoff(player2, moveC, moveD)) + eol;
		buf.append(tmp + "\n");
		buf.append(hline + "\n");
		//
		// The body, second line
		//
		tmp = moveD + " & " + Formatter.fmt(getPayoff(player1, moveD, moveC)) + " & "
				+ Formatter.fmt(getPayoff(player1, moveD, moveD)) + eol;
		buf.append(tmp + "\n");
		tmp = emptyCell + " & " + shiftCell + Formatter.fmt(getPayoff(player2, moveD, moveC)) + " & " + shiftCell
				+ Formatter.fmt(getPayoff(player2, moveD, moveD)) + eol;
		buf.append(tmp + "\n");
		buf.append(hline + "\n");
		buf.append("\\end{tabular}\n");
		fmt.add(buf.toString());
	}

}
