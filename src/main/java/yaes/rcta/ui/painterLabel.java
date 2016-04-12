package yaes.rcta.ui;

import java.awt.Color;
import java.awt.Graphics2D;

import yaes.ui.visualization.VisualCanvas;
import yaes.ui.visualization.VisualizationProperties;
import yaes.ui.visualization.painters.IPainter;
import yaes.ui.visualization.painters.PainterHelper;
import yaes.world.physical.location.IMoving;
import yaes.world.physical.location.INamed;

public class painterLabel implements IPainter {

	
	protected String  labelFontName = "Sans";
	protected String forcedLabel = null;
	protected double size = 10;
	
	public painterLabel(String forcedLabel){
		this.forcedLabel = forcedLabel;
	}
	
	

	@Override
	public int getLayer() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void paint(Graphics2D g, Object o, VisualCanvas panel) {
		
		IMoving displayLocation = (IMoving) o;
		INamed displayText = (INamed)o;
		
		String text = "<html><font face='" + labelFontName
                + "' size="+this.size+">" + displayText.getName()+ "</font></html>";
		if(forcedLabel != null){
			text = "<html><font face='" + labelFontName
	                + "' size="+this.size+">" + forcedLabel+ "</font></html>";
		}
		 
	            
        PainterHelper.paintHtmlLabel(text, displayLocation.getLocation(), 0, 0,
                Color.white, false, 1, Color.black, false, g, panel);

	}
	

	@Override
	public void registerParameters(
			VisualizationProperties visualizationProperties) {
		// TODO Auto-generated method stub

	}

}
