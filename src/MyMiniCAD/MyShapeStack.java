package MyMiniCAD;

import java.util.ArrayList;

/* A stack to store all MyShape Objects in the cad file */
public class MyShapeStack {
    /* Use an ArrayList to implement the stack */
    private ArrayList<MyShape> myShapesStack;
    /* Stack top pointer */
    private int stackTop;

    /* Default constructor */
    MyShapeStack() {
        myShapesStack = new ArrayList<>();
        stackTop = 0;
    }

    /* Method to judge if the stack is empty
     * Note: the index 0 is always kept for the background and it is not count */
    public boolean isEmpty() {
        return stackTop <= 0;
    }

    /* Return the size of the stack */
    public int size() {
        return stackTop;
    }

    /* Pop the MyShape Object and decrement the stack top pointer */
    public MyShape pop() {
        return isEmpty() ? null : myShapesStack.get(stackTop--);
    }

    /* Push a MyShape Object into the stack and increment the stack top pointer */
    public void push(MyShape shape) {
        /* I wanted to implement undo and redo functions but now I don't make it
         * So below code now is useless */
        if (stackTop < myShapesStack.size() - 1) {
            for (int i=myShapesStack.size()-1; i>=stackTop; i--) {
                myShapesStack.remove(i);
            }
        }

        /* useful part */
        /* stack top pointer increments only when a geometric object is pushed
         * it won't increment when the background object is pushed */
        if (myShapesStack.size() > 0) {
            stackTop++;
        }
        myShapesStack.add(stackTop, shape);
    }

    /* Method to judge which MyShape Object is selected by a mouse click event */
    public MyShape select(double x, double y) {
        for (int i=stackTop; i>0; i--) {
            if (myShapesStack.get(i).selectShape(x, y)) {
                return myShapesStack.get(i);
            }
        }
        return null;
    }

    /* Print the information of the stack in Console */
    public void printInfo() {
        System.out.println("----------------");
        System.out.println("Stack Infomation");
        for (int i=stackTop; i>0; i--) {
            System.out.println("Index " + i + ": " + myShapesStack.get(i).getSettings().getShape());
        }
        System.out.println("stack top = " + stackTop);
        System.out.println("----------------");
    }

    /* Remove a specific MyShape Object in stack */
    public boolean remove(int index) {
        if (index > stackTop || index <= 0) {
            System.out.println("remove nothing");
            return false;
        }
        else {
            for (int i=index; i<=myShapesStack.size()-1; i++) {
                if (i!=index) {
                    myShapesStack.get(i).setIndex(i-1);
                }
            }
            myShapesStack.remove(index);
            stackTop--;
            return true;
        }
    }

    /* Remove all MyShape Object in the stack
     * Note: the No.0 MyShape Object won't be removed for it always remains as the background */
    public void clear() {
        for (int i=myShapesStack.size()-1; i>0; i--) {
            remove(i);
        }
        stackTop = 0;
    }

    /* Return a MyShape Object by input its index */
    public MyShape get(int index) {
        if (index > 0 && index <= stackTop) {
            return myShapesStack.get(index);
        }
        return null;
    }
}
