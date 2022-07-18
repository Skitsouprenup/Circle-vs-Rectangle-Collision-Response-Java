import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Color;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.EventQueue;

public class CircRect {
	
	CircObject c1,c2;
	Rectangle2D r1;
	boolean corner_col,side_col;
	boolean xCollide,yCollide;
	boolean prev_x_col,prev_y_col;
	float c1OffsetX,c1OffsetY;
	float r1CenterX,r1CenterY;
	
	public static void main(String[] args) {
		new CircRect();
	}
	
	public CircRect() {
		
		c1 = new CircObject(0f,0f,40f,40f);
		c2 = new CircObject(0f,0f,40f,40f);
		r1 = new Rectangle2D.Float(150f,150f,110f,50f);
		r1CenterX = (float)(r1.getX() + r1.getWidth() * 0.5f);
		r1CenterY = (float)(r1.getY() + r1.getHeight() * 0.5f);
		
		EventQueue.invokeLater(new Runnable(){
			
			@Override
			public void run() {
				JFrame jf = new JFrame("CircRect");
				Panel pnl = new Panel();
				pnl.addMouseMotionListener(new MouseMotion());
				jf.add(pnl);
				jf.pack();
				jf.setResizable(false);
				jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				jf.setLocationRelativeTo(null);
				jf.setVisible(true);
			}
			
		});
		
	}
	
	void updateData(){
		//System.out.println("updating...");
		c1OffsetX = c1.getX() - c1.getWidth() * 0.5f;
		c1OffsetY = c1.getY() - c1.getHeight() * 0.5f;
		
		//get the xCollide and yCollide values before c1 and r1 collide
		if(!side_col){
		  prev_x_col = xCollide;
		  prev_y_col = yCollide;
		}
		
		//reset values
		xCollide = false;
		yCollide = false;
		side_col = false;
		corner_col = false;
		
		if(c1.getX() < r1.getX() + r1.getWidth() && 
		   c1.getX() > r1.getX()) xCollide = true;
		if(c1.getY() < r1.getY() + r1.getHeight() && 
		   c1.getY() > r1.getY()) yCollide = true;
		
		float dst_x = Math.abs(c1.getX() - r1CenterX);
		float dst_y = Math.abs(c1.getY() - r1CenterY);
		
		if(xCollide || yCollide){
		  float avg_x = (float)( c1.getRadX() + r1.getWidth() * 0.5f );
		  float avg_y = (float)( c1.getRadY() + r1.getHeight() * 0.5f );
		
		  if(dst_x < avg_x && dst_y < avg_y) side_col = true;
		  else side_col = false;
		}
		else{
		  float corner_dst_x = Math.abs(dst_x - (float)r1.getWidth() * 0.5f);
		  float corner_dst_y = Math.abs(dst_y - (float)r1.getHeight() * 0.5f);
			
		  if(corner_dst_x * corner_dst_x + corner_dst_y * corner_dst_y < c1.getRadX() * c1.getRadX())
			 corner_col = true;
		  else corner_col = false;
			
		}	
		
		//If there's a side or corner collision then we need to do a collision response.
		if(side_col || corner_col) 
		  collisionResponse(prev_x_col,prev_y_col,side_col,corner_col);
	}
	
	void collisionResponse(boolean xCol,boolean yCol,
						   boolean side_col,boolean corner_col){
		
		//use rect-vs-rect collision response when doing a collision response with sides.
		//rect-vs-rect collision response here is modified.
		if(side_col){
		  
		  //reposition c2(collision response circle) if both xCollide and yCollide are false. This can happen when 
		  //c1 initially(right when the program starts) collides with r1. It can also
		  //happen when c1 is colliding with one of the corners and suddenly go inside r1.
		  if(!xCol && !yCol){
			float avgWidth = (float)( c1.getRadX() + r1.getWidth() * 0.5f );
		    float avgHeight = (float)( c1.getRadY() + r1.getHeight() * 0.5f );
			double dst_x = Math.abs(c1.getX() - r1CenterX);
			double dst_y = Math.abs(c1.getY() - r1CenterY);
			double overlap_x = avgWidth - dst_x;
			double overlap_y = avgHeight - dst_y;
			if(overlap_x > overlap_y) prev_x_col = true;
			else prev_y_col = true;
		  }
		  /*Note: if-else(w/o braces) with an if statement is not a good combination and ruins the
		  result of the whole statement.
		  e.g.
		  if()
			if()
		  else
		  */
	      //if xCollide is true then c1 was on top or bottom of r1 before colliding with it. So,
		  //reposition c2 to the top or bottom of r1
		  if(xCol){
			  
			if(c1.getY() < r1CenterY){
			  if(!checkCntrXOut()) c2.getCirc().setFrame(c1OffsetX, r1.getY() - c1.getHeight(), 40f, 40f); 
			}
		    else{
			  if(!checkCntrXOut()) c2.getCirc().setFrame(c1OffsetX, r1.getY() + r1.getHeight(), 40f, 40f);
			}
			
		  }
		  //if yCollide is true then c1 was on left or right of r1 before colliding with it. So,
		  //reposition c2 to the left or right of r1
		  else if(yCol){
			  
			if(c1.getX() < r1CenterX){
			  if(!checkCntrYOut()) c2.getCirc().setFrame(r1.getX() - c1.getWidth(),c1OffsetY, 40f, 40f);
			}
		    else{
			  if(!checkCntrYOut()) c2.getCirc().setFrame(r1.getX() + r1.getWidth(),c1OffsetY, 40f, 40f);
			}
			
		 }
		}
		//use the clamp method when doing a collision response with corners.
		else if(corner_col){
		  float nearestX = clamp(c1.getX(),(float)r1.getX(),(float)( r1.getX()+r1.getWidth() ));
		  float nearestY = clamp(c1.getY(),(float)r1.getY(),(float)( r1.getY()+r1.getHeight() ));
		
		  float dstX = c1.getX()-nearestX;
		  float dstY = c1.getY()-nearestY;
		  float centerRadius = (float)Math.sqrt(dstX * dstX + dstY * dstY);
		  
		  float norm_x = dstX/centerRadius;
		  float norm_y = dstY/centerRadius;
		
		  float c2_center_x = nearestX + c1.getRadX() * norm_x;
		  float c2_center_y = nearestY + c1.getRadY() * norm_y;
		
		  c2.getCirc().setFrame(c2_center_x-c2.getRadX(),c2_center_y-c2.getRadY(),40f,40f);
		}

	}
	
