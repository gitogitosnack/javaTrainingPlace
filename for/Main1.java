public class Main1{
    public static void main(String[] args){
        System.out.println("Start: the methods calling from now on.");
        sayHello(args);
        System.out.println("End: the methods ended.");
    }

    public static void sayHello(String[] names){
        for (String name : names){
            System.out.println("Hello Mr. " + name + " ");
        }
    }
}


