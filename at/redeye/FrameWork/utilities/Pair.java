/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.utilities;

/**
 *
 * @author martin
 */
public class Pair<T, S>
{
  public Pair(T f, S s)
  { 
    first = f;
    second = s;   
  }
 
  public T getFirst()
  {
    return first;
  }
 
  public S getSecond() 
  {
    return second;
  }
 
  @Override
  public String toString()
  { 
    return "(" + first.toString() + ", " + second.toString() + ")"; 
  }
 
  private T first;
  private S second;
}

