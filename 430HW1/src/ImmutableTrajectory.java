
import java.awt.Point;

public class ImmutableTrajectory
{
  private final Point[] data;

  public ImmutableTrajectory(Point[] data)
  {
    //deep copy
    Point [] tempArray = new Point[data.length];
    int i = 0;
    for(Point p : data){
      Point temp = new Point(p.x,p.y);
      tempArray[i] = temp;
    }

    this.data = tempArray;
  }
  //clone
  public Point[] getValues()
  {
    return data.clone();
  }
  // clone
  public Point getValue(int index)
  {
    return new Point(data[index].x,data[index].y);

  }
  
}