	//Checks if c1 centerx is outside of r1. This method is used reposition c2(collision response circle)
	//when c1's center comes from the inside of r1. This method reposition c2 from top or bottom to left
	//or right based off of c1's center x-value.
	boolean checkCntrXOut(){
		
		boolean result = false;
		
		if(c1.getX() > r1.getX() + r1.getWidth()){
		  c2.getCirc().setFrame(r1.getX() + r1.getWidth(),c1OffsetY, 40f, 40f);
		  prev_x_col = false;
		  prev_y_col = true;
		  result = true;
		}
		else if(c1.getX() < r1.getX()){
		  c2.getCirc().setFrame(r1.getX() - c1.getWidth(),c1OffsetY, 40f, 40f);
		  prev_x_col = false;
		  prev_y_col = true;
		  result = true;
		}
		
		return result;
		
	}
	
	//Checks if c1 centery is outside of r1. This method is used reposition c2(collision response circle)
	//when c1's center comes from the inside of r1. This method reposition c2 from left or right to top
	//or bottom based off of c1's center y-value.
	boolean checkCntrYOut(){
		
		boolean result = false;
		
		if(c1.getY() > r1.getX() + r1.getHeight()){
			c2.getCirc().setFrame(c1OffsetX, r1.getY() + r1.getHeight(), 40f, 40f);
			prev_x_col = true;
		    prev_y_col = false;
			result = true;
		}
		else if(c1.getY() < r1.getY()){
			c2.getCirc().setFrame(c1OffsetX, r1.getY() - c1.getHeight(), 40f, 40f);
			prev_x_col = true;
		    prev_y_col = false;
			result = true;
		}
		
		return result;
	}
	
	//restricts value between min and max values. returns the
	//clamped value.
	float clamp(float value,float min,float max)
	{
		if(value < min) return min;
		else if(value > max) return max;
		
		return value;
	}
	
	void drawObjects(Graphics2D g2d){
		//System.out.println("drawing objects...");
		g2d.setPaint(Color.GREEN);
		g2d.fill(r1);
		c1.getCirc().setFrame(c1OffsetX,c1OffsetY,c1.getWidth(),c1.getHeight());
		if(!corner_col && !side_col) g2d.setPaint(Color.YELLOW);
		else{ 
		  g2d.setPaint(Color.YELLOW);
		  g2d.fill(c2.getCirc());
		  g2d.setPaint(Color.RED);
		}
		g2d.draw(c1.getCirc());
	}
	
	class Panel extends JPanel {
		
		Panel(){
			Timer timer = new Timer(16, new ActionListener(){
				
				@Override
				public void actionPerformed(ActionEvent e){
					updateData();
					repaint();
				}
			});
			timer.start();
		}
		
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(400,400);
		}
		
		@Override
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setPaint(Color.BLACK);
			g2d.fillRect(0,0,getWidth(),getHeight());
			drawObjects(g2d);
			g2d.setPaint(Color.WHITE);
			g2d.drawString("Mouse-controlled circle will turn red if there's a collision", 60f, 20f);
			g2d.drawLine((int)c1.getX(),(int)c1.getY(),(int)r1CenterX,(int)r1CenterY);
			g2d.dispose();
		}
	}
	
	class CircObject {
		private float x,y,width,height;
		private Ellipse2D circ;
		
		CircObject(float x, float y, float width, float height){
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			circ = new Ellipse2D.Float(x,y,width,height);
		}
		
		float getX(){return x;}
		float getY(){return y;}
		float getWidth(){return width;}
		float getHeight(){return height;}
		float getRadX(){return width * 0.5f;}
		float getRadY(){return height * 0.5f;}
		Ellipse2D getCirc(){return circ;}
		
		void setX(float x){this.x = x;}
		void setY(float y){this.y = y;}
	}
	
	class MouseMotion implements MouseMotionListener {
	
		@Override
		public void mouseDragged(MouseEvent e){}
	
		@Override
		public void mouseMoved(MouseEvent e){
			c1.setX(e.getX());
			c1.setY(e.getY());
		}
	}
	
}

