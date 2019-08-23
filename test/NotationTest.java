
import java.io.IOException;

import javax.swing.JFrame;

import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.score.ScoreEditor;
import de.uos.fmt.musitech.score.epec.EpecParser;
import de.uos.fmt.musitech.score.epec.EpecParser.yyException;


/**
 * Test class for notation.
 * @author Tillman Weyde
 *
 */
public class NotationTest {
	
	public static void main(String[] args) {
		Piece piece = new Piece();
		NotationSystem nsys = piece.getScore();
		if(nsys == null) {
			nsys = new NotationSystem(piece.getContext());
			piece.setScore(nsys);
		}
        EpecParser parser = new EpecParser();
		try {
			parser.run(nsys,"T3/4 cdefgg 1.g");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (yyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ScoreEditor ed = new ScoreEditor(nsys);
		JFrame jFrame = new JFrame();
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.getContentPane().add(ed);
		jFrame.pack();
		jFrame.setVisible(true);
	}
}
