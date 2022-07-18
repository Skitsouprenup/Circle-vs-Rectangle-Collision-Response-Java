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

public class CircRectRes1 {
	
	CircObject c1,c2;
	Rectangle2D r1;
	boolean isColliding;
	float c1OffsetX,c1OffsetY;
	float dstX,dstY;
	
	public static void main(String[] args) {
		new CircRectRes1();
	}
	
	public CircRectRes1() {
		
		c1 = new CircObject(0f,0f,40f,40f);
		c2 = new CircObject(0f,0f,40f,40f);
		r1 = new Rectangle2D.Float(150f,150f,110f,50f);
		
		EventQueue.invokeLater(new Runnable(){
			
			@Override
			public void run() {
				JFrame jf = new JFrame("CircRectRes1");
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
	
	float clamp(float value,float min,float max)
	{
		if(value < min) return min;
		else if(value > max) return max;
		
		return value;
	}
	
	void updateData(){
		//System.out.println("updating...");
		c1OffsetX = c1.getX() - c1.getWidth() * 0.5f;
		c1OffsetY = c1.getY() - c1.getHeight() * 0.5f;
		
		float nearestX = clamp(c1.getX(),(float)r1.getX(),(float)( r1.getX()+r1.getWidth() ));
		float nearestY = clamp(c1.getY(),(float)r1.getY(),(float)( r1.getY()+r1.getHeight() ));
		
		dstX = c1.getX()-nearestX;
		dstY = c1.getY()-nearestY;
		float centerRadius = dstX * dstX + dstY * dstY;
		 
		if(centerRadius < c1.getRadX() * c1.getRadX()) isColliding = true;
		else isColliding = false;
		
		if(isColliding) collisionResponse(nearestX,nearestY,centerRadius);
	}
	
	void collisionResponse(float nearestX,float nearestY,float radius){
		
	}
	
	void drawObjects(Graphics2D g2d){
		//System.out.println("drawing objects...");
		g2d.setPaint(Color.GREEN);
		g2d.fill(r1);
		c1.getCirc().setFrame(c1OffsetX,c1OffsetY,c1.getWidth(),c1.getHeight());
	    if(!isColliding) g2d.setPaint(Color.YELLOW);
		else{
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
			g2d.drawString("dstX: " + dstX + " ,dstY: " + dstY, 60f, 40f);
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

