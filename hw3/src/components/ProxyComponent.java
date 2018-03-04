package components;

import java.util.HashMap;
import java.util.Map;

/**
 * Component representing an abstraction of a simple database.
 */
public class ProxyComponent extends ThreadedComponent
{  
  /**
   * Maps our id to original request.
   */
  private Map<Integer, IMessage> clientRequests = new HashMap<Integer, IMessage>();
  
  private ProxyWorker worker;
  
  public ProxyComponent(ProxyWorker worker)
  {
    this.worker = worker;
  }
  
  @Override
  public void handleRequest(RequestMessage msg)
  {
    RequestMessage ourRequest = new RequestMessage(this, msg.getKey());
    clientRequests.put(ourRequest.getId(), msg);
    worker.send(ourRequest);
  }
  
  @Override
  public void handleResult(ResultMessage msg)
  {
    int ourId = msg.getCorrelationId();
    String result = msg.getResult();
    
    // find the original request and make the worker available
    IMessage originalRequest = clientRequests.remove(ourId);
    
    // if we got a valid result from the worker, send result to client
    if (result != null)
    {
      IMessage reply = new ResultMessage(originalRequest.getId(), this, result);
      originalRequest.getSender().send(reply);
    }
  }
  
}
