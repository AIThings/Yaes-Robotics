package yaes.rcta.movement;

import java.util.ArrayList;
import java.util.Stack;

public class TSPNearestNeighbor{
    private int numberOfNodes;
    private Stack<Integer> stack;
    ArrayList<Integer> tspOrder;
    
    public TSPNearestNeighbor()
    {
        stack = new Stack<Integer>();
        tspOrder = new ArrayList<Integer>();
    }
 
    public ArrayList<Integer> tsp(int adjacencyMatrix[][])
    {
        numberOfNodes = adjacencyMatrix[1].length - 1;
        int[] visited = new int[numberOfNodes + 1];
        visited[1] = 1;
        stack.push(1);
        int element, dst = 0, i;
        int min = Integer.MAX_VALUE;
        boolean minFlag = false;
        System.out.print(1 + "\t");
        tspOrder.add(1);
        
        while (!stack.isEmpty())
        {
            element = stack.peek();
            i = 1;
            min = Integer.MAX_VALUE;
            while (i <= numberOfNodes)
            {
                if (adjacencyMatrix[element][i] > 1 && visited[i] == 0)
                {
                    if (min > adjacencyMatrix[element][i])
                    {
                        min = adjacencyMatrix[element][i];
                        dst = i;
                        minFlag = true;
                    }
                }
                i++;
            }
            if (minFlag)
            {
                visited[dst] = 1;
                stack.push(dst);
                System.out.print(dst + "\t");
                tspOrder.add(dst);
                minFlag = false;
                continue;
            }
            stack.pop();
        }
        return tspOrder;
    }
}
