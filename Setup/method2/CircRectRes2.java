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

public class CircRectRes2 {
	
	CircObject c1,c2;
	Rectangle2D r1;
	boolean corner_col,side_col;
	boolean xCollide,yCollide;
	boolean prev_x_col,prev_y_col;
	float c1OffsetX,c1OffsetY;
	float r1CenterX,r1CenterY;
	
	public static void main(String[] args) {
		new CircRectRes2();
	}
	
	public CircRectRes2() {
		
		c1 = new CircObject(0f,0f,40f,40f);
		c2 = new CircObject(0f,0f,40f,40f);
		r1 = new Rectangle2D.Float(150f,150f,110f,50f);
		r1CenterX = (float)(r1.getX() + r1.getWidth() * 0.5f);
		r1CenterY = (float)(r1.getY() + r1.getHeight() * 0.5f);
		
		EventQueue.invokeLater(new Runnable(){
			
			@Override
			public void run() {
				JFrame jf = new JFrame("CircRectRes2");
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

